# Pokemon Effectiveness

A Gradle multi-project that calculates Pokemon type effectiveness, packaged as three
containers behind a single ingress-shaped entry point, with an MCP server bolted onto
the API service.

## Modules

| Module | What it is | Container |
|---|---|---|
| `end` | Spring Boot 4 REST API, effectiveness logic, MCP server | `pokemon-end` |
| `frontend` | Nuxt 4 UI, talks to the API through a generated TypeScript client | `pokemon-frontend` |
| `gateway` | nginx, path-routes to the other two — the stand-in for a k8s Ingress | `pokemon-gateway` |
| `backend` | The original Swing desktop app. Not containerized, not part of the web stack | — |

`api/openapi.yaml` is the single source of truth: the Spring interfaces in `end` and the
TypeScript client in `frontend` are both generated from it.

`buildSrc/` holds the `java-conventions` and `openapi-conventions` plugins. They used to
live in a separate project and resolve from `mavenLocal()`, which meant no build container
could ever compile this repo — vendoring them here is what makes the Docker builds work.

## Run it with Docker

```bash
docker compose up --build
```

Then open <http://localhost:8081>. Only the gateway publishes a port, the same way only an
Ingress is reachable from outside a cluster.

| Path | Goes to |
|---|---|
| `/` | Nuxt frontend |
| `/api/…` | `end`, with the `/api` prefix stripped |
| `/mcp` | `end`'s MCP endpoint |

Because everything is one origin, the browser never makes a cross-origin request and CORS
never comes up. `APP_CORS_ALLOWED_ORIGINS` still exists for when you publish `end`'s port
and call it directly.

## Run it locally

```bash
./gradlew :end:bootRun
```

```bash
./gradlew :frontend:pnpmRunDev
```

The frontend reads `NUXT_PUBLIC_API_BASE_URL` at runtime and defaults to
`http://localhost:8080`, so the two work together with no extra configuration.

## The API

```bash
curl "http://localhost:8081/api/effectiveness?attackingType=rock&defendingType1=fire&defendingType2=flying"
```

```bash
curl "http://localhost:8081/api/effectiveness/matchups?defendingType1=fire&defendingType2=flying"
```

Type names are the lowercase wire values from the spec (`fire`, `water`, …) and are matched
case-insensitively. Swagger UI is at <http://localhost:8081/api/swagger-ui.html>.

## The MCP server

`end` runs a Spring AI MCP server over Streamable HTTP at `/mcp`, exposing three tools:

- `calculate_combined_effectiveness` — the multiplier for one attacking type against one or
  two defending types
- `get_type_matchups` — every attacking type grouped by multiplier
- `list_pokemon_types` — the accepted type names

Adding a tool takes an `@McpTool` method on any bean; the starter's annotation scanner finds
it, no registration code required. See `end/src/main/java/com/middle/end/mcp/`.

To point an MCP client at it:

```json
{
  "mcpServers": {
    "pokemon-effectiveness": {
      "type": "http",
      "url": "http://localhost:8081/mcp"
    }
  }
}
```

Two things worth knowing if you extend the tools:

- `spring.ai.mcp.server.protocol` has to be set explicitly. The auto-configuration is gated
  on the property being *present*, so relying on the documented default leaves `/mcp`
  returning 404.
- Tool parameters use `McpPokemonType`, not the generated `PokemonType`. The schema generator
  emits Java constant names while the generated enum's `@JsonValue` deserializer accepts only
  the lowercase wire values, so the generated enum advertises a schema it cannot parse.
  `McpPokemonTypeTest` keeps the adapter enum in sync with the spec.
