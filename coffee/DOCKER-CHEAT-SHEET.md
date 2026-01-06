# Docker Cheat Sheet — Coffee App Production

## Quick Start (Local)

```powershell
# 1. Build
.\mvnw.cmd -DskipTests package

# 2. Start
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build

# 3. Check
docker-compose -f docker-compose.prod.yml ps

# 4. Logs
docker-compose -f docker-compose.prod.yml logs -f app

# 5. Test
curl http://localhost:8080/actuator/health

# 6. Stop
docker-compose -f docker-compose.prod.yml down
```

## Common Commands

```powershell
# Status
docker-compose -f docker-compose.prod.yml ps

# Logs (all services)
docker-compose -f docker-compose.prod.yml logs -f

# Logs (only app)
docker-compose -f docker-compose.prod.yml logs -f app

# Logs (only db)
docker-compose -f docker-compose.prod.yml logs -f db

# Restart app
docker-compose -f docker-compose.prod.yml restart app

# Restart db
docker-compose -f docker-compose.prod.yml restart db

# Rebuild image
docker-compose -f docker-compose.prod.yml up -d --build

# Remove everything
docker-compose -f docker-compose.prod.yml down -v

# Shell into app
docker exec -it coffee-app /bin/bash
# or
docker exec -it coffee-app /bin/sh

# Shell into MySQL
docker exec -it coffee-db mysql -u root -p

# View environment (app)
docker exec coffee-app env | grep SPRING
```

## Troubleshooting

```powershell
# Port already in use?
netstat -ano | findstr :3306
netstat -ano | findstr :8080
# Kill process:
taskkill /PID <PID> /F

# Docker image size?
docker image ls

# Prune unused
docker system prune -a

# View image layers
docker history coffee-app:latest

# Build without cache
docker-compose -f docker-compose.prod.yml build --no-cache

# View network
docker network ls
docker network inspect coffee_default

# Backup MySQL
docker exec coffee-db mysqldump -u root -p$MYSQL_ROOT_PASSWORD coffee > backup.sql

# Restore MySQL
docker exec -i coffee-db mysql -u root -p$MYSQL_ROOT_PASSWORD coffee < backup.sql
```

## Environment Variables

| Var | Purpose | Example |
|-----|---------|---------|
| MYSQL_ROOT_PASSWORD | Root password | `prod123` |
| MYSQL_DATABASE | DB name | `coffee` |
| MYSQL_USER | App user | `coffee_user` |
| MYSQL_PASSWORD | App password | `app123` |
| SPRING_DATASOURCE_URL | JDBC URL | `jdbc:mysql://db:3306/coffee` |
| JWT_SECRET | JWT key | `base64_random` |
| JWT_EXPIRATION_MS | Token lifetime | `86400000` |

## File Locations

```
coffee/
├── Dockerfile                  # Build image
├── docker-compose.prod.yml     # Orchestration
├── .env.prod                   # Secrets (local, don't commit)
├── .env.prod.example           # Template (commit)
├── src/main/resources/
│   ├── application.yml         # Default config
│   └── application-prod.yml    # Prod config (uses env vars)
└── README-DOCKER-PROD.md       # Full guide
```

## Compose File Variables

In `docker-compose.prod.yml`, these are read from `.env.prod`:
- `${MYSQL_ROOT_PASSWORD}`
- `${MYSQL_DATABASE}`
- `${MYSQL_USER}`
- `${MYSQL_PASSWORD}`
- `${SPRING_DATASOURCE_URL}`
- `${SPRING_DATASOURCE_USERNAME}`
- `${SPRING_DATASOURCE_PASSWORD}`
- `${JWT_SECRET}`
- `${JWT_EXPIRATION_MS}`

## Health Checks

```bash
# App health
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}

# MySQL from host
mysql -h 127.0.0.1 -u coffee_user -p

# MySQL from container
docker exec coffee-db mysql -u coffee_user -p
```

## Production Checklist

- [ ] `.env.prod` created with strong secrets
- [ ] `JWT_SECRET` is randomly generated (min 32 chars)
- [ ] MySQL passwords are strong
- [ ] Network access restricted (firewall)
- [ ] Backup strategy in place
- [ ] Logs monitored
- [ ] SSL/TLS configured (via Nginx)
- [ ] Database backups scheduled
- [ ] Health checks working

---

Print this page or bookmark it!

