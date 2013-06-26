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

package org.easysoa.proxy.app.api;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.osoa.sca.annotations.Service;

/**
 * TODO LATER extract web framework in separate project,
 * or even enrich FraSCAti ServletImplementationVelocity (ex. Wrapper for object httpRequest
 * to maps the different params in a hashMap for easy access)
 *
 * @author jguillemotte
 */
@Service
public interface View {

    /**
     * Rendering interface
     * @param params Parameters to be used in the template
     * @return
     */
    public String render(Map<String, Object> params, HttpServletRequest request, User user);

}
