#!/bin/bash
# Coffee PDV - Docker Helper Scripts
# Usage: ./docker-helper.sh <command>

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

echo_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        echo_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    echo_info "Docker is installed"
}

# Check if Docker Compose is installed
check_docker_compose() {
    if ! docker compose version &> /dev/null; then
        echo_error "Docker Compose is not installed. Please install Docker Compose."
        exit 1
    fi
    echo_info "Docker Compose is installed"
}

# Start services
start_services() {
    echo_info "Starting Coffee PDV services..."
    check_docker
    check_docker_compose

    if [ ! -f ".env.docker" ]; then
        echo_warn ".env.docker not found. Creating from template..."
        cp .env.example .env.docker 2>/dev/null || echo_warn "Could not create .env.docker from template"
    fi

    docker compose --env-file .env.docker up -d
    echo_info "Services started successfully!"
    echo_info "App URL: http://localhost:8080"
    echo_info "MySQL: localhost:3306"
    echo_info "RabbitMQ Management: http://localhost:15672 (guest/guest)"
}

# Stop services
stop_services() {
    echo_info "Stopping Coffee PDV services..."
    docker compose down
    echo_info "Services stopped"
}

# View logs
view_logs() {
    docker compose logs -f "$1"
}

# Clean everything
clean_all() {
    echo_warn "This will remove all containers and volumes. Continue? (y/n)"
    read -r response
    if [ "$response" = "y" ]; then
        echo_info "Cleaning up..."
        docker compose down -v
        docker compose rm -f
        echo_info "Cleanup completed"
    else
        echo_info "Cleanup cancelled"
    fi
}

# Build image
build_image() {
    echo_info "Building Docker image..."
    check_docker
    docker compose build coffee-app
    echo_info "Build completed"
}

# Run database migrations
run_migrations() {
    echo_info "Running database migrations..."
    docker compose exec coffee-app mvn flyway:migrate
    echo_info "Migrations completed"
}

# Check service health
health_check() {
    echo_info "Checking service health..."

    echo "MySQL Health:"
    docker compose exec mysql mysqladmin ping -h localhost || echo_error "MySQL is not responding"

    echo "RabbitMQ Health:"
    docker compose exec rabbitmq rabbitmq-diagnostics ping || echo_error "RabbitMQ is not responding"

    echo "App Health:"
    curl -f http://localhost:8080/actuator/health || echo_error "App is not responding"
}

# Display usage
usage() {
    echo "Coffee PDV - Docker Helper"
    echo ""
    echo "Usage: $0 <command>"
    echo ""
    echo "Commands:"
    echo "  start       - Start all services"
    echo "  stop        - Stop all services"
    echo "  restart     - Restart all services"
    echo "  logs        - View all logs"
    echo "  logs-app    - View app logs only"
    echo "  build       - Build Docker image"
    echo "  clean       - Remove all containers and volumes"
    echo "  migrate     - Run database migrations"
    echo "  health      - Check service health"
    echo "  help        - Show this help message"
    echo ""
}

# Main command handling
case "${1:-help}" in
    start)
        start_services
        ;;
    stop)
        stop_services
        ;;
    restart)
        stop_services
        sleep 2
        start_services
        ;;
    logs)
        view_logs ""
        ;;
    logs-app)
        view_logs "coffee-app"
        ;;
    logs-mysql)
        view_logs "mysql"
        ;;
    logs-rabbit)
        view_logs "rabbitmq"
        ;;
    build)
        build_image
        ;;
    clean)
        clean_all
        ;;
    migrate)
        run_migrations
        ;;
    health)
        health_check
        ;;
    help)
        usage
        ;;
    *)
        echo_error "Unknown command: $1"
        usage
        exit 1
        ;;
esac

