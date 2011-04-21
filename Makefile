.PHONY: test run

test:
	mvn clojure:test

target/dependency:
	mvn dependency:copy-dependencies

run:	target/dependency
	java -cp 'src/test/clojure:src/main/clojure:target/dependency/*' clojure.main src/scripts/clojure/rundemos.clj
