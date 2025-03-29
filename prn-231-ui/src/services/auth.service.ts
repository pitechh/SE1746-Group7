import apiClient from '@/http-auth'
import type { LoginRequest, LoginResponse } from '@/types/auth'

export const AuthService = {
  isAuthenticated() {
    const token = localStorage.getItem('token')
    return token !== null && token !== undefined
  },

  async login(credentials: LoginRequest) {
    try {
      const response = await apiClient.post(
        '/api/auth/login', 
        credentials,
      )
      if (response.data.accessToken) {
        localStorage.setItem('token', response.data.accessToken)
      }
      return response.data
    } catch (error) {
      console.error('Login error:', error)
      throw error
    }
  },

  logout() {
    localStorage.removeItem('token')
    window.location.href = '/login'
  }
}