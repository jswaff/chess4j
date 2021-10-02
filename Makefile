.PHONY: ALL prophet-build prophet-test prophet-clean
.PHONY: mvn-compile mvn-install mvn-clean clean

ALL: mvn-install


mvn-install: mvn-package
	(cd chess4j-java && mvn install)

mvn-package: prophet-test
	mvn compile

mvn-clean:
	mvn clean

clean: prophet-clean

prophet-clean: mvn-clean
	(cd lib/prophet && $(MAKE) clean)

prophet-test: prophet-build
	(cd lib/prophet && $(MAKE) test && ./prophet4_test)

prophet-build:
	(cd lib/prophet && $(MAKE))
