.PHONY: run
run: clean build docker
	@echo "Application is up and running!"


.PHONY: clean
clean:
	@echo "Cleaning previous builds"
	@./gradlew clean

.PHONY: build
build:
	@echo "Running ./gradlew build"
	@./gradlew build

.PHONY: docker
docker:
	@echo "Running ./gradlew clean docker"
	@./gradlew docker
	@echo "Running docker-compose up -d"
	@docker-compose up -d

.PHONY: test
test:
	@echo "Running ./gradlew test"
	@./gradlew test

