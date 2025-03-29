import apiClient from '@/http-common'
import type { ComplaintRequest, Request } from '@/types/request'

export const RequestService = {
  async getPendingRequests(departmentId: number) {
    try {
      const response = await apiClient.get(`/api/departments/${departmentId}/pending-requests`)
      return response.data
    } catch (error) {
      console.error('Error fetching requests:', error)
      throw error
    }
  },
  async updateRequestStatus(departmentId: number, requestId: number, status: 'approved' | 'rejected') {
    try {
      const response = await apiClient.post(
        `/api/departments/${departmentId}/complaint-requests/${requestId}/status`,
        { status }
      )
      return response.data
    } catch (error) {
      console.error('Error updating request status:', error)
      throw error
    }
  },
  async createComplaintRequest(data: ComplaintRequest) {
    try {
      const response = await apiClient.post('/api/employees/complaint-requests', data)
      return response.data
    } catch (error) {
      console.error('Error creating complaint request:', error)
      throw error
    }
  }  
}