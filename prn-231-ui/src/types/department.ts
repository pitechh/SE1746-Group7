export interface Manager {
  id: number;
  name: string;
  avatar: string;
}

export interface Department {
  id: number;
  name: string;
  number_of_member: number;
  created_at: string;
  updated_at: string;
  is_deleted: number;
  deleted_at: string | null;
}

export interface DepartmentFilter {
  search?: string;
  company?: string;
  hasManager?: boolean;
}
