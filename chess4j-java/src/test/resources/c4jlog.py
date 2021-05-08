#!/usr/bin/python

# Extract metrics from chess4j log file
import re
import sys

print('Executing c4jlog.py')

print ('Argument List:' + str(sys.argv))

logfile = open(sys.argv[1], 'r')
Lines  = logfile.readlines()

count = 0
countSearchTime = 0
sumSearchTime = 0.0
countEbf = 0
sumEbf = 0.0
countFh = 0
sumFh1 = 0.0
sumFh4 = 0.0

for line in Lines:
	count += 1
	# time to depth
	if re.search('# search time', line):
		countSearchTime += 1
		m = re.search('search time: (\d+\.\d+)', line)
		st = m.group(1)
		sumSearchTime += float(st)

	# effective branching factor
	if re.search('# ebf', line):
		countEbf += 1
		m = re.search('# ebf avg: (\d+\.\d+)', line)
		ebf = m.group(1)
		sumEbf += float(ebf)

	# fail high move 1
	if re.search('# fail high', line):
		countFh += 1
		formattedLine = line.replace(',','').replace('%','').replace('(','').replace(')','')
		m = re.search('# fail high mv1: (\d+) (\d+\.\d+) mv2: (\d+) (\d+\.\d+) mv3: (\d+) (\d+\.\d+) mv4: (\d+) (\d+\.\d+)', formattedLine)
		fh1 = m.group(2)
		fh4 = m.group(8)
		sumFh1 += float(fh1)
		sumFh4 += float(fh4)

print('Read ' + repr(count) + ' lines.')
print('Search time - count: ' + repr(countSearchTime) + ', avg: ' + "{:.2f}".format(sumSearchTime / countSearchTime))
print('EBF - count: ' + repr(countEbf) + ', avg: ' + "{:.2f}".format(sumEbf / countEbf))
print('FH - count: ' + repr(countFh) + ', fh1 avg: ' + "{:.2f}".format(sumFh1 / countFh) + ', fh4 avg: ' + 
	"{:.2f}".format(sumFh4 / countFh))
print('Execution complete.  Bye.')
