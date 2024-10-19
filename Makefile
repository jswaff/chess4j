.PHONY: ALL prophet-build prophet-test prophet-clean
.PHONY: native-package mvn-install mvn-clean clean

ALL: mvn-install


mvn-install: native-package
	(cd chess4j-java && mvn install)

native-package: prophet-test
	(cd chess4j-java && mvn generate-sources && cd ../chess4j-native && mvn package)

mvn-clean:
	mvn clean

clean: prophet-clean

prophet-clean: mvn-clean
	(cd lib/prophet && $(MAKE) clean)

prophet-test: prophet-build
	(cd lib/prophet && $(MAKE) test && ./prophet4_test)

prophet-build:
	(cd lib/prophet && $(MAKE))
