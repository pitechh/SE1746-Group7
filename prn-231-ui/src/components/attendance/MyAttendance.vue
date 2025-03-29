<template>
    <div class="my-attendance">
      <div class="page-header">
        <h2>Chấm công của tôi</h2>
        
        <div class="filter-section">
          <div class="date-filter">
            <select v-model="selectedMonth" class="form-control">
              <option v-for="month in months" :key="month.value" :value="month.value">
                {{ month.label }}
              </option>
            </select>
            <select v-model="selectedYear" class="form-control">
              <option v-for="year in years" :key="year" :value="year">
                {{ year }}
              </option>
            </select>
          </div>
  
          <div class="summary-stats">
            <div class="stat-item">
              <span class="stat-label">Đi muộn:</span>
              <span class="stat-value late">{{ lateCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">Về sớm:</span>
              <span class="stat-value early">{{ earlyCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">Đúng giờ:</span>
              <span class="stat-value ontime">{{ ontimeCount }}</span>
            </div>
          </div>
        </div>
      </div>
  
      <div class="attendance-content">
        <div v-if="loading" class="loading">
          <i class="fas fa-spinner fa-spin"></i> Đang tải...
        </div>
  
        <div v-else-if="error" class="error-message">
          {{ error }}
        </div>
  
        <div v-else-if="filteredAttendance.length === 0" class="no-data">
          Không có dữ liệu chấm công trong tháng này
        </div>
  
        <div v-else class="attendance-table">
          <table>
            <thead>
              <tr>
                <th>Ngày</th>
                <th>Check-in</th>
                <th>Check-out</th>
                <th>Trạng thái</th>
                <th>Ghi chú</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="record in filteredAttendance" 
                  :key="record.id"
                  :class="getRowClass(record)"
              >
                <td>{{ formatDate(record.date) }}</td>
                <td>{{ formatTime(record.check_in) }}</td>
                <td>{{ formatTime(record.check_out) }}</td>
                <td>
                  <span :class="getStatusClass(record)">
                    {{ getStatusText(record) }}
                  </span>
                </td>
                <td>{{ record.warning || '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, ref, computed, onMounted } from 'vue'
  import { AttendanceService } from '@/services/attendance.service'
  import type { Attendance } from '@/types/attendance'
  
  export default defineComponent({
    name: 'MyAttendance',
  
    setup() {
      const attendance = ref<Attendance[]>([])
      const loading = ref(true)
      const error = ref('')
  
      const currentDate = new Date()
      const selectedMonth = ref(currentDate.getMonth() + 1)
      const selectedYear = ref(currentDate.getFullYear())
  
      // Tạo danh sách tháng
      const months = computed(() => {
        return Array.from({ length: 12 }, (_, i) => ({
          value: i + 1,
          label: `Tháng ${i + 1}`
        }))
      })
  
      // Tạo danh sách năm (từ năm hiện tại trở về 2 năm trước)
      const years = computed(() => {
        const currentYear = currentDate.getFullYear()
        return [currentYear - 2, currentYear - 1, currentYear]
      })
  
      // Lọc attendance theo tháng và năm
      const filteredAttendance = computed(() => {
        return attendance.value.filter(record => {
          const date = new Date(record.date)
          return date.getMonth() + 1 === selectedMonth.value && 
                 date.getFullYear() === selectedYear.value
        })
      })
  
      // Tính toán thống kê
      const lateCount = computed(() => {
        return filteredAttendance.value.filter(record => {
          const checkIn = new Date(`2000-01-01T${record.check_in}`)
          return checkIn.getHours() >= 9 && checkIn.getMinutes() > 0
        }).length
      })
  
      const earlyCount = computed(() => {
        return filteredAttendance.value.filter(record => {
          if (!record.check_out) return false
          const checkOut = new Date(`2000-01-01T${record.check_out}`)
          return checkOut.getHours() < 17
        }).length
      })
  
      const ontimeCount = computed(() => {
        return filteredAttendance.value.filter(record => {
          if (!record.check_out) return false
          const checkIn = new Date(`2000-01-01T${record.check_in}`)
          const checkOut = new Date(`2000-01-01T${record.check_out}`)
          return checkIn.getHours() < 9 && checkOut.getHours() >= 17
        }).length
      })
  
      const fetchAttendance = async () => {
        try {
          loading.value = true
          error.value = ''
          const data = await AttendanceService.getMyAttendance()
          attendance.value = data
        } catch (err: any) {
          console.error('Error fetching attendance:', err)
          error.value = 'Không thể tải dữ liệu chấm công'
        } finally {
          loading.value = false
        }
      }
  
      const formatDate = (date: string) => {
        return new Date(date).toLocaleDateString('vi-VN', {
          weekday: 'long',
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        })
      }
  
      const formatTime = (time: string | null) => {
        if (!time) return '--:--'
        return time.substring(0, 5)
      }
  
      const getStatusText = (record: Attendance) => {
        if (!record.check_out) return 'Chưa check-out'
        
        const checkIn = new Date(`2000-01-01T${record.check_in}`)
        const checkOut = new Date(`2000-01-01T${record.check_out}`)
  
        if (checkIn.getHours() >= 9 && checkIn.getMinutes() > 0) {
          return 'Đi muộn'
        }
        if (checkOut.getHours() < 17) {
          return 'Về sớm'
        }
        return 'Đúng giờ'
      }
  
      const getStatusClass = (record: Attendance) => {
        const status = getStatusText(record)
        return {
          'status-late': status === 'Đi muộn',
          'status-early': status === 'Về sớm',
          'status-ontime': status === 'Đúng giờ',
          'status-pending': status === 'Chưa check-out'
        }
      }
  
      const getRowClass = (record: Attendance) => {
        return {
          'row-warning': record.warning
        }
      }
  
      onMounted(fetchAttendance)
  
      return {
        attendance,
        loading,
        error,
        selectedMonth,
        selectedYear,
        months,
        years,
        filteredAttendance,
        lateCount,
        earlyCount,
        ontimeCount,
        formatDate,
        formatTime,
        getStatusText,
        getStatusClass,
        getRowClass
      }
    }
  })
  </script>
  
  <style scoped>
  .my-attendance {
    padding: 20px;
  }
  
  .page-header {
    margin-bottom: 30px;
  }
  
  .filter-section {
    margin-top: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .date-filter {
    display: flex;
    gap: 10px;
  }
  
  .form-control {
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    min-width: 120px;
  }
  
  .summary-stats {
    display: flex;
    gap: 20px;
  }
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .stat-label {
    font-weight: 500;
    color: #4a5568;
  }
  
  .stat-value {
    padding: 4px 8px;
    border-radius: 12px;
    font-weight: 600;
  }
  
  .late { background: #fed7d7; color: #c53030; }
  .early { background: #feebc8; color: #9c4221; }
  .ontime { background: #c6f6d5; color: #276749; }
  
  .attendance-table {
    overflow-x: auto;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    background: white;
    border-radius: 8px;
    overflow: hidden;
  }
  
  th, td {
    padding: 12px 16px;
    text-align: left;
    border-bottom: 1px solid #e2e8f0;
  }
  
  th {
    background: #f7fafc;
    font-weight: 600;
    color: #4a5568;
  }
  
  tr:hover {
    background: #f7fafc;
  }
  
  .row-warning {
    background: #fff5f5;
  }
  
  .status-late,
  .status-early,
  .status-ontime,
  .status-pending {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 14px;
  }
  
  .status-late { background: #fed7d7; color: #c53030; }
  .status-early { background: #feebc8; color: #9c4221; }
  .status-ontime { background: #c6f6d5; color: #276749; }
  .status-pending { background: #e2e8f0; color: #4a5568; }
  
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
      align-items: stretch;
    }
  
    .summary-stats {
      flex-direction: column;
      gap: 10px;
    }
  
    .attendance-table {
      margin-top: 20px;
    }
  }
  </style>