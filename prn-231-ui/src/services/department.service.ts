import apiClient from '@/http-common'
import type { Department } from '@/types/department'

export const DepartmentService = {
  async getAllDepartments() {
    try {
      const response = await apiClient.get('/api/departments')
      return response.data
    } catch (error) {
      console.error('Error fetching departments:', error)
      throw error
    }
  }
}