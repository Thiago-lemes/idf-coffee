#!/bin/bash
# generate-secrets.sh
# Script para gerar secrets aleatórios e seguros
# Uso: ./scripts/generate-secrets.sh

set -e

echo "🔐 Gerando Secrets Aleatórios e Seguros"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Verificar dependências
if ! command -v openssl &> /dev/null; then
    echo "❌ OpenSSL não está instalado. Por favor instale:"
    echo "  Ubuntu: sudo apt-get install openssl"
    echo "  macOS: brew install openssl"
    echo "  Windows: choco install openssl"
    exit 1
fi

# Gerar secrets
JWT_SECRET=$(openssl rand -base64 32)
DB_PASSWORD=$(openssl rand -base64 32 | tr -d '\n' | cut -c1-24)  # 24 caracteres
RABBIT_PASSWORD=$(openssl rand -base64 32)
KEYSTORE_PASSWORD=$(openssl rand -base64 32 | tr -d '\n' | cut -c1-16)

echo ""
echo "📋 Secrets Gerados (Copie e cole em .env.prod):"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "JWT_SECRET=$JWT_SECRET"
echo "SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD"
echo "RABBIT_PASSWORD=$RABBIT_PASSWORD"
echo "SSL_KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Opção de salvar em arquivo
read -p "Salvar em .env.prod.generated? (s/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Ss]$ ]]; then
    cat > .env.prod.generated << EOF
# Secrets Gerados em $(date)
# NÃO COMMITAR ESTE ARQUIVO NO GIT

JWT_SECRET=$JWT_SECRET
SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD
RABBIT_PASSWORD=$RABBIT_PASSWORD
SSL_KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD

# Outros valores (preencha conforme necessário)
SPRING_PROFILES_ACTIVE=prod
LOG_LEVEL=WARN
SPRING_DATASOURCE_URL=jdbc:mysql://seu-db-host:3306/coffee
SPRING_DATASOURCE_USERNAME=coffee_app
DB_POOL_SIZE=20
RABBIT_HOST=seu-rabbitmq-host
RABBIT_USER=coffee_app
RABBITMQ_EXCHANGE=coffee.exchange.prod
RABBITMQ_QUEUE=fechamento.caixa.queue.prod
RABBITMQ_ROUTING_KEY=fechamento.caixa
CORS_ALLOWED_ORIGINS=https://seu-dominio.com
EOF

    echo "✅ Secrets salvos em .env.prod.generated"
    echo "⚠️  NÃO COMMITAR este arquivo no Git!"
    echo "   Adicione à .gitignore se ainda não estiver"
fi

echo ""
echo "✅ Secrets gerados com sucesso!"
echo ""
echo "💡 Próximos passos:"
echo "   1. Copie os valores acima"
echo "   2. Configure em seu secrets manager (Fly.io, AWS, etc)"
echo "   3. Nunca commite .env.prod ou .env.prod.generated no Git"

