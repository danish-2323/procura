export type Role =
  | 'SUPER_ADMIN'
  | 'EMPLOYEE'
  | 'DEPARTMENT_MANAGER'
  | 'FINANCE_MANAGER'
  | 'PROCUREMENT_OFFICER'
  | 'WAREHOUSE_MANAGER'
  | 'DIRECTOR'
  | 'AUDITOR'
  | 'SUPPLIER';

export interface User {
  id: number;
  email: string;
  fullName: string;
  role: Role;
  departmentId?: number;
  warehouseId?: number;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface ApiError {
  timestamp: string;
  status: number;
  code: string;
  message: string;
  path: string;
  traceId?: string;
  ruleId?: string;
  fieldErrors?: Array<{
    field: string;
    rejectedValue?: string;
    message: string;
  }>;
}
