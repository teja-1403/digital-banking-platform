package com.digitalbanking.transaction.client;

import com.digitalbanking.transaction.dto.AccountOwnershipResponse;
import com.digitalbanking.transaction.dto.TransferRequest;
import com.digitalbanking.transaction.exception.AccountServiceBusinessException;
import com.digitalbanking.transaction.exception.AccountServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AccountServiceClient {

    private final RestClient restClient;
    private final String internalServiceSecret;

    public AccountServiceClient(
            RestClient accountServiceRestClient,
            @Value("${internal.service-secret}")
                    String internalServiceSecret
    ) {
        this.restClient = accountServiceRestClient;
        this.internalServiceSecret = internalServiceSecret;
    }

    public void executeTransfer(
            Long userId,
            TransferRequest request
    ) {

        InternalTransferPayload payload =
                new InternalTransferPayload(
                        userId,
                        request.getSourceAccountId(),
                        request.getDestinationAccountId(),
                        request.getAmount()
                );

        try {

            restClient.post()
                    .uri("/internal/accounts/transfer")
                    .header(
                            "X-Internal-Service-Secret",
                            internalServiceSecret
                    )
                    .body(payload)
                    .retrieve()

                    .onStatus(
                            status -> status.is4xxClientError(),
                            (requestSpec, clientResponse) -> {
                                throw new AccountServiceBusinessException(
                                        "Account Service rejected the transfer: "
                                                + clientResponse.getStatusText()
                                );
                            }
                    )

                    .onStatus(
                            status -> status.is5xxServerError(),
                            (requestSpec, clientResponse) -> {
                                throw new AccountServiceUnavailableException(
                                        "Account Service returned "
                                                + clientResponse.getStatusCode()
                                );
                            }
                    )

                    .toBodilessEntity();

        } catch (AccountServiceBusinessException |
                AccountServiceUnavailableException ex) {

            throw ex;

        } catch (RestClientException ex) {

            throw new AccountServiceUnavailableException(
                    "Unable to communicate with Account Service",
                    ex
            );
        }
    }

    public boolean isAccountOwnedByUser(
            Long userId,
            Long accountId
    ) {

        try {

            AccountOwnershipResponse response =
                    restClient.get()
                            .uri(
                                    uriBuilder ->
                                            uriBuilder
                                                    .path(
                                                            "/internal/accounts/{accountId}/ownership"
                                                    )
                                                    .queryParam(
                                                            "userId",
                                                            userId
                                                    )
                                                    .build(accountId)
                            )
                            .header(
                                    "X-Internal-Service-Secret",
                                    internalServiceSecret
                            )
                            .retrieve()

                            .onStatus(
                                    status -> status.is4xxClientError(),
                                    (requestSpec, clientResponse) -> {
                                        throw new AccountServiceBusinessException(
                                                "Account Service rejected ownership check: "
                                                        + clientResponse.getStatusText()
                                        );
                                    }
                            )

                            .onStatus(
                                    status -> status.is5xxServerError(),
                                    (requestSpec, clientResponse) -> {
                                        throw new AccountServiceUnavailableException(
                                                "Account Service returned "
                                                        + clientResponse.getStatusCode()
                                        );
                                    }
                            )

                            .body(AccountOwnershipResponse.class);

            return response != null &&
                    response.isOwner();

        } catch (AccountServiceBusinessException |
                AccountServiceUnavailableException ex) {

            throw ex;

        } catch (RestClientException ex) {

            throw new AccountServiceUnavailableException(
                    "Unable to communicate with Account Service",
                    ex
            );
        }
    }

    private record InternalTransferPayload(
            Long userId,
            Long sourceAccountId,
            Long destinationAccountId,
            java.math.BigDecimal amount
    ) {
    }
}