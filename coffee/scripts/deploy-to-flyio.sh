#!/bin/bash
# deploy-to-flyio.sh
# Script para fazer deploy seguro no Fly.io
# Uso: ./deploy-to-flyio.sh [ambiente]

set -e  # Exit on error

AMBIENTE=${1:-prod}
APP_NAME="coffee-pdv"

echo "🚀 Iniciando deploy para Fly.io - Ambiente: $AMBIENTE"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 1. Validar que estamos na branch correta
echo "📌 Verificando branch..."
BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [ "$BRANCH" != "main" ] && [ "$BRANCH" != "master" ]; then
    echo "⚠️  Você está na branch '$BRANCH'. Deploy deve ser feito de 'main' ou 'master'."
    read -p "Continuar mesmo assim? (s/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Ss]$ ]]; then
        exit 1
    fi
fi

# 2. Verificar que não há mudanças não-commitadas
echo "📌 Verificando status do Git..."
if [ -n "$(git status --porcelain)" ]; then
    echo "❌ Há mudanças não-commitadas. Commit ou discard antes de fazer deploy."
    git status
    exit 1
fi

# 3. Build local
echo "📌 Build local..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Build falhou!"
    exit 1
fi

# 4. Verificar fly.toml não contém secrets
echo "📌 Validando fly.toml..."
if grep -i "password\|secret\|token" fly.toml | grep -v "^#"; then
    echo "❌ fly.toml contém possíveis secrets! Remova antes de fazer deploy."
    exit 1
fi

# 5. Carregar variáveis de ambiente do arquivo .env.prod
echo "📌 Carregando secrets de .env.prod..."
if [ ! -f ".env.prod" ]; then
    echo "⚠️  Arquivo .env.prod não encontrado. Usando valores de prompt."
    read -p "JWT_SECRET: " JWT_SECRET
    read -sp "SPRING_DATASOURCE_PASSWORD: " DB_PASSWORD
    echo
else
    source .env.prod
fi

# 6. Definir secrets no Fly.io
echo "📌 Configurando secrets no Fly.io..."
echo "  - JWT_SECRET"
flyctl secrets set \
    SPRING_PROFILES_ACTIVE=prod \
    LOG_LEVEL=WARN \
    JWT_SECRET="${JWT_SECRET}" \
    SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL}" \
    SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME}" \
    SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD}" \
    DB_POOL_SIZE="${DB_POOL_SIZE:-20}" \
    RABBIT_HOST="${RABBIT_HOST}" \
    RABBIT_USER="${RABBIT_USER}" \
    RABBIT_PASSWORD="${RABBIT_PASSWORD}" \
    CORS_ALLOWED_ORIGINS="${CORS_ALLOWED_ORIGINS}"

# 7. Deploy
echo "📌 Fazendo deploy no Fly.io..."
flyctl deploy --app $APP_NAME

# 8. Monitorar logs
echo "📌 Monitorando logs..."
echo "Aguarde a app iniciar (30-60 segundos)..."
sleep 10
flyctl logs --app $APP_NAME | head -50

# 9. Testar health
echo "📌 Testando health endpoint..."
DOMAIN=$(flyctl info --app $APP_NAME | grep "hostname" | awk '{print $NF}')
sleep 5
curl -s https://$DOMAIN/api/actuator/health | jq .

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Deploy concluído com sucesso!"
echo "🔗 URL: https://$DOMAIN"
echo "📊 Dashboard: https://fly.io/apps/$APP_NAME"

