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

package org.easysoa;

import java.util.List;
import java.util.Map;

import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.content.template.factories.BaseContentFactory;
import org.nuxeo.ecm.platform.content.template.service.ACEDescriptor;
import org.nuxeo.ecm.platform.content.template.service.TemplateItemDescriptor;
import org.nuxeo.runtime.api.Framework;

public class EasySOAContentFactory extends BaseContentFactory {

    protected boolean isTargetEmpty(DocumentModel eventDoc) throws ClientException  {
        // If we already have children : exit !!!
        return session.getChildren(eventDoc.getRef()).isEmpty();
    }


    @Override
    public void createContentStructure(DocumentModel eventDoc)
            throws ClientException {
        initSession(eventDoc);

        if (eventDoc.isVersion() || !isTargetEmpty(eventDoc)) {
            return;
        }

        DocumentService docService = Framework.getLocalService(DocumentService.class);
        // Touch default application
        docService.getDefaultAppliImpl(session);

    }

    @Override
    public boolean initFactory(Map<String, String> opionst,
            List<ACEDescriptor> rootAcl, List<TemplateItemDescriptor> template) {

        return true;
    }

}
