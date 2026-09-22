import {
  Alert,
  Box,
  Chip,
  CircularProgress,
  FormControl,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
} from "@mui/material";

import { useEffect, useMemo, useState } from "react";

import { getAccounts } from "../../api/accountApi";
import {
  getAccountTransactions,
  getTransactionDetails,
} from "../../api/transactionApi";

import { getApiErrorMessage } from "../../utils/apiError";

import PageHeader from "../../components/common/PageHeader";
import StatusChip from "../../components/common/StatusChip";
import EmptyState from "../../components/common/EmptyState";

import type { Account } from "../../types/account";
import type { TransactionResponse } from "../../types/transaction";

export default function Transactions() {
  const [accounts, setAccounts] = useState<Account[]>([]);

  const [selectedAccountId, setSelectedAccountId] = useState("");

  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);

  const [isLoadingAccounts, setIsLoadingAccounts] = useState(true);

  const [isLoadingTransactions, setIsLoadingTransactions] = useState(false);

  const [error, setError] = useState("");

  const [selectedTransaction, setSelectedTransaction] =
    useState<TransactionResponse | null>(null);

  const [isLoadingDetails, setIsLoadingDetails] = useState(false);

  const [detailsError, setDetailsError] = useState("");

  useEffect(() => {
    const loadAccounts = async () => {
      setIsLoadingAccounts(true);
      setError("");

      try {
        const data = await getAccounts();

        const activeAccounts = data.filter(
          (account) => account.status === "ACTIVE",
        );

        setAccounts(activeAccounts);

        if (activeAccounts.length > 0) {
          setSelectedAccountId(String(activeAccounts[0].id));
        }
      } catch {
        setError("Unable to load your accounts.");
      } finally {
        setIsLoadingAccounts(false);
      }
    };

    void loadAccounts();
  }, []);

  useEffect(() => {
    if (!selectedAccountId) {
      setTransactions([]);
      return;
    }

    const loadTransactions = async () => {
      setIsLoadingTransactions(true);
      setError("");

      try {
        const data = await getAccountTransactions(Number(selectedAccountId));

        setTransactions(data);
      } catch {
        setError("Unable to load transaction history.");
      } finally {
        setIsLoadingTransactions(false);
      }
    };

    void loadTransactions();
  }, [selectedAccountId]);

  const handleOpenTransactionDetails = async (transactionReference: string) => {
    setSelectedTransaction(null);
    setDetailsError("");
    setIsLoadingDetails(true);

    try {
      const data = await getTransactionDetails(transactionReference);

      setSelectedTransaction(data);
    } catch (error) {
      setDetailsError(
        getApiErrorMessage(error, "Unable to load transaction details."),
      );
    } finally {
      setIsLoadingDetails(false);
    }
  };

  const handleCloseTransactionDetails = () => {
    if (isLoadingDetails) {
      return;
    }

    setSelectedTransaction(null);
    setDetailsError("");
  };

  const selectedAccount = useMemo(
    () => accounts.find((account) => account.id === Number(selectedAccountId)),
    [accounts, selectedAccountId],
  );

  if (isLoadingAccounts) {
    return (
      <Box
        sx={{
          minHeight: "50vh",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <PageHeader
        title="Transaction History"
        subtitle="View the transaction activity for your accounts."
      />

      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      {accounts.length === 0 ? (
        <Alert severity="info">You don't have any active accounts.</Alert>
      ) : (
        <>
          <FormControl
            fullWidth
            sx={{
              maxWidth: 500,
              mb: 3,
            }}
          >
            <InputLabel>Account</InputLabel>

            <Select
              value={selectedAccountId}
              label="Account"
              onChange={(event) => setSelectedAccountId(event.target.value)}
            >
              {accounts.map((account) => (
                <MenuItem key={account.id} value={String(account.id)}>
                  {account.accountType} - {account.accountNumber}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {selectedAccount && (
            <Alert severity="info" sx={{ mb: 3 }}>
              Current balance: {selectedAccount.currency}{" "}
              {selectedAccount.balance.toFixed(2)}
            </Alert>
          )}

          {isLoadingTransactions ? (
            <Box
              sx={{
                py: 6,
                display: "flex",
                justifyContent: "center",
              }}
            >
              <CircularProgress />
            </Box>
          ) : transactions.length === 0 ? (
            <EmptyState
              title="No transactions yet"
              description="Transactions for this account will appear here once you make or receive a transfer."
            />
          ) : (
            <TableContainer
              component={Paper}
              sx={{
                overflowX: "auto",
              }}
            >
              <Table
                sx={{
                  minWidth: 760,
                }}
              >
                <TableHead>
                  <TableRow>
                    <TableCell>Date</TableCell>

                    <TableCell>Reference</TableCell>

                    <TableCell>Type</TableCell>

                    <TableCell>Description</TableCell>

                    <TableCell align="right">Amount</TableCell>

                    <TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>

                <TableBody>
                  {transactions.map((transaction) => (
                    <TableRow
                      key={transaction.id}
                      hover
                      tabIndex={0}
                      onClick={() =>
                        void handleOpenTransactionDetails(
                          transaction.transactionReference,
                        )
                      }
                      onKeyDown={(event) => {
                        if (event.key === "Enter" || event.key === " ") {
                          event.preventDefault();

                          void handleOpenTransactionDetails(
                            transaction.transactionReference,
                          );
                        }
                      }}
                      sx={{
                        cursor: "pointer",
                      }}
                    >
                      <TableCell>
                        {new Date(transaction.createdAt).toLocaleString()}
                      </TableCell>

                      <TableCell>{transaction.transactionReference}</TableCell>

                      <TableCell>{transaction.type}</TableCell>

                      <TableCell>{transaction.description || "—"}</TableCell>

                      <TableCell align="right">
                        {transaction.currency} {transaction.amount.toFixed(2)}
                      </TableCell>

                      <TableCell>
                        <StatusChip status={transaction.status} />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </>
      )}
      <Dialog
        open={
          isLoadingDetails ||
          selectedTransaction !== null ||
          detailsError !== ""
        }
        onClose={handleCloseTransactionDetails}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>Transaction Details</DialogTitle>

        <DialogContent dividers>
          {isLoadingDetails && (
            <Box
              sx={{
                minHeight: 200,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <CircularProgress />
            </Box>
          )}

          {!isLoadingDetails && detailsError && (
            <Alert severity="error">{detailsError}</Alert>
          )}

          {!isLoadingDetails && selectedTransaction && (
            <Box>
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: {
                    xs: "flex-start",
                    sm: "center",
                  },
                  flexDirection: {
                    xs: "column",
                    sm: "row",
                  },
                  gap: 1,
                  mb: 2,
                }}
              >
                <Typography
                  variant="h6"
                  sx={{
                    overflowWrap: "anywhere",
                  }}
                >
                  {selectedTransaction.transactionReference}
                </Typography>

                <Chip
                  label={selectedTransaction.status}
                  color={
                    selectedTransaction.status === "COMPLETED"
                      ? "success"
                      : selectedTransaction.status === "FAILED"
                        ? "error"
                        : "warning"
                  }
                />
              </Box>

              <Divider sx={{ mb: 2 }} />

              <Box
                sx={{
                  display: "grid",
                  gridTemplateColumns: {
                    xs: "1fr",
                    sm: "1fr 1fr",
                  },
                  gap: 2,
                }}
              >
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Transaction ID
                  </Typography>
                  <Typography>{selectedTransaction.id}</Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Type
                  </Typography>
                  <Typography>{selectedTransaction.type}</Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Amount
                  </Typography>
                  <Typography variant="h6">
                    {selectedTransaction.currency}{" "}
                    {selectedTransaction.amount.toFixed(2)}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Description
                  </Typography>
                  <Typography>
                    {selectedTransaction.description || "—"}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Source Account ID
                  </Typography>
                  <Typography>{selectedTransaction.sourceAccountId}</Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Destination Account ID
                  </Typography>
                  <Typography>
                    {selectedTransaction.destinationAccountId}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Created At
                  </Typography>
                  <Typography>
                    {new Date(selectedTransaction.createdAt).toLocaleString()}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Completed At
                  </Typography>
                  <Typography>
                    {selectedTransaction.completedAt
                      ? new Date(
                          selectedTransaction.completedAt,
                        ).toLocaleString()
                      : "—"}
                  </Typography>
                </Box>
              </Box>

              <Box sx={{ mt: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Idempotency Key
                </Typography>

                <Typography
                  sx={{
                    wordBreak: "break-all",
                  }}
                >
                  {selectedTransaction.idempotencyKey}
                </Typography>
              </Box>
            </Box>
          )}
        </DialogContent>

        <DialogActions>
          <Button
            onClick={handleCloseTransactionDetails}
            disabled={isLoadingDetails}
          >
            Close
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
