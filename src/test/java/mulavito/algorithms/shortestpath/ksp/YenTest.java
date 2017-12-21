package mulavito.algorithms.shortestpath.ksp;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.collections15.Transformer;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Regarding license: See the website https://github.com/TomasJohansson/MuLaViTo-fork
 * 
 * @author Tomas Johansson ( http://www.programmerare.com ) is the author of ONLY this test class and not for the code being tested 
 */
public final class YenTest {

	private Yen<String, Edge> yenKShortestPaths;
	
	private final String v0 = "0"; // vertex 0
	private final String v1 = "1"; // vertex 1
	private final String v2 = "2";
	private final String v3 = "3";
	private final String v4 = "4";
	
	private Graph<String, Edge> graph;
	private Transformer<Edge, Number> weightTransformer;
	
	@Before
	public void setUp() throws Exception {
		// "Number" need to be used because of this constructor: "public Yen(Graph<V, E> graph, Transformer<E, Number> nev) {"
		weightTransformer = new Transformer<Edge, Number>() {
			@Override
			public Number transform(Edge edge) {
				return edge.getWeight();
			}
		};
		
		graph = new DirectedOrderedSparseMultigraph<String, Edge>();
		// The below defined graph (and the expected paths in the test assertions in the test method)
		// are the same as being used here:
		// https://github.com/TomasJohansson/algorithms-sedgewick-wayne/blob/e31dafb8c82daa38781cb3b2a25f706f5d8f7203/test/chapter4/section4/KShortestPathsTest.java
		// which is based on the following test data defined in xml: 
		// https://github.com/TomasJohansson/adapters-shortest-paths/blob/41f85194ad21d05eab98d8baeb3f3f646469c141/adapters-shortest-paths-test/src/test/resources/test_graphs/origin_yanqi/test_5.xml
		addEdgeToGraph(v0, v1, 1);
		addEdgeToGraph(v0, v2, 7);
		addEdgeToGraph(v1, v2, 1);
		addEdgeToGraph(v2, v1, 1);
		addEdgeToGraph(v1, v3, 3);
		addEdgeToGraph(v1, v4, 2);
		addEdgeToGraph(v2, v4, 4);
		addEdgeToGraph(v3, v4, 1);
		
		yenKShortestPaths = new Yen<String, Edge>(graph, weightTransformer);
	}

	private void addEdgeToGraph(String startVertex, String endVertex, double weight) {
		graph.addEdge(new Edge(startVertex, endVertex, weight), startVertex, endVertex, EdgeType.DIRECTED);
	}


	@Test
	public void testGetShortestPaths() {
		final List<List<Edge>> paths = yenKShortestPaths.getShortestPaths(v0, v4, 10);
		assertEquals(6, paths.size());
		// the first path (i.e. THE shortest path) has total weight 3, and the path is the vertices 0,1,4
		// i.e. from vertex 0 to vertex 1 and then to vertex 4 ( 0-->1 , 1-->4 )
		assertPath(paths.get(0), 3, 	v0, v1, v4);    // expected total weight 3, and expected path: 0-->1 , 1-->4
		assertPath(paths.get(1), 5, 	v0, v1, v3, v4); // expected total weight 5, and expected path: 0-->1 , 1-->3 , 3-->4 
		assertPath(paths.get(2), 6, 	v0, v1, v2, v4); // and so on ... (i.e. with similar comments as above)
		assertPath(paths.get(3), 10, 	v0, v2, v1, v4);
		assertPath(paths.get(4), 11, 	v0, v2, v4);
		assertPath(paths.get(5), 12, 	v0, v2, v1, v3, v4);		
	}
	
	private void assertPath(
		final List<Edge> actualPath, 
		final double expectedTotalWeight, 
		final String... expectedSequenceOfVertices
	) {
		final double actualTotalWeight = calculateTotalWeightForPath(actualPath);
		assertEquals(expectedTotalWeight, actualTotalWeight, DELTA_VALUE_FOR_COMPARISONS_WITH_TYPE_DOUBLE);

		// In a path sequence (i.e. the list of edges), the start vertex of an 
		// edge is the same as the end vertex of the previous edge,
		// but except from the first edge which does not have a previous edge.
		// Therefore, the number of edges and number of vertices are almost equal, 
		// but one more vertex than there are edges:
		assertEquals(expectedSequenceOfVertices.length, actualPath.size() + 1);
		for (int i = 0; i < actualPath.size(); i++) {
			final Edge edge = actualPath.get(i);
			assertEquals(expectedSequenceOfVertices[i], edge.getStartVertex());
			assertEquals(expectedSequenceOfVertices[i+1], edge.getEndVertex());
		}		
	}

	private double calculateTotalWeightForPath(final List<Edge> edgesForPath) {
		double totalWeight = 0;
		for (Edge edge : edgesForPath) {
			totalWeight += edge.getWeight();
		}
		return totalWeight;
	}

	private final static double DELTA_VALUE_FOR_COMPARISONS_WITH_TYPE_DOUBLE = 0.0000001;
	
	// --------------------------------------------------------------------------
	private final static class Edge {
		private final String startVertex;
		private final String endVertex;
		private final double weight;

		public Edge(String startVertex, String endVertex, double weight) {
			this.startVertex = startVertex;
			this.endVertex = endVertex;
			this.weight = weight;
		}

		public double getWeight() {
			return weight;
		}

		public String getStartVertex() {
			return startVertex;
		}

		public String getEndVertex() {
			return endVertex;
		}
	}
	// --------------------------------------------------------------------------
}