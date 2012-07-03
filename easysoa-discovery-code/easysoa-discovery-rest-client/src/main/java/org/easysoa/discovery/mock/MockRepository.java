package org.easysoa.discovery.mock;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.easysoa.discovery.rest.model.Deliverable;
import org.easysoa.discovery.rest.model.ServiceImpl;
import org.easysoa.discovery.rest.model.SoaNode;
import org.easysoa.discovery.rest.model.SoaNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class MockRepository extends OutputStream {

    private static final Logger logger = LoggerFactory.getLogger(MockRepository.class);

    private StringBuffer requestBuffer = new StringBuffer();

    private Graph<String, String> graph = new DirectedSparseGraph<String, String>();

    private Map<String, SoaNode> nodes = new HashMap<String, SoaNode>();

    public OutputStream getOutputStream() {
        return this;
    }

    @Override
    public void write(int c) throws IOException {
        requestBuffer.append((char) c);
    }

    @Override
    public void close() throws IOException {
        super.close();

        // Build request string
        String requestString = requestBuffer.toString();
        requestBuffer = new StringBuffer();

        // Handle request
        if (!requestString.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                JsonNode readTree = mapper.readTree(requestString);
                Iterator<JsonNode> discoveryIt = readTree.getElements();

                // Handle each discovery
                while (discoveryIt.hasNext()) {
                    JsonNode discovery = discoveryIt.next();
                    SoaNodeType soaNodeType = SoaNodeType.valueOf(discovery.get("soaNodeType")
                            .getTextValue());
                    SoaNode newNode = null;
                    switch (soaNodeType) {
                    case Deliverable:
                        newNode = mapper.readValue(discovery, Deliverable.class);
                        break;
                    case ServiceImpl:
                        newNode = mapper.readValue(discovery, ServiceImpl.class);
                        break;
                    }

                    // Add graph vertex
                    if (!graph.containsVertex(newNode.getId())) {
                        graph.addVertex(newNode.getId());
                    }

                    // Add graph edges
                    for (Entry<String, SoaNodeType> relation : newNode.getRelations().entrySet()) {
                        addEdge(newNode.getId(), newNode.getSoaNodeType(), relation.getKey(),
                                relation.getValue());
                    }

                    // Store/merge node contents (XXX only the name/version/relations are merged)
                    SoaNode prevNode = nodes.get(newNode.getId());
                    if (prevNode != null) {
                        if (newNode.getName() == null) {
                            newNode.setName(prevNode.getName());
                        }
                        if (newNode.getVersion() == null) {
                            newNode.setVersion(prevNode.getVersion());
                        }
                        Map<String, SoaNodeType> relations = prevNode.getRelations();
                        relations.putAll(newNode.getRelations());
                        newNode.setRelations(relations);
                    }
                    nodes.put(newNode.getId(), newNode); // XXX Overriden instead of merged
                }

            } catch (Exception e) {
                logger.error("Failed to parse JSON discovery", e);
            }
        }
    }

    public void traceRepository() {
        logger.info("Repository contents");
        logger.info("-------------------");
        Collection<String> vertices = graph.getVertices();
        for (String vertice : vertices) {
            if (graph.getPredecessors(vertice).isEmpty()) {
                traceRepository(vertice, 0);
            }
        }
    }

    private void traceRepository(String currentId, int depth) {
        if (depth < 10) {
            SoaNode soaNode = nodes.get(currentId);
            if (soaNode != null) {
                logger.info(spacing(depth) + soaNode.toString());
                Collection<String> successors = graph.getSuccessors(currentId);
                for (String successor : successors) {
                    traceRepository(successor, depth + 1);
                }
            } else {
                logger.info(spacing(depth) + "???");
            }
        } else {
            logger.info(spacing(depth) + "...");
        }
    }

    private String spacing(int depth) {
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            spaces.append("  ");
        }
        return spaces.toString();
    }

    private void addEdge(String nodeId1, SoaNodeType nodeType1, String nodeId2,
            SoaNodeType nodeType2) {
        if (!isValidEdgeDirection(nodeType1, nodeType2)) {
            String buffer = nodeId1;
            nodeId1 = nodeId2;
            nodeId2 = buffer;
        }
        graph.addEdge(nodeId1 + " > " + nodeId2, nodeId1, nodeId2, EdgeType.DIRECTED);
    }

    private boolean isValidEdgeDirection(SoaNodeType from, SoaNodeType to) {
        return getTypeLevel(from) - getTypeLevel(to) > 0;
    }

    private int getTypeLevel(SoaNodeType soaNodeType) {
        switch (soaNodeType) {
        case Deliverable:
            return 8;
        case ServiceImpl:
            return 5;
        case System:
            return 10;
        default:
            return 0;
        }
    }

}
