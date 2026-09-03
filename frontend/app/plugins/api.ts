import { Configuration, EffectivenessApi, HelloApi } from '~/generated'

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()

  const configuration = new Configuration({
    basePath: config.public.apiBaseUrl
  })

  return {
    provide: {
      api: {
        HelloAPI: new HelloApi(configuration),
        EffectivenessAPI: new EffectivenessApi(configuration)
      }
    }
  }
})
