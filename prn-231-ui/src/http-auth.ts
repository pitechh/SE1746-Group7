import axios, { AxiosInstance } from "axios";

const apiClient: AxiosInstance = axios.create({
  baseURL: process.env.API_LOGIN_URL || 'http://localhost:8080',
  withCredentials: false,
  headers: {
    "Content-type": "application/json",
    'Accept': '*/*',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,PATCH,OPTIONS'
    },
});

apiClient.interceptors.response.use(
  async response => {
    try {
      const data = response.data;
      if (response.request) {
        response.request.abort();
      }
      return {
        data,
        status: response.status,
        statusText: response.statusText,
        headers: response.headers
      };
    } catch (err) {
      console.error('Error in response interceptor:', err);
      return response;
    }
  },
  error => {
    if (error.response?.data) {
      const { data, status, statusText, headers } = error.response;
      if (error.response.request) {
        error.response.request.abort();
      }
      return {
        data,
        status,
        statusText,
        headers
      };
    }
    return Promise.reject(error);
  }
);

export default apiClient;