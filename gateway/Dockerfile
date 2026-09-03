# syntax=docker/dockerfile:1
#
# The ingress-shaped entry point in front of the frontend and end services.
#
#   docker build -t pokemon-gateway ./gateway

FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

# 127.0.0.1 rather than localhost: nginx listens on IPv4 only, and localhost
# resolves to ::1 first inside the container.
HEALTHCHECK --interval=10s --timeout=3s --start-period=5s --retries=5 \
    CMD wget -qO- http://127.0.0.1/ >/dev/null || exit 1
