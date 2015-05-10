Divider
=======

This is a Swing-based desktop application for preprocessing data for finite-element modeling of deformation and creep problems (started as [Paul Lysak's](https://github.com/paul-lysak/divider) graduate work). User can create some figure, build a mesh from it and apply edge conditions (forces and fixed points).
This version is merging of Paul's v2.9a (completed in 2009) and path v2.9b (I didn't know his name yet) and my own changes. See *doc/changes.md* (Russian, utf-8) for details.

How to run
----------
You'll need [Maven](https://maven.apache.org/) to build it.

	cd <path-to-project>
	mvn package
	mvn exec:java
