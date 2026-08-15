package com.digitalbanking.account.service;

import com.digitalbanking.account.dto.BeneficiaryResponse;
import com.digitalbanking.account.dto.CreateBeneficiaryRequest;
import com.digitalbanking.account.entity.Account;
import com.digitalbanking.account.entity.AccountStatus;
import com.digitalbanking.account.entity.Beneficiary;
import com.digitalbanking.account.entity.Customer;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.ResourceNotFoundException;
import com.digitalbanking.account.repository.AccountRepository;
import com.digitalbanking.account.repository.BeneficiaryRepository;
import com.digitalbanking.account.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository,
            CustomerRepository customerRepository,
            AccountRepository accountRepository
    ) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public BeneficiaryResponse createBeneficiary(
            Long userId,
            CreateBeneficiaryRequest request
    ) {

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        )
                );

        Account beneficiaryAccount = accountRepository
                .findByAccountNumber(request.getBeneficiaryAccountNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Beneficiary account not found"
                        )
                );

        if (!beneficiaryAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new BusinessRuleException(
                    "Beneficiary account is not active"
            );
        }

        if (beneficiaryAccount.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException(
                    "You cannot add your own account as a beneficiary"
            );
        }

        if (beneficiaryRepository
                .existsByCustomerIdAndBeneficiaryAccountNumber(
                        customer.getId(),
                        request.getBeneficiaryAccountNumber()
                )) {

            throw new BusinessRuleException(
                    "Beneficiary already exists"
            );
        }

        Beneficiary beneficiary = new Beneficiary();

        beneficiary.setCustomer(customer);
        beneficiary.setBeneficiaryAccountNumber(
                request.getBeneficiaryAccountNumber()
        );
        beneficiary.setNickname(request.getNickname());

        Beneficiary savedBeneficiary =
                beneficiaryRepository.save(beneficiary);

        return toResponse(savedBeneficiary);
    }

    @Transactional(readOnly = true)
    public List<BeneficiaryResponse> getCurrentUserBeneficiaries(
            Long userId
    ) {

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        )
                );

        return beneficiaryRepository
                .findByCustomerId(customer.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BeneficiaryResponse getBeneficiary(
            Long userId,
            Long beneficiaryId
    ) {

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        )
                );

        Beneficiary beneficiary = beneficiaryRepository
                .findById(beneficiaryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Beneficiary not found"
                        )
                );

        if (!beneficiary.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException(
                    "You do not have access to this beneficiary"
            );
        }

        return toResponse(beneficiary);
    }

    @Transactional
    public void deleteBeneficiary(
            Long userId,
            Long beneficiaryId
    ) {

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        )
                );

        Beneficiary beneficiary = beneficiaryRepository
                .findById(beneficiaryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Beneficiary not found"
                        )
                );

        if (!beneficiary.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException(
                    "You do not have access to this beneficiary"
            );
        }

        beneficiaryRepository.delete(beneficiary);
    }

    private BeneficiaryResponse toResponse(
            Beneficiary beneficiary
    ) {

        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getBeneficiaryAccountNumber(),
                beneficiary.getNickname(),
                beneficiary.getCreatedAt()
        );
    }
}