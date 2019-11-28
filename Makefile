JAMES_CASSANDRA_PROJECT_IMAGE := codejamninja/james-cassandra-project
JAMES_PROJECT_IMAGE := codejamninja/james-project
VERSION := 0.0.2

MAJOR := $(shell echo $(VERSION) | cut -d. -f1)
MINOR := $(shell echo $(VERSION) | cut -d. -f2)
PATCH := $(shell echo $(VERSION) | cut -d. -f3)

.EXPORT_ALL_VARIABLES:

.PHONY: all
all: clean build

.PHONY: build
build: build-james-project build-m2 build-keystore build-james-cassandra-project

.PHONY: build-james-project
build-james-project:
	@docker-compose -f docker-james-project.yaml build

.PHONY: build-m2
build-m2: ~/.m2
~/.m2:
	@docker run -v $$HOME/.m2:/root/.m2 -v $$PWD:/origin \
		-v $$PWD/dockerfiles/run/guice/cassandra/destination:/cassandra/destination \
		-t $(JAMES_PROJECT_IMAGE) -s HEAD

.PHONY: build-keystore
build-keystore: dockerfiles/run/guice/cassandra/destination/conf/keystore
dockerfiles/run/guice/cassandra/destination/conf/keystore:
	@keytool -genkey -alias james -keyalg RSA -keystore dockerfiles/run/guice/cassandra/destination/conf/keystore

.PHONY: build-james-cassandra-project
build-james-cassandra-project:
	@docker-compose -f docker-james-cassandra-project.yaml build

.PHONY: pull
pull:
	@docker-compose -f docker-james-project.yaml pull
	@docker-compose -f docker-james-cassandra-project.yaml pull

.PHONY: push
push:
	@docker-compose -f docker-james-project.yaml push
	@docker-compose -f docker-james-cassandra-project.yaml push
