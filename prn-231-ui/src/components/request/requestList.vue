<template>
    <div class="request-page">
      <div class="page-header">
        <h2>Danh sách yêu cầu</h2>
        <div class="header-actions">
          <div class="filter-section">
            <div class="filter-buttons">
              <button 
                v-for="(name, id) in REQUEST_TYPES" 
                :key="id"
                class="btn" 
                :class="{ 'btn-active': selectedType === Number(id) }"
                @click="selectedType = Number(id)"
              >
                {{ name }}
              </button>
              <button 
                class="btn" 
                :class="{ 'btn-active': selectedType === 0 }"
                @click="selectedType = 0"
              >
                Tất cả
              </button>
            </div>
  
            <div class="status-filter">
              <select v-model="selectedStatus" class="form-control">
                <option value="">Tất cả trạng thái</option>
                <option value="pending">Chờ duyệt</option>
                <option value="rejected">Từ chối</option>
              </select>
            </div>
  
            <div class="search-box">
              <input 
                type="text" 
                v-model="searchQuery"
                class="form-control" 
                placeholder="Tìm kiếm..."
              >
            </div>
          </div>
        </div>
      </div>
  
      <div class="request-list">
        <div v-if="loading" class="loading">
          <i class="fas fa-spinner fa-spin"></i> Đang tải...
        </div>
  
        <div v-else-if="error" class="error-message">
          {{ error }}
        </div>
  
        <div v-else-if="filteredRequests.length === 0" class="no-data">
          Không có yêu cầu nào
        </div>
  
        <div v-else class="request-cards">
          <div v-for="request in filteredRequests" 
               :key="request.id" 
               class="request-card"
               :class="{ 'rejected': request.status === 'rejected' }"
          >
            <div class="request-header">
              <span class="request-type" :class="`type-${request.type_request_id}`">
                {{ REQUEST_TYPES[request.type_request_id as keyof typeof REQUEST_TYPES] }}
              </span>
              <span class="request-status" :class="`status-${request.status}`">
                {{ getStatusText(request.status) }}
              </span>
            </div>
  
            <div class="request-body">
              <div class="employee-info">
                <strong>{{ getEmployeeName(request.employee_id) }}</strong>
                <span class="employee-code">{{ getEmployeeCode(request.employee_id) }}</span>
              </div>
  
              <div class="time-info">
                <template v-if="request.type_request_id === 3">
                  <!-- Điều chỉnh công -->
                  <div>Ngày: {{ formatDate(request.date) }}</div>
                  <div>
                    {{ formatTime(request.check_in) }} - {{ formatTime(request.check_out) }}
                  </div>
                </template>
                <template v-else>
                  <!-- Nghỉ phép/không lương -->
                  <div>
                    Từ: {{ formatDate(request.from_date) }}
                    <template v-if="request.to_date !== request.from_date">
                      đến {{ formatDate(request.to_date) }}
                    </template>
                  </div>
                  <div>Số giờ: {{ request.hours }}</div>
                </template>
              </div>
  
              <div class="request-reason">
                <strong>Lý do:</strong>
                <p>{{ getReasonText(request) }}</p>
              </div>
  
              <div class="request-meta">
                <div>
                  <strong>Người duyệt:</strong> {{ request.to_approve }}
                </div>
                <div>
                  <strong>CC:</strong> {{ request.cc }}
                </div>
              </div>
            </div>
  
            <div class="request-actions">
              <button 
                class="btn btn-view" 
                @click="viewDetail(request)"
                title="Xem chi tiết"
              >
                <i class="fas fa-eye"></i>
              </button>
              <button 
                v-if="request.status === 'pending'"
                class="btn btn-approve" 
                @click="approveRequest(request)"
                title="Phê duyệt"
              >
                <i class="fas fa-check"></i>
              </button>
              <button 
                v-if="request.status === 'pending'"
                class="btn btn-reject" 
                @click="rejectRequest(request)"
                title="Từ chối"
              >
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, ref, computed, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import type { Request } from '@/types/request'
  import { REQUEST_TYPES } from '@/types/request'
  import { RequestService } from '@/services/request.service'
  
  export default defineComponent({
    name: 'RequestList',
  
    setup() {
      const route = useRoute()
      const router = useRouter()
      const requests = ref<Request[]>([])
      const loading = ref(true)
      const error = ref('')
      const selectedType = ref(0)
      const selectedStatus = ref('')
      const searchQuery = ref('')
  
      const fetchRequests = async () => {
        try {
          loading.value = true
          error.value = ''
          const departmentId = Number(route.params.departmentId)
          const response = await RequestService.getPendingRequests(departmentId)
          if (response.status === 'success') {
            requests.value = response.data
          }
        } catch (err: any) {
          console.error('Error fetching requests:', err)
          error.value = 'Không thể tải danh sách yêu cầu'
        } finally {
          loading.value = false
        }
      }
  
      const filteredRequests = computed(() => {
        let result = requests.value
  
        // Filter by type
        if (selectedType.value !== 0) {
          result = result.filter(r => r.type_request_id === selectedType.value)
        }
  
        // Filter by status
        if (selectedStatus.value) {
          result = result.filter(r => r.status === selectedStatus.value)
        }
  
        // Filter by search
        if (searchQuery.value) {
          const query = searchQuery.value.toLowerCase()
          result = result.filter(r => 
            getEmployeeName(r.employee_id).toLowerCase().includes(query) ||
            getEmployeeCode(r.employee_id).toLowerCase().includes(query) ||
            getReasonText(r).toLowerCase().includes(query)
          )
        }
  
        return result
      })
  
      const formatDate = (date: string | undefined) => {
        if (!date) return ''
        return new Date(date).toLocaleDateString('vi-VN')
      }
  
      const formatTime = (time: string | undefined) => {
        if (!time) return ''
        return time.substring(0, 5)
      }
  
      const getStatusText = (status: string) => {
        const statusMap: Record<string, string> = {
          pending: 'Chờ duyệt',
          approved: 'Đã duyệt',
          rejected: 'Từ chối'
        }
        return statusMap[status] || status
      }
  
      const getEmployeeName = (employeeId: number) => {
        // Implement employee name lookup
        return `Nhân viên ${employeeId}`
      }
  
      const getEmployeeCode = (employeeId: number) => {
        return `EMP${employeeId.toString().padStart(3, '0')}`
      }
  
      const getReasonText = (request: Request) => {
        if (request.custom_reason) return request.custom_reason
        if (request.message) return request.message
        if (request.evidence) return request.evidence
        return 'Không có lý do'
      }
  
      const viewDetail = (request: Request) => {
        router.push(`/requests/${request.id}`)
      }
  
      const approveRequest = async (request: Request) => {
        if (confirm('Bạn có chắc muốn phê duyệt yêu cầu này?')) {
          // Implement approve logic
          console.log('Approve request:', request)
        }
      }
  
      const rejectRequest = async (request: Request) => {
        if (confirm('Bạn có chắc muốn từ chối yêu cầu này?')) {
          // Implement reject logic
          console.log('Reject request:', request)
        }
      }
  
      onMounted(fetchRequests)
  
      return {
        requests,
        loading,
        error,
        selectedType,
        selectedStatus,
        searchQuery,
        filteredRequests,
        REQUEST_TYPES,
        formatDate,
        formatTime,
        getStatusText,
        getEmployeeName,
        getEmployeeCode,
        getReasonText,
        viewDetail,
        approveRequest,
        rejectRequest
      }
    }
  })
  </script>
  
  <style scoped>
  .request-page {
    padding: 20px;
  }
  
  .page-header {
    margin-bottom: 30px;
  }
  
  .filter-section {
    margin-top: 20px;
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
  }
  
  .filter-buttons {
    display: flex;
    gap: 10px;
  }
  
  .btn {
    padding: 8px 16px;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    background: white;
    cursor: pointer;
  }
  
  .btn-active {
    background: #667eea;
    color: white;
    border-color: #667eea;
  }
  
  .form-control {
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    min-width: 200px;
  }
  
  .request-cards {
    display: grid;
    gap: 20px;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  }
  
  .request-card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }
  
  .request-header {
    padding: 15px;
    background: #f7fafc;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .request-type {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
  }
  
  .type-1 { background: #bee3f8; color: #2c5282; }
  .type-2 { background: #feebc8; color: #9c4221; }
  .type-3 { background: #e9d8fd; color: #553c9a; }
  
  .request-status {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
  }
  
  .status-pending { background: #fefcbf; color: #975a16; }
  .status-approved { background: #c6f6d5; color: #276749; }
  .status-rejected { background: #fed7d7; color: #c53030; }
  
  .request-body {
    padding: 15px;
  }
  
  .employee-info {
    margin-bottom: 10px;
  }
  
  .employee-code {
    color: #718096;
    font-size: 14px;
    margin-left: 8px;
  }
  
  .time-info {
    margin-bottom: 10px;
    color: #4a5568;
  }
  
  .request-reason {
    margin-bottom: 10px;
  }
  
  .request-reason p {
    margin-top: 5px;
    color: #4a5568;
  }
  
  .request-meta {
    font-size: 14px;
    color: #718096;
  }
  
  .request-actions {
    padding: 15px;
    border-top: 1px solid #e2e8f0;
    display: flex;
    gap: 10px;
    justify-content: flex-end;
  }
  
  .btn-view { background: #4299e1; color: white; }
  .btn-approve { background: #48bb78; color: white; }
  .btn-reject { background: #e53e3e; color: white; }
  
  .loading,
  .error-message,
  .no-data {
    text-align: center;
    padding: 40px;
    color: #4a5568;
  }
  
  @media (max-width: 768px) {
    .filter-section {
      flex-direction: column;
    }
  
    .filter-buttons {
      overflow-x: auto;
    }
  
    .form-control {
      width: 100%;
    }
  }
  </style>