<template>
    <div class="complaint-page">
      <!-- Header Section -->
      <div class="page-header">
        <h2>Danh sách yêu cầu</h2>
        <button 
          class="btn btn-create"
          @click="createNewRequest"
        >
          <i class="fas fa-plus"></i>
          <span>Tạo yêu cầu mới</span>
        </button>
        <div class="header-actions">
          <div class="filter-controls">
            <div class="filter-buttons">
              <button 
                v-for="type in requestTypes" 
                :key="type.id"
                class="btn" 
                :class="{ 'btn-active': currentType === type.id }"
                @click="filterByType(type.id)"
              >
                <i :class="type.icon"></i> {{ type.name }}
              </button>
            </div>
  
            <div class="search-box">
              <input 
                type="text" 
                class="form-control" 
                placeholder="Tìm kiếm..." 
                v-model="searchQuery"
              >
              <i class="fas fa-search"></i>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Table Section -->
      <div class="table-container">
        <table class="table">
          <thead>
            <tr>
              <th>Loại yêu cầu</th>
              <th>Nhân viên</th>
              <th>Thời gian</th>
              <th>Lý do</th>
              <th>Người duyệt</th>
              <th>CC</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="8" class="text-center">
                <div class="loading-spinner">
                  <i class="fas fa-spinner fa-spin"></i> Đang tải...
                </div>
              </td>
            </tr>
            <tr v-else-if="error">
              <td colspan="8" class="text-center text-danger">
                {{ error }}
              </td>
            </tr>
            <tr v-else v-for="complaint in filteredComplaints" :key="complaint.id">
              <td>
                <span class="type-badge" :class="getTypeClass(complaint.type_request_id)">
                  {{ getTypeName(complaint.type_request_id) }}
                </span>
              </td>
              <td>
                <div class="employee-info">
                  <span class="employee-name">{{ getEmployeeName(complaint.employee_id) }}</span>
                  <span class="employee-code">{{ getEmployeeCode(complaint.employee_id) }}</span>
                </div>
              </td>
              <td>
                <div class="time-info">
                  <template v-if="complaint.date">
                    {{ formatDate(complaint.date) }}
                    <div class="time-detail">
                      {{ formatTime(complaint.check_in) }} - {{ formatTime(complaint.check_out) }}
                    </div>
                  </template>
                  <template v-else-if="complaint.from_date">
                    {{ formatDate(complaint.from_date) }}
                    <template v-if="complaint.to_date && complaint.to_date !== complaint.from_date">
                      - {{ formatDate(complaint.to_date) }}
                    </template>
                    <div class="time-detail">
                      {{ complaint.hours }} giờ
                    </div>
                  </template>
                </div>
              </td>
              <td>
                <div class="reason-text">
                  {{ getReasonText(complaint) }}
                </div>
                <div class="evidence-text" v-if="complaint.evidence">
                  <i class="fas fa-paperclip"></i> {{ complaint.evidence }}
                </div>
              </td>
              <td>{{ complaint.to_approve }}</td>
              <td>{{ complaint.cc }}</td>
              <td>
                <span class="status-badge" :class="getStatusClass(complaint.status)">
                  {{ getStatusText(complaint.status) }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <button 
                    class="btn btn-icon btn-info" 
                    @click="viewDetail(complaint)"
                    title="Xem chi tiết"
                  >
                    <i class="fas fa-eye">detail</i>
                  </button>
                  <button 
                    class="btn btn-icon btn-success" 
                    @click="approveRequest(complaint)"
                    title="Phê duyệt"
                  >
                    <i class="fas fa-check">Approve</i>
                  </button>
                  <button 
                    class="btn btn-icon btn-danger" 
                    @click="rejectRequest(complaint)"
                    title="Từ chối"
                  >
                    <i class="fas fa-times">Reject</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, ref, computed, onMounted } from 'vue'
  import type { Complaint } from '@/types/complaint'
  import { ComplaintService } from '@/services/complaint.service'
  import { useRoute, useRouter } from 'vue-router'
  import { RequestService } from '@/services/request.service'
  import type { Request } from '@/types/request'

  export default defineComponent({
    name: 'ComplaintList',
  
    setup() {
      const complaints = ref<Complaint[]>([])
      const loading = ref(true)
      const error = ref('')
      const currentType = ref(0)
      const searchQuery = ref('')
      const isProcessing = ref(false)
      const route = useRoute()
      const router = useRouter()

      const requestTypes = [
        { id: 0, name: 'Tất cả', icon: 'fas fa-list' },
        { id: 1, name: 'Nghỉ phép', icon: 'fas fa-umbrella-beach' },
        { id: 2, name: 'Nghỉ không lương', icon: 'fas fa-calendar-times' },
        { id: 3, name: 'Điều chỉnh công', icon: 'fas fa-clock' }
      ]
  
      const fetchComplaints = async () => {
        try {
          loading.value = true
          error.value = ''
          const departmentId = Number(route.params.id)
          const response = await ComplaintService.getPendingRequests(departmentId)
          if (response.status === 'success') {
            complaints.value = response.data
          }
        } catch (err: any) {
          console.error('Error fetching complaints:', err)
          error.value = 'Không thể tải danh sách yêu cầu'
        } finally {
          loading.value = false
        }
      }
  
      const filterByType = (typeId: number) => {
        currentType.value = typeId
      }
  
      const formatDate = (date: string) => {
        return new Date(date).toLocaleDateString('vi-VN')
      }
  
      const formatTime = (time: string | undefined) => {
        return time ? time.substring(0, 5) : ''
      }
  
      const getTypeName = (typeId: number) => {
        const type = requestTypes.find(t => t.id === typeId)
        return type ? type.name : 'Không xác định'
      }
  
      const getTypeClass = (typeId: number) => {
        return `type-${typeId}`
      }
  
      const getStatusClass = (status: string) => {
        return `status-${status}`
      }
  
      const getStatusText = (status: string) => {
        switch (status) {
          case 'pending': return 'Chờ duyệt'
          case 'approved': return 'Đã duyệt'
          case 'rejected': return 'Từ chối'
          default: return 'Không xác định'
        }
      }
  
      const getEmployeeName = (employeeId: number) => {
        // Implement employee name lookup
        return `Nhân viên ${employeeId}`
      }
  
      const getEmployeeCode = (employeeId: number) => {
        return `EMP${employeeId.toString().padStart(3, '0')}`
      }
  
      const getReasonText = (complaint: Complaint) => {
        if (complaint.custom_reason) return complaint.custom_reason
        if (complaint.message) return complaint.message
        if (complaint.evidence) return complaint.evidence
        return 'Không có lý do'
      }
  
      const filteredComplaints = computed(() => {
        let result = complaints.value
  
        if (currentType.value !== 0) {
          result = result.filter(c => c.type_request_id === currentType.value)
        }
  
        if (searchQuery.value) {
          const query = searchQuery.value.toLowerCase()
          result = result.filter(c => 
            getEmployeeName(c.employee_id).toLowerCase().includes(query) ||
            getEmployeeCode(c.employee_id).toLowerCase().includes(query) ||
            getReasonText(c).toLowerCase().includes(query)
          )
        }
  
        return result
      })
  
      const viewDetail = (complaint: Complaint) => {
        // Implement view detail logic
        console.log('View detail:', complaint)
      }
  
      const approveRequest = async (complaint: Complaint) => {
      try {
        if (isProcessing.value) return
        
        if (!confirm('Bạn có chắc muốn phê duyệt yêu cầu này?')) {
          return
        }

        isProcessing.value = true
        const departmentId = Number(route.params.id)
        
        await RequestService.updateRequestStatus(departmentId, complaint.id, 'approved')
        
        await fetchComplaints()

      } catch (err: any) {
        console.error('Error approving request:', err)
      } finally {
        isProcessing.value = false
      }
    }
  
      const rejectRequest = async (complaint: Complaint) => {
        try {
        if (isProcessing.value) return
        
        if (!confirm('Bạn có chắc muốn phê duyệt yêu cầu này?')) {
          return
        }

        isProcessing.value = true
        const departmentId = Number(route.params.id)
        
        await RequestService.updateRequestStatus(departmentId, complaint.id, 'approved')
        
        await fetchComplaints()

      } catch (err: any) {
        console.error('Error approving request:', err)
      } finally {
        isProcessing.value = false
      }
      }
      const createNewRequest = () => {
        router.push('/requests/create')
    }
  
      onMounted(fetchComplaints)
  
      return {
        complaints,
        loading,
        error,
        currentType,
        searchQuery,
        requestTypes,
        filteredComplaints,
        filterByType,
        formatDate,
        formatTime,
        getTypeName,
        getTypeClass,
        getStatusClass,
        getStatusText,
        getEmployeeName,
        getEmployeeCode,
        getReasonText,
        viewDetail,
        approveRequest,
        rejectRequest,
        createNewRequest
      }
    }
  })
  </script>
  
  <style scoped>
  .complaint-page {
    padding: 20px;
  }
  
  .page-header {
    margin-bottom: 20px;
  }
  
  .filter-controls {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 15px;
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
    color: #4a5568;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .btn-active {
    background: #667eea;
    color: white;
    border-color: #667eea;
  }
  
  .btn-icon {
    width: 32px;
    height: 32px;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .btn-success {
    background: #48bb78;
    color: white;
  }
  
  .btn-danger {
    background: #e53e3e;
    color: white;
  }
  
  .type-badge {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
  }
  
  .type-1 { background: #bee3f8; color: #2c5282; }
  .type-2 { background: #feebc8; color: #9c4221; }
  .type-3 { background: #e9d8fd; color: #553c9a; }
  
  .status-badge {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
  }
  
  .status-pending { background: #fefcbf; color: #975a16; }
  .status-approved { background: #c6f6d5; color: #276749; }
  .status-rejected { background: #fed7d7; color: #c53030; }
  
  .employee-info {
    display: flex;
    flex-direction: column;
  }
  
  .employee-code {
    font-size: 12px;
    color: #718096;
  }
  
  .time-info {
    display: flex;
    flex-direction: column;
  }
  
  .time-detail {
    font-size: 12px;
    color: #718096;
  }
  
  .reason-text {
    margin-bottom: 4px;
  }
  
  .evidence-text {
    font-size: 12px;
    color: #718096;
  }
  
  .action-buttons {
    display: flex;
    gap: 8px;
  }
  
  @media (max-width: 768px) {
    .filter-controls {
      flex-direction: column;
      gap: 15px;
    }
  
    .filter-buttons {
      width: 100%;
      overflow-x: auto;
    }
  
    .search-box {
      width: 100%;
    }
  }
  </style>