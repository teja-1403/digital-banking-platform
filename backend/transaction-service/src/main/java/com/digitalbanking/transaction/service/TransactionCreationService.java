package com.digitalbanking.transaction.service;

import com.digitalbanking.transaction.dto.TransferRequest;
import com.digitalbanking.transaction.entity.Transaction;
import com.digitalbanking.transaction.entity.TransactionStatus;
import com.digitalbanking.transaction.entity.TransactionType;
import com.digitalbanking.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionCreationService {

    private final TransactionRepository transactionRepository;
    private final TransactionReferenceGenerator referenceGenerator;

    public TransactionCreationService(
            TransactionRepository transactionRepository,
            TransactionReferenceGenerator referenceGenerator
    ) {
        this.transactionRepository = transactionRepository;
        this.referenceGenerator = referenceGenerator;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction createPendingTransaction(
            String idempotencyKey,
            TransferRequest request
    ) {

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                referenceGenerator.generate()
        );

        transaction.setIdempotencyKey(idempotencyKey);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);

        transaction.setSourceAccountId(
                request.getSourceAccountId()
        );

        transaction.setDestinationAccountId(
                request.getDestinationAccountId()
        );

        transaction.setAmount(
                request.getAmount()
        );

        transaction.setCurrency(
                request.getCurrency().toUpperCase()
        );

        transaction.setDescription(
                request.getDescription()
        );

        return transactionRepository.saveAndFlush(
                transaction
        );
    }
}