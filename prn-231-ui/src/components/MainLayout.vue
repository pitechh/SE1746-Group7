<template>
    <div class="main-layout" >
      <Header v-if="isAuthenticated" />
      
      <div class="layout-container">
        <!-- Sidebar -->
        <aside class="sidebar" :class="{ 'sidebar-collapsed': isSidebarCollapsed }" v-if="isAuthenticated">
          <div class="sidebar-header">
            <button class="collapse-btn" @click="toggleSidebar">
              <i class="fas fa-bars"></i>
            </button>
          </div>
  
          <nav class="sidebar-nav">
            <ul class="nav-list">
              <!-- Dashboard -->
              <li class="nav-item">
                <router-link to="/dashboard" class="nav-link">
                  <i class="fas fa-home"></i>
                  <span v-show="!isSidebarCollapsed">Dashboard</span>
                </router-link>
              </li>
  
              <!-- Chấm công -->
              <li class="nav-item">
                <router-link to="/attendance" class="nav-link">
                  <i class="fas fa-clock"></i>
                  <span v-show="!isSidebarCollapsed">Chấm công</span>
                </router-link>
              </li>
  
              <!-- Khiếu nại -->
              <li class="nav-item">
                <router-link to="/complaints" class="nav-link">
                  <i class="fas fa-comment-alt"></i>
                  <span v-show="!isSidebarCollapsed">Khiếu nại</span>
                </router-link>
              </li>
              <!-- Yêu cầu rời đi -->
              <li class="nav-item">
                <router-link to="/leaves" class="nav-link">
                  <i class="fas fa-comment-alt"></i>
                  <span v-show="!isSidebarCollapsed">Xin ra ngoài</span>
                </router-link>
              </li>
              <!-- Phòng ban -->
              <li class="nav-item">
                  <router-link to="/departments" class="nav-link">
                    <i class="fas fa-building"></i>
                    <span v-show="!isSidebarCollapsed">Phòng ban</span>
                  </router-link>
                </li>
              <!-- Nhân viên -->
              <li class="nav-item">
                  <router-link to="/employees" class="nav-link">
                    <i class="fas fa-users"></i>
                    <span v-show="!isSidebarCollapsed">Nhân viên</span>
                  </router-link>
                </li>
              <!-- Admin Section -->
              <template v-if="isAdmin">
                <li class="nav-section">
                  <span v-show="!isSidebarCollapsed">Quản trị</span>
                </li>
  
                <li class="nav-item">
                  <router-link to="/admin/reports" class="nav-link">
                    <i class="fas fa-chart-bar"></i>
                    <span v-show="!isSidebarCollapsed">Báo cáo</span>
                  </router-link>
                </li>
              </template>
  
              <!-- Settings Section -->
              <li class="nav-section">
                <span v-show="!isSidebarCollapsed">Cài đặt</span>
              </li>
              
              <li class="nav-item">
                <router-link to="/profile" class="nav-link">
                  <i class="fas fa-user"></i>
                  <span v-show="!isSidebarCollapsed">Hồ sơ</span>
                </router-link>
              </li>
  
              <li class="nav-item">
                <router-link to="/settings" class="nav-link">
                  <i class="fas fa-cog"></i>
                  <span v-show="!isSidebarCollapsed">Cài đặt</span>
                </router-link>
              </li>
            </ul>
          </nav>
        </aside>
  
        <!-- Main Content -->
        <main class="main-content" :class="{ 'content-expanded': isSidebarCollapsed }">
          <div class="content-wrapper">
            <slot></slot>
          </div>
        </main>
      </div>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, ref, computed } from 'vue'
  import { AuthService } from '@/services/auth.service'
  import Header from './shared/layout/Header.vue'
  
  export default defineComponent({
    name: 'MainLayout',
    
    components: {
      Header
    },
  
    setup() {
      const isSidebarCollapsed = ref(false)
      const isAdmin = ref(true) 
  
      const toggleSidebar = () => {
        isSidebarCollapsed.value = !isSidebarCollapsed.value
        localStorage.setItem('sidebarState', isSidebarCollapsed.value.toString())
      }
  
      const savedState = localStorage.getItem('sidebarState')
      if (savedState) {
        isSidebarCollapsed.value = savedState === 'true'
      }
  
      const isAuthenticated = computed(() => {
      return AuthService.isAuthenticated()
    })

      return {
        isSidebarCollapsed,
        isAdmin,
        toggleSidebar,
        isAuthenticated
      }
    }
  })
  </script>
  
  <style scoped>
  .main-layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }
  
  .layout-container {
    flex: 1;
    display: flex;
    background-color: #f7fafc;
  }
  
  /* Sidebar Styles */
  .sidebar {
    width: 250px;
    background: #2d3748;
    color: #fff;
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
  }
  
  .sidebar-collapsed {
    width: 64px;
  }
  
  .sidebar-header {
    padding: 1rem;
    display: flex;
    justify-content: flex-end;
  }
  
  .collapse-btn {
    background: none;
    border: none;
    color: #fff;
    cursor: pointer;
    padding: 0.5rem;
  }
  
  .collapse-btn:hover {
    color: #63b3ed;
  }
  
  .sidebar-nav {
    flex: 1;
  }
  
  .nav-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }
  
  .nav-section {
    padding: 1rem;
    font-size: 0.75rem;
    text-transform: uppercase;
    color: #a0aec0;
    letter-spacing: 0.05em;
  }
  
  .nav-item {
    margin: 0.25rem 0;
  }
  
  .nav-link {
    display: flex;
    align-items: center;
    padding: 0.75rem 1rem;
    color: #e2e8f0;
    text-decoration: none;
    transition: all 0.2s ease;
  }
  
  .nav-link:hover {
    background: #4a5568;
    color: #fff;
  }
  
  .nav-link.router-link-active {
    background: #4299e1;
    color: #fff;
  }
  
  .nav-link i {
    width: 20px;
    text-align: center;
    margin-right: 1rem;
  }
  
  /* Main Content Styles */
  .main-content {
    flex: 1;
    padding: 2rem;
    transition: all 0.3s ease;
  }
  
  .content-expanded {
    margin-left: -186px;
  }
  
  .content-wrapper {
    background: #fff;
    border-radius: 0.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    padding: 1.5rem;
    min-height: calc(100vh - 4rem - 64px);
  }
  
  /* Responsive Design */
  @media (max-width: 768px) {
    .sidebar {
      position: fixed;
      height: 100vh;
      z-index: 1000;
      transform: translateX(-100%);
    }
  
    .sidebar-collapsed {
      transform: translateX(0);
      width: 250px;
    }
  
    .content-expanded {
      margin-left: 0;
    }
  }
  </style>