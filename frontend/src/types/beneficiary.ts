export interface Beneficiary {
  id: number;
  beneficiaryAccountId: number;
  beneficiaryAccountNumber: string;
  nickname: string;
  createdAt?: string;
}

export interface CreateBeneficiaryRequest {
  beneficiaryAccountNumber: string;
  nickname: string;
}
