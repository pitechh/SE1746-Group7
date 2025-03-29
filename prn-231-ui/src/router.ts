import { createWebHistory, createRouter } from "vue-router";
import { RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import("./components/auth/Login.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: '/departments/:id/attendance',
    name: 'Attendance',
    component: () => import("./components/attendance/AttendanceList.vue"),
  },
  {
    path: '/leaves',
    name: 'LeaveRequests',
    component: () => import("./components/leave/LeaveRequestList.vue"),
  },
  {
    path: '/departments',
    name: 'Departments',
    component: () => import("./components/department/DepartmentList.vue"),
  },
  {
    path: '/departments/:id/members',
    name: 'Employees',
    component: () => import("./components/employee/EmployeeList.vue"),
  },
  {
    path: '/employees/:id',
    name: 'EmployeeDetail',
    component: () => import("./components/employee/EmployeeDetail.vue"),
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import("./components/employee/EmployeeDetail.vue"),
  },
  {
    path: '/departments/:id/complaints',
    name: 'Complaints',
    component: () => import("./components/complaint/ComplaintList.vue"),
  },
  {
    path: '/departments/:id/requests',
    name: 'Requests',
    component: () => import("./components/request/requestList.vue"),
  },
  {
    path:'/attendance',
    name: 'Attendance',
    component: () => import("./components/attendance/MyAttendance.vue"),
  },
  {
    path: '/requests/create',
    name: 'CreateComplaintRequest',
    component: () => import("./components/complaint/CreateComlainRequest.vue"),
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

const isValidToken = (): boolean => {
  const token = localStorage.getItem('token')
  if (!token) return false

  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    if (payload.exp && payload.exp < Date.now() / 1000) {
      localStorage.removeItem('token')
      return false
    }
    return true
  } catch {
    localStorage.removeItem('token')
    return false
  }
}

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth) {
    if (!isValidToken()) {
      next({
        path: '/login',
        query: { redirect: to.fullPath } 
      })
    } else {
      next()
    }
  } else {
    if (to.path === '/login' && isValidToken()) {
      next({ path: '/' })
    } else {
      next()
    }
  }
})

export default router;
