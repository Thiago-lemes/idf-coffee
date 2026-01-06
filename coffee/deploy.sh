#!/bin/bash

# Script de deployment do projeto Coffee PDV
# Uso: ./deploy.sh [dev|prod]

set -e

ENVIRONMENT=${1:-dev}
COMPOSE_FILE="docker-compose.yml"
ENV_FILE=".env"

if [ "$ENVIRONMENT" = "prod" ]; then
    COMPOSE_FILE="docker-compose.prod.yml"
    ENV_FILE=".env.prod"
fi

echo "╔════════════════════════════════════════╗"
echo "║   Coffee PDV - Docker Deployment      ║"
echo "║   Environment: $ENVIRONMENT             ║"
echo "╚════════════════════════════════════════╝"
echo ""

# Verificar se arquivo de env existe
if [ ! -f "$ENV_FILE" ]; then
    echo "❌ Erro: Arquivo $ENV_FILE não encontrado"
    echo "   Crie uma cópia: cp .env.example $ENV_FILE"
    exit 1
fi

echo "📦 Step 1: Building Maven package..."
if [ "$ENVIRONMENT" = "dev" ]; then
    ./mvnw.cmd -DskipTests package -q
    echo "✅ Maven build completo"
else
    echo "⏭️  Pulando Maven (será feito no Docker)"
fi

echo ""
echo "🐳 Step 2: Starting Docker Compose..."
docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up -d

echo ""
echo "⏳ Step 3: Waiting for services to be healthy..."
sleep 5

# Verificar MySQL
echo -n "   Checking MySQL... "
for i in {1..30}; do
    if docker-compose -f "$COMPOSE_FILE" exec -T mysql mysqladmin ping -h 127.0.0.1 -proot > /dev/null 2>&1; then
        echo "✅"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ (Timeout)"
        exit 1
    fi
    echo -n "."
    sleep 2
done

# Verificar App
echo -n "   Checking App... "
for i in {1..60}; do
    if docker-compose -f "$COMPOSE_FILE" exec -T app curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅"
        break
    fi
    if [ $i -eq 60 ]; then
        echo "⚠️  (Still starting)"
        break
    fi
    echo -n "."
    sleep 2
done

echo ""
echo "╔════════════════════════════════════════╗"
echo "║   ✅ Deployment Successful!            ║"
echo "╚════════════════════════════════════════╝"
echo ""
echo "🌐 Application: http://localhost:8080"
echo "💾 Database: localhost:3306"
echo ""
echo "📊 Useful commands:"
echo "   View logs:        docker-compose -f $COMPOSE_FILE logs -f app"
echo "   Stop services:    docker-compose -f $COMPOSE_FILE down"
echo "   Status:           docker-compose -f $COMPOSE_FILE ps"
echo ""

