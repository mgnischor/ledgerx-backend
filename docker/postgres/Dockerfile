# syntax=docker/dockerfile:1
#
# PostgreSQL image for local development with TLS enforced end-to-end between
# the ledgerx-backend service and the database. The certificate is
# self-signed and generated at build time purely to enable encryption
# in-transit (the backend connects with sslmode=require); it is not meant to
# authenticate the server's identity. For staging/production, mount a
# certificate issued by a real CA instead of relying on this image's cert.
FROM postgres:18

RUN apt-get update \
    && apt-get install -y --no-install-recommends openssl \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /etc/postgresql/certs \
    && openssl req -new -x509 -days 3650 -nodes \
        -subj "/CN=postgres" \
        -newkey rsa:4096 \
        -keyout /etc/postgresql/certs/server.key \
        -out /etc/postgresql/certs/server.crt \
    && chown -R postgres:postgres /etc/postgresql/certs \
    && chmod 600 /etc/postgresql/certs/server.key \
    && chmod 644 /etc/postgresql/certs/server.crt

COPY postgresql.conf /etc/postgresql/postgresql.conf
COPY pg_hba.conf /etc/postgresql/pg_hba.conf
RUN chown postgres:postgres /etc/postgresql/postgresql.conf /etc/postgresql/pg_hba.conf

CMD ["postgres", "-c", "config_file=/etc/postgresql/postgresql.conf", "-c", "hba_file=/etc/postgresql/pg_hba.conf"]
