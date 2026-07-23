# Kubernetes deployment

Manifests to run LedgerX Backend and its dependencies (Postgres, RabbitMQ,
and the optional Grafana LGTM observability stack) in a Kubernetes cluster.
Everything lives in the `ledgerx` namespace.

## Files

| File                    | Purpose                                                       |
| ----------------------- | -------------------------------------------------------------- |
| `00-namespace.yaml`     | The `ledgerx` namespace                                        |
| `01-configmap.yaml`     | Non-secret app configuration (hosts, ports, seed toggle)       |
| `02-secrets.yaml`       | Placeholder credentials — **replace before real use**         |
| `03-postgres.yaml`      | Postgres `StatefulSet` + `PersistentVolumeClaim` + `Service`   |
| `04-rabbitmq.yaml`      | RabbitMQ `StatefulSet` + `PersistentVolumeClaim` + `Service`   |
| `05-grafana-lgtm.yaml`  | Optional Grafana/Loki/Tempo/Mimir all-in-one, receives OTLP    |
| `06-app.yaml`           | LedgerX Backend `Deployment` + `Service`                       |
| `07-ingress.yaml`       | Ingress routing `ledgerx.local` to the app (needs an ingress controller) |
| `08-hpa.yaml`           | Horizontal Pod Autoscaler (needs `metrics-server`)             |
| `kustomization.yaml`    | Applies all of the above with a single command                 |

## Build the image

The app image is not published anywhere; build it locally first with the
repo's `Dockerfile` and make it available to your cluster:

```
docker build -t ledgerx-backend:latest .
```

- **kind**: `kind load docker-image ledgerx-backend:latest`
- **minikube**: `minikube image load ledgerx-backend:latest`
- **Docker Desktop Kubernetes**: no extra step, it shares the local Docker daemon.

## Deploy

```
kubectl apply -k k8s/
```

Or apply the files individually in numeric order if you don't want to use
Kustomize.

## Verify

```
kubectl -n ledgerx get pods -w
kubectl -n ledgerx port-forward svc/ledgerx-backend 8080:8080
```

Then browse:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Grafana (if deployed): `kubectl -n ledgerx port-forward svc/grafana-lgtm 3000:3000`, then `http://localhost:3000`

If you deployed `07-ingress.yaml` and have an ingress controller running,
add `ledgerx.local` to your hosts file pointing at the controller's external
IP instead of port-forwarding.

## Known limitations / things to adjust before anything beyond manual testing

- `02-secrets.yaml` ships plaintext placeholder credentials committed to the
  repo — fine for local testing, not for anywhere shared. Replace with a
  vault/sealed-secrets/external-secrets pipeline.
- No Actuator dependency is on the app's classpath yet, so probes in
  `06-app.yaml` reuse the public `/v3/api-docs` OpenAPI endpoint as a health
  check stand-in. Add `spring-boot-starter-actuator` and point the probes at
  `/actuator/health/{readiness,liveness}` for real health semantics.
- Postgres/RabbitMQ run as single-replica `StatefulSet`s with no backup
  strategy — adequate for manual testing, not for production data.
