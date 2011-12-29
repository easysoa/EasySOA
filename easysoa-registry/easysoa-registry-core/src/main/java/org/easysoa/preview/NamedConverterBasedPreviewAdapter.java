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

package org.easysoa.preview;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.platform.preview.adapter.base.AbstractHtmlPreviewAdapter;
import org.nuxeo.ecm.platform.preview.api.PreviewException;
import org.nuxeo.runtime.api.Framework;

/**
 * Preview adapter that used a named converter instead of an input Mime-Type.
 * This is required because WSDL do not have a specific mime type.
 * (Should be moved inside nuxeo-core-convert-plugins)
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 *
 */
public class NamedConverterBasedPreviewAdapter extends
        AbstractHtmlPreviewAdapter {

    protected DocumentModel doc;

    protected String converterName;

    public NamedConverterBasedPreviewAdapter(DocumentModel doc,
            String converterName) {
        super();
        this.doc = doc;
        this.converterName = converterName;
    }

    @Override
    protected List<Blob> getPreviewBlobs() throws PreviewException {

        List<Blob> blobs = new ArrayList<Blob>();

        ConversionService cs = Framework.getLocalService(ConversionService.class);
        BlobHolder bh = doc.getAdapter(BlobHolder.class);

        try {
            BlobHolder result = cs.convert(converterName, bh, null);
            blobs.addAll(result.getBlobs());
        } catch (Exception e) {
            throw new PreviewException("Unable to run convertion", e);

        }
        return blobs;
    }

    @Override
    protected List<Blob> getPreviewBlobs(String arg0) throws PreviewException {
        return getPreviewBlobs();
    }

    @Override
    public boolean cachable() {
        return false;
    }

    @Override
    public void cleanup() {
        // NOP cache is managed by conversion service
    }

}
