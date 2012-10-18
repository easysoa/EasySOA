package org.easysoa.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import edu.uci.ics.jung.algorithms.shortestpath.ShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * 
 * @author mkalam-alami
 *
 */
public class SoaMetamodelServiceImpl extends DefaultComponent implements SoaMetamodelService {

    private static Logger logger = Logger.getLogger(SoaMetamodelServiceImpl.class);
    
    public static final String EXTENSIONPOINT_TYPES = "types";

    public static final String EXTENSIONPOINT_INHERITEDFACETS = "inheritedFacets";
    
    private Graph<String, String> graph = new DirectedSparseGraph<String, String>();
   
    private UnweightedShortestPath<String, String> shortestPath = new UnweightedShortestPath<String, String>(graph);
    
    private Set<String> inheritedFacets = new HashSet<String>();
    
    @Override
    public void registerContribution(Object contribution, String extensionPoint,
            ComponentInstance contributor) throws Exception {
        if (EXTENSIONPOINT_TYPES.equals(extensionPoint)) {
            SoaNodeTypeDescriptor descriptor = (SoaNodeTypeDescriptor) contribution;
            graph.addVertex(descriptor.name);
            for (String subtype : descriptor.subtypes) {
                graph.addVertex(subtype);
                graph.addEdge(descriptor.name + " contains " + subtype, descriptor.name, subtype);
            }
        }
        else if (EXTENSIONPOINT_INHERITEDFACETS.equals(extensionPoint)) {
        	InheritedFacetDescriptor descriptor = (InheritedFacetDescriptor) contribution;
        	if (descriptor.name != null) {
        		inheritedFacets.add(descriptor.name);
        	}
        	else {
        		logger.error("Invalid " + EXTENSIONPOINT_INHERITEDFACETS + " contribution, @name is null");
        	}
        }
    }
    
    public Collection<String> getChildren(String type) {
        return graph.getSuccessors(type);
    }

    public List<String> getPath(String fromType, String toType) {
        return getPath(graph, shortestPath, fromType, toType);
    }

    // Modified version of ShortestPathUtils.getPath()
    private static <V, E> List<V> getPath(Graph<V, E> graph, ShortestPath<V, E> sp, V source, V target) {
        LinkedList<V> path = new LinkedList<V>();
        Map<V, E> incomingEdges = sp.getIncomingEdgeMap(source);

        if (incomingEdges.isEmpty() || incomingEdges.get(target) == null) {
            return null;
        }
        V current = target;
        while (!current.equals(source)) {
            E incoming = incomingEdges.get(current);
            path.addFirst(current);
            Pair<V> endpoints = graph.getEndpoints(incoming);
            if (endpoints.getFirst().equals(current)) {
                current = endpoints.getSecond();
            } else {
                current = endpoints.getFirst();
            }
        }

        return path;
    }

    public Set<String> getInheritedFacets() {
		return inheritedFacets;
	}
    
    public Set<String> getInheritedFacets(Set<String> facetsToTest) {
    	Set<String> facetsSubset = new HashSet<String>(inheritedFacets);
    	facetsSubset.retainAll(facetsToTest);
    	return facetsSubset;
    }
    
}