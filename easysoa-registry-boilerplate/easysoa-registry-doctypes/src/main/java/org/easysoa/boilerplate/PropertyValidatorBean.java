/**
 * EasySOA Registry
 * Copyright 2011-2012 Open Wide
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

package org.easysoa.boilerplate;

import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("easysoaPropertyValidator")
@Scope(ScopeType.EVENT)
@Install(precedence = Install.FRAMEWORK)
public class PropertyValidatorBean {

    private static final FacesMessage ERROR_MESSAGE = new FacesMessage("Invalid URL");

    public void validateUrl(FacesContext context, UIComponent component, Object value) {
        String urlString = (String) value;
        if (urlString.equals(urlString.trim())) {
            try {
                new URL((String) value);
            } catch (MalformedURLException e) {
                throw new ValidatorException(ERROR_MESSAGE, e);
            }
        } else {
            throw new ValidatorException(ERROR_MESSAGE);
        }
    }

}