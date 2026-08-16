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
} from "@mui/material";

import { useEffect, useMemo, useState } from "react";

import { getAccounts } from "../../api/accountApi";
import { getAccountTransactions } from "../../api/transactionApi";

import type { Account } from "../../types/account";
import type { TransactionResponse } from "../../types/transaction";

export default function Transactions() {
  const [accounts, setAccounts] = useState<Account[]>([]);

  const [selectedAccountId, setSelectedAccountId] = useState("");

  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);

  const [isLoadingAccounts, setIsLoadingAccounts] = useState(true);

  const [isLoadingTransactions, setIsLoadingTransactions] = useState(false);

  const [error, setError] = useState("");

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
      <Typography variant="h4" gutterBottom>
        Transaction History
      </Typography>

      <Typography color="text.secondary" sx={{ mb: 3 }}>
        View the transaction activity for your accounts.
      </Typography>

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
            <Alert severity="info">
              No transactions found for this account.
            </Alert>
          ) : (
            <TableContainer component={Paper}>
              <Table>
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
                    <TableRow key={transaction.id} hover>
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
                        <Chip
                          label={transaction.status}
                          color={
                            transaction.status === "COMPLETED"
                              ? "success"
                              : transaction.status === "FAILED"
                                ? "error"
                                : "warning"
                          }
                          size="small"
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </>
      )}
    </Box>
  );
}
