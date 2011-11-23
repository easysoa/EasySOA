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

package org.easysoa.sca.visitors;

import org.easysoa.api.EasySOAApiSession;
import org.easysoa.sca.IScaImporter;

/**
 * Visitor for REST reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mdutoo
 *
 */
public abstract class ScaVisitorBase implements ScaVisitor {

    protected IScaImporter scaImporter;

    protected EasySOAApiSession api;
    
    public ScaVisitorBase(IScaImporter scaImporter, EasySOAApiSession api) {
        this.scaImporter = scaImporter;
        this.api = api;
    }
    
    
    
    //@Override
    public String getDescription() {
        return this.toString();
    }

}
