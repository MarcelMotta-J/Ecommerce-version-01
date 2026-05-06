#!/bin/bash

DATE=$(date +%Y-%m-%d_%H-%M)

docker run --rm \
  -e PGPASSWORD='SENHA' \
  -v "$PWD":/backup \
  postgres:18 \
  pg_dump \
  -h HOST_RENDER \
  -U USER \
  -d DATABASE \
  -F p \
  -f /backup/store_backup_$DATE.sql
  
  echo "✅ Backup criado: store_backup_$DATE.sql"

# autorization   chmod +x backup.sh
# run ./backup.sh
