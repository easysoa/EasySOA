package org.openwide.easysoa.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

@Consumes("application/json+nxrequest; charset=UTF-8")
@Provider
public class BodyReader implements MessageBodyReader<String> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mt) {
		if(mt.getType().equals("application/json+nxrequest; charset=UTF-8")){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String readFrom(Class<String> arg0, Type arg1, Annotation[] arg2, MediaType arg3, MultivaluedMap<String, String> arg4, InputStream arg5) throws IOException, WebApplicationException {
		byte[] byteArray = new byte[arg5.available()];
		arg5.read(byteArray);
		return new String(byteArray);
	}

}
