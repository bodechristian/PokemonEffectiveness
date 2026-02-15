import { HelloApi, Configuration } from "../api-client";

export const useHelloWorld = () => {
  const config = new Configuration({
    basePath: 'http://localhost:8080',
  });

  const api = new HelloApi(config);

  return {
    getGreeting: () => api.hello(),
  };
};

