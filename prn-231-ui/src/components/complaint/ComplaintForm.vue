<template>
  <div class="create-request">
    <h2>Tạo yêu cầu điều chỉnh công</h2>

    <form @submit.prevent="handleSubmit" class="request-form">
      <div class="form-group">
        <label for="date">Ngày cần điều chỉnh <span class="required">*</span></label>
        <input
          type="date"
          id="date"
          v-model="formData.date"
          class="form-control"
          required
          :min="minDate"
          :max="maxDate"
        >
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="check_in">Giờ vào <span class="required">*</span></label>
          <input
            type="time"
            id="check_in"
            v-model="formData.check_in"
            class="form-control"
            required
          >
        </div>

        <div class="form-group">
          <label for="check_out">Giờ ra <span class="required">*</span></label>
          <input
            type="time"
            id="check_out"
            v-model="formData.check_out"
            class="form-control"
            required
          >
        </div>
      </div>

      <div class="form-group">
        <label for="evidence">Lý do điều chỉnh <span class="required">*</span></label>
        <textarea
          id="evidence"
          v-model="formData.evidence"
          class="form-control"
          rows="4"
          required
          placeholder="Vui lòng nêu rõ lý do cần điều chỉnh công..."
        ></textarea>
      </div>

      <div class="form-group">
        <label for="to_approve">Người phê duyệt <span class="required">*</span></label>
        <select
          id="to_approve"
          v-model="formData.to_approve"
          class="form-control"
          required
        >
          <option value="">Chọn người phê duyệt</option>
          <option value="Manager">Quản lý</option>
          <option value="Supervisor">Giám sát</option>
        </select>
      </div>

      <div class="form-group">
        <label for="cc">CC</label>
        <select
          id="cc"
          v-model="formData.cc"
          class="form-control"
          multiple
        >
          <option value="HR">HR</option>
          <option value="Manager">Quản lý</option>
          <option value="Supervisor">Giám sát</option>
        </select>
        <small class="form-text">Giữ Ctrl để chọn nhiều người</small>
      </div>

      <div class="form-actions">
        <button 
          type="button" 
          class="btn btn-secondary" 
          @click="$router.go(-1)"
          :disabled="isSubmitting"
        >
          Hủy
        </button>
        <button 
          type="submit" 
          class="btn btn-primary"
          :disabled="isSubmitting"
        >
          <i class="fas fa-spinner fa-spin" v-if="isSubmitting"></i>
          {{ isSubmitting ? 'Đang gửi...' : 'Gửi yêu cầu' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { RequestService } from '@/services/request.service'
import type { ComplaintRequest } from '@/types/request'

export default defineComponent({
  name: 'CreateComplaintRequest',

  setup() {
    const router = useRouter()
    const isSubmitting = ref(false)

    const formData = ref<ComplaintRequest>({
      date: '',
      check_in: '',
      check_out: '',
      evidence: '',
      to_approve: '',
      cc: 'HR',
      status: 'pending',
      type_request_id: 3 // Điều chỉnh công
    })

    // Giới hạn ngày chọn (30 ngày trước đến hiện tại)
    const minDate = computed(() => {
      const date = new Date()
      date.setDate(date.getDate() - 30)
      return date.toISOString().split('T')[0]
    })

    const maxDate = computed(() => {
      return new Date().toISOString().split('T')[0]
    })

    const handleSubmit = async () => {
      try {
        isSubmitting.value = true

        const checkIn = new Date(`2000-01-01T${formData.value.check_in}`)
        const checkOut = new Date(`2000-01-01T${formData.value.check_out}`)

        if (checkOut <= checkIn) {
          return
        }

        // Format CC array to string
        const formattedData = {
          ...formData.value,
          cc: Array.isArray(formData.value.cc) 
            ? formData.value.cc.join(', ') 
            : formData.value.cc
        }

        await RequestService.createComplaintRequest(formattedData)
        
        router.push('/requests')

      } catch (err: any) {
        console.error('Error submitting request:', err)
      } finally {
        isSubmitting.value = false
      }
    }

    return {
      formData,
      isSubmitting,
      minDate,
      maxDate,
      handleSubmit
    }
  }
})
</script>

<style scoped>
.create-request {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.request-form {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #2d3748;
}

.required {
  color: #e53e3e;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 16px;
}

.form-control:focus {
  border-color: #4299e1;
  outline: none;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.2);
}

textarea.form-control {
  resize: vertical;
}

.form-text {
  font-size: 14px;
  color: #718096;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #4299e1;
  color: white;
  border: none;
}

.btn-primary:hover:not(:disabled) {
  background: #3182ce;
}

.btn-secondary {
  background: #e2e8f0;
  color: #2d3748;
  border: none;
}

.btn-secondary:hover:not(:disabled) {
  background: #cbd5e0;
}

@media (max-width: 640px) {
  .form-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .request-form {
    padding: 16px;
  }
}
</style>