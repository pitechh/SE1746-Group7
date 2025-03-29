<template>
  <div class="attendance-page">
    <!-- Header Section -->
    <div class="page-header">
      <h2>Danh sách chấm công</h2>
      <div class="header-actions">
        <!-- Filter Controls -->
        <div class="filter-controls">
          <div class="filter-buttons">
            <button 
              class="btn" 
              :class="{ 'btn-active': currentFilter === 'daily' }"
              @click="changeFilter('daily')"
            >
              <i class="fas fa-calendar-day"></i> Theo ngày
            </button>
            <button 
              class="btn" 
              :class="{ 'btn-active': currentFilter === 'monthly' }"
              @click="changeFilter('monthly')"
            >
              <i class="fas fa-calendar-alt"></i> Theo tháng
            </button>
            <button 
              class="btn" 
              :class="{ 'btn-active': currentFilter === 'yearly' }"
              @click="changeFilter('yearly')"
            >
              <i class="fas fa-calendar"></i> Theo năm
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
            <th>Ngày</th>
            <th>Mã NV</th>
            <th>Tên nhân viên</th>
            <th>Check In</th>
            <th>Check Out</th>
            <th>Trạng thái</th>
            <th>Ghi chú</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="text-center">
              <div class="loading-spinner">
                <i class="fas fa-spinner fa-spin"></i> Đang tải...
              </div>
            </td>
          </tr>
          <tr v-else-if="error">
            <td colspan="7" class="text-center text-danger">
              {{ error }}
            </td>
          </tr>
          <tr v-else v-for="record in filteredAttendances" :key="record.id"
              :class="{ 'late-record': record.warning === 'late' }">
            <td>{{ formatDate(record.date) }}</td>
            <td>{{ record.employee_id }}</td>
            <td>{{ getEmployeeName(record.employee_id) }}</td>
            <td :class="{ 'text-danger': isLate(record.check_in) }">
              {{ formatTime(record.check_in) }}
            </td>
            <td>{{ formatTime(record.check_out) }}</td>
            <td>
              <span class="status-badge" :class="getStatusClass(record)">
                {{ getStatusText(record) }}
              </span>
            </td>
            <td>{{ record.warning || '-' }}</td>
          </tr>
          <tr v-if="!loading && !error && filteredAttendances.length === 0">
            <td colspan="7" class="text-center">
              Không có dữ liệu chấm công
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Summary Section -->
    <div class="summary-section" v-if="!loading && !error">
      <div class="summary-card">
        <div class="summary-title">Tổng số bản ghi</div>
        <div class="summary-value">{{ filteredAttendances.length }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-title">Đi muộn</div>
        <div class="summary-value text-warning">
          {{ getLateCount() }}
        </div>
      </div>
      <div class="summary-card">
        <div class="summary-title">Đúng giờ</div>
        <div class="summary-value text-success">
          {{ getOnTimeCount() }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from 'vue'
import { AttendanceService } from '@/services/attendance.service'
import type { Attendance } from '@/types/attendance'

export default defineComponent({
  name: 'AttendanceList',

  setup() {
    const attendances = ref<Attendance[]>([])
    const loading = ref(true)
    const error = ref('')
    const currentFilter = ref('monthly')
    const searchQuery = ref('')

    const fetchAttendances = async () => {
      try {
        loading.value = true
        error.value = ''
        const response = await AttendanceService.getDepartmentAttendances(1, currentFilter.value)
        if (response.data) {
          attendances.value = response.data
        }
      } catch (err: any) {
        console.error('Error fetching attendances:', err)
        error.value = 'Không thể tải dữ liệu chấm công'
      } finally {
        loading.value = false
      }
    }

    const changeFilter = async (filter: string) => {
      currentFilter.value = filter
      await fetchAttendances()
    }

    const formatDate = (date: string) => {
      return new Date(date).toLocaleDateString('vi-VN')
    }

    const formatTime = (time: string) => {
      return time.substring(0, 5)
    }

    const isLate = (checkIn: string) => {
      const [hours, minutes] = checkIn.split(':').map(Number)
      return hours > 8 || (hours === 8 && minutes > 0)
    }

    const getStatusClass = (record: any) => {
      if (record.warning === 'late') return 'status-late'
      return 'status-normal'
    }

    const getStatusText = (record: any) => {
      if (record.warning === 'late') return 'Đi muộn'
      return 'Đúng giờ'
    }

    const getEmployeeName = (employeeId: number) => {
      // Implement employee name lookup
      return `Nhân viên ${employeeId}`
    }

    const filteredAttendances = computed(() => {
      if (!searchQuery.value) return attendances.value
      
      return attendances.value.filter(record => 
        getEmployeeName(record.employee_id).toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        record.date.includes(searchQuery.value)
      )
    })

    const getLateCount = () => {
      return filteredAttendances.value.filter(record => record.warning === 'late').length
    }

    const getOnTimeCount = () => {
      return filteredAttendances.value.filter(record => !record.warning).length
    }

    onMounted(fetchAttendances)

    return {
      attendances,
      loading,
      error,
      currentFilter,
      searchQuery,
      filteredAttendances,
      changeFilter,
      formatDate,
      formatTime,
      isLate,
      getStatusClass,
      getStatusText,
      getEmployeeName,
      getLateCount,
      getOnTimeCount
    }
  }
})
</script>

<style scoped>
.attendance-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.header-actions {
  margin-top: 15px;
}

.filter-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th {
  background: #f7fafc;
  padding: 12px;
  text-align: left;
  font-weight: 500;
  color: #4a5568;
  border-bottom: 2px solid #e2e8f0;
}

.table td {
  padding: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.late-record {
  background-color: #fff5f5;
}

.text-danger {
  color: #e53e3e;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-late {
  background: #fed7d7;
  color: #c53030;
}

.status-normal {
  background: #c6f6d5;
  color: #2f855a;
}

.loading-spinner {
  padding: 20px;
  text-align: center;
  color: #4a5568;
}

.summary-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.summary-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.summary-title {
  color: #4a5568;
  font-size: 14px;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 24px;
  font-weight: 600;
  color: #2d3748;
}

.text-warning {
  color: #d69e2e;
}

.text-success {
  color: #38a169;
}

@media (max-width: 768px) {
  .filter-controls {
    flex-direction: column;
    gap: 15px;
  }

  .search-box {
    width: 100%;
  }

  .filter-buttons {
    width: 100%;
    justify-content: space-between;
  }
}
</style>