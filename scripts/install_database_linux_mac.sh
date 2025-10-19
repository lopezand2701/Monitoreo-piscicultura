#!/bin/bash

echo "===================================="
echo "INSTALADOR BASE DE DATOS PISCICULTURA"
echo "===================================="

read -p "Nombre de la base de datos (default: psicultura): " DB_NAME
DB_NAME=${DB_NAME:-psicultura}

read -p "Usuario PostgreSQL (default: postgres): " DB_USER
DB_USER=${DB_USER:-postgres}

read -s -p "Password PostgreSQL: " DB_PASSWORD
echo

read -p "Host (default: localhost): " DB_HOST
DB_HOST=${DB_HOST:-localhost}

read -p "Puerto (default: 5432): " DB_PORT
DB_PORT=${DB_PORT:-5432}

echo
echo "Creando base de datos $DB_NAME..."
export PGPASSWORD=$DB_PASSWORD
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -c "DROP DATABASE IF EXISTS $DB_NAME;"
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -c "CREATE DATABASE $DB_NAME;"

echo
echo "Ejecutando script de base de datos..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "database/piscicultura_complete.sql"

echo
echo "✅ Base de datos instalada correctamente!"
echo
echo "📊 Datos cargados:"
echo "   - 4 usuarios (2 admin, 2 piscicultor)"
echo "   - 3 estaciones con departamento/municipio"
echo "   - 4 estanques"
echo "   - 4 especies"
echo "   - 5 sensores"
echo "   - 5 mediciones de ejemplo"
echo
echo "🔐 Credenciales de prueba:"
echo "   Admin: admin@piscicultura.com / admin123"
echo "   Piscicultor: piscicultor@piscicultura.com / user123"