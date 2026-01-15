.PHONY: help docker-build docker-up docker-down docker-logs docker-clean docker-test docker-push

help:
	@echo "Coffee PDV - Docker Commands"
	@echo "============================"
	@echo ""
	@echo "Development (Docker Compose):"
	@echo "  make docker-up          - Start all services (MySQL, RabbitMQ, App)"
	@echo "  make docker-down        - Stop all services"
	@echo "  make docker-build       - Build Docker image"
	@echo "  make docker-rebuild     - Rebuild Docker image (no cache)"
	@echo "  make docker-logs        - View logs from all services"
	@echo "  make docker-logs-app    - View logs from app only"
	@echo "  make docker-clean       - Remove containers and volumes"
	@echo ""
	@echo "Production:"
	@echo "  make docker-build-prod  - Build production image"
	@echo "  make docker-push        - Push image to registry"
	@echo ""
	@echo "Development Build:"
	@echo "  make build              - Build application with Maven"
	@echo "  make test               - Run tests"
	@echo ""

# Build the Docker image
docker-build:
	docker-compose build coffee-app

# Rebuild without cache
docker-rebuild:
	docker-compose build --no-cache coffee-app

# Start all services
docker-up:
	docker-compose --env-file .env.docker up -d

# Stop all services
docker-down:
	docker-compose down

# View logs
docker-logs:
	docker-compose logs -f

# View app logs only
docker-logs-app:
	docker-compose logs -f coffee-app

# Remove everything (containers, volumes, images)
docker-clean:
	docker-compose down -v
	docker-compose rm -f

# Build production image
docker-build-prod:
	docker build -t coffee:latest -t coffee:prod .

# Push to registry (requires REGISTRY_URL env var)
docker-push: docker-build-prod
	docker tag coffee:latest ${REGISTRY_URL}/coffee:latest
	docker push ${REGISTRY_URL}/coffee:latest

# Maven build
build:
	cd coffee && mvn clean package -DskipTests

# Run tests
test:
	cd coffee && mvn test

# Development shortcuts
dev-up: docker-up
dev-down: docker-down
dev-logs: docker-logs-app

.DEFAULT_GOAL := help

