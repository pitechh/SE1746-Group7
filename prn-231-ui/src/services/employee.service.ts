import axios from 'axios'
import { Employee } from '@/types/employee'
import http from "@/http-common";

const API_URL = 'https://r0qx7xtm-8000.asse.devtunnels.ms/api/departments'

export const EmployeeService = {
    // Lấy danh sách nhân viên theo department
    async getEmployeesByDepartment(departmentId: number) {
        try {
            // const response = await axios.get(`${API_URL}/api/departments/${departmentId}/employees`)
            const response = await http.get(`/api/departments/${departmentId}/employees`)
            console.log('Response:', response.data)
            return response.data
        } catch (error) {
            console.error('Error fetching employees:', error)
            throw error
        }
    },

    // Lấy tất cả nhân viên
    async getAllEmployees(page: number = 1, search: string = '') {
        try {
            const response = await http.get(`${API_URL}/api/employees`, {
                params: {
                    page,
                    search
                }
            })
            return response.data
        } catch (error) {
            console.error('Error fetching all employees:', error)
            throw error
        }
    },

    // Lấy chi tiết một nhân viên
    async getEmployeeById(id: number) {
        try {
            const response = await http.get(`/api/employees/${id}`)
            console.log('Response:', response.data)
            return response.data
        } catch (error) {
            console.error('Error fetching employee details:', error)
            throw error
        }
    },

    async getProfile() {
        try {
            const response = await http.get(`/api/employees`)
            console.log('Response:', response.data)
            return response.data
        } catch (error) {
            console.error('Error fetching employee details:', error)
            throw error
        }
    },

    // Thêm nhân viên mới
    async createEmployee(employeeData: Partial<Employee>) {
        try {
            const response = await axios.post(`${API_URL}/api/employees`, employeeData)
            return response.data
        } catch (error) {
            console.error('Error creating employee:', error)
            throw error
        }
    },

    // Cập nhật thông tin nhân viên
    async updateEmployee(id: number, employeeData: Partial<Employee>) {
        try {
            const response = await axios.put(`${API_URL}/api/employees/${id}`, employeeData)
            return response.data
        } catch (error) {
            console.error('Error updating employee:', error)
            throw error
        }
    },

    // Xóa nhân viên
    async deleteEmployee(id: number) {
        try {
            const response = await axios.delete(`${API_URL}/api/employees/${id}`)
            return response.data
        } catch (error) {
            console.error('Error deleting employee:', error)
            throw error
        }
    },

    // Export danh sách nhân viên
    async exportEmployees(filters: any) {
        try {
            const response = await axios.get(`${API_URL}/api/employees/export`, {
                params: filters,
                responseType: 'blob'
            })
            return response.data
        } catch (error) {
            console.error('Error exporting employees:', error)
            throw error
        }
    },

    // Tìm kiếm nhân viên
    async searchEmployees(query: string, page: number = 1) {
        try {
            const response = await axios.get(`${API_URL}/api/employees/search`, {
                params: {
                    q: query,
                    page
                }
            })
            return response.data
        } catch (error) {
            console.error('Error searching employees:', error)
            throw error
        }
    }
}