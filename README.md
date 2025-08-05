# chess4j

an XBoard compatible Java based chess engine

## Introduction 

chess4j is a chess program written using Java technologies. It is a test bed of sorts for various interests. Those interests include experimenting with different JVM based languages, parallel and distributed computing, and machine learning.

## Installing

To play chess4j, you'll need a Java 11 or later JRE and Winboard or Xboard.  To see if you have a JRE installed, open a command prompt and type 'java -version'.  If you need to download a JRE you can download one from the Oracle website:

https://www.oracle.com/java/technologies/downloads/

See http://www.gnu.org/software/xboard for details on installing Winboard or Xboard.

Once those prerequisites are met you can download the latest release and extract the zipfile.  Once you extract the zipfile, open the 'chess4j-wb.bat' file (or 'chess4j-xb.sh' on Linux) and modify the path to match the location you just unzipped to.

## Building from Source

chess4j can be built with or without <a href="https://github.com/jswaff/prophet" target="_blank">Prophet</a> bundled as a static library. 

Whether you want to bundle Prophet or not, you will need a Java 11 (or better) JDK and Maven.  You will probably also need to ensure the JAVA_HOME environment variable is properly set.


### Without the Prophet Engine


Clone the repository and go into the chess4j/chess4j-java directory.
 
 ```mvn clean install```  

Once this process is complete you should see the build artifact in the target directory.  Verify everything is working:

```java -jar chess4j-6.1-uber.jar -mode test -epd ../src/test/resources/suites/wac2.epd```

You should see the program search for about 10 seconds and display the result.  


### With the Prophet Engine 

*** Currently for Linux only ***

This option is slightly more complex.  In addition to the other prerequisites, you'll also need a working C/C++ toolchain.  I always use gcc / g++.  Others may work but have not been tested.  You'll also need 'make' and 'cmake'.

Once you have the prerequisites, clone the chess4j repository.  Since Prophet is a separate project, you'll need to do a recursive clone, e.g.

```git clone --recurse-submodules git@github.com:jswaff/chess4j.git```

If that worked you should see the contents of chess4j/lib/prophet populated.  Now, just go into the top level 'chess4j' directory and execute:

```make```

That will kick off the build process, first building Prophet, then the JNI code that is the "bridge" between the Java and C layer, and finally chess4j itself.  The final build artifact will be in the chess4j-java/target directory.

Verify everything is working:

```java -jar chess4j-6.1-uber.jar -mode test -epd ../src/test/resources/suites/wac2.epd -native```

You should see the program search for about 10 seconds and display the result.  

## Native Mode

*** Currently for Linux only ***

Assuming you built with Prophet bundled in or are using one of the supplied platform dependent builds, you can enable native mode using a command line argument:

```-native```

Native mode is significantly faster than Java mode, on the order of 2-3x in most cases.

## Opening Book

chess4j has a small opening book but it is not enabled by default.  If you would like to enable the opening book, you can do it with a command line parameter:

```-book book.db```

## Using a Neural Network

By default, chess4j still uses a hand crafted evaluation.  You can enable a neural network based evaluation using a command line parameter:

```-nn nn-24-q.txt```

However, this is only recommended when running in native mode.  Using a neural network for evaluation, even with NNUE, is significantly slower
than a traditional code based evaluator.  In native mode, AVX intrinsics partially compensate for this.  In Java, the overhead seems to be 
too high.  I will continue to investigate this, but it's likely that in a future release I'll remove support for neural networks when not in native mode.

## Memory Usage

chess4j currently employs two transposition tables.  One is used in the main search and one in the pawn evaluation.  The default sizes are 64 mb and 8 mb respectively.
 
You can specify the maximum memory allocated to each table via command line parameters: 

```
-hash 256 -phash 128
``` 

The above arguments would allocate 256 mb to the main table, and 128 mb to the pawn table.  
 
Note: the XBoard protocol has a 'memory' command to specify the maximum memory usage, and chess4j does respect that.  When this command is received, chess4j will reallocate the memory for the two hash tables, dividing the memory equally between them.

## Running Test Suites

You can run EPD formatted test suites with chess4j by putting it in test mode.  The default time per problem is 10 seconds, but that can also be changed with the 'time' argument.

```
java -jar chess4j-6.1-uber.jar -mode test -epd wac.epd -time 30
```

The command above would start chess4j to process the Win At Chess (WAC) test suite, giving it 30 seconds per problem.  (A few test suites can be found in the test/resources folder.)


## Changelog

6.1
* NNUE (recommended for native mode only)
* fixed bug in draw by rep detection when using native engine
* more cleanup and refactoring

6.0
* neural network mode! (alpha - not ready for prime time yet)
* cleaned up command line parameters
* lots of code refactoring and dependency updates

5.1
* Passed pawn by rank (was a single value)
* Non-linear mobility (was a single value)
* Knight outposts
* Trapped bishop penalty


5.0 
* added logistic regresssion with gradient descent
* fully implemented tapered eval (previously was just kings)
* simple mobility terms for bishop and queens

## Roadmap

The next area of focus will likely be Lazy SMP.  I also plan to investigate Java's Foreign Function & Memory (FFM) API as a potential alternative to Java Native Interface (JNI).  From what I've read, FFM is safer, more straightforward, and potentially more performant.  


You can see the combined Prophet / chess4j backlog here: https://trello.com/b/dhcOEaCO/chess4j-board .

Read about the latest development efforts at https://jamesswafford.dev/ .
