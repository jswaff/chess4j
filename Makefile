.PHONY: ALL prophet4-build prophet4-test prophet4-clean
.PHONY: mvn-compile mvn-install mvn-clean clean

ALL: mvn-install


mvn-install: mvn-package
	(cd chess4j-java && mvn install)

mvn-package: prophet4-test
	mvn compile

mvn-clean:
	mvn clean

clean: prophet4-clean

prophet4-clean: mvn-clean
	(cd lib/prophet4 && $(MAKE) clean)

prophet4-test: prophet4-build
	(cd lib/prophet4 && $(MAKE) test)

prophet4-build:
	(cd lib/prophet4 && $(MAKE))
