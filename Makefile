.PHONY: ALL mvn-install native-build prophet-test prophet-build
.PHONY: clean prophet-clean mvn-clean

ALL: mvn-install


mvn-install: prophet-build
	(cd chess4j-java && mvn install)

prophet-test: prophet-build
	(cd lib/prophet/build && ./prophet_test)

prophet-build:
	(cd lib/prophet && mkdir build && cd build && cmake -DCMAKE_BUILD_TYPE=Release .. && make -j8 install)

clean: prophet-clean

prophet-clean: mvn-clean
	(cd lib/prophet && rm -rf build && rm -rf install)

mvn-clean:
	mvn clean

