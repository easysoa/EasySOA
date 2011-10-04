package org.easysoa.doctypes;

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

@Name("easysoaValidator")
@Scope(ScopeType.EVENT)
@Install(precedence = Install.FRAMEWORK)
public class PropertyValidatorBean extends EasySOADoctype {

    private final static FacesMessage ERROR_MESSAGE = new FacesMessage("Invalid URL");
    
    public void validateUrl(FacesContext context,
            UIComponent component, Object value) {
        String urlString = (String) value;
        if (urlString.equals(urlString.trim())) {
            try {
                new URL((String) value);
            }
            catch (MalformedURLException e) {
                throw new ValidatorException(ERROR_MESSAGE, e);
            }
        }
        else {
            throw new ValidatorException(ERROR_MESSAGE);
        }
        
    }
    
}