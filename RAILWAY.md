# Coffee PDV - Railway.app Deployment Configuration
#
# Este arquivo documenta como fazer deploy no Railway.app
# Railway é a forma mais fácil de fazer deploy desta aplicação!
#
# Passos:
# 1. Criar conta em https://railway.app
# 2. Conectar repositório GitHub
# 3. Railway auto-detectará Maven + Java
# 4. Adicionar plugins: MySQL, RabbitMQ (opcional)
# 5. Configurar variáveis de ambiente (veja abaixo)
# 6. Deploy automático!

# ============================================
# VARIÁVEIS DE AMBIENTE OBRIGATÓRIAS
# ============================================
# Adicione estas no painel do Railway (Variables tab)

# Spring Profile (IMPORTANTE!)
SPRING_PROFILES_ACTIVE=prod

# Banco de Dados (MySQL será auto-configurado se usar o plugin)
# Railway injeta RAILWAY_MYSQL_URL automaticamente
# Mas você pode definir customizado:
# SPRING_DATASOURCE_URL=jdbc:mysql://host:port/coffee?useSSL=true&serverTimezone=UTC
# SPRING_DATASOURCE_USERNAME=root
# SPRING_DATASOURCE_PASSWORD=sua-senha-forte

# Se não usar o plugin MySQL do Railway, você DEVE definir:
# spring.datasource.url - URL completa do banco
# spring.datasource.username - usuário do banco
# spring.datasource.password - senha do banco

# ============================================
# SEGURANÇA - GERADO COM OPENSSL
# ============================================
# No terminal:
# openssl rand -base64 32
#
# Resultado exemplo: 9rXjK2mL5nQ8vP1sT3uW4xYz6aB7cD0eF2gH3iJ4kL5mN6oP7qR8sT9uV0wX1yZ2

JWT_SECRET=GERAR_COM_OPENSSL_RAND_BASE64_32

# ============================================
# CONFIGURAÇÕES OPCIONAIS
# ============================================

# Logging
LOG_LEVEL=WARN

# Server port (Railway injeta automaticamente)
# SERVER_PORT será auto-injetado, não precisa definir

# JWT Expiration (24 horas padrão, em ms)
# JWT_EXPIRATION_MS=86400000

# Database pool tuning
# DB_POOL_SIZE=20
# DB_POOL_MIN_IDLE=10

# ============================================
# RABBITMQ (OPCIONAL)
# ============================================
# Se quiser usar RabbitMQ gerenciado:
# 1. Adicionar plugin CloudAMQP no Railway (marketplace)
# 2. CloudAMQP injetará automaticamente:
#    - CLOUDAMQP_URL (formato: amqp://user:pass@host:port/vhost)
#
# Depois, você precisa extrair desta URL:
# RABBIT_HOST=<extrair de CLOUDAMQP_URL>
# RABBIT_PORT=5672
# RABBIT_USER=<extrair de CLOUDAMQP_URL>
# RABBIT_PASSWORD=<extrair de CLOUDAMQP_URL>

# Ou definir manualmente se usar serviço externo:
# RABBIT_HOST=sua-instancia.cloudamqp.com
# RABBIT_USER=usuario
# RABBIT_PASSWORD=senha

# ============================================
# PASSOS PARA DEPLOY NO RAILWAY
# ============================================

# 1. Criar conta e projeto no Railway
#    https://railway.app

# 2. Conectar repositório GitHub
#    - Dashboard > Create > GitHub Repo
#    - Selecionar repositório do Coffee

# 3. Railway automaticamente detectará:
#    ✓ Java 21 (pom.xml)
#    ✓ Maven
#    ✓ Spring Boot

# 4. Adicionar MySQL (RECOMENDADO via plugin)
#    - Marketplace > MySQL
#    - Railway injeta RAILWAY_MYSQL_URL automaticamente
#    - application-prod.yml lê dessa URL

# 5. Configurar Variáveis de Ambiente
#    - Variables tab > Add Variable
#    - Adicione JWT_SECRET (gerado com openssl)
#    - Adicione qualquer outra var conforme necessário

# 6. Deploy automático
#    - A cada push para main/master, Railway faz deploy
#    - Ou fazer deploy manual em Deployments tab

# 7. Ver logs
#    - Logs tab
#    - Ou: railway logs

# ============================================
# COMANDOS RAILWAY CLI (OPCIONAL)
# ============================================

# Instalar CLI
# https://docs.railway.app/guides/cli

# Login
# railway login

# Linkar projeto local ao Railway
# railway link

# Ver variáveis
# railway variables

# Definir variável
# railway variables set JWT_SECRET="..."
# railway variables set SPRING_DATASOURCE_PASSWORD="..."

# Fazer deploy
# railway up

# Ver logs em tempo real
# railway logs --follow

# ============================================
# TROUBLESHOOTING NO RAILWAY
# ============================================

# Problema: "Spring profile dev-mysql not found"
# Solução: Verificar se SPRING_PROFILES_ACTIVE=prod está definido

# Problema: "Cannot connect to database"
# Solução: Verificar se MySQL plugin foi adicionado e variáveis estão corretas

# Problema: "JWT Secret not configured"
# Solução: Definir JWT_SECRET nas variáveis

# Problema: "Build fails - Maven not found"
# Solução: Railway auto-detecta Maven. Se não funcionar:
#          - Verificar se pom.xml está na raiz
#          - Fazer commit e push novamente

# ============================================
# RECURSOS ÚTEIS
# ============================================

# Documentação Railway: https://docs.railway.app
# Guia Spring Boot: https://docs.railway.app/guides/springboot
# FAQ: https://docs.railway.app/support/faq
# Comunidade Discord: https://discord.gg/railway

