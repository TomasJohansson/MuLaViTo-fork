/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2008-2011, The 100GET-E3-R3G Project Team.
 * 
 * This work has been funded by the Federal Ministry of Education
 * and Research of the Federal Republic of Germany
 * (BMBF Förderkennzeichen 01BP0775). It is part of the EUREKA project
 * "100 Gbit/s Carrier-Grade Ethernet Transport Technologies
 * (CELTIC CP4-001)". The authors alone are responsible for this work.
 *
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of MuLaViTo (Multi-Layer Visualization Tool).
 *
 * MuLaViTo is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * MuLaViTo is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with MuLaViTo; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package mulavito.algorithms.shortestpath.disjoint;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.ListUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Implementation of the SuurballeTarjan algorithm as found in:
 * 
 * <pre>
 * Disjoint Paths in a Network
 * 
 * J. W. Suurballe
 *   Bell Telephone Laboratories, Inc., Holmdel, New Jersey
 * </pre>
 * 
 * and
 * 
 * <pre>
 * A Quick Method for Finding Shortest Pairs of Disjoint Paths
 * 
 * J. W. Suurballe
 *   Bell Laboratories, West Long Branch, New Jersey
 * R. E. Tarjan
 *   Bell Laboratories,Murray Hill, New Jersey
 * </pre>
 * 
 * @author Thilo Mueller
 * @author Michael Duelli
 * @since 2010-09-15
 */
public class SuurballeTarjan<V, E> {
	private final Graph<V, E> g;
	private final Transformer<E, Double> weightTrans;
	private final DijkstraShortestPath<V, E> dijkstra;

	/**
	 * Constructor of the SuurballeTarjan-Algorithm
	 * 
	 * @param g
	 *            the original graph
	 * @param weightTrans
	 *            the weight-transformer
	 */
	public SuurballeTarjan(Graph<V, E> g, Transformer<E, Double> weightTrans) {
		if (g == null)
			throw new IllegalArgumentException();
		if (weightTrans == null)
			throw new IllegalArgumentException();

		this.g = g;
		this.weightTrans = weightTrans;
		this.dijkstra = new DijkstraShortestPath<V, E>(g, weightTrans);
	}

	/**
	 * The heart of the SuurballeTarjan algorithm.
	 * 
	 * @param source
	 *            the source node
	 * @param target
	 *            the target node
	 * @return two disjoint paths from the source to target node.
	 */
	public List<List<E>> suurballe(V source, V target) {
		List<E> path1 = dijkstra.getPath(source, target);

		// Determine length of shortest path from "source" to any other node.
		Map<V, Number> lengthMap = dijkstra.getDistanceMap(source);

		// Length transformation.
		Transformer<E, Double> lengthTrans = lengthTransformation(g,
				MapTransformer.getInstance(lengthMap));

		DijkstraShortestPath<V, E> alg3 = new DijkstraShortestPath<V, E>(g,
				lengthTrans);
		List<E> l3 = alg3.getPath(source, target);

		// Get shortest path in g with reversed shortest Dijkstra path...
		DijkstraShortestPath<V, E> alg4 = new DijkstraShortestPath<V, E>(
				reverseEdges(g, l3), lengthTrans);
		List<E> path2 = alg4.getPath(source, target);

		return findTwoWays(path1, path2);
	}

	/**
	 * Combines two disjoint paths from two SuurballeTarjan input paths.
	 * 
	 * @param path1
	 *            List with some links
	 * @param path2
	 *            List with some links
	 * @return the two disjoint paths
	 */
	public List<List<E>> findTwoWays(List<E> path1, List<E> path2) {
		if (path1 == null && path2 == null)
			throw new IllegalArgumentException();
		if (path1.size() <= 1 && path2.size() <= 1)
			throw new IllegalArgumentException();
		List<E> deleteFromPath1 = new LinkedList<E>();
		List<E> deleteFromPath2 = new LinkedList<E>();

		// if there is the same link in both paths, delete them
		for (E iLink : path1)
			for (E oLink : path2)
				if ((g.getSource(iLink).equals(g.getDest(oLink)))
						&& (g.getDest(iLink).equals(g.getSource(oLink)))
						|| (g.getSource(iLink).equals(g.getSource(oLink)))
						&& (g.getDest(iLink).equals(g.getDest(oLink)))) {
					deleteFromPath1.add(iLink);
					deleteFromPath2.add(oLink);
				}
		for (E e : deleteFromPath1)
			path1.remove(e);
		for (E e : deleteFromPath2)
			path2.remove(e);

		// Now recombine the two paths.
		List<E> union = ListUtils.union(path1, path2);
		final V target = g.getDest(path1.get(path1.size() - 1));

		LinkedList<E> p1 = new LinkedList<E>(); // provides getLast
		p1.add(path1.get(0));
		union.remove(path1.get(0));
		V curDest;
		while (!(curDest = g.getDest(p1.getLast())).equals(target)) {
			for (E e : union)
				if (g.getSource(e).equals(curDest)) {
					p1.add(e);
					break;
				}
			union.remove(p1.getLast());
		}

		LinkedList<E> p2 = new LinkedList<E>(); // provides getLast
		p2.add(path2.get(0));
		union.remove(path2.get(0));
		while (!(curDest = g.getDest(p2.getLast())).equals(target)) {
			for (E e : union)
				if (g.getSource(e).equals(curDest)) {
					p2.add(e);
					break;
				}
			union.remove(p2.getLast());
		}

		if (!union.isEmpty())
			throw new AssertionError("BUG");

		List<List<E>> solution = new LinkedList<List<E>>();
		solution.add(p1);
		solution.add(p2);
		return solution;
	}

	/**
	 * This method reverse the path "path" in the graph "graph" and returns it.
	 * 
	 * @param graph
	 *            the input graph which will not be changed.
	 * @param path
	 *            the path tu reverse
	 * @return a new graph with the reversed path
	 */
	public Graph<V, E> reverseEdges(Graph<V, E> graph, List<E> path) {
		if (graph == null || path == null)
			throw new IllegalArgumentException();
		Graph<V, E> clone = new DirectedOrderedSparseMultigraph<V, E>();

		for (V v : graph.getVertices())
			clone.addVertex(v);
		for (E e : graph.getEdges())
			clone.addEdge(e, graph.getEndpoints(e));

		for (E link : path) {
			V src = clone.getSource(link);
			V dst = clone.getDest(link);
			clone.removeEdge(link);
			clone.addEdge(link, dst, src, EdgeType.DIRECTED);
		}
		return clone;
	}

	/**
	 * This method does the following length transformation:
	 * 
	 * <pre>
	 *  c'(v,w) = c(v,w) - d (s,w) + d (s,v)
	 * </pre>
	 * 
	 * @param graph1
	 *            the graph
	 * @param slTrans
	 *            The shortest length transformer
	 * @return the transformed graph
	 */
	public Transformer<E, Double> lengthTransformation(Graph<V, E> graph1,
			Transformer<V, Number> slTrans) {
		Map<E, Double> map = new HashMap<E, Double>();

		for (E link : graph1.getEdges()) {
			double newWeight = weightTrans.transform(link)
					- slTrans.transform(graph1.getDest(link)).doubleValue()
					+ slTrans.transform(graph1.getSource(link)).doubleValue();

			map.put(link, newWeight);
		}
		return MapTransformer.getInstance(map);
	}
}
