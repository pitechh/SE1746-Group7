export interface Attendance {
  id: number;
  date: string;
  check_in: string;
  check_out: string;
  warning: string | null;
  employee_id: number;
  created_at: string;
  updated_at: string;
  is_deleted: number;
  deleted_at: string | null;
}