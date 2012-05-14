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

package org.easysoa.beans;

import java.net.URL;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.rest.RestHelper;

/**
 * URL redirection bean 
 * 
 * @author mkalam-alami
 * 
 */
@Name("webEngineRedirection")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class WebEngineRedirectionBean {

    @In(create = true)
    NavigationContext navigationContext;

    public void downloadSoapUIConfForCurrentDocument() throws Exception {
        String path = "/nuxeo/site/easysoa/soapui/"; // SoapUI service path
        path +=  navigationContext.getCurrentDocument().getId(); // Param: doc ID
        path += ".xml";
        redirect(path);
    }
    
    public void downloadPropertiesForCurrentDocument() throws Exception {
        String path = "/nuxeo/site/easysoa/properties/"; // SoapUI service path
        path +=  navigationContext.getCurrentDocument().getId(); // Param: doc ID
        path += ".properties";
        redirect(path);
    }
    
    private void redirect(String filePath) throws Exception {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        
        URL requestUrl = new URL(request.getRequestURL().toString());
    	String redirectionUrl = requestUrl.toString().substring(0, requestUrl.toString().length() - requestUrl.getPath().toString().length()) + filePath; // Keep host only
    	
        RestHelper.handleRedirect(response, redirectionUrl);
    }
    
}
