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

package org.easysoa.doctypes;


/**
 * Mirror of the properties defined in the XML contributions.
 * Defines all of the custom Workspace data.
 * 
 * @author mkalam-alami
 *
 */
public class Workspace {

    public static final String DOCTYPE = "Workspace";
    public static final String SCHEMA = "workspacedef";
    public static final String SCHEMA_PREFIX = "wsdef:";
    
    // Workspace properties
    public static final String PROP_REFERENCEDWORKSPACE = "referencedWorkspace";
    public static final String PROP_ISVALIDATED = "isValidated";
    public static final String PROP_VALIDATIONLOG = "validationLog";
    
}