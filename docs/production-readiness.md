# Operación y despliegue

## Configuración

En desarrollo se conservan los defaults locales de PostgreSQL y los usuarios de Keycloak Dev Services. En producción no hay credenciales ni URL de base de datos por defecto: configurar estas variables:

| Variable | Uso |
| --- | --- |
| `PORT` | Puerto HTTP asignado por la plataforma (default local: `8080`) |
| `DB_JDBC_URL` | URL JDBC PostgreSQL |
| `DB_USERNAME`, `DB_PASSWORD` | Credenciales de PostgreSQL |
| `OIDC_AUTH_SERVER_URL` | Issuer/base URL del proveedor OIDC |
| `DB_POOL_MAX_SIZE` | Máximo de conexiones (default: `10`) |
| `OTEL_EXPORTER_OTLP_ENABLED` | Habilita exportación de spans OTLP (default: `false`) |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | Collector OTLP gRPC (default: `http://localhost:4317`) |
| `OTEL_SERVICE_NAME` | Nombre del servicio en los recursos OTel |
| `OTEL_TRACES_SAMPLER_ARG` | Proporción de muestreo de trazas, entre `0` y `1` (default: `1.0`) |
| `FRONTEND_ORIGIN` | Único origin permitido por CORS en prod, por ejemplo `https://portal.example.com` |

En producción Flyway conserva la propiedad del schema y Hibernate valida el schema existente. Un fallo de PostgreSQL durante arranque detiene el inicio si Flyway no puede conectarse; una caída posterior degrada readiness y los errores HTTP se responden con el contrato común sin incluir SQL o stack traces.

## Health, métricas y trazas

* `GET /q/health/live`: estado del proceso, sin dependencia de PostgreSQL.
* `GET /q/health/ready`: incluye el health check JDBC y refleja si PostgreSQL está disponible.
* `GET /q/metrics`: formato Prometheus/OpenMetrics. Está autenticado en prod y permitido en dev/test. Se incluyen métricas HTTP de método, status, URI normalizada y duración; métricas de workflow e idempotencia; y gauges agregados del outbox (`loan_outbox_pending`, `loan_outbox_processing`, `loan_outbox_published`, `loan_outbox_failed`). Los gauges cuentan filas por estado actual, no son totales históricos.
* `GET /q/openapi` y Swagger UI: contrato público v1; Swagger UI se incluye en dev y se deshabilita en prod.

OpenTelemetry instrumenta HTTP y JDBC. Las trazas se generan y propagan sin requerir proveedor. La exportación OTLP queda optativa para no bloquear local/producción cuando no existe collector; configurá `OTEL_EXPORTER_OTLP_ENABLED=true` junto al endpoint para enviarlas. En dev se evita iniciar Grafana LGTM automáticamente, por lo que no se requiere Docker para ejecutar la aplicación.

## Logs y correlación

En prod los logs de consola son JSON, con nombre de servicio, timestamp, nivel, campos MDC y los campos de traza disponibles (`trace`/`spanId`). El filtro continúa usando `X-Correlation-ID`: preserva valores válidos (`[A-Za-z0-9._:-]{1,128}`), genera uno cuando falta o no valida y lo devuelve en la respuesta. Correlation ID identifica la operación funcional; trace ID/span ID identifican el contexto técnico de OpenTelemetry. El publisher del outbox registra `eventId`, `eventType`, `aggregateId` y `correlationId`, sin serializar el payload.

Los mappers específicos existentes mantienen sus códigos y status y ahora comparten `code`, `message`, `timestamp`, `correlationId`. Errores inesperados se devuelven como `500 INTERNAL_ERROR` genérico; el detalle queda sólo en log interno.

## Seguridad y límites posteriores

`/api/*` sigue requiriendo autenticación y los resources conservan roles y ownership. Health es público para probes; metrics requiere autenticación en prod. CORS usa `FRONTEND_ORIGIN` explícito en prod (no comodín), y localhost:5173 en dev. `Dockerfile.jvm` ejecuta la imagen JVM Java 25 con UID no-root y recibe `PORT` a través de Quarkus. Para despliegues expuestos a internet, el rate limiting distribuido debe aplicarse luego en gateway/proxy o con infraestructura dedicada; esta entrega no incorpora Redis ni un limitador distribuido.

El test generado `GreetingResourceTest` y su variante IT se eliminaron porque `/hello` no es una capacidad del producto. Fueron reemplazados por pruebas del API y observabilidad reales en `ProductionReadinessTest`.
