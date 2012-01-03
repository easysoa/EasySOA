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

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.preview.adapter.PreviewAdapterFactory;
import org.nuxeo.ecm.platform.preview.api.HtmlPreviewAdapter;

/**
 *
 * Factory for {@link HtmlPreviewAdapter} for the Service doc type of EasySOA
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 *
 */
public class EasySOAPreviewAdapterFactory implements PreviewAdapterFactory {

	@Override
	public HtmlPreviewAdapter getAdapter(DocumentModel doc) {

		if ("Service".equals(doc.getType())) {
			return new NamedConverterBasedPreviewAdapter(doc, "wsdl2html");
		}

		return null;
	}

}
