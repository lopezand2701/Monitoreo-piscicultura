@echo off
echo ====================================
echo INSTALADOR BASE DE DATOS PISCICULTURA
echo ====================================

set /p DB_NAME="Nombre de la base de datos (default: psicultura): "
if "%DB_NAME%"=="" set DB_NAME=psicultura

set /p DB_USER="Usuario PostgreSQL (default: postgres): "
if "%DB_USER%"=="" set DB_USER=postgres

set /p DB_PASSWORD="Password PostgreSQL: "

set /p DB_HOST="Host (default: localhost): "
if "%DB_HOST%"=="" set DB_HOST=localhost

set /p DB_PORT="Puerto (default: 5432): "
if "%DB_PORT%"=="" set DB_PORT=5432

echo.
echo Creando base de datos %DB_NAME%...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d postgres -c "DROP DATABASE IF EXISTS %DB_NAME%;"
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d postgres -c "CREATE DATABASE %DB_NAME%;"

echo.
echo Ejecutando script de base de datos...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f "database\piscicultura_complete.sql"

echo.
echo ✅ Base de datos instalada correctamente!
echo.
echo 📊 Datos cargados:
echo    - 4 usuarios (2 admin, 2 piscicultor)
echo    - 3 estaciones con departamento/municipio
echo    - 4 estanques
echo    - 4 especies
echo    - 5 sensores
echo    - 5 mediciones de ejemplo
echo.
echo 🔐 Credenciales de prueba:
echo    Admin: admin@piscicultura.com / admin123
echo    Piscicultor: piscicultor@piscicultura.com / user123
echo.
pause