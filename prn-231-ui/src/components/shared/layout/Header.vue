<template>
  <header  class="header">
    <nav class="navbar navbar-expand-lg">
      <div class="container-fluid">
        <!-- Logo/Brand -->
        <router-link to="/" class="navbar-brand">
          <i class="fas fa-clock"></i>
          <span>HR System</span>
        </router-link>

        <!-- Toggle Button -->
        <button
          class="navbar-toggler"
          type="button"
          @click="isMenuOpen = !isMenuOpen"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <!-- Navigation Menu -->
        <div
          class="collapse navbar-collapse"
          :class="{ 'show': isMenuOpen }"
        >
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <!-- Menu Items -->
            <li class="nav-item">
              <router-link to="/dashboard" class="nav-link">
                <i class="fas fa-home"></i> Dashboard
              </router-link>
            </li>
            <li class="nav-item">
              <router-link to="/attendance" class="nav-link">
                <i class="fas fa-calendar-check"></i> Chấm công
              </router-link>
            </li>
            <li class="nav-item">
              <router-link to="/complaints" class="nav-link">
                <i class="fas fa-comment-alt"></i> Khiếu nại
              </router-link>
            </li>
            <!-- Admin Menu -->
            <li class="nav-item dropdown" v-if="isAdmin">
              <a
                class="nav-link dropdown-toggle"
                href="#"
                @click.prevent="isAdminMenuOpen = !isAdminMenuOpen"
              >
                <i class="fas fa-user-shield"></i> Quản lý
              </a>
              <ul 
                class="dropdown-menu"
                :class="{ 'show': isAdminMenuOpen }"
              >
                <li>
                  <router-link to="/admin/employees" class="dropdown-item">
                    Quản lý nhân viên
                  </router-link>
                </li>
                <li>
                  <router-link to="/admin/departments" class="dropdown-item">
                    Quản lý phòng ban
                  </router-link>
                </li>
                <li>
                  <router-link to="/admin/reports" class="dropdown-item">
                    Báo cáo
                  </router-link>
                </li>
              </ul>
            </li>
          </ul>

          <!-- Right Menu -->
          <div class="navbar-nav ms-auto">
            <!-- Notifications -->
            <div class="nav-item dropdown">
              <a
                class="nav-link"
                href="#"
                @click.prevent="isNotifyOpen = !isNotifyOpen"
              >
                <i class="fas fa-bell"></i>
                <span v-if="notificationCount" class="badge bg-danger">
                  {{ notificationCount }}
                </span>
              </a>
              <div
                class="dropdown-menu dropdown-menu-end notification-menu"
                :class="{ 'show': isNotifyOpen }"
              >
                <div class="notification-header">
                  <h6>Thông báo</h6>
                  <a href="#" @click.prevent="markAllAsRead">Đánh dấu đã đọc</a>
                </div>
                <div class="notification-list">
                  <a
                    v-for="notification in notifications"
                    :key="notification.id"
                    href="#"
                    class="dropdown-item"
                    :class="{ 'unread': !notification.read }"
                  >
                    {{ notification.message }}
                  </a>
                </div>
              </div>
            </div>

            <!-- User Menu -->
            <div class="nav-item dropdown">
              <a
                class="nav-link user-menu"
                href="#"
                @click.prevent="isUserMenuOpen = !isUserMenuOpen"
              >
                <img
                  :src="userAvatar"
                  alt="User Avatar"
                  class="user-avatar"
                />
                <span class="user-name">{{ userName }}</span>
              </a>
              <ul
                class="dropdown-menu dropdown-menu-end"
                :class="{ 'show': isUserMenuOpen }"
              >
                <li>
                  <router-link to="/profile" class="dropdown-item">
                    <i class="fas fa-user"></i> Hồ sơ
                  </router-link>
                </li>
                <li>
                  <router-link to="/settings" class="dropdown-item">
                    <i class="fas fa-cog"></i> Cài đặt
                  </router-link>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li>
                  <a href="#" class="dropdown-item" @click="handleLogout">
                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                  </a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </header>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { AuthService } from '@/services/auth.service'

export default defineComponent({
  name: 'Header',
  
  setup() {
    const router = useRouter()
    const isMenuOpen = ref(false)
    const isAdminMenuOpen = ref(false)
    const isNotifyOpen = ref(false)
    const isUserMenuOpen = ref(false)

    // Mock data - thay thế bằng dữ liệu thực tế sau
    const isAdmin = ref(true)
    const userName = ref('John Doe')
    const userAvatar = ref('')
    const notifications = ref([
      { id: 1, message: 'Bạn có yêu cầu chấm công mới', read: false },
      { id: 2, message: 'Có khiếu nại mới cần xử lý', read: false }
    ])

    const notificationCount = computed(() => {
      return notifications.value.filter(n => !n.read).length
    })

    const markAllAsRead = () => {
      notifications.value.forEach(n => n.read = true)
    }

    const handleLogout = async () => {
      // Xử lý logout
      AuthService.logout()
      window.location.href = '/login'
      // await router.push('/login')
    }

    const isAuthenticated = computed(() => {
      return AuthService.isAuthenticated()
    })

    return {
      isMenuOpen,
      isAdminMenuOpen,
      isNotifyOpen,
      isUserMenuOpen,
      isAdmin,
      userName,
      userAvatar,
      notifications,
      notificationCount,
      markAllAsRead,
      handleLogout,
      isAuthenticated
    }
  }
})
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.navbar {
  padding: 0.5rem 1rem;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: #2d3748;
}

.navbar-brand i {
  color: #4299e1;
}

.nav-link {
  color: #4a5568;
  padding: 0.5rem 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-link:hover {
  color: #4299e1;
}

.nav-link i {
  font-size: 1.1rem;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  font-weight: 500;
}

.notification-menu {
  width: 300px;
  padding: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.notification-header h6 {
  margin: 0;
  font-weight: 600;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.notification-list .dropdown-item {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #f7fafc;
}

.notification-list .unread {
  background-color: #ebf8ff;
}

.badge {
  position: absolute;
  top: 0;
  right: 0;
  font-size: 0.7rem;
  padding: 0.25rem 0.5rem;
}

/* Responsive */
@media (max-width: 992px) {
  .navbar-collapse {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    padding: 1rem;
    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  }
}
</style>
