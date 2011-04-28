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
package mulavito.samples.utils;

import mulavito.graph.AbstractLayerStack;
import mulavito.graph.IEdge;
import mulavito.graph.ILayer;
import mulavito.graph.IVertex;
import mulavito.graph.LayerChangedEvent;
import mulavito.graph.generators.IEdgeGenerator;
import mulavito.graph.generators.RandomEdgeGenerator;
import mulavito.graph.generators.ReachabilityEnsuringEdgeGeneratorWrapper;
import mulavito.utils.distributions.UniformStream;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * simple graph document, currently hosting one multilayergraph, but could also
 * have some metadata, or alternative multilayergraphs, or a composite graph
 * whatsoever. it offers changesupport, i.e. if one component of the
 * multilayergraph changes, all viewing controls will be notified.
 * 
 * @author Julian Ott
 * @author Michael Duelli
 * @since 2010-08-24
 */
public class SampleGraphDocument {
	/**
	 * vertex class for MyL
	 */
	public class MyV implements IVertex {
		private int id = -1;

		@Override
		public int getId() {
			return id;
		}

		@Override
		public String getLabel() {
			return "V";
		}

		@Override
		public String toString() {
			return getLabel() + id;
		}
	}

	/**
	 * simple edge class, could fill this with some data
	 */
	public abstract class MyE implements IEdge {
		private int id = -1;

		@Override
		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return "E" + id;
		}
	}

	public class MyEA extends MyE {
		public int paramA;
	}

	public class MyEB extends MyE {
		public int paramB;
	}

	/**
	 * simple layer class with change support
	 */
	@SuppressWarnings("serial")
	public abstract class MyL extends DirectedSparseMultigraph<MyV, MyE>
			implements ILayer<MyV, MyE> {
		private int layer = -1;
		private MyMLG owner = null;

		@Override
		public boolean addVertex(MyV vertex) {
			if (!super.addVertex(vertex))
				return false;
			vertex.id = getVertexCount();
			if (owner != null)
				owner.fireLayerChanged(this);
			return true;
		}

		@Override
		public boolean removeVertex(MyV vertex) {
			if (!super.removeVertex(vertex))
				return false;
			vertex.id = -1;
			if (owner != null)
				owner.fireLayerChanged(this);
			return true;
		}

		@Override
		public boolean addEdge(MyE edge, Pair<? extends MyV> endpoints,
				EdgeType edgeType) {
			if (!super.addEdge(edge, endpoints, edgeType))
				return false;
			edge.id = getEdgeCount();
			if (owner != null)
				owner.fireLayerChanged(this);
			return true;
		}

		@Override
		public boolean removeEdge(MyE edge) {
			if (!super.removeEdge(edge))
				return false;
			edge.id = -1;
			if (owner != null)
				owner.fireLayerChanged(this);
			return true;
		}

		@Override
		public String getLabel() {
			return "Layer " + layer;
		}

		@Override
		public int getLayer() {
			return layer;
		}

		@Override
		public int hashCode() {
			return layer;
		}

		public MyMLG getOwner() {
			return owner;
		}
	}

	@SuppressWarnings("serial")
	private class MyLA extends MyL {
		@Override
		public Factory<MyE> getEdgeFactory() {
			return new Factory<MyE>() {
				@Override
				public MyE create() {
					return new MyEA();
				}
			};
		}
	}

	@SuppressWarnings("serial")
	private class MyLB extends MyL {
		@Override
		public Factory<MyE> getEdgeFactory() {
			return new Factory<MyE>() {
				@Override
				public MyE create() {
					return new MyEB();
				}
			};
		}
	}

	/** simple multi-layer graph with change support */
	private class MyMLG extends AbstractLayerStack<MyL> {
		private void fireLayerChanged(MyL layer) {
			fireStateChanged(new LayerChangedEvent<MyL>(this, layer, false));
		}

		@Override
		public void addLayer(MyL layer) {
			if (layer != null && !layers.contains(layer)) {
				layers.add(layer);
				layer.layer = layers.size() - 1;
				layer.owner = this;
				fireStateChanged(new LayerChangedEvent<MyL>(this, layer, false));
			}
		}

		// private void removeLayer(MyL layer) {
		// if (layers.contains(layer)) {
		// layers.remove(layer);
		// layer.layer = -1;
		// layer.owner = null;
		// fireStateChanged(new LayerChangedEvent<MyV, MyE>(this, layer,
		// true));
		// }
		// }

		// public void clear() {
		// while (!layers.isEmpty())
		// removeLayer(layers.get(0));
		// }
	}

	private MyMLG mlg = new MyMLG();

	public MyMLG getMlg() {
		return mlg;
	}

	/**
	 * creates a simple demo document, and fills it with the given number of
	 * random layers
	 * 
	 * @param numlayers
	 *            number of layers
	 */
	public static SampleGraphDocument createDemo(int numlayers) {
		UniformStream rnd = new UniformStream();
		SampleGraphDocument ret = new SampleGraphDocument();
		// create 2 layers
		for (int i = 0; i < numlayers; i++) {
			MyL layer;
			if (i % 2 != 0)
				layer = ret.new MyLA();
			else
				layer = ret.new MyLB();

			// create 3-23 vertices
			int numVertices = rnd.nextInt(20) + 3;
			for (int j = 0; j < numVertices; j++)
				layer.addVertex(ret.new MyV());

			// create 0-2*|V| edges
			int numEdges = rnd.nextInt(numVertices * 2);
			MyV[] vertices = layer.getVertices().toArray(new MyV[0]);
			for (int j = 0; j < numEdges; j++) {
				MyV a = vertices[rnd.nextInt(numVertices)], b = vertices[rnd
						.nextInt(numVertices)];
				if (a == b || layer.findEdge(a, b) != null)
					continue;
				layer.addEdge(layer.getEdgeFactory().create(), a, b);
			}
			ret.getMlg().addLayer(layer);
		}
		return ret;
	}

	/**
	 * Creates a demo document, and fills it with the given number of random
	 * layers that are guaranteed to be connected
	 * 
	 * @param numlayers
	 *            number of layers
	 */
	public static SampleGraphDocument createConnectedDemo(int numlayers) {
		UniformStream rnd = new UniformStream();
		SampleGraphDocument ret = new SampleGraphDocument();
		// create 2 layers
		for (int i = 0; i < numlayers; i++) {
			MyL layer;
			if (i % 2 != 0)
				layer = ret.new MyLA();
			else
				layer = ret.new MyLB();

			// create 3-23 vertices
			int numVertices = rnd.nextInt(20) + 3;
			for (int j = 0; j < numVertices; j++)
				layer.addVertex(ret.new MyV());

			// create 0-2*|V| edges
			IEdgeGenerator<MyV, MyE> edgeGen = new ReachabilityEnsuringEdgeGeneratorWrapper<MyV, MyE>(
					new RandomEdgeGenerator<MyV, MyE>(rnd.nextDouble()));

			edgeGen.generate(layer);

			ret.getMlg().addLayer(layer);
		}
		return ret;
	}
}
