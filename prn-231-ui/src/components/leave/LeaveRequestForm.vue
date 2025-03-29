<template>
  <div class="modal-overlay" v-if="show" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h2>{{ isEdit ? 'Edit Request' : 'New Request' }}</h2>
        <button class="close-button" @click="handleClose">&times;</button>
      </div>

      <div class="modal-body">
        <div class="status-tabs">
          <span class="status-tab" :class="{ active: status === 'Draft' }">Draft</span>
          <span class="status-tab" :class="{ active: status === 'Submitted' }">Submitted</span>
          <span class="status-tab" :class="{ active: status === 'Approved' }">Approved</span>
        </div>

        <div class="form-content">
          <div class="form-group">
            <label>Employee</label>
            <input type="text" v-model="form.employee" class="form-control" disabled />
          </div>

          <div class="form-group">
            <label>Department</label>
            <input type="text" v-model="form.department" class="form-control" disabled />
          </div>

          <div class="form-group">
            <label>Leave Type</label>
            <select v-model="form.leaveType" class="form-control">
              <option value="">Select the leave type</option>
              <option v-for="type in leaveTypes" :key="type.id" :value="type.value">
                {{ type.label }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>
              <input type="checkbox" v-model="form.isHalfDay" />
              Nghỉ phép trong ngày
            </label>
          </div>

          <div class="form-row">
            <div class="form-group col">
              <label>From</label>
              <input type="datetime-local" v-model="form.fromDate" class="form-control" />
            </div>
            <div class="form-group col">
              <label>To</label>
              <input type="datetime-local" v-model="form.toDate" class="form-control" />
            </div>
          </div>

          <div class="form-group">
            <label>Reason</label>
            <select v-model="form.reason" class="form-control">
              <option value="">Select the reason</option>
              <option v-for="reason in reasons" :key="reason.id" :value="reason.value">
                {{ reason.label }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>To Approvers</label>
            <select v-model="form.approvers" class="form-control" multiple>
              <option v-for="approver in approvers" :key="approver.id" :value="approver.id">
                {{ approver.name }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Cc</label>
            <textarea v-model="form.cc" class="form-control" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>Message</label>
            <textarea v-model="form.message" class="form-control" rows="4"></textarea>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-primary" @click="handleSave">Save</button>
        <button class="btn btn-secondary" @click="handleClose">Discard</button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from 'vue'

interface LeaveForm {
  employee: string;
  department: string;
  leaveType: string;
  isHalfDay: boolean;
  fromDate: string;
  toDate: string;
  reason: string;
  approvers: string[];
  cc: string;
  message: string;
}

export default defineComponent({
  name: 'LeaveRequestForm',
  
  props: {
    show: {
      type: Boolean,
      required: true
    },
    isEdit: {
      type: Boolean,
      default: false
    },
    initialData: {
      type: Object,
      default: () => ({})
    }
  },

  emits: ['update:show', 'save'],

  setup(props, { emit }) {
    const status = ref('Draft')

    const form = reactive<LeaveForm>({
      employee: 'Nguyễn Văn Phúc',
      department: 'HB2',
      leaveType: '',
      isHalfDay: false,
      fromDate: '',
      toDate: '',
      reason: '',
      approvers: [],
      cc: '',
      message: ''
    })

    const leaveTypes = [
      { id: 1, value: 'annual', label: 'Annual Leave' },
      { id: 2, value: 'sick', label: 'Sick Leave' },
      { id: 3, value: 'personal', label: 'Personal Leave' }
    ]

    const reasons = [
      { id: 1, value: 'sick', label: 'Bị ốm' },
      { id: 2, value: 'personal', label: 'Bận việc cá nhân' },
      { id: 3, value: 'family', label: 'Nghỉ việc gia đình (bố/mẹ chồng/vợ mất)' },
      { id: 4, value: 'maternity', label: 'Nghỉ thai sản' },
      { id: 5, value: 'other', label: 'Khác' }
    ]

    const approvers = [
      { id: 1, name: 'Manager 1' },
      { id: 2, name: 'Manager 2' },
      { id: 3, name: 'HR Manager' }
    ]

    const handleSave = () => {
      emit('save', form)
      handleClose()
    }

    const handleClose = () => {
      emit('update:show', false)
    }

    return {
      status,
      form,
      leaveTypes,
      reasons,
      approvers,
      handleSave,
      handleClose
    }
  }
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #718096;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.status-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.status-tab {
  padding: 8px 16px;
  border-radius: 4px;
  color: #718096;
}

.status-tab.active {
  background-color: #e2e8f0;
  color: #2d3748;
}

.form-content {
  max-width: 800px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #4a5568;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 14px;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-row {
  display: flex;
  gap: 20px;
}

.col {
  flex: 1;
}

select[multiple] {
  height: 100px;
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.btn-primary {
  background-color: #667eea;
  color: white;
  border: none;
}

.btn-secondary {
  background-color: #e2e8f0;
  color: #4a5568;
  border: none;
}

/* Responsive Design */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
  }
  
  .form-content {
    max-width: 100%;
  }
}
</style>
