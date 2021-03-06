Multi-Layer Visualization Tool (MuLaViTo)
=========================================

Features
--------

* Multi-layer graph framework based on JUNG
    * GUI class for a paneled main window (mulavito.gui.Gui)
    * Components for viewing a multi-layer graph (mulavito.gui.components.GraphPanel)
        * (Un)Synchronized view of individual layers
        * Hide or maximize certain layers using an improved GridLayout
        * Auto-rearranging and auto-resizing of multi-layer graph
    * SelectionPanel and QuickSearchBar for searching components
      in the multi-layer graph using regular expressions (e.g. V[1-5])
* Run any algorithm derived from mulavito.algorithms.IAlgorithm in a separate
  Java thread and show status information using ProgressBarDialog and
  an arbitrary number of AbstractAlgorithmStatus 
* FloatablePanel that can be docked/undocked as well as closed, e.g.
    * ConsolePanel to intercept stdout/stderr
    * LayerDataPanel for displaying graph degree data
* LocatableFileChooser remembering last path with different categories
* Scalable vector graphics (SVG) export of any layer (mulavito.utils.SVGExporter)
* Basis for content-aware pop-up menus
* Graph algorithms
    * Disjoint paths (mulavito.algorithms.shortestpath.disjoint)
        * Suurballe-Turjan
    * k-shortest paths (mulavito.algorithms.shortestpath.ksp)
        * Eppstein
        * Yen
* Graph generators (mulavito.graph.generators)
    * Random
    * Fully-meshed
    * Waxman
    * Wrapper to ensure reachability
* Basic random number stream generators for (mulavito.utils.distributions)
    * Uniform
    * Negative-exponential / Poisson
    * Deterministic
* Resource accessor for using embedded icons of the Tango project
  or icons outside of MuLaViTo in derived projects
* Class scanner utils (mulavito.utils.ClassScanner) for dynamic class loading
* Basic file filter
* Demonstrators (mulavito.samples)
* JUnits
* Ant-based build scripts with automatic downloads of dependencies

Requirements
------------
 
* Java 7 or higher, http://www.openjdk.org or http://java.oracle.com
* The JUNG graph framework v2.0.1, http://jung.sf.net
* Batik SVG Toolkit v1.7, http://xmlgraphics.apache.org/batik
* Tango Icon Library, http://tango.freedesktop.org
    * Included in MuLaViTo (package img)
* JUnit v4.x (for unit testing only), http://www.junit.org
    * E.g. built into Eclipse
* ANT v1.7 (for running the build script,
  not required for use in Eclipse), http://ant.apache.org

Basic Structure
---------------

The Java package hierarchy of MuLaViTo is structured as follows:

* ''AUTHORS'' -- A list of contributors to MuLaViTo and their affiliations
* ''COPYING'' -- The license information (GPL, LGPL)
* ''README'' -- This file 
* ''src''
    * ''img''
        * ''icons'' -- Tango icon set for use in this and derived projects, http://tango.freedesktop.org
    * ''mulavito''
        * ''algorithms'' -- basic classes and patterns for algorithms
            * ''shortestpath'' -- disjoint and k-shortest path algorithms
        * ''graph'' -- classes for graph data structure
            * ''generators'' -- Graph generators
        * ''gui'' -- all components and control structures
        * ''samples'' -- GUI demos
        * ''utils'' -- random distributions, class scanner, and everything else
	* ''tests'' -- JUnit tests, usually not exported      
* ''libs'' -- used libraries
* ''build.xml'' -- ANT that creates JARs for use by end user
  
Event Reference
---------------

Nearly all GUI class designs follow the setter/event handler pattern.
Here is a comprehensive list of events that can be listened to:

* mulavito.gui.components.GraphPanel<?,?>
    * PropertyChange "LayerStack": called before the LayerStack property changes
      with NewValue being the LayerStack to be set and OldValue the current loaded
      LayerStack
    * PropertyChange "Viewers": called when LayerViewers get added or removed.
        * after adding: NewValue is the new viewer, OldValue is null
        * before removing: NewValue is null, OldValue is the Viewer being removed.
      Classes having a property binding to a GraphPanel should listen to these events.
