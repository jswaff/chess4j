# chess4j
a Java based chess engine

chess4j is a chess program written using Java technologies. It is not meant to be super competitive, but just a test bed of sorts for various interests. Those interests include experimenting with different JVM based languages, parallel and distributed computing, and machine learning.

To build chess4j, you'll need a Java SDK and Maven.  Once you've downloaded the code just do 'mvn clean install'.  If you just want binaries, you can download them from http://jamesswafford.com/chess4j.

chess4j is a Winboard compatible chess engine.  See http://www.gnu.org/software/xboard for details on installing Winboard or Xboard.

## Opening Book

chess4j has a small opening book but it is not enabled by default.  If you
would like to enable the opening book, you can do it with a command line parameter:

```-book=book.db```


## Memory Usage

Normally you wouldn't need to worry about memory usage, but if you want to tweak
chess4j here is some important information.

chess4j currently employs three transposition tables.  Two are used in the main 
search (with different replacement strategies), and one in the pawn evaluation. 
 
You can specify the maximum memory allocated to each table via a command line
parameter, but you would really only want to do this if you were running the program 
directly from the command line, and not using a Winboard compatible GUI or test harness. 
(I do this when running test suites but that's about it.)  

```
-hash=256 -phash=256
``` 

The above command would allocate 256 MB to each of the two tables used in the main search,
and 256 MB to the pawn hash table, for a total of 256 MB * 3 = 768 MB.  
 
Winboard / XBoard has an option to specify the maximum memory usage, and chess4j does
respect that.  The allocation strategy is to give each of the three tables equal share.


## Running Test Suites

You can run EPD formatted test suites with chess4j using the 'suite' command line argument.  The
default time per problem is 5 seconds, but that can also be changed with the 'time'
argument.

```
java -jar chess4j-3.4-uber.jar -suite=wac.epd  -time=10
```

The command above would start chess4j to process the Win At Chess (WAC) test suite,
giving it 10 seconds per problem.  (A few test suites can be found in the test/resources folder.)
