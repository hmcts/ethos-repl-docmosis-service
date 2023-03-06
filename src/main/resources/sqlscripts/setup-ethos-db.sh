#!/usr/bin/env bash
# Creates ethos db, and its tables and functions that are required by ethos-repl-docmosis-service

echo "Creating ethos database"
psql postgresql://localhost:6432 -v ON_ERROR_STOP=1 -U postgres <<-EOSQL
  CREATE USER ethos WITH PASSWORD 'ethos';

  CREATE DATABASE ethos
    WITH OWNER = ethos
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;

EOSQL

set -e

echo "Running tbls_ethosCaseRefGen.sql"
psql postgresql://localhost:6432/ethos -U ethos -f ./tbls_ethosCaseRefGen.sql

echo "Running tbls_ethosMultipleCaseRefGen.sql"
psql postgresql://localhost:6432/ethos -U ethos -f ./tbls_ethosMultipleCaseRefGen.sql

echo "Running tbls_ethosSubMultipleCaseRefGen.sql"
psql postgresql://localhost:6432/ethos -U ethos -f ./tbls_ethosSubMultipleCaseRefGen.sql

echo "Running fn_ethosCaseRefGen.sql"
psql postgresql://localhost:6432/ethos -U ethos -f ./fn_ethosCaseRefGen.sql

echo "Running fn_ethosMultipleCaseRefGen.sql"
psql postgresql://localhost:6432/ethos -U ethos -f ./fn_ethosMultipleCaseRefGen.sql

echo "Running fn_ethosSubMultipleCaseRefGen.sql"
psql postgresql://localhost:6432/ethos -U ethos -f ./fn_ethosSubMultipleCaseRefGen.sql

echo "Running tbls_referenceData.sql"
psql postgresql://localhost:6432/ethos -U ethos -f ./migration/tbls_referenceData.sql

echo "Running DevReferenceData.sql"
psql postgresql://localhost:6432/ethos -U ethos -f ./dev/DevReferenceData.sql