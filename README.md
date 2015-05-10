divider
=======

This is a Swing-based desktop application for preprocessing data for finite-element modeling 
of deformation and creep problems (started as [Paul Lysak's](https://github.com/paul-lysak/divider) graduate work). User can create some figure, build a mesh from it and apply edge conditions 
(forces and fixed points)
It was complete back in 2009 and wasn't updated since then (other then switching to Maven)

How to run
----------
You'll need maven to build it.

    mvn clean install
    java -jar target/divider-2.6a.jar
