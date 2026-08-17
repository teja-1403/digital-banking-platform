import axios from "axios";

export const getApiErrorMessage = (
  error: unknown,
  fallback: string,
): string => {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.message;

    if (typeof message === "string" && message.trim()) {
      return message;
    }

    const errorMessage = error.response?.data?.error;

    if (typeof errorMessage === "string" && errorMessage.trim()) {
      return errorMessage;
    }
  }

  return fallback;
};
