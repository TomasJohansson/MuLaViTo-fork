CHANGELOG MuLaViTo
==================

Version 0.9.1, 2011-12-30
-------------------------
- Update for Java 7 and release of MuLaNEO
- Fix IndexOutOfBounds exception with samples
- README in Markdown syntax

Version 0.9, 2011-10-28
-----------------------
- Apply fix to build script to allow building via ant as described in the wiki
- Improve implementation of Suurballe-Tarjan disjoint shortest path algorithm
  - Derive from common ShortestPathAlgorithm class
  - Allow to generalize comparison of edges for disjointness
  - In case there is no disjoint path, return just one path and not an
    additional empty list
  - Skip a superfluous shortest path calculation
  - Fix possible infinite loop
  - Adopt algorithms for multi-graphs
- Add BernoulliStream distribution to mulavito.utils.distributions
- Fix build error regarding Java META-INF that prevented signing of JARs

Version 0.8, 2011-05-31
-----------------------
- Better check for Java version on Apple
- Force build script to Java 1.5 code
- Fix compilation issue (SelectionPanelMouseAdapter) on Microsoft Windows
  using the ant build script
- Fix ClassScanner on Microsoft Windows platforms
- Add missing @Override annotations
- Fire update whenever a changelister is added or removed
- Update ignore file for automatic downloads
- Add demonstrator for Eppstein, Yen, and Suurballe-Tarjan algorithms
- Resolve issue with Suurballe when no disjoint paths could be found
- Split SampleGraphDocument into separate classes
  (MyE, MyEA, MyEB, MyL, MyLA, MyLB, MyMLG) for more general accessibility
- Refactor method names of SuurballeTarjan
- Generalize SuurballeTarjan algorithm by using Number instead of Double
- Add SampleGraphDocument#createConnectedDemo method
- Fix of by one error in ReachabilityEnsuringEdgeGenerator

Version 0.7, 2011-04-27
-----------------------
Initial public release under GPL/LGPL.
