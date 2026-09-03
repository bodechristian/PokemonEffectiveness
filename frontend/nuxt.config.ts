// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  runtimeConfig: {
    public: {
      // Overridden at container runtime via NUXT_PUBLIC_API_BASE_URL.
      // Behind the gateway this is '/api'; in local dev it is the end service directly.
      apiBaseUrl: 'http://localhost:8080'
    }
  }
})
