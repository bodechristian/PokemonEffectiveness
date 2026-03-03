export const useHelloWorld = () => {
  const { $api } = useNuxtApp()

  return {
    getGreeting: () => $api.HelloAPI.hello(),
  };
};
