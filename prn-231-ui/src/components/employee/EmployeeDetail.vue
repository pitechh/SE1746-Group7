<template>
    <div class="employee-detail-page">
      <!-- Loading và Error states -->
      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i> Đang tải thông tin...
      </div>
      
      <div v-else-if="error" class="error-state">
        <i class="fas fa-exclamation-circle"></i> {{ error }}
      </div>
  
      <div v-else class="employee-detail-container">
        <!-- Header với ảnh và thông tin cơ bản -->
        <div class="detail-header">
          <div class="employee-avatar">
            <img 
              :src="employee.image || '/default-avatar.png'" 
              :alt="employee.fullname"
            >
          </div>
          <div class="employee-basic-info">
            <h1>{{ employee.fullname }}</h1>
            <div class="employee-code">{{ employee.code }}</div>
            <div class="status-badge" :class="{ 'active': !employee.is_deleted }">
              {{ employee.is_deleted ? 'Không hoạt động' : 'Đang hoạt động' }}
            </div>
          </div>
        </div>
  
        <!-- Thông tin chi tiết -->
        <div class="detail-content">
          <div class="info-section">
            <h2>Thông tin cá nhân</h2>
            <div class="info-grid">
              <div class="info-item">
                <label>Email</label>
                <div class="info-value">
                  <i class="fas fa-envelope"></i>
                  <a :href="'mailto:' + employee.email">{{ employee.email }}</a>
                </div>
              </div>
  
              <div class="info-item">
                <label>Số điện thoại</label>
                <div class="info-value">
                  <i class="fas fa-phone"></i>
                  <a :href="'tel:' + employee.phone">{{ employee.phone }}</a>
                </div>
              </div>
  
              <div class="info-item">
                <label>Phòng ban</label>
                <div class="info-value">
                  <i class="fas fa-building"></i>
                  <span>{{ getDepartmentName(employee.department_id) }}</span>
                </div>
              </div>
  
              <div class="info-item">
                <label>Vai trò</label>
                <div class="info-value">
                  <i class="fas fa-user-tag"></i>
                  <span>{{ getRoleName(employee.role_id) }}</span>
                </div>
              </div>
  
              <div class="info-item">
                <label>Lương</label>
                <div class="info-value">
                  <i class="fas fa-money-bill-wave"></i>
                  <span>{{ formatSalary(employee.salary) }}</span>
                </div>
              </div>
  
              <div class="info-item">
                <label>Ngày tham gia</label>
                <div class="info-value">
                  <i class="fas fa-calendar-alt"></i>
                  <span>{{ formatDate(employee.created_at) }}</span>
                </div>
              </div>
            </div>
          </div>
  
          <!-- Action buttons -->
          <div class="action-buttons">
            <button class="btn btn-edit" @click="editEmployee">
              <i class="fas fa-edit"></i> Chỉnh sửa
            </button>
            <button 
              class="btn btn-delete" 
              @click="confirmDelete"
              v-if="!employee.is_deleted"
            >
              <i class="fas fa-trash"></i> Xóa
            </button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { EmployeeService } from '@/services/employee.service'
  
  export default defineComponent({
    name: 'EmployeeDetail',
  
    setup() {
      const route = useRoute()
      const router = useRouter()
      const employee = ref<any>(null)
      const loading = ref(true)
      const error = ref('')
  
      const fetchEmployeeDetails = async () => {
        try {
          loading.value = true
          const employeeId = route.params.id
          if(employeeId) {
          const response = await EmployeeService.getEmployeeById(Number(employeeId))
          
          if (response.data) {
            employee.value = response.data
          }
        } else {
          const response = await EmployeeService.getProfile()
        }
        } catch (err: any) {
          console.error('Error fetching employee details:', err)
          error.value = 'Không thể tải thông tin nhân viên'
        } finally {
          loading.value = false
        }
      }
  
      const formatDate = (date: string) => {
        return new Date(date).toLocaleDateString('vi-VN')
      }
  
      const formatSalary = (salary: number) => {
        return new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND'
        }).format(salary)
      }
  
      const getDepartmentName = (departmentId: number) => {
        // Implement department name lookup
        return `Phòng ban ${departmentId}`
      }
  
      const getRoleName = (roleId: number) => {
        // Implement role name lookup
        return roleId === 1 ? 'Admin' : 'Nhân viên'
      }
  
      const editEmployee = () => {
        router.push(`/employees/${employee.value.id}/edit`)
      }
  
      const confirmDelete = async () => {
        if (confirm('Bạn có chắc muốn xóa nhân viên này?')) {
          try {
            await EmployeeService.deleteEmployee(employee.value.id)
            router.push('/employees')
          } catch (err) {
            console.error('Error deleting employee:', err)
            error.value = 'Không thể xóa nhân viên'
          }
        }
      }
  
      onMounted(fetchEmployeeDetails)
  
      return {
        employee,
        loading,
        error,
        formatDate,
        formatSalary,
        getDepartmentName,
        getRoleName,
        editEmployee,
        confirmDelete
      }
    }
  })
  </script>
  
  <style scoped>
  .employee-detail-page {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .loading-state,
  .error-state {
    text-align: center;
    padding: 40px;
    font-size: 18px;
  }
  
  .employee-detail-container {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }
  
  .detail-header {
    display: flex;
    align-items: center;
    padding: 30px;
    background: linear-gradient(to right, #667eea, #764ba2);
    color: white;
  }
  
  .employee-avatar {
    margin-right: 30px;
  }
  
  .employee-avatar img {
    width: 120px;
    height: 120px;
    border-radius: 60px;
    border: 4px solid white;
    object-fit: cover;
  }
  
  .employee-basic-info h1 {
    margin: 0 0 10px 0;
    font-size: 24px;
  }
  
  .employee-code {
    font-size: 16px;
    opacity: 0.9;
    margin-bottom: 10px;
  }
  
  .status-badge {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 14px;
    background: rgba(255, 255, 255, 0.2);
  }
  
  .status-badge.active {
    background: #48bb78;
  }
  
  .detail-content {
    padding: 30px;
  }
  
  .info-section {
    margin-bottom: 30px;
  }
  
  .info-section h2 {
    margin: 0 0 20px 0;
    color: #2d3748;
  }
  
  .info-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }
  
  .info-item {
    padding: 15px;
    background: #f7fafc;
    border-radius: 8px;
  }
  
  .info-item label {
    display: block;
    color: #718096;
    font-size: 14px;
    margin-bottom: 8px;
  }
  
  .info-value {
    display: flex;
    align-items: center;
    gap: 10px;
    color: #2d3748;
  }
  
  .info-value i {
    color: #667eea;
  }
  
  .info-value a {
    color: #667eea;
    text-decoration: none;
  }
  
  .info-value a:hover {
    text-decoration: underline;
  }
  
  .action-buttons {
    display: flex;
    gap: 10px;
    margin-top: 20px;
  }
  
  .btn {
    padding: 10px 20px;
    border-radius: 6px;
    border: none;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .btn-edit {
    background: #667eea;
    color: white;
  }
  
  .btn-delete {
    background: #e53e3e;
    color: white;
  }
  
  @media (max-width: 768px) {
    .detail-header {
      flex-direction: column;
      text-align: center;
    }
  
    .employee-avatar {
      margin: 0 0 20px 0;
    }
  
    .info-grid {
      grid-template-columns: 1fr;
    }
  }
  </style>