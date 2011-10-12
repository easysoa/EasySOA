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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.sca.visitors;

import javax.xml.namespace.QName;

import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.ScaImporter;

/**
 * Visitor for REST reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mdutoo
 *
 */
public class RestReferenceBindingVisitor extends ReferenceBindingVisitorBase {
    
    public RestReferenceBindingVisitor(IScaImporter scaImporter) {
        super(scaImporter);
    }
    
    public boolean isOkFor(QName bindingQName) {
        return bindingQName.equals(new QName(ScaImporter.SCA_URI, "binding.rest"));
    }

    @Override
    protected String getBindingUrl() {
        // getting referenced service url
        String refUrl = compositeReader.getAttributeValue(ScaImporter.FRASCATI_URI, "uri");
        return refUrl;
    }

}
