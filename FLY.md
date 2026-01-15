# Coffee PDV - Fly.io Deployment Configuration
#
# Fly.io oferece:
# ✓ Deploy global automático
# ✓ Free tier generoso
# ✓ Suporte a múltiplas regiões
# ✓ SSL/HTTPS automático
# ✓ Healthchecks integrados
#
# Melhor para: Aplicações que precisam de baixa latência global

# ============================================
# QUICK START
# ============================================

# 1. Instalar Fly CLI
#    Windows: choco install flyctl (or via https://fly.io/docs/getting-started/)
#    Mac:     brew install flyctl
#    Linux:   curl -L https://fly.io/install.sh | sh

# 2. Fazer login
#    flyctl auth login
#    (abrirá navegador para autenticar)

# 3. Criar app
#    cd coffee
#    flyctl app create coffee-app
#    (Railway e Fly.io injetam um ID único: coffee-app-xxxxx)

# 4. Copiar Dockerfile para raiz (já deve estar em coffee/)

# 5. Configurar variáveis de ambiente (SECRETS)
#    flyctl secrets set JWT_SECRET="seu-secret-gerado-com-openssl"
#    flyctl secrets set SPRING_DATASOURCE_PASSWORD="sua-senha-db"

# 6. Deploy
#    flyctl deploy

# 7. Ver logs
#    flyctl logs

# ============================================
# VARIÁVEIS OBRIGATÓRIAS
# ============================================
# Definir com: flyctl secrets set NOME="valor"

# Spring Profile (IMPORTANTE!)
SPRING_PROFILES_ACTIVE=prod

# JWT Secret (gerar com: openssl rand -base64 32)
JWT_SECRET=GERAR_COM_OPENSSL_RAND_BASE64_32

# Banco de Dados - você DEVE usar um serviço gerenciado:
# Opção 1: PlanetScale (MySQL compatível com Fly)
# Opção 2: Neon (PostgreSQL)
# Opção 3: AWS RDS, Google Cloud SQL, Azure Database, etc.

SPRING_DATASOURCE_URL=jdbc:mysql://seu-banco.com:3306/coffee?useSSL=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=seu-usuario
SPRING_DATASOURCE_PASSWORD=SUA_SENHA_FORTE

# ============================================
# REGIÕES DISPONÍVEIS
# ============================================
# Use -r para especificar região
# Regiões disponíveis: fly regions list
#
# Mais próximas do Brasil:
# - gru (São Paulo) - RECOMENDADO
# - bos (Boston)
# - sjc (São José, CA)
#
# Exemplo:
# flyctl deploy --region gru

# ============================================
# FLY.TOML (CONFIGURAÇÃO PRINCIPAL)
# ============================================
# Arquivo na raiz: fly.toml
# Exemplo para Coffee:

# app = 'coffee-app'
# primary_region = 'gru'  # São Paulo
#
# [build]
#   dockerfile = './Dockerfile'
#
# [[services]]
#   internal_port = 8080
#   protocol = "tcp"
#
#   [[services.ports]]
#     port = 80
#     handlers = ["http"]
#
#   [[services.ports]]
#     port = 443
#     handlers = ["tls", "http"]
#
# [[vm]]
#   memory = '1gb'
#   cpus = 1

# ============================================
# BANCO DE DADOS - OPÇÕES
# ============================================

# OPÇÃO 1: PlanetScale (MySQL compatível, FREE)
# https://planetscale.com
# 1. Criar conta
# 2. Criar database "coffee"
# 3. Obter connection string
# 4. Configurar: flyctl secrets set SPRING_DATASOURCE_URL="..."

# OPÇÃO 2: Neon (PostgreSQL, FREE)
# https://neon.tech
# 1. Criar conta
# 2. Criar projeto
# 3. Copiar connection string PostgreSQL
# 4. VOCÊ TERÁ QUE MUDAR O DIALECT DO HIBERNATE!
#    (nosso app está configurado para MySQL, não PostgreSQL)

# OPÇÃO 3: AWS RDS (pago, mas confiável)
# 1. Criar RDS MySQL
# 2. Configurar security group
# 3. Obter endpoint
# 4. Configurar secrets

# OPÇÃO 4: Google Cloud SQL (pago)
# 1. Cloud Console > SQL > Create Instance
# 2. Configurar IP autorizado
# 3. Obter connection string

# ============================================
# CONFIGURAÇÃO PASSO A PASSO
# ============================================

# 1. Instalar Fly CLI
#    https://fly.io/docs/getting-started/installing-fly/

# 2. Login
flyctl auth login

# 3. Na pasta coffee/, criar/atualizar fly.toml
#    (pode copiar do projeto e customizar)

# 4. Gerar JWT Secret
#    bash: openssl rand -base64 32
#    PowerShell: [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((1..32 | ForEach-Object {[char](Get-Random -Minimum 33 -Maximum 127)}) -join ''))

# 5. Criar app no Fly.io
flyctl app create coffee-app

