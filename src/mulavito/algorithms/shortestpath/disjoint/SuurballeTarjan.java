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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mulavito.algorithms.shortestpath.ShortestPathAlgorithm;

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
 * @author Michael Duelli
 * @author Thilo Mueller
 * @since 2010-09-15
 */
public class SuurballeTarjan<V, E> extends ShortestPathAlgorithm<V, E> {
	/**
	 * Constructor of the SuurballeTarjan-Algorithm
	 * 
	 * @param graph
	 *            the original graph
	 * @param nev
	 *            the weight-transformer
	 */
	public SuurballeTarjan(Graph<V, E> graph, Transformer<E, Number> nev) {
		super(graph, nev);
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
	public List<List<E>> getDisjointPaths(V source, V target) {
		List<E> path1 = dijkstra.getPath(source, target);

		// Determine length of shortest path from "source" to any other node.
		Map<V, Number> lengthMap = dijkstra.getDistanceMap(source);

		// Length transformation.
		Transformer<E, Double> lengthTrans = lengthTransformation(graph,
				MapTransformer.getInstance(lengthMap));

		DijkstraShortestPath<V, E> alg3 = new DijkstraShortestPath<V, E>(graph,
				lengthTrans);
		List<E> l3 = alg3.getPath(source, target);

		// Get shortest path in g with reversed shortest Dijkstra path...
		DijkstraShortestPath<V, E> alg4 = new DijkstraShortestPath<V, E>(
				reverseEdges(graph, l3), lengthTrans);
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
		if (path1 == null || path2 == null)
			throw new IllegalArgumentException();
		else if (path1.isEmpty() || path2.isEmpty()) {
			// no disjoint solution found
			LinkedList<List<E>> result = new LinkedList<List<E>>();
			result.add(path1);
			result.add(new LinkedList<E>());
			return result;
		}

		List<E> copy = new LinkedList<E>(path1);

		// if there is the same link in both paths, delete them
		Iterator<E> it1 = path1.iterator();
		while (it1.hasNext()) {
			E iLink = it1.next();

			Iterator<E> it2 = path2.iterator();
			while (it2.hasNext()) {
				E oLink = it2.next();
				if ((graph.getSource(iLink).equals(graph.getDest(oLink)))
						&& (graph.getDest(iLink).equals(graph.getSource(oLink)))
						|| (graph.getSource(iLink).equals(graph
								.getSource(oLink)))
						&& (graph.getDest(iLink).equals(graph.getDest(oLink)))) {
					it1.remove();
					it2.remove();
				}
			}
		}

		if (path1.isEmpty() || path2.isEmpty()) {
			// no disjoint solution found
			LinkedList<List<E>> result = new LinkedList<List<E>>();
			result.add(copy);
			result.add(new LinkedList<E>());
			return result;
		}

		// Now recombine the two paths.
		List<E> union = ListUtils.union(path1, path2);
		final V target = graph.getDest(path1.get(path1.size() - 1));

		LinkedList<E> p1 = new LinkedList<E>(); // provides getLast
		p1.add(path1.get(0));
		union.remove(path1.get(0));
		V curDest;
		while (!(curDest = graph.getDest(p1.getLast())).equals(target)) {
			for (E e : union)
				if (graph.getSource(e).equals(curDest)) {
					p1.add(e);
					break;
				}
			union.remove(p1.getLast());
		}

		LinkedList<E> p2 = new LinkedList<E>(); // provides getLast
		p2.add(path2.get(0));
		union.remove(path2.get(0));
		while (!(curDest = graph.getDest(p2.getLast())).equals(target)) {
			for (E e : union)
				if (graph.getSource(e).equals(curDest)) {
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
	private Transformer<E, Double> lengthTransformation(Graph<V, E> graph1,
			Transformer<V, Number> slTrans) {
		Map<E, Double> map = new HashMap<E, Double>();

		for (E link : graph1.getEdges()) {
			double newWeight = nev.transform(link).doubleValue()
					- slTrans.transform(graph1.getDest(link)).doubleValue()
					+ slTrans.transform(graph1.getSource(link)).doubleValue();

			map.put(link, newWeight);
		}
		return MapTransformer.getInstance(map);
	}
}
