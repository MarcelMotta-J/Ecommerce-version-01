#!/bin/bash

FILE=$1

if [ -z "$FILE" ]; then
  echo "❌ Uso: ./restore.sh arquivo.sql"
  exit 1
fi

docker run --rm -i \
  -e PGPASSWORD='SUA_SENHA' \
  postgres:18 \
  psql \
  -h dpg-d7jb999kh4rs73fj5cfg-a.oregon-postgres.render.com \
  -U storeuser \
  -d storedb_biny < $FILE

echo "✅ Restore concluído: $FILE"

# autorização chmod +x backup.sh
# rodar ./backup.sh
