export interface Complaint {
    id: number;
    date?: string;
    from_date?: string;
    to_date?: string;
    check_in?: string;
    check_out?: string;
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
    deleted_at: string | null;
  }