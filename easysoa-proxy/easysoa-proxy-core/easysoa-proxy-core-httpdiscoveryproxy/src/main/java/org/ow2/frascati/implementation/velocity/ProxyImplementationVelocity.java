/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

/**
 * OW2 FraSCAti: SCA Implementation Velocity
 * Copyright (C) 2011 Inria, University Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Romain Rouvoy
 *
 * Contributor:
 *
 */
package org.ow2.frascati.implementation.velocity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.eclipse.stp.sca.Component;
import org.eclipse.stp.sca.ComponentReference;
import org.eclipse.stp.sca.PropertyValue;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;


/**
 * OW2 FraSCAti implementation template component class for Java proxy.
 * 
 * @author Romain Rouvoy - University Lille 1
 * @version 1.5
 */
public class ProxyImplementationVelocity extends ImplementationVelocity {
	@Property(name = "arguments")
	protected String params = "args";

	protected final String invoke(String method, VelocityContext context, String[] args, Object... parameters) {
		// VelocityContext context = new VelocityContext(this.velocityContext);
		context.put(params, parameters);
		// FIXME: should not be called but @Lifecycle does not work as expected.
		registerScaProperties();
		StringWriter sw = new StringWriter();
		// **** EasySOA Hack begin
		System.out.println("TEST PASSING in ProxyImplementationVelocity Hack");
		int pathArgIndex = Integer.parseInt((String) context.get("pathArgIndex")); // pathArgIndex has been set as an SCA xsd:int property
		int storeIndex = Integer.parseInt((String) context.get("storeIndex")); // storeIndex has been set as an SCA xsd:int property
		System.out.println("pathArgIndex = " + pathArgIndex);
		Template template = null;
		if (parameters.length > pathArgIndex) {
			Object templatePathFound = parameters[pathArgIndex];
			Object storeNameFound = parameters[storeIndex];
			System.out.println("templatePathFound = " + templatePathFound);
			System.out.println("storeNameFound = " + storeNameFound);
			if (templatePathFound instanceof String) {
				template = this.velocityEngine.getTemplate((String)storeNameFound + "/" + (String) templatePathFound);
				System.out.println("template = " + template);
			}
		}
		if (template == null) {
		// *** EasySOA Hack end
			String name = this.velocityEngine.templateExists(method + ".vm") ? method + ".vm" : this.defaultResource;
			template = this.velocityEngine.getTemplate(name);
		}
		if (!this.velocityEngine.invokeVelocimacro(method, method, args, context, sw)) {
			template.merge(context, sw);
		}
		sw.flush();
		System.out.println("returned by Proxy Velocity = " + sw.toString());
		return sw.toString();
	}

	protected static final String prefix = "arg";

	public static void generateContent(Component component, Class<?> itf, ProcessingContext processingContext, String outputDirectory, String packageGeneration, String contentClassName)
			throws FileNotFoundException {
		File packageDirectory = new File(outputDirectory + '/' + packageGeneration.replace('.', '/'));
		packageDirectory.mkdirs();

		PrintStream file = new PrintStream(new FileOutputStream(new File(packageDirectory, contentClassName + ".java")));

		file.println("package " + packageGeneration + ";\n");
		file.println("import org.apache.velocity.VelocityContext;\n");
		file.println("@" + Service.class.getName() + "(" + itf.getName() + ".class)");
		file.println("public class " + contentClassName + " extends " + ProxyImplementationVelocity.class.getName() + " implements " + itf.getName());
		file.println("{");
		int index = 0;
		for (PropertyValue propertyValue : component.getProperty()) {
			// Get the property value and class.
			Object propertyValueObject = processingContext.getData(propertyValue, Object.class);
			Class<?> propertyValueClass = (propertyValueObject != null) ? propertyValueObject.getClass() : String.class;
			file.println("  @" + Property.class.getName() + "(name = \"" + propertyValue.getName() + "\")");
			file.println("  protected " + propertyValueClass.getName() + " property" + index + ";\n");
			index++;
		}
		index = 0;
		for (ComponentReference componentReference : component.getReference()) {
			file.println("  @" + Reference.class.getName() + "(name = \"" + componentReference.getName() + "\")");
			file.println("  protected Object reference" + index + ";\n");
			index++;
		}

		for (Method m : itf.getMethods()) {
			String signature = "  public " + m.getReturnType().getName() + " " + m.getName() + "(";
			index = 0;
			for (Class<?> type : m.getParameterTypes()) {
				if (index > 0)
					signature += ", ";
				signature += type.getName() + " " + prefix + index;
				index++;
			}
			file.println(signature + ") {");
			file.println("    VelocityContext context = new VelocityContext(this.velocityContext);");
			String names = "";
			for (int i = 0; i < index; i++) {
				putContext(file, prefix + i, i);
				for (Annotation a : m.getParameterAnnotations()[i]) {
					if (a instanceof PathParam)
						names += putContext(file, ((PathParam) a).value(), i);
					else if (a instanceof FormParam)
						names += putContext(file, ((FormParam) a).value(), i);
					else if (a instanceof QueryParam)
						names += putContext(file, ((QueryParam) a).value(), i);
					else if (a instanceof HeaderParam)
						names += putContext(file, ((HeaderParam) a).value(), i);
					else if (a instanceof CookieParam)
						names += putContext(file, ((CookieParam) a).value(), i);
				}
			}

			String params = "";
			for (int i = 0; i < index; i++) {
				params += ", " + prefix + i;
			}

			names = names.length() > 0 ? names.substring(1) : names;
			file.println("    return invoke(\"" + m.getName() + "\", context, new String[]{" + names + "}" + params + ");");
			file.println("  }\n");
		}

		file.println("}");
		file.flush();
		file.close();
	}

	private static final String putContext(PrintStream file, String id, int index) {
		file.println("    context.put(\"" + id + "\", arg" + index + ");");
		return ", \"" + id + "\"";
	}
}
