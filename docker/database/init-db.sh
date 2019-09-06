#!/usr/bin/env bash

set -e

# Ethos Repl database
if [ -z "$ETHOS_REPL_DB_PASSWORD" ]; then
  echo "ERROR: Missing environment variables. Set value for 'ETHOS_REPL_DB_PASSWORD'."
  exit 1
fi

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=ethos --set PASSWORD=${ETHOS_REPL_DB_PASSWORD} <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';
  CREATE SEQUENCE seqOffice
    INCREMENT 1
    MINVALUE 10
    MAXVALUE 12
    START 10
    CYCLE;
  CREATE DATABASE ethos
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
EOSQL
