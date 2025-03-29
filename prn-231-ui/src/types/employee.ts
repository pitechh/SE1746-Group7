export interface Employee {
  id: number
  code: string
  email: string
  username: string
  fullname: string
  phone: string
  salary: string
  image: string | null
  role_id: number
  department_id: number
  created_at: string
  updated_at: string
  is_deleted: number
  deleted_at: string | null
}

export enum EmployeeStatus {
  Active = 'Active',
  Inactive = 'Inactive',
  OnLeave = 'On Leave',
  Remote = 'Remote'
}

export interface EmployeeFilter {
  search?: string;
  department?: string;
  status?: EmployeeStatus;
  position?: string;
}

export interface ApiResponse<T> {
  status: string
  data: T
  message?: string
}
