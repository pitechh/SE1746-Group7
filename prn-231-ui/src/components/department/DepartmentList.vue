<template>
  <div class="department-page">
    <!-- Header Section -->
    <div class="page-header">
      <h2>Danh sách phòng ban</h2>
      <div class="header-actions">
        <div class="search-box">
          <input 
            type="text" 
            class="form-control" 
            placeholder="Tìm kiếm phòng ban..." 
            v-model="searchQuery"
          >
          <i class="fas fa-search"></i>
        </div>

        <button class="btn btn-primary" @click="openAddDepartment">
          <i class="fas fa-plus"></i> Thêm phòng ban
        </button>
      </div>
    </div>

    <!-- Grid View -->
    <div class="department-grid">
      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i> Đang tải...
      </div>

      <div v-else-if="error" class="error-state">
        <i class="fas fa-exclamation-circle"></i> {{ error }}
      </div>

      <template v-else>
        <div class="dept-card" v-for="dept in filteredDepartments" :key="dept.id">
          <div class="dept-card-header">
            <h3>{{ dept.name }}</h3>
            <div class="dept-actions">
              <button class="btn-icon" @click="editDepartment(dept)" title="Chỉnh sửa">
                <i class="fas fa-edit"></i>
              </button>
              <button 
                class="btn-icon" 
                @click="deleteDepartment(dept)"
                v-if="!dept.is_deleted"
                title="Xóa"
              >
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>

          <div class="dept-card-body">
            <div class="dept-stats">
              <div class="stat-item">
                <i class="fas fa-users"></i>
                <span>{{ dept.number_of_member }} Thành viên</span>
              </div>
              <div class="stat-item">
                <i class="fas fa-calendar-alt"></i>
                <span>Ngày tạo: {{ formatDate(dept.created_at) }}</span>
              </div>
            </div>

            <div class="dept-actions-full">
              <button class="btn btn-view" @click="viewMembers(dept)">
                <i class="fas fa-eye"></i> Xem thành viên
              </button>
              <button class="btn btn-attendance" @click="viewAttendance(dept)">
                <i class="fas fa-clock"></i> Xem chấm công
              </button>
              <button class="btn btn-view" @click="viewComplaint(dept)">
                <i class="fas fa-eye"></i> Xem phàn nàn
              </button>
            </div>
          </div>
        </div>

        <div v-if="filteredDepartments.length === 0" class="no-data">
          Không tìm thấy phòng ban nào
        </div>
      </template>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { Department } from '@/types/department'
import { DepartmentService } from '@/services/department.service'

export default defineComponent({
  name: 'DepartmentList',

  setup() {
    const router = useRouter()
    const departments = ref<Department[]>([])
    const loading = ref(true)
    const error = ref('')
    const searchQuery = ref('')

    const fetchDepartments = async () => {
      try {
        loading.value = true
        error.value = ''
        const response = await DepartmentService.getAllDepartments()
        if (response.status === 'success') {
          departments.value = response.data
        }
      } catch (err: any) {
        console.error('Error fetching departments:', err)
        error.value = 'Không thể tải danh sách phòng ban'
      } finally {
        loading.value = false
      }
    }

    const filteredDepartments = computed(() => {
      if (!searchQuery.value) return departments.value
      
      const query = searchQuery.value.toLowerCase()
      return departments.value.filter(dept => 
        dept.name.toLowerCase().includes(query)
      )
    })

    const formatDate = (date: string) => {
      return new Date(date).toLocaleDateString('vi-VN')
    }

    const openAddDepartment = () => {
      // Implement add department logic
      console.log('Add department')
    }

    const editDepartment = (dept: Department) => {
      // Implement edit department logic
      console.log('Edit department:', dept)
    }

    const deleteDepartment = async (dept: Department) => {
      if (confirm(`Bạn có chắc muốn xóa phòng ban ${dept.name}?`)) {
        // Implement delete logic
        console.log('Delete department:', dept)
      }
    }

    const viewMembers = (dept: Department) => {
      router.push(`/departments/${dept.id}/members`)
    }

    const viewAttendance = (dept: Department) => {
      router.push(`/departments/${dept.id}/attendance`)
    }
    const viewComplaint = (dept: Department) => {
      router.push(`/departments/${dept.id}/complaints`)
    }

    onMounted(fetchDepartments)

    return {
      departments,
      loading,
      error,
      searchQuery,
      filteredDepartments,
      formatDate,
      openAddDepartment,
      editDepartment,
      deleteDepartment,
      viewMembers,
      viewAttendance,
      viewComplaint
    }
  }
})
</script>

<style scoped>
.department-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.header-actions {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
}

.search-box {
  position: relative;
  width: 300px;
}

.search-box input {
  width: 100%;
  padding: 8px 32px 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
}

.search-box i {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #a0aec0;
}

.btn-primary {
  background: #667eea;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.department-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.loading-state,
.error-state,
.no-data {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  color: #4a5568;
}

.dept-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.dept-card-header {
  padding: 15px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dept-card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2d3748;
}

.dept-actions {
  display: flex;
  gap: 8px;
}

.btn-icon {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  background: #f7fafc;
  color: #4a5568;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon:hover {
  background: #edf2f7;
}

.dept-card-body {
  padding: 15px;
}

.dept-stats {
  margin-bottom: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4a5568;
  margin-bottom: 8px;
}

.dept-actions-full {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.btn {
  padding: 8px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
}

.btn-view:hover {
  background: #ebf4ff;
  color: #3182ce;
}

.btn-attendance:hover {
  background: #ebf8ff;
  color: #2c5282;
}

@media (max-width: 768px) {
  .header-actions {
    flex-direction: column;
  }

  .search-box {
    width: 100%;
  }

  .btn-primary {
    width: 100%;
    justify-content: center;
  }
}
</style>