# 6. Configurar secrets
flyctl secrets set JWT_SECRET="seu-secret-aqui"
flyctl secrets set SPRING_DATASOURCE_URL="jdbc:mysql://..."
flyctl secrets set SPRING_DATASOURCE_PASSWORD="sua-senha"

# 7. Deploy
flyctl deploy

# 8. Ver status
flyctl status

# 9. Ver logs em tempo real
flyctl logs --follow

# 10. Acessar aplicação
#     https://coffee-app.fly.dev (URL padrão Fly)

# ============================================
# COMANDOS ÚTEIS
# ============================================

# Ver status do app
flyctl status

# Ver logs em tempo real
flyctl logs --follow

# Ver logs dos últimos 100 linhas
flyctl logs -n 100

# Escalar para 3 instâncias
flyctl scale count 3

# Configurar memória
flyctl scale memory 2048  # 2GB

# Ver secr ets (não mostra valores!)
flyctl secrets list

# Atualizar secret
flyctl secrets set JWT_SECRET="novo-valor"

# Remover secret
flyctl secrets unset JWT_SECRET

# Desempenho: conectar SSH ao container
flyctl ssh console

# Restartar app
flyctl restart

# Destruir app (CUIDADO!)
flyctl app destroy coffee-app

# ============================================
# MONITORAMENTO NO FLY.IO
# ============================================

# Healthchecks automáticos
# - Fly.io monitora /actuator/health
# - Se falhar 3x, máquina é reiniciada automaticamente

# Métricas disponíveis
# - Dashboard mostra CPU, memória, conectados
# - Ver: flyctl status

# Alerts (pago - plano Fly Org)
# - Configurar threshold de CPU/memória
# - Notificações via email/Slack

# ============================================
# TROUBLESHOOTING
# ============================================

# Problema: "Build failed - Dockerfile not found"
# Solução: Verificar se Dockerfile está na raiz do projeto

# Problema: "Can't connect to database"
# Solução:
# 1. Verificar se SPRING_DATASOURCE_URL está correto
# 2. Verificar se banco acessa Fly.io (IP whitelist)
# 3. flyctl logs | grep "Connection refused"

# Problema: "App keeps crashing"
# Solução:
# 1. Ver logs: flyctl logs
# 2. Aumentar memória: flyctl scale memory 2048
# 3. Verificar secrets estão configurados

# Problema: "JWT_SECRET is undefined"
# Solução: flyctl secrets set JWT_SECRET="..."

# Problema: "Slow performance"
# Solução:
# 1. Aumentar CPU/memória: flyctl scale
# 2. Adicionar múltiplas instâncias: flyctl scale count 3
# 3. Usar Fly Postgres para cache (redis alternative)

# Problema: "SSL certificate issues"
# Solução: Fly.io auto-gera certificados. Se problema:
#         flyctl certs remove seu-dominio.com
#         flyctl certs create seu-dominio.com

# ============================================
# BOAS PRÁTICAS FLY.IO
# ============================================

# 1. Use a região mais próxima do seu usuário (gru para Brasil)
# 2. Implemente healthchecks (/actuator/health - já está!)
# 3. Use secrets, não variáveis, para dados sensíveis
# 4. Configure auto-scaling via flyctl scale
# 5. Monitore logs e métricas regularmente
# 6. Teste deploy em staging antes de produção
# 7. Use Fly Postgres para sessões/cache se necessário

# ============================================
# ALTERNATIVAS AO BANCO EXTERNO
# ============================================

# Fly oferece Fly Postgres integrado:
# flyctl postgres create coffee-db --region gru
#
# MAS: Nosso app usa MySQL, não PostgreSQL
# Seria necessário mudar toda configuração do Hibernate
# Mais fácil: usar PlanetScale (MySQL) ou AWS RDS

# ============================================
# CUSTOS ESTIMADOS
# ============================================

# FREE TIER:
# - 3 máquinas de 256MB + 30GB SSD
# - Suficiente para dev/testing

# PAGO (estimado/mês):
# - 1 app com 1GB RAM: ~$5-10/mês
# - + banco externo: $10-20/mês (PlanetScale, AWS RDS)
# - + múltiplas regiões: +$5 por app/região

# ============================================
# LINKS ÚTEIS
# ============================================

# Documentação: https://fly.io/docs/
# Spring Boot Guide: https://fly.io/docs/languages-and-frameworks/golang/ (adaptar para Java)
# Dashboard: https://fly.io/dashboard
# Comunidade Discord: https://fly.io/docs/getting-help/
# Preços: https://fly.io/docs/about/pricing/

# ============================================
# RESUMO RÁPIDO
# ============================================

# 1. flyctl auth login
# 2. cd coffee
# 3. flyctl app create coffee-app
# 4. Configurar fly.toml (copiar do repo)
# 5. Gerar JWT_SECRET com openssl
# 6. flyctl secrets set JWT_SECRET="..."
# 7. flyctl secrets set SPRING_DATASOURCE_URL="..."
# 8. flyctl deploy
# 9. flyctl status & flyctl logs
# 10. Pronto! App rodando em https://coffee-app.fly.dev

