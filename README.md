# chess4j

an XBoard compatible Java based chess engine

## Introduction 

chess4j is a chess program written using Java technologies. It is not meant to be super competitive, but just a test bed of sorts for various interests. Those interests include experimenting with different JVM based languages, parallel and distributed computing, and machine learning.

You can read more about chess4j at http://www.jamesswafford.com/chess4j/ .

## Installing

To play chess4j, you'll need a Java 11 or later JRE and Winboard or Xboard.

To see if you have a JRE installed, open a command prompt and type 'java -version'.  If you need to download a JRE you can download one from the Oracle website:

https://www.oracle.com/java/technologies/javase-downloads.html

See http://www.gnu.org/software/xboard for details on installing Winboard or Xboard.

Once those prerequisites are met you can download the latest release and extract the zipfile.  Once you extract the zipfile, open the 'chess4j.bat' file and modify the path to match the location you just unzipped to.

## Building from Source

chess4j can be built with or without <a href="https://github.com/jswaff/prophet4" target="_blank">Prophet4</a> bundled as a static library.  The benefit of bundling Prophet4 is simply speed.  When Prophet is bundled and activated (using the '-native' command line argument), chess4j will leverage Prophet when "thinking."  Since Prophet is written in C and is native, it will execute about 2-3 times faster.  It doesn't move any faster, but it will "see further" and therefore play stronger moves.  Otherwise, the algorithms are the same.  Just keep in mind that native code is platform dependent.  Currently the only platform supported for bundling Prophet is Linux, but it may work on Mac as well.  I do plan to add support for Windows in the future.


Whether you want to bundle Prophet or not, you will need a Java 11 (or better) SDK and Maven.  You will probably also need to ensure the JAVA_HOME environment variable is properly set.


### Without the Prophet4 Engine


Clone the repository and go into the chess4j/chess4j-java directory.
 
 ```mvn clean install```  

Once this process is complete you should see the build artifact in the target directory, e.g. chess4j-java-4.0-uber.jar.  Verify everything is working:

```java -jar chess4j-java-4.0-uber.jar -suite=../src/test/resources/suites/wac2.epd```

You should see the program search for about 10 seconds and display the result.  


### With the Prophet4 Engine

This option is slighly more complex.  In addition to the other prerequisites, you'll also need a working C/C++ toolchain.  I always use gcc / g++.  Others may work but have not been tested.  You'll also need 'make' and a copy of  Google Test from https://github.com/google/googletest .  Finally, set an environment variable GTEST_DIR to point to the 'googletest' project (we don't need the googlemock stuff).

Once you have the prerequisites, clone the chess4j repository.  Since Prophet4 is a separate project, you'll need to do a recursive clone, e.g.

```git clone --recurse-submodules git@github.com:jswaff/chess4j.git```

If that worked you should see the contents of chess4j/lib/prophet4 populated.  Now, just go into the top level 'chess4j' directory and execute:

```make```

That will kick off the build process, first building Prophet, then the JNI code that is the "bridge" between the Java and C layer, and finally chess4j itself.  The final build artifact will be in the chess4j-java/target directory.

You now have the option to run with or without the native (Prophet4) code enabled by using the '-native' command line argument.  If you omit that argument, the native code will not be enabled.

Verify everything is working:

```java -jar chess4j-java-4.0-uber.jar -suite=../src/test/resources/suites/wac2.epd -native```

(Note the '-native' argument.)  

You should see the program search for about 10 seconds and display the result.  


## Opening Book

chess4j has a small opening book but it is not enabled by default.  If you would like to enable the opening book, you can do it with a command line parameter:

```-book=book.db```


## Memory Usage

Normally you wouldn't need to worry about memory usage, but if you want to tweak chess4j here is some important information.

chess4j currently employs two transposition tables.  One is used in the main search and one in the pawn evaluation.  The default size for each table is 128 MB.  (If you run with the -native option, the default size may be different.)
 
You can specify the maximum memory allocated to each table via command line parameters, but you would really only want to do this if you were running the program directly from the command line, and not using a Winboard compatible GUI or test harness. 
(I do this when running test suites but that's about it.)  

```
-hash=256 -phash=256
``` 

The above arguments would allocate 256 MB to each table.  
 
Winboard / XBoard has an option to specify the maximum memory usage, and chess4j does respect that.  chess4j will divide the memory equally between the two tables.


## Running Test Suites

You can run EPD formatted test suites with chess4j using the 'suite' command line argument.  The default time per problem is 10 seconds, but that can also be changed with the 'time' argument.

```
java -jar chess4j-java-4.0-uber.jar -suite=wac.epd  -time=30
```

The command above would start chess4j to process the Win At Chess (WAC) test suite, giving it 30 seconds per problem.  (A few test suites can be found in the test/resources folder.)
