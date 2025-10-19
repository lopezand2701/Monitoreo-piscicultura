@echo off
setlocal EnableDelayedExpansion

echo ====================================
echo ACTUALIZADOR BASE DE DATOS PISCICULTURA
echo ====================================

set DB_NAME=psicultura
set DB_USER=postgres
set DB_HOST=localhost
set DB_PORT=5432

echo.
echo Verificando si la base de datos existe...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -lqt | cut -d \| -f 1 | grep -wq %DB_NAME%

if errorlevel 1 (
    echo ❌ La base de datos %DB_NAME% no existe. Creándola...
    psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d postgres -c "CREATE DATABASE %DB_NAME%;"
    if !errorlevel! equ 0 (
        echo ✅ Base de datos creada exitosamente
    ) else (
        echo ❌ Error al crear la base de datos
        pause
        exit /b 1
    )
) else (
    echo ✅ La base de datos %DB_NAME% ya existe
)

echo.
echo Cargando/Actualizando con el backup...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f "database\psicultura.sql"

if !errorlevel! equ 0 (
    echo.
    echo ✅ Base de datos cargada/actualizada correctamente!
    echo 📊 Se aplicó el backup completo: database\psicultura.sql
) else (
    echo.
    echo ❌ Error al cargar el backup
)

echo.
pause