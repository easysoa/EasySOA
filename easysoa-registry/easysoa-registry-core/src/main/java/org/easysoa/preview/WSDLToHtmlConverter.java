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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.impl.blob.StreamingBlob;
import org.nuxeo.ecm.core.convert.api.ConversionException;
import org.nuxeo.ecm.core.convert.cache.SimpleCachableBlobHolder;
import org.nuxeo.ecm.core.convert.extension.Converter;
import org.nuxeo.ecm.core.convert.extension.ConverterDescriptor;
import org.easysoa.preview.WSDLTransformer;
import org.nuxeo.runtime.services.streaming.InputStreamSource;

/**
 * Converter that uses XSLT to generate HTML view from a WSDL Because WSDL files
 * do not have a specific Mime-Type this converter needs to be called by it's
 * name ...
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 *
 */
public class WSDLToHtmlConverter implements Converter {

	@Override
	public BlobHolder convert(BlobHolder blobHolder,
			Map<String, Serializable> params) throws ConversionException {

		File out = null;
		try {
			out = File.createTempFile("wdslToHtml", ".html");
			WSDLTransformer.generateHtmlView(blobHolder.getBlob().getStream(),
					new FileOutputStream(out));

			Blob mainBlob = new FileBlob(new FileInputStream(out), "text/html",
					"UTF-8");
			Blob jsBlob = new StreamingBlob(new InputStreamSource(
					WSDLToHtmlConverter.class
							.getResourceAsStream("/XSLT/wsdl-viewer.js")));

			mainBlob.setFilename("index.html");
			jsBlob.setFilename("wsdl-viewer.js");

			List<Blob> blobs = new ArrayList<Blob>();
			blobs.add(mainBlob);
			blobs.add(jsBlob);
			return new SimpleCachableBlobHolder(blobs);

		} catch (Exception e) {
			throw new ConversionException("Error during html generation", e);
		} finally {
			if (out != null) {
				out.delete();
			}
		}
	}

	@Override
	public void init(ConverterDescriptor desc) {
		// NOP
	}

}
