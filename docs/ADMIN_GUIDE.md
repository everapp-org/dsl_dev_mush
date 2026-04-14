# MCMS Administrator Guide
### Mushroom Cultivation Management System — Administration Reference

---

## Table of Contents

1. [Administrator Responsibilities](#administrator-responsibilities)
2. [User Management](#user-management)
3. [Production Configuration](#production-configuration)
4. [Database](#database)
5. [Security Hardening](#security-hardening)
6. [Monitoring and Health](#monitoring-and-health)
7. [Backup and Recovery](#backup-and-recovery)
8. [Running in Production](#running-in-production)
9. [Upgrading](#upgrading)
10. [Log Management](#log-management)

---

## Administrator Responsibilities

As MCMS administrator you are responsible for:
- Creating and managing user accounts
- Configuring the application for production use
- Keeping the database backed up
- Monitoring system health
- Managing the JWT secret and security configuration
- Applying updates

---

## User Management

User management is in the **Administration → User Management** menu. Only ADMIN-role users can access this.

### Creating a User

1. Go to **Administration → User Management → New User**
2. Fill in:
   - **Login** — username (lowercase, no spaces)
   - **First name, Last name**
   - **Email** — used for notifications and password reset
   - **Language** — display language preference
   - **Profiles (Roles)** — assign one or more: ROLE\_ADMIN, ROLE\_MANAGER, ROLE\_OPERATOR, ROLE\_USER
3. Save

The user receives a temporary password or activation link (depending on your mail configuration). If mail is not configured, set the password manually via the API or directly inform the user.

### Editing a User

1. Go to **Administration → User Management**
2. Find the user and click **Edit**
3. Change roles, email, or activation status
4. Save

### Deactivating a User

On the user list, toggle the **Activated** flag off. Deactivated users cannot log in but their data is preserved. Do not delete users who have created records — this will orphan their data.

### Role Assignments — Guidelines

| Role | Who to assign it to |
|---|---|
| ROLE\_ADMIN | Farm owner, IT administrator — 1–2 people maximum |
| ROLE\_MANAGER | Farm manager, production supervisor |
| ROLE\_OPERATOR | Production floor staff, pickers, inventory staff |
| ROLE\_USER | Investors, auditors, read-only stakeholders |

Assign the minimum role required. If in doubt, start with ROLE\_USER and upgrade as needed.

---

## Production Configuration

For production deployment, edit `mcms-jhipster-base/src/main/resources/config/application-prod.yml`.

### Database

Replace the H2 development database with PostgreSQL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-db-host:5432/mcms
    username: mcms
    password: YOUR_STRONG_DB_PASSWORD
```

Create the PostgreSQL database before first startup:
```sql
CREATE DATABASE mcms;
CREATE USER mcms WITH PASSWORD 'YOUR_STRONG_DB_PASSWORD';
GRANT ALL PRIVILEGES ON DATABASE mcms TO mcms;
```

Liquibase creates all tables automatically on first startup.

### JWT Secret

The JWT secret signs authentication tokens. Use a strong random secret in production:

```bash
# Generate a secure base64 secret:
openssl rand -base64 64
```

Set it as an environment variable (recommended — do not put secrets in config files):
```bash
export JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET="your-generated-secret-here"
```

Or add it to application-prod.yml:
```yaml
jhipster:
  security:
    authentication:
      jwt:
        base64-secret: your-generated-secret-here
        token-validity-in-seconds: 86400
        token-validity-in-seconds-for-remember-me: 2592000
```

> Changing the JWT secret invalidates all existing sessions. All users will need to log in again.

### Server Port

Default is 8080. To change:
```yaml
server:
  port: 8080
```

### HTTPS / TLS

To enable HTTPS, add to application-prod.yml:
```yaml
server:
  port: 443
  ssl:
    key-store: classpath:config/tls/keystore.p12
    key-store-password: YOUR_KEYSTORE_PASSWORD
    key-store-type: PKCS12
    key-alias: selfsigned
```

For production, obtain a certificate from Let's Encrypt or your CA. A reverse proxy (nginx, Apache) in front of the application is often simpler than configuring TLS directly.

### CORS

If the frontend and backend run on different domains, configure CORS:
```yaml
jhipster:
  cors:
    allowed-origins: "https://your-frontend-domain.com"
    allowed-methods: "GET,PUT,POST,DELETE,PATCH,OPTIONS"
    allowed-headers: "*"
    allow-credentials: true
    max-age: 1800
```

### Mail (optional)

For password reset and activation emails:
```yaml
spring:
  mail:
    host: smtp.your-mail-provider.com
    port: 587
    username: your-smtp-username
    password: your-smtp-password
    protocol: smtp
    tls: true
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true

jhipster:
  mail:
    base-url: https://your-app-domain.com
    from: noreply@your-domain.com
```

---

## Database

### Development Database (H2)

Located at `mcms-jhipster-base/target/h2db/db/mcms.*`

Access the console at: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./target/h2db/db/mcms`
- Username: `mcms`
- Password: *(empty)*

> The H2 database is for development only. Do not use it in production. Data in H2 is lost when you delete the `target/` directory.

### Schema Migrations

MCMS uses Liquibase for schema management. Migrations run automatically on startup. The changelog is at:
```
mcms-jhipster-base/src/main/resources/config/liquibase/
```

You do not need to run migrations manually. Do not modify the changelog files unless you are adding custom schema changes.

### Sample Data

The `faker` Liquibase context loads sample data in development. This runs automatically with the `dev` profile. Production uses the `prod` profile which does not load sample data.

To reset development data:
```bash
rm -rf mcms-jhipster-base/target/h2db
# Restart the backend — Liquibase recreates the schema and loads fresh sample data
```

### PostgreSQL Maintenance

```bash
# Connect to the database
psql -U mcms -d mcms

# Check database size
SELECT pg_size_pretty(pg_database_size('mcms'));

# List tables with row counts
SELECT schemaname, relname, n_live_tup
FROM pg_stat_user_tables
ORDER BY n_live_tup DESC;

# Vacuum and analyze (run periodically)
VACUUM ANALYZE;
```

> The `sensor_reading` table grows the fastest. If you log sensor readings frequently, monitor its size and consider partitioning by month after the first year.

---

## Security Hardening

Before going live, complete this checklist:

- [ ] Change the default admin password (admin/admin)
- [ ] Generate and set a new JWT secret
- [ ] Set a strong PostgreSQL password
- [ ] Remove or restrict the H2 console endpoint (`spring.h2.console.enabled: false` in prod)
- [ ] Configure HTTPS/TLS or put the app behind a reverse proxy with TLS
- [ ] Set CORS to your specific frontend origin (not wildcard)
- [ ] Set JWT token validity to a sensible duration (86400 seconds = 24 hours is the default)
- [ ] Ensure the server firewall only exposes port 80/443 (not 8080 directly)
- [ ] Disable the Swagger UI in production if external access is not needed

To disable Swagger in production:
```yaml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

---

## Monitoring and Health

### Health Endpoint

```
GET http://localhost:8080/management/health
```
Returns `{"status":"UP"}` when healthy. Suitable for load balancer health checks.

### Metrics

```
GET http://localhost:8080/management/metrics
```
Returns JVM metrics, HTTP request counts, database connection pool stats, and more.

### Admin UI Metrics

Go to **Administration → Metrics** in the application for a visual dashboard showing:
- JVM memory usage
- Thread pool status
- HTTP request statistics
- Database connection pool

### Admin UI Health

Go to **Administration → Health** for component health status:
- Database connectivity
- Disk space
- Mail service (if configured)

### Logs

Go to **Administration → Logs** to change log levels at runtime without restarting. Useful for debugging specific issues.

Default log levels in production:
- ROOT: INFO
- com.mcms: INFO
- org.springframework: WARN

---

## Backup and Recovery

### Development (H2)

Back up the entire H2 database directory:
```bash
cp -r mcms-jhipster-base/target/h2db /your/backup/location/
```

### Production (PostgreSQL)

**Automated daily backup:**
```bash
# Create a backup script: /opt/mcms/backup.sh
#!/bin/bash
BACKUP_DIR=/var/backups/mcms
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR
pg_dump -U mcms -d mcms -F c -f $BACKUP_DIR/mcms_$DATE.dump
# Keep only last 30 days
find $BACKUP_DIR -name "*.dump" -mtime +30 -delete
```

Schedule with cron:
```bash
# Run daily at 2am
0 2 * * * /opt/mcms/backup.sh
```

**Restore from backup:**
```bash
pg_restore -U mcms -d mcms -c mcms_YYYYMMDD_HHMMSS.dump
```

### What to back up

| Item | Location | Frequency |
|---|---|---|
| Database | PostgreSQL data directory | Daily |
| Application config | `application-prod.yml` | On every change |
| Uploaded files | `target/uploads/` (if any) | Daily |
| JWT secret | Environment variable / config | Secure storage |

---

## Running in Production

### Building the Production JAR

```bash
cd mcms-jhipster-base
./mvnw -Pprod clean package -DskipTests
```

Output: `target/mcms-0.0.1-SNAPSHOT.jar`

### Running the JAR

```bash
java -jar target/mcms-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET="your-secret"
```

### Running as a systemd Service (Linux)

Create `/etc/systemd/system/mcms.service`:

```ini
[Unit]
Description=MCMS Mushroom Cultivation Management System
After=network.target postgresql.service

[Service]
User=mcms
WorkingDirectory=/opt/mcms
ExecStart=/usr/bin/java -jar /opt/mcms/mcms-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
EnvironmentFile=/opt/mcms/.env
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Create `/opt/mcms/.env`:
```
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=your-secret-here
SPRING_DATASOURCE_PASSWORD=your-db-password
```

Enable and start:
```bash
systemctl daemon-reload
systemctl enable mcms
systemctl start mcms
systemctl status mcms
```

### Docker (alternative)

```bash
# Build the Docker image
npm run java:docker:prod

# Start with Docker Compose
docker compose -f src/main/docker/app.yml up -d
```

### Nginx Reverse Proxy (recommended for production)

```nginx
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## Upgrading

When a new version of MCMS is available:

1. **Back up the database** before anything else
2. Pull or download the new version
3. Review the changelog for any breaking changes or manual migration steps
4. Build the new JAR: `./mvnw -Pprod clean package -DskipTests`
5. Stop the running application
6. Replace the JAR file
7. Start the application — Liquibase applies any new migrations automatically
8. Verify health: `curl http://localhost:8080/management/health`
9. Test a login and a few key functions

If something goes wrong, restore the database backup and roll back to the previous JAR.

---

## Log Management

### Application Logs

By default, logs go to stdout. To write to a file, add to application-prod.yml:
```yaml
logging:
  file:
    name: /var/log/mcms/application.log
  logback:
    rollingpolicy:
      max-file-size: 50MB
      max-history: 30
```

### Log Rotation

If using systemd, use `journald` rotation:
```bash
journalctl -u mcms -f          # Follow logs live
journalctl -u mcms --since today  # Today's logs
journalctl -u mcms -n 100      # Last 100 lines
```

### Increasing Log Detail for Debugging

Temporarily increase log level via the admin UI (**Administration → Logs**) or permanently in configuration:
```yaml
logging:
  level:
    com.mcms: DEBUG
    org.springframework.security: DEBUG
```

Reset to INFO after debugging is complete.
