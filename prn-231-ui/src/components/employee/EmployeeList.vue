<template>
  <div class="employee-page">
    <!-- Header Section -->
    <div class="page-header">
      <h2>Employees</h2>
      <div class="header-actions">
        <div class="search-box">
          <input 
            type="text" 
            class="form-control" 
            placeholder="Search..." 
            v-model="searchQuery"
          >
          <button class="btn btn-search">
            <i class="fas fa-search"></i>
          </button>
        </div>
        
        <div class="filter-tags">
          <span class="filter-tag">
            Approved
            <button class="remove-tag" @click="removeFilter('status')">×</button>
          </span>
        </div>

        <div class="view-controls">
          <button class="btn btn-filter" @click="toggleFilters">
            <i class="fas fa-filter"></i> Filters
          </button>
          <button class="btn btn-group">
            <i class="fas fa-layer-group"></i> Group By
          </button>
          <button class="btn btn-favorite">
            <i class="far fa-star"></i> Favorites
          </button>
          <div class="view-switcher">
            <button 
              class="btn btn-view" 
              :class="{ active: viewMode === 'grid' }"
              @click="viewMode = 'grid'"
            >
              <i class="fas fa-th-large"></i>
            </button>
            <button 
              class="btn btn-view" 
              :class="{ active: viewMode === 'list' }"
              @click="viewMode = 'list'"
            >
              <i class="fas fa-list"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Employee Table -->
    <div class="table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th>Code</th>
            <th>Full Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Salary</th>
            <th>Department</th>
            <th>Role</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="9" class="text-center">
              <i class="fas fa-spinner fa-spin"></i> Loading...
            </td>
          </tr>
          <tr v-else-if="error">
            <td colspan="9" class="text-center text-danger">
              {{ error }}
            </td>
          </tr>
          <tr v-else v-for="employee in employees" :key="employee.id">
            <td>
              <div class="employee-code">{{ employee.code }}</div>
            </td>
            <td>
              <div class="employee-info">
                <!-- <img 
                  :src="employee.image || '/default-avatar.png'" 
                  :alt="employee.fullname"
                  class="employee-avatar"
                > -->
                <div class="employee-name">{{ employee.fullname }}</div>
              </div>
            </td>
            <td>
              <a :href="`mailto:${employee.email}`" class="email-link">
                {{ employee.email }}
              </a>
            </td>
            <td>{{ employee.phone }}</td>
            <td>{{ employee.salary }}</td>
            <td>{{ employee.department_id }}</td>
            <td>{{ employee.role_id }}</td>
            <td>
              <span 
                class="status-badge"
                :class="{ 'active': !employee.is_deleted, 'inactive': employee.is_deleted }"
              >
                {{ employee.is_deleted ? 'Inactive' : 'Active' }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button 
                  class="btn btn-icon btn-info" 
                  @click="viewEmployeeDetail(employee.id)"
                  title="View Details"
                >
                  <i class="fas fa-eye">Details</i>
                </button>
                <!-- <button 
                  class="btn btn-icon btn-primary" 
                  @click="editEmployee(employee)"
                  title="Edit"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button 
                  class="btn btn-icon btn-danger" 
                  @click="deleteEmployee(employee)"
                  title="Delete"
                >
                  <i class="fas fa-trash"></i>
                </button> -->
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination-container">
      <span class="record-info">1-80 / 486</span>
      <div class="pagination-controls">
        <button class="btn btn-nav" :disabled="currentPage === 1">
          <i class="fas fa-chevron-left"></i>
        </button>
        <button class="btn btn-nav">
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, onMounted, onBeforeMount } from 'vue'
import { Employee, EmployeeStatus } from '@/types'
import { EmployeeService } from '@/services/employee.service'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'

export default defineComponent({
  name: 'EmployeeList',
  
  setup() {
    const router = useRouter()
    const searchQuery = ref('')
    const viewMode = ref<'list' | 'grid'>('list')
    const currentPage = ref(1)
    const loading = ref(false)
    const error = ref('')
    const departmentId = Number(router.currentRoute.value.params.id)

    let employees = ref<Employee[]>([])

    // let employees = reactive<Employee[]>([])

    const toggleFollow = (employee: Employee) => {
      // employee.isFollowing = !employee.isFollowing
    }

    const fetchEmployees = async () => {
      try {
        loading.value = true
        const response = await EmployeeService.getEmployeesByDepartment(departmentId)
        if (response.status === 'success') {
          employees.value = response.data.map((emp: Employee) => ({
            ...emp,
            isFollowing: false, 
            messageCount: 0,    
            reportCount: 0,     
            department: 'Loading...', 
            position: 'Employee'
          }))
        }
        console.log('employees', employees)
      } catch (err) {
        console.error('Error fetching employees:', err)
        error.value = 'Failed to load employees. Please try again.'
      } finally {
        loading.value = false
      }
    }

    const removeFilter = (filterType: string) => {
      // Implement filter removal logic
    }
    const viewEmployeeDetail = (employeeId: number) => {
      router.push(`/employees/${employeeId}`)
    }
    const toggleFilters = () => {
      // Implement filter toggle logic
    }
    onMounted(() => {
      fetchEmployees()
    })

    return {
      searchQuery,
      viewMode,
      currentPage,
      employees,
      toggleFollow,
      removeFilter,
      toggleFilters,
      fetchEmployees,
      viewEmployeeDetail,
      loading,
      error
    }
  }
})
</script>

<style scoped>
.employee-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.header-actions {
  margin-top: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  width: 300px;
}

.filter-tags {
  display: flex;
  gap: 8px;
}

.filter-tag {
  background-color: #e2e8f0;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.table-responsive {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 2px solid #e2e8f0;
  font-weight: 500;
  color: #4a5568;
}

.table td {
  padding: 12px 15px;
  border-bottom: 1px solid #e2e8f0;
  vertical-align: middle;
}

.avatar-col {
  width: 50px;
}

.employee-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.employee-name {
  font-weight: 500;
  color: #2d3748;
}

.employee-location {
  font-size: 12px;
  color: #718096;
  margin-top: 2px;
}

.email-link {
  color: #4a5568;
  text-decoration: none;
}

.email-link:hover {
  text-decoration: underline;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background-color: #9ae6b4;
  color: #22543d;
}

.text-center {
  text-align: center;
}

.btn-follow {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  background-color: #667eea;
  color: white;
  border: none;
  cursor: pointer;
}

.btn-follow:hover {
  background-color: #5a67d8;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.record-info {
  color: #718096;
}

.pagination-controls {
  display: flex;
  gap: 8px;
}

.btn-nav {
  padding: 8px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #718096;
  cursor: pointer;
}

.btn-nav:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .header-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    width: 100%;
  }
  
  .view-controls {
    justify-content: space-between;
  }
}
</style>
