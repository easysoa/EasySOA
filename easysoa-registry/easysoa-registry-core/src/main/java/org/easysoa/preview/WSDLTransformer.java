/**
 * EasySOA Registry
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

package org.easysoa.preview;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple helper class that generates an HTML rendition of a WSDL.
 *
 * It uses the XSLT provided by http://tomi.vanek.sk/xml/wsdl-viewer.xsl
 * (http://code.google.com/p/wsdl-viewer/)
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 *
 */
public class WSDLTransformer {

	protected static Log log = LogFactory.getLog(WSDLTransformer.class);

	protected static InputStream getXLST() {
		return WSDLTransformer.class
				.getResourceAsStream("/XSLT/wsdl-viewer.xsl");
	}

	public static void generateHtmlView(InputStream xmlStream, OutputStream out)
			throws Exception {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(
				getXLST()));
		transformer.setErrorListener(new ErrorListener() {

			@Override
			public void warning(TransformerException exception)
					throws TransformerException {
				log.warn("Problem during transformation", exception);
			}

			@Override
			public void fatalError(TransformerException exception)
					throws TransformerException {
				log.error("Fatal error during transformation", exception);
			}

			@Override
			public void error(TransformerException exception)
					throws TransformerException {
				log.error("Error during transformation", exception);
			}
		});
		transformer.setURIResolver(null);
		transformer.transform(new StreamSource(xmlStream),
				new StreamResult(out));
	}
}
