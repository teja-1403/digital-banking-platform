import axiosClient from "./axiosClient";
import type {
  Beneficiary,
  CreateBeneficiaryRequest,
} from "../types/beneficiary";

export const getBeneficiaries = async (): Promise<Beneficiary[]> => {
  const response = await axiosClient.get<Beneficiary[]>("/api/beneficiaries");

  return response.data;
};

export const createBeneficiary = async (
  request: CreateBeneficiaryRequest,
): Promise<Beneficiary> => {
  const response = await axiosClient.post<Beneficiary>(
    "/api/beneficiaries",
    request,
  );

  return response.data;
};

export const getBeneficiary = async (
  beneficiaryId: number,
): Promise<Beneficiary> => {
  const response = await axiosClient.get<Beneficiary>(
    `/api/beneficiaries/${beneficiaryId}`,
  );

  return response.data;
};

export const deleteBeneficiary = async (
  beneficiaryId: number,
): Promise<void> => {
  await axiosClient.delete(`/api/beneficiaries/${beneficiaryId}`);
};
