# Loan Origination Platform

Plataforma de originación de préstamos construida como monolito modular: API Quarkus en Java 25, PostgreSQL y un pequeño portal React para mostrar el flujo financiero.

## Arquitectura

```text
frontend (OIDC SPA) → Quarkus REST API → dominio hexagonal → PostgreSQL
```

El backend incluye clientes, solicitudes, evaluación, ofertas, préstamos, desembolsos, cuotas, auditoría, outbox e idempotencia. El contrato HTTP público es `/api/v1`. El frontend expone flujos por rol sobre estos casos de uso; toda autorización y propiedad sigue imponiéndose en la API.

## Backend y desarrollo local

Guía reproducible de arranque en Windows: [`docs/local-development-startup.md`](docs/local-development-startup.md).

Requiere Java 25, Maven y PostgreSQL. `docker compose up -d postgres` inicia la base local en `localhost:5432`; el backend permite ajustar el puerto mediante `DB_JDBC_URL`.

```powershell
$env:DB_JDBC_URL='jdbc:postgresql://localhost:5432/loan_origination'
./mvnw quarkus:dev
```

Dev Services aporta Keycloak local en modo dev. Usuarios de demostración locales (no usar en producción): `customer / customer` (CUSTOMER), `loan-officer / loan-officer` (LOAN_OFFICER), `auditor / auditor` (AUDITOR) y `admin / admin` (ADMIN).

## Frontend

El portal de demo está en `frontend/` y usa React, TypeScript, Vite y Bun. Configurá `frontend/.env` con los valores de `frontend/.env.example`, registrados como SPA pública en el proveedor OIDC y con Authorization Code + PKCE habilitado. El proveedor debe emitir roles `CUSTOMER`, `LOAN_OFFICER`, `AUDITOR` o `ADMIN`; la API sigue imponiendo acceso y ownership.

```powershell
cd frontend
bun install
bun run dev
bun run build
```

`VITE_API_URL` define la base API (vacía usa proxy local a `localhost:8080`). En producción configurá `FRONTEND_ORIGIN` en el backend y la URL pública de API como `VITE_API_URL` antes del build. `VITE_OIDC_AUTHORITY`, `VITE_OIDC_CLIENT_ID`, `VITE_OIDC_REDIRECT_URI` y `VITE_OIDC_POST_LOGOUT_REDIRECT_URI` configuran el cliente público OIDC. Los valores `VITE_*` quedan incluidos en el bundle: nunca pongas secretos allí.

### Demostración por roles

1. Entrá como `customer`, creá una solicitud en **Solicitudes** y enviála.
2. Cambiá a `loan-officer`, abrí la solicitud desde la cola, ejecutá evaluación; los casos `REFER` admiten resolución manual. Aprobá cuando corresponda y emití una oferta desde el detalle.
3. Volvé a `customer`, revisá la oferta y aceptala. El préstamo aparecerá en **Préstamos**.
4. Como `loan-officer`, filtrá préstamos `PENDING_DISBURSEMENT`, iniciá el desembolso local y revisá el resultado.
5. Como `customer`, abrí el préstamo activo y consultá el calendario y el desglose de cuotas.
6. Como `auditor`, buscá eventos recientes o filtrá por aggregate ID para seguir la acción con correlation ID. `admin` puede combinar las áreas operativas y de auditoría.

La UI lista las solicitudes propias del cliente y ofrece colas paginadas por estado a operadores. No requiere guardar IDs en localStorage ni obtenerlos manualmente.

## Quality gate

```powershell
./mvnw test
./mvnw verify
./mvnw package
```

GitHub Actions ejecuta Java 25 y `mvn verify` en cada push a `main` y pull request.

## Docker y Railway

La imagen JVM se construye después de `./mvnw package` usando `docker build -f src/main/docker/Dockerfile.jvm -t loan-origination-platform .`. En producción, Railway necesita PostgreSQL privado y API pública. Configurá `PORT`, `DB_USERNAME`, `DB_PASSWORD`, `DB_JDBC_URL`, `OIDC_AUTH_SERVER_URL`, `FRONTEND_ORIGIN` y las variables `OTEL_*` sólo si se exportarán trazas. Flyway ejecuta migraciones al arranque y Hibernate valida el schema.

El provider OIDC real debe aceptar la URL pública de frontend como redirect URI y CORS origin, emitir tokens JWT con `sub` estable y los roles esperados, y confiar en la URL issuer configurada en `OIDC_AUTH_SERVER_URL`. Las credenciales/cuentas de Railway, GitHub y el issuer productivo se configuran fuera del repo.

## Live demo

Sin URL publicada por el momento. Este espacio queda listo para enlazar la API y el portal cuando se configure el proveedor OIDC y se desplieguen los servicios.

## Operación

- Liveness: `/q/health/live`; readiness incluye PostgreSQL: `/q/health/ready`.
- OpenAPI: `/q/openapi`; métricas Prometheus: `/q/metrics` (autenticado en prod).
- Trazas OTLP opcionales y logs JSON en prod. Variables detalladas en [`docs/production-readiness.md`](docs/production-readiness.md).
