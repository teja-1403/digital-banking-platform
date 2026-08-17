export interface AdminUserStats {
  totalUsers: number;
}

export interface AdminAccountStats {
  totalCustomers: number;
  totalAccounts: number;
  activeAccounts: number;
  totalBalance: number;
}

export interface AdminTransactionStats {
  totalTransactions: number;
  completedTransactions: number;
  failedTransactions: number;
  totalTransactionVolume: number;
}
