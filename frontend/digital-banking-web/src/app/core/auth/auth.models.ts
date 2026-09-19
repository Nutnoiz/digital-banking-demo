export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  username: string;
  roles: string[];
}

export interface Account {
  id: number;
  accountNo: string;
  customerId: number;
  customerName: string;
  accountType: string;
  balance: number;
  currency: string;
  status: string;
}

export interface BankTransaction {
  id: number;
  accountId: number;
  transactionType: string;
  amount: number;
  balanceBefore: number;
  balanceAfter: number;
  referenceNo: string;
  description: string;
  createdAt: string;
}

export interface TransferRequest {
  sourceAccountId: number;
  destinationAccountId: number;
  amount: number;
  description?: string;
}

export interface TransferResponse {
  referenceNo: string;
  sourceAccountId: number;
  sourceAccountNo: string;
  destinationAccountId: number;
  destinationAccountNo: string;
  amount: number;
  sourceBalance: number;
  destinationBalance: number;
  status: string;
  createdAt: string;
}