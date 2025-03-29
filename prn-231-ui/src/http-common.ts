import axios, { AxiosInstance } from "axios";
import router from '@/router'

const apiClient: AxiosInstance = axios.create({
  baseURL: process.env.API_URL || 'http://localhost:8080',
  headers: {
    "Content-type": "application/json",
  }
});


apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)


apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401 || 
      error.response?.status === 403 ||
      error.response?.data?.message?.toLowerCase().includes('unauthorized')) {
    
    // Xóa token và auth state
    // authStore.setAuth(null)
    localStorage.removeItem('token')

    // Lưu lại current URL để sau khi login có thể quay lại
    const currentPath = window.location.pathname
    
    // Redirect về trang login và reload để reset app state
    if (window.location.pathname !== '/login') {
      window.location.href = `/login?redirect=${encodeURIComponent(currentPath)}`
    }
  }
  
  return Promise.reject(error)
  }
)

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