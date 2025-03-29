import axios from 'axios';
import { Attendance } from '@/types/attendance';
import http from '@/http-common'

const API_URL = process.env.API_URL;

export const AttendanceService = {
  async getDepartmentAttendances(departmentId: number, filter: string = 'daily') {
    try {
      const response = await http.get(`/api/departments/${departmentId}/attendances`, {
        params: { filter }
      })
      return response.data
    } catch (error) {
      console.error('Error fetching attendances:', error)
      throw error
    }
  },

  
  async getMyAttendance() {
    try {
      const response = await http.get(`/api/employees/attendance`, {
        params: {}
      })
      return response.data
    } catch (error) {
      console.error('Error fetching attendances:', error)
      throw error
    }
  },

  async exportAttendances(filters: any) {
    try {
      const response = await axios.get(`${API_URL}/attendances/export`, {
        params: filters,
        responseType: 'blob'
      });
      return response.data;
    } catch (error) {
      console.error('Error exporting attendances:', error);
      throw error;
    }
  }
};
