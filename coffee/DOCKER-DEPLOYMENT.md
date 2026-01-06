# Guia de Deployment com Docker

## Pré-requisitos

- Docker 20.10+
- Docker Compose 2.0+
- Java 17+ (para build local)
- Maven 3.8+ (para build local)

## Estrutura de Environments

### Desenvolvimento (docker-compose.yml)
- MySQL 8.0
- Aplicação Spring Boot
- Perfil: `dev`
- Hot reload via volumes

### Produção (docker-compose.prod.yml)
- MySQL 8.0
- Aplicação Spring Boot otimizada
- Perfil: `prod`
- Healthchecks configurados
- Restart policy: always
- Limites de recursos

## Variáveis de Ambiente

Copie `.env.example` para `.env` e configure:

```bash
cp .env.example .env
```

### Variáveis importantes:
- `MYSQL_ROOT_PASSWORD`: Senha do root do MySQL
- `MYSQL_DATABASE`: Nome do banco de dados
- `MYSQL_USER`: Usuário do banco
- `MYSQL_PASSWORD`: Senha do usuário
- `JWT_SECRET`: Chave secreta para JWT (mude em produção!)
- `JWT_EXPIRATION_MS`: Tempo de expiração do token (ms)

## Build da Imagem

### Build local (recomendado antes de deployar)

```bash
# Na raiz do projeto
.\mvnw.cmd -DskipTests package

# Depois, testar localmente
docker-compose up -d
```

### Verificar logs

```bash
docker-compose logs -f app
docker-compose logs -f mysql
```

### Parar a stack

```bash
docker-compose down
```

## Deploy em Produção

1. **Preparar arquivo `.env.prod`:**

```bash
cp .env.example .env.prod
# Editar com valores de produção
```

2. **Build e start:**

```bash
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d
```

3. **Verificar status:**

```bash
docker-compose -f docker-compose.prod.yml ps
docker-compose -f docker-compose.prod.yml logs -f app
```

4. **Aplicação está pronta quando:**

```bash
curl http://localhost:8080/actuator/health
```

Resposta esperada:
```json
{"status":"UP"}
```

## Troubleshooting

### Erro: "Failed to configure a DataSource"
- Verifique se MySQL está rodando: `docker-compose ps`
- Verifique se as variáveis de ambiente estão corretas no `.env`
- Aguarde o healthcheck do MySQL passar

### Erro: "java.sql.SQLException: Cannot connect to MySQL"
- MySQL ainda está inicializando, aguarde 10-20 segundos
- Verifique a porta 3306: `netstat -an | find ":3306"`

### Logs muito grandes
- Docker está limitando automaticamente a 10MB
- Limpar logs antigos: `docker container prune`

## Performance e Segurança

### Dockerfile Otimizado
- Multi-stage build reduz tamanho da imagem final (~300MB)
- Alpine Linux para footprint mínimo
- JVM com G1GC para melhor gerenciamento de memória
- Usuário não-root para segurança

### Docker Compose
- Healthchecks em ambos os serviços
- Restart policy: `always` para auto-recovery
- Limites de recursos (CPU/Memory)
- Logging centralizado com rotação

## Tamanho das Imagens

```bash
docker images | grep coffee
```

Esperado: ~500-700MB para a imagem final

## Backup do Banco

### Backup manual
```bash
docker-compose exec mysql mysqldump -u root -p coffee > backup.sql
```

### Restaurar
```bash
docker-compose exec -T mysql mysql -u root -p coffee < backup.sql
```

## Próximas Melhorias

- [ ] Implementar nginx como reverse proxy
- [ ] Adicionar monitoring (Prometheus + Grafana)
- [ ] Configurar CI/CD (GitHub Actions)
- [ ] Implementar backup automático
- [ ] SSL/TLS com Let's Encrypt

