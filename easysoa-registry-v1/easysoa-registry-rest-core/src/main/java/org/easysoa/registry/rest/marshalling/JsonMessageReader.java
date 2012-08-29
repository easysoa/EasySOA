package org.easysoa.registry.rest.marshalling;

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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class JsonMessageReader implements MessageBodyReader<Object> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        JsonNode jsonNode = mapper.readValue(entityStream, JsonNode.class);
        if (jsonNode.isArray()) {
            return mapper.readValue(jsonNode, SoaNodeInformation[].class);
        }
        else {
            JsonNode idNode = jsonNode.get("id");
            if (idNode != null && idNode.has("type") && idNode.has("name")) {
                return mapper.readValue(jsonNode, SoaNodeInformation.class);
            }
            else if (jsonNode.has("result")) {
                return mapper.readValue(jsonNode, OperationResult.class);
            }
        }
        return null;
    }

}
