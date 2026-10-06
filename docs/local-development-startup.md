# Arranque local en Windows

Procedimiento para iniciar el backend, Keycloak de desarrollo y el frontend sin recrear la base ni borrar datos.

## Antes de empezar

- Docker Desktop abierto y con el engine Linux (`desktop-linux`) listo.
- Java 25, Maven y Bun instalados.
- El servicio Windows `postgresql-x64-18` disponible. En el entorno validado, Quarkus conecta al PostgreSQL existente en `localhost:5433` (`loan_origination`). El contenedor Docker `loan-origination-postgres` publicado en `5432` es una instancia aparte; no cambies el destino a ese puerto si no decidís explícitamente usar esa otra base.
- No hace falta arrancar Kafka para el modo local actual. Quarkus inicia Keycloak Dev Services usando el realm incluido en el proyecto.

## 1. Comprobar Docker y PostgreSQL

En PowerShell:

```powershell
$docker = "$env:ProgramFiles\Docker\Docker\resources\bin\docker.exe"
if (-not (Test-Path $docker)) {
  $docker = "$env:LOCALAPPDATA\Programs\DockerDesktop\resources\bin\docker.exe"
}
& $docker context show
& $docker info
& $docker ps --format "table {{.Names}}`t{{.Status}}`t{{.Ports}}"
Get-Service postgresql-x64-18
Test-NetConnection localhost -Port 5433
```

El contexto esperado es `desktop-linux`; el engine debe contestar a `docker info`. En la configuración validada, Docker ya conserva `loan-origination-postgres` en `5432`, mientras que el backend apunta al servicio PostgreSQL de Windows en `5433`. No ejecutes `docker compose down -v`, no elimines el volumen `postgres_data` y no cambies el JDBC URL para intentar arreglar Keycloak.

Si el servicio PostgreSQL figura detenido y confirmaste que es la base usada por el entorno, podés iniciarlo sin recrear datos:

```powershell
Start-Service postgresql-x64-18
Test-NetConnection localhost -Port 5433
```

## 2. Iniciar Quarkus y esperar a Keycloak

Desde una nueva terminal PowerShell:

```powershell
Set-Location C:\IdeaBankProjects\loan-origination-platform
$env:JAVA_TOOL_OPTIONS = '-Duser.timezone=UTC'
$env:DB_JDBC_URL = 'jdbc:postgresql://localhost:5433/loan_origination'
mvn quarkus:dev
```

Usá la misma instancia PostgreSQL que contiene los datos E2E. El `DB_JDBC_URL` aquí sólo explicita el destino ya validado; no apunta al contenedor separado de `5432`.

Quarkus Dev Services usa Testcontainers y Docker para iniciar Keycloak en `8180` e importar `src/main/resources/keycloak/loan-origination-realm.json`; Testcontainers también crea su contenedor auxiliar Ryuk para limpiar recursos propios al cerrar la JVM. Como `DB_JDBC_URL` está definido, Quarkus no crea PostgreSQL Dev Services: usa la base existente en `5433`. La configuración del repo fija el puerto de Keycloak y conserva el realm `loan-origination`; no copies un puerto aleatorio del log de OIDC Dev Services al frontend.

Esperá a que el log indique que Keycloak Dev Services inició y que Loan Origination está escuchando en `8080`. Flyway debe validar las migraciones y decir que el schema está actualizado; en la validación de esta guía validó 14 migraciones, schema `14`, sin aplicar ninguna migración.

Verificá ambos servicios desde otra terminal:

```powershell
Invoke-RestMethod http://localhost:8180/realms/loan-origination/.well-known/openid-configuration
Invoke-RestMethod http://localhost:8080/q/health/ready
```

El `issuer` esperado es `http://localhost:8180/realms/loan-origination` y readiness debe devolver `UP`, incluyendo el health check de PostgreSQL.

## 3. Iniciar el frontend

Sólo después de que el discovery de Keycloak responda, abrí otra terminal:

```powershell
Set-Location C:\IdeaBankProjects\loan-origination-platform\frontend
$bun = "$env:USERPROFILE\.bun\bin\bun.exe"
if (-not (Test-Path $bun)) { $bun = (Get-Command bun -ErrorAction Stop).Source }
if (-not (Test-Path .env.local)) { Copy-Item .env.example .env.local }
& $bun install
& $bun run dev
```

`frontend/.env.local` debe apuntar a API `http://localhost:8080`, authority `http://localhost:8180/realms/loan-origination` y client `loan-frontend`. El realm configura el redirect `http://localhost:5173/*` y el web origin `http://localhost:5173`. El ejemplo del repo usa el mismo client ID.

URLs esperadas:

- Frontend: `http://localhost:5173`
- API: `http://localhost:8080`
- Keycloak realm: `http://localhost:8180/realms/loan-origination`
- OIDC discovery: `http://localhost:8180/realms/loan-origination/.well-known/openid-configuration`

Usuarios locales (sólo desarrollo): `customer / customer` (`CUSTOMER`), `loan-officer / loan-officer` (`LOAN_OFFICER`), `auditor / auditor` (`AUDITOR`) y `admin / admin` (`ADMIN`).

## Apagar

Detené Vite y Quarkus con `Ctrl+C` en sus terminales. No detengas ni elimines PostgreSQL para apagar la aplicación. Evitá `docker compose down -v`, eliminar `postgres_data` o recrear contenedores/volúmenes como troubleshooting.

## Troubleshooting

- **8180 rechaza conexiones:** comprobá `docker info`, `docker ps` y los mensajes de Testcontainers/Keycloak en la terminal de Quarkus. El frontend no podrá autenticar hasta que el discovery responda.
- **Quarkus anuncia OIDC Dev Service en un puerto aleatorio:** eso no es el Keycloak del realm importado. Confirmá que estás usando perfil `dev`, que el log dice `Dev Services for Keycloak started` y que el puerto `8180` no está ocupado. No cambies `VITE_OIDC_AUTHORITY` al puerto aleatorio.
- **Docker CLI funciona pero Testcontainers no:** el CLI y la JVM pueden tener distintos permisos para el named pipe `\\.\pipe\docker_engine`. Revisá `docker context show` y que el proceso Maven pueda acceder al socket. En esta sesión el sandbox de Codex recibió `AccessDeniedException` aunque el CLI funcionaba; al ejecutar Maven con acceso al socket, Testcontainers conectó con Docker Desktop y Keycloak inició. Testcontainers puede guardar automáticamente su estrategia detectada en `C:\Users\axelf\.testcontainers.properties`; no la borres ni la cambies si apunta al socket local válido.
- **Puerto ocupado:** inspeccioná el PID antes de detener nada: `netstat -ano | findstr ":8080"`, `netstat -ano | findstr ":8180"` o `netstat -ano | findstr ":5173"`. No mates procesos desconocidos.
- **PostgreSQL no disponible:** verificá `Get-Service postgresql-x64-18` y `Test-NetConnection localhost -Port 5433`. No cambies a `5432` sin confirmar que esa instancia contiene los datos que querés usar.
- **Vite se inició antes que Keycloak:** esperá a que discovery responda y luego reiniciá Vite para volver a cargar sus variables `VITE_*`.

En este entorno, el wrapper `mvnw.cmd` falla antes de ejecutar Maven por el cálculo de `C:\.m2`; por eso se usa el Maven instalado (`mvn`). Si el wrapper funciona en otra terminal, puede usarse en su lugar.
