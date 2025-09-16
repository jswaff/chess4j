# chess4j

an Xboard compatible Java based chess engine

## Installing

To play chess4j, you'll need a Java 24 or later JRE and Winboard or Xboard.  To see if you have a JRE installed, open a command prompt and type 'java -version'.  If you need to download a JRE you can download one from the Oracle website:

https://www.oracle.com/java/technologies/downloads/

See http://www.gnu.org/software/xboard for details on installing Winboard or Xboard.

Once those prerequisites are met you can download the latest release and extract the zipfile.  Once you extract the zipfile, open the 'chess4j.bat' file (or 'chess4j.sh' on Linux) and modify the path to match the location you just unzipped to.

## Building from Source

You will need a Java 24 (or better) JDK and Maven.

Clone the repository and go into the chess4j directory.
 
 ```mvn clean install```  

Once this process is complete you should see the build artifact in the target directory.  Verify everything is working:

```java -jar chess4j-6.2-uber.jar -mode test -epd ../src/test/resources/suites/wac2.epd```

You should see the program search for about 10 seconds and display the result.  


## Opening Book

chess4j has a small opening book but it is not enabled by default.  If you would like to enable the opening book, you can do it with a command line parameter:

```-book book.db```

## Using a Neural Network

By default, chess4j still uses a handcrafted evaluation. You can enable a neural network based evaluation using a command line parameter:

```-nn nn-32-q.txt```

## Changelog

6.2
* replaced JNI integration with newer Foreign Function and Memory API (FMM)
* Removed native code submodule
* Native mode is no longer publicly supported

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
