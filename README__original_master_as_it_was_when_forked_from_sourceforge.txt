The git commit 0e5c52ccbc34af7b4704ef7c0a1b1b7c02030f46 from the date 2012-03-15 was the last commit (as of today 2017-12-21 i.e. 5+ years later)
at the sourceforge website from where this repository was copied i.e. from the following website:
https://sourceforge.net/projects/mulavito/

I have never been involved in that project but recently found it when looking for implementations of "k shortest paths"
(e.g. implementations using Yen's algorithm https://en.wikipedia.org/wiki/Yen%27s_algorithm )
I found one such implementation here:
https://sourceforge.net/p/mulavito/git/ci/master/tree/src/mulavito/algorithms/shortestpath/ksp/Yen.java

I wanted to use that MuLaViTo implementation to create an adapter for it within the following project:
https://github.com/TomasJohansson/adapters-shortest-paths

However, the project at sourceforge was not a maven (nor gradle) project and not as convenient to reuse as such projects are.
Also, there were some dependencies which probably should be possible to remove (at least some of them I hope, when only being interested in "k shortest paths").
Therefore I copied/forked the project into my github site with the purpose of creating a maven pom file and trying to eliminate some dependencies.

At the website, the following dependencies are specified:
	Requirements
		Java 7 or higher, http://www.openjdk.org or http://java.oracle.com
		The JUNG graph framework v2.0.1, http://jung.sf.net
		Batik SVG Toolkit v1.7, http://xmlgraphics.apache.org/batik
		Tango Icon Library, http://tango.freedesktop.org
			Included in MuLaViTo (package img)
		JUnit v4.x (for unit testing only), http://www.junit.org
			E.g. built into Eclipse
		ANT v1.7 (for running the build script,
		not required for use in Eclipse), http://ant.apache.org

I have forked the project into the following github URL:
https://github.com/TomasJohansson/MuLaViTo-fork

There I created a branch and tag named "original_master_as_it_was_when_forked_from_sourceforge" and I do not currently have intention to do 
any further changes into this branch, but instead intend to use some other branch for creating maven file and removing files not needed for my purpose.

/ Tomas Johansson , 2017-12-21
