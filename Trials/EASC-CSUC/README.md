== Energy Aware Software Controller ==

This project is part of [DC4Cities](www.dc4cities.eu)

= Installation =

To install, type:

    mvn install -DskipTests

= Usage =

The application can be started with:

    bin/EASC.sh 

An help screen with command line arguments can be obtained with:

    bin/EASC.sh -h

= Tests =
 
To run the test suite, launch:

    mvn test

= Eclipse =

To generate Eclipse files (.classpath and .project), run:

    mvn eclipse:eclipse

Then in Eclipse go to "File/Import/Existing project into workspace" and select the project.
