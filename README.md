# chess4j
a Java based chess engine

chess4j is a chess program written using Java technologies. It is not meant to be super competitive, but just a test bed of sorts for various interests. Those interests include experimenting with different JVM based languages, parallel and distributed computing, and machine learning.

To build chess4j, you'll need a Java SDK and Maven.  Once you've downloaded the code just do 'mvn clean install'.  If you just want binaries, you can download them from http://jamesswafford.com/chess4j.

chess4j is a Winboard compatible chess engine.  See http://www.gnu.org/software/xboard for details on installing Winboard or Xboard.

## Opening Book
chess4j has a small opening book but it is not enabled by default.  If you
would like to enable the opening book, you can do it with a command line parameter:

-book=book.db

