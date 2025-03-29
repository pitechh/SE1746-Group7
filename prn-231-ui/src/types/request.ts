export interface Request {
    id: number;
    date?: string;
    check_in?: string;
    check_out?: string;
    from_date?: string;
    to_date?: string;
    hours?: number;
    evidence?: string;
    reason?: string;
    custom_reason?: string;
    message?: string;
    to_approve: string;
    cc: string;
    status: string;
    type_request_id: number;
    employee_id: number;
    created_at: string;
    updated_at: string;
    is_deleted: number;
    deleted_at: null;
  }
  
  export interface ComplaintRequest {
    date: string;
    check_in: string;
    check_out: string;
    evidence: string;
    to_approve: string;
    cc: string;
    status: string;
    type_request_id: number;
  }

  export const REQUEST_TYPES = {
    1: 'Nghỉ phép',
    2: 'Nghỉ không lương',
    3: 'Điều chỉnh công'
  } as const;