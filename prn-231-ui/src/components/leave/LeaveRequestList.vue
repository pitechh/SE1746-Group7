<template>
  <div class="leave-request-page">
    <!-- Header Section -->
    <div class="page-header">
      <h2>Request</h2>
      <div class="header-actions">
        <div class="action-buttons">
          <button class="btn btn-primary" @click="openCreateForm">
            Create
          </button>
          <button class="btn btn-secondary" @click="handleImport">
            Import
          </button>
          <button class="btn btn-info" @click="createMenstrualLeaves">
            Create Menstrual Leaves
          </button>
        </div>

        <div class="search-filters">
          <div class="search-box">
            <input 
              type="text" 
              class="form-control" 
              placeholder="Search..." 
              v-model="searchQuery"
            >
          </div>
          
          <div class="filter-tags">
            <span class="filter-tag">
              Active Member
              <button class="remove-tag" @click="removeFilter('activeMember')">×</button>
            </span>
            <span class="filter-tag">
              This Month
              <button class="remove-tag" @click="removeFilter('thisMonth')">×</button>
            </span>
          </div>

          <div class="filter-group">
            <button class="btn btn-filter" @click="toggleFilters">
              <i class="fas fa-filter"></i> Filters
            </button>
            <button class="btn btn-group">
              <i class="fas fa-layer-group"></i> Group By
            </button>
            <button class="btn btn-favorite">
              <i class="far fa-star"></i> Favorites
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!leaveRequests.length" class="empty-state">
      <div class="empty-icon">
        <i class="fas fa-arrow-up fa-2x"></i>
      </div>
      <p>Click to add a new request.</p>
    </div>

    <!-- Table Section -->
    <div v-else class="table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th><input type="checkbox" v-model="selectAll"></th>
            <th>Employee</th>
            <th>Leave Type</th>
            <th>From Date</th>
            <th>To Date</th>
            <th>Days</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="request in leaveRequests" :key="request.id">
            <td><input type="checkbox" v-model="request.selected"></td>
            <td>{{ request.employee }}</td>
            <td>{{ request.leaveType }}</td>
            <td>{{ formatDate(request.fromDate) }}</td>
            <td>{{ formatDate(request.toDate) }}</td>
            <td>{{ request.days }}</td>
            <td>
              <span :class="getStatusBadgeClass(request.status)">
                {{ request.status }}
              </span>
            </td>
            <td>
              <button class="btn btn-sm btn-edit" @click="openEditForm(request)">
                <i class="fas fa-edit"></i>
              </button>
              <button class="btn btn-sm btn-delete" @click="deleteRequest(request)">
                <i class="fas fa-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination-container">
      <span class="record-info">1-80 / 231</span>
      <div class="pagination-controls">
        <button class="btn btn-nav" :disabled="currentPage === 1">
          <i class="fas fa-chevron-left"></i>
        </button>
        <button class="btn btn-nav">
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>

    <!-- Add the form modal -->
    <LeaveRequestForm
      v-model:show="showForm"
      :is-edit="isEditing"
      :initial-data="selectedRequest || {}"
      @save="handleSaveRequest"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive } from 'vue'
import LeaveRequestForm from './LeaveRequestForm.vue'

export default defineComponent({
  name: 'LeaveRequestList',
  
  components: {
    LeaveRequestForm
  },

  setup() {
    const searchQuery = ref('')
    const selectAll = ref(false)
    const currentPage = ref(1)
    const showForm = ref(false)
    const isEditing = ref(false)
    const selectedRequest = ref(null)

    const leaveRequests = reactive([
      {
        id: 1,
        selected: false,
        employee: 'Nguyễn Văn Phúc',
        leaveType: 'Annual Leave',
        fromDate: '2024-03-10',
        toDate: '2024-03-12',
        days: 3,
        status: 'Pending'
      },
      // Thêm các bản ghi khác
    ])

    const formatDate = (date: string) => {
      return new Date(date).toLocaleDateString('en-GB')
    }

    const getStatusBadgeClass = (status: string) => {
      const classes: Record<string, string> = {
        'Pending': 'badge-warning',
        'Approved': 'badge-success', 
        'Rejected': 'badge-danger'
      }
      return `badge ${classes[status] || 'badge-secondary'}`
    }

    const openCreateForm = () => {
      isEditing.value = false
      selectedRequest.value = null
      showForm.value = true
    }

    const openEditForm = (request: any) => {
      isEditing.value = true
      selectedRequest.value = request
      showForm.value = true
    }

    const handleSaveRequest = (formData: any) => {
      console.log('Save request:', formData)
      // Implement save logic here
    }

    const handleImport = () => {
      // Implement import logic
    }

    const createMenstrualLeaves = () => {
      // Implement menstrual leaves creation
    }

    const removeFilter = (filterType: string) => {
      // Implement filter removal logic
    }

    const toggleFilters = () => {
      // Implement filter toggle logic
    }

    const deleteRequest = (request: any) => {
      // Implement delete logic
    }

    return {
      searchQuery,
      selectAll,
      currentPage,
      leaveRequests,
      formatDate,
      getStatusBadgeClass,
      showForm,
      isEditing,
      selectedRequest,
      openCreateForm,
      openEditForm,
      handleSaveRequest,
      handleImport,
      createMenstrualLeaves,
      removeFilter,
      toggleFilters,
      deleteRequest
    }
  }
})
</script>

<style scoped>
.leave-request-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.header-actions {
  margin-top: 20px;
}

.action-buttons {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}

.btn-primary {
  background-color: #4a5568;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
}

.btn-secondary {
  background-color: #718096;
  color: white;
}

.btn-info {
  background-color: #4299e1;
  color: white;
}

.search-filters {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
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

.remove-tag {
  background: none;
  border: none;
  color: #718096;
  cursor: pointer;
  padding: 0 4px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #718096;
}

.empty-icon {
  margin-bottom: 15px;
  color: #a0aec0;
}

.table {
  margin-top: 20px;
}

.badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.badge-warning {
  background-color: #fbd38d;
  color: #744210;
}

.badge-success {
  background-color: #9ae6b4;
  color: #22543d;
}

.badge-danger {
  background-color: #feb2b2;
  color: #822727;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 14px;
}

.btn-edit {
  color: #4a5568;
  margin-right: 5px;
}

.btn-delete {
  color: #e53e3e;
}

/* Responsive Design */
@media (max-width: 768px) {
  .search-filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-tags {
    flex-wrap: wrap;
  }
  
  .action-buttons {
    flex-wrap: wrap;
  }
}
</style>
