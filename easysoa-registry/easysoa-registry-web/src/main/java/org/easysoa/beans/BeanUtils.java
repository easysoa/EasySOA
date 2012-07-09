package org.easysoa.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public class BeanUtils {

    public static List<SelectItem> modelsToSelectItems(DocumentModelList modelList) throws ClientException {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (DocumentModel model : modelList) {
            items.add(new SelectItem(model.getId(), model.getTitle()));
        }
        return items;
    }
    
    public static List<SelectItem> stringsToSelectItems(String[] strings) {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (String string : strings) {
            items.add(new SelectItem(string, string));
        }
        return items;
    }
}
