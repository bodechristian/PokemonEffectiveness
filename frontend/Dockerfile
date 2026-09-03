# syntax=docker/dockerfile:1
#
# Build context is the repository root, because the API client is generated from
# the shared spec in api/ using the same Gradle task the developers run.
#
#   docker build -f frontend/Dockerfile -t pokemon-frontend .

# ---------- generate the typed API client ----------
FROM gradle:9.2.0-jdk21 AS apiclient
WORKDIR /workspace

# Only the modules this stage needs are copied, settings.gradle skips the rest.
COPY settings.gradle build.gradle gradle.properties ./
COPY gradle/ gradle/
COPY buildSrc/ buildSrc/
COPY api/ api/
COPY frontend/build.gradle frontend/

RUN gradle --no-daemon :frontend:openApiGenerate

# ---------- build the Nuxt app ----------
FROM node:22-alpine AS build
WORKDIR /app

RUN corepack enable

# Dependencies first, so a source-only change does not reinstall them.
COPY frontend/package.json frontend/pnpm-lock.yaml frontend/pnpm-workspace.yaml ./
RUN pnpm install --frozen-lockfile

COPY frontend/nuxt.config.ts frontend/tsconfig.json ./
COPY frontend/app/ app/
COPY frontend/public/ public/
COPY --from=apiclient /workspace/frontend/app/generated/ app/generated/

RUN pnpm build

# ---------- runtime ----------
FROM node:22-alpine AS runtime
WORKDIR /app

COPY --from=build --chown=node:node /app/.output/ ./.output/

USER node
EXPOSE 3000

ENV PORT=3000 \
    HOST=0.0.0.0 \
    NODE_ENV=production

# 127.0.0.1 rather than localhost: nitro binds IPv4 only, and localhost
# resolves to ::1 first inside the container.
HEALTHCHECK --interval=10s --timeout=3s --start-period=15s --retries=5 \
    CMD wget -qO- http://127.0.0.1:3000/ >/dev/null || exit 1

CMD ["node", ".output/server/index.mjs"]
