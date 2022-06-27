.PHONY: test up down

test: down up
	if [ "$(SBT_VERSION)" == "" ]; then   \
		echo "SBT_VERSION is not set"; \
		exit 1                       ; \
	fi
	docker-compose exec -T scala ./sbt ^^$(SBT_VERSION) test:compile clean
	docker-compose exec -T scala ./sbt ^^$(SBT_VERSION) scripted

up:
	docker-compose build
	docker-compose up -d

down:
	docker-compose down

sbt:
	curl -Ls https://git.io/sbt > sbt
	chmod +x ./sbt
