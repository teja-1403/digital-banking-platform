import axiosClient from "./axiosClient";
import type { Customer } from "../types/customer";

export interface CreateCustomerRequest {
  firstName: string;
  lastName: string;
  phoneNumber: string;
}

export const getCurrentCustomer = async (): Promise<Customer> => {
  const response = await axiosClient.get<Customer>("/api/customers/me");

  return response.data;
};

export const createCustomer = async (
  request: CreateCustomerRequest,
): Promise<Customer> => {
  const response = await axiosClient.post<Customer>("/api/customers", request);

  return response.data;
};
