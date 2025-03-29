import apiClient from '@/http-common'
import type { Complaint } from '@/types/complaint'

export const ComplaintService = {
  async getPendingRequests(departmentId: number) {
    try {
      const response = await apiClient.get(`/api/departments/${departmentId}/pending-requests`)
      return response.data
    } catch (error) {
      console.error('Error fetching pending requests:', error)
      throw error
    }
  }
}