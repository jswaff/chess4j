# chess4j

an Xboard compatible Java based chess engine

## Introduction 

chess4j is a chess program written using Java technologies. It is a test bed of sorts for various interests. Those interests include experimenting with different JVM based languages, parallel and distributed computing, and machine learning.

## Installing

To play chess4j, you'll need a Java 24 or later JRE and Winboard or Xboard.  To see if you have a JRE installed, open a command prompt and type 'java -version'.  If you need to download a JRE you can download one from the Oracle website:

https://www.oracle.com/java/technologies/downloads/

See http://www.gnu.org/software/xboard for details on installing Winboard or Xboard.

Once those prerequisites are met you can download the latest release and extract the zipfile.  Once you extract the zipfile, open the 'chess4j-wb.bat' file (or 'chess4j-xb.sh' on Linux) and modify the path to match the location you just unzipped to.

## Building from Source

You will need a Java 24 (or better) JDK and Maven.

Clone the repository and go into the chess4j/chess4j-java directory.
 
 ```mvn clean install```  

Once this process is complete you should see the build artifact in the target directory.  Verify everything is working:

```java -jar chess4j-6.2-uber.jar -mode test -epd ../src/test/resources/suites/wac2.epd```

You should see the program search for about 10 seconds and display the result.  


## Opening Book

chess4j has a small opening book but it is not enabled by default.  If you would like to enable the opening book, you can do it with a command line parameter:

```-book book.db```

## Memory Usage

chess4j currently employs two transposition tables.  One is used in the main search and one in the pawn evaluation.  The default sizes are 64 mb and 8 mb respectively.
 
You can specify the maximum memory allocated to each table via command line parameters: 

```
-hash 256 -phash 128
``` 

The above arguments would allocate 256 mb to the main table, and 128 mb to the pawn table.  
 
Note: the Xboard protocol has a 'memory' command to specify the maximum memory usage, and chess4j does respect that.  When this command is received, chess4j will reallocate the memory for the two hash tables, dividing the memory equally between them.

## Running Test Suites

You can run EPD formatted test suites with chess4j by putting it in test mode.  The default time per problem is 10 seconds, but that can also be changed with the 'time' argument.

```
java -jar chess4j-6.2-uber.jar -mode test -epd wac.epd -time 30
```

The command above would start chess4j to process the Win At Chess (WAC) test suite, giving it 30 seconds per problem.  (A few test suites can be found in the test/resources folder.)


## Changelog

6.2
* replaced JNI integration with newer Foreign Function and Memory API (FMM)
* Removed native code submodule

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

The next area of focus will be evaluate Java's Vector API.  Lazy SMP is also on the horizon. 

You can see the combined Prophet / chess4j backlog here: https://trello.com/b/dhcOEaCO/chess4j-board .

Read about the latest development efforts at https://jamesswafford.dev/ .
