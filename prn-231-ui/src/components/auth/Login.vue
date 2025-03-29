<template>
  <div class="login-container">
    <div class="login-card">
      <h2>Đăng nhập</h2>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group" :class="{ 'has-error': errors.email }">
          <label for="email">Email</label>
          <input
            type="email"
            id="email"
            v-model="email"
            class="form-control"
            placeholder="Nhập email"
            required
            :disabled="loading"
          >
          <span class="error-text" v-if="errors.email">{{ errors.email }}</span>
        </div>

        <div class="form-group" :class="{ 'has-error': errors.password }">
          <label for="password">Mật khẩu</label>
          <div class="password-input">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              v-model="password"
              class="form-control"
              placeholder="Nhập mật khẩu"
              required
              :disabled="loading"
            >
            <button 
              type="button" 
              class="toggle-password"
              @click="showPassword = !showPassword"
            >
              <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
            </button>
          </div>
          <span class="error-text" v-if="errors.password">{{ errors.password }}</span>
        </div>

        <div class="form-group">
          <label class="checkbox-container">
            <input 
              type="checkbox" 
              v-model="rememberMe"
              :disabled="loading"
            >
            <span class="checkmark"></span>
            Ghi nhớ đăng nhập
          </label>
        </div>

        <div class="alert alert-danger" v-if="error">
          {{ error }}
        </div>

        <button 
          type="submit" 
          class="btn btn-primary btn-block"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner">
            <i class="fas fa-spinner fa-spin"></i>
          </span>
          <span v-else>Đăng nhập</span>
        </button>
      </form>

      <div class="login-footer">
        <router-link to="/forgot-password" class="forgot-password">
          Quên mật khẩu?
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { AuthService } from '@/services/auth.service'

export default defineComponent({
  name: 'Login',

  setup() {
    const router = useRouter()
    const email = ref('')
    const password = ref('')
    const rememberMe = ref(false)
    const showPassword = ref(false)
    const loading = ref(false)
    const error = ref('')
    const errors = reactive({
      email: '',
      password: ''
    })

    const validateForm = () => {
      let isValid = true
      errors.email = ''
      errors.password = ''

      if (!email.value) {
        errors.email = 'Email không được để trống'
        isValid = false
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
        errors.email = 'Email không hợp lệ'
        isValid = false
      }

      if (!password.value) {
        errors.password = 'Mật khẩu không được để trống'
        isValid = false
      } else if (password.value.length < 6) {
        errors.password = 'Mật khẩu phải có ít nhất 6 ký tự'
        isValid = false
      }

      return isValid
    }

    const handleLogin = async () => {
      if (!validateForm()) return

      try {
        loading.value = true
        error.value = ''

        const response = await AuthService.login({
          email: email.value,
          password: password.value
        })

        if (rememberMe.value) {
          localStorage.setItem('rememberMe', 'true')
        }

        // router.push('/dashboard')
        window.location.href = '/dashboard'
      } catch (err: any) {
        console.error('Login error:', err)
        error.value = err.response?.data?.message || 'Đăng nhập thất bại'
      } finally {
        loading.value = false
      }
    }

    return {
      email,
      password,
      rememberMe,
      showPassword,
      loading,
      error,
      errors,
      handleLogin
    }
  }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7fafc;
  padding: 20px;
}

.login-card {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #2d3748;
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
  border-radius: 6px;
  font-size: 16px;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.password-input {
  position: relative;
}

.toggle-password {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #a0aec0;
  cursor: pointer;
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}

.btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.spinner {
  margin-right: 8px;
}

.has-error .form-control {
  border-color: #e53e3e;
}

.error-text {
  color: #e53e3e;
  font-size: 14px;
  margin-top: 4px;
}

.alert {
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.alert-danger {
  background: #fed7d7;
  color: #c53030;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}

.forgot-password {
  color: #667eea;
  text-decoration: none;
}

.forgot-password:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-card {
    padding: 20px;
  }
}
</style>