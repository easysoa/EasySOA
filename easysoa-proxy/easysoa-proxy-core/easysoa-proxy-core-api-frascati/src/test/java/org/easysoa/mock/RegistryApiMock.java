/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
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

package org.easysoa.mock;

import javax.ws.rs.Path;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.OperationResult;
import org.easysoa.registry.rest.SoaNodeInformation;
import org.easysoa.registry.rest.SoaNodeInformations;

/**
 *
 * @author jguillemotte
 */
@Path("easysoa/registry")
public class RegistryApiMock implements RegistryApi {

    @Override
    public OperationResult post(SoaNodeInformation soaNodeInfo) throws Exception {
        return new OperationResult(true);
    }

    @Override
    public SoaNodeInformations query(String subprojectId, String query) throws Exception {
        return new SoaNodeInformations();
    }

    @Override
    public SoaNodeInformation get(String subprojectId) throws Exception {
        return new SoaNodeInformation(null, null, null);
    }

    @Override
    public SoaNodeInformations get(String subprojectId, String doctype) throws Exception {
        return new SoaNodeInformations();
    }

    @Override
    public SoaNodeInformation get(String subprojectId, String doctype, String name) throws Exception {
        return new SoaNodeInformation(null, null, null);
    }

    @Override
    public OperationResult delete(String subprojectId, String doctype, String name) throws Exception {
        return new OperationResult(true);
    }

    @Override
    public OperationResult delete(String subprojectId, String doctype, String name, String correlatedSubprojectId, String correlatedDoctype, String correlatedName) throws Exception {
        return new OperationResult(true);
    }

}
