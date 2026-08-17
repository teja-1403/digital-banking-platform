import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  Snackbar,
  TextField,
  Typography,
} from "@mui/material";

import { useEffect, useMemo, useState } from "react";

import { getAccounts } from "../../api/accountApi";
import { getBeneficiaries } from "../../api/beneficiaryApi";
import { createTransfer } from "../../api/transactionApi";

import type { Account } from "../../types/account";
import type { Beneficiary } from "../../types/beneficiary";
import type { TransactionResponse } from "../../types/transaction";
import { getApiErrorMessage } from "../../utils/apiError";

export default function Transfer() {
  const [accounts, setAccounts] = useState<Account[]>([]);

  const [beneficiaries, setBeneficiaries] = useState<Beneficiary[]>([]);

  const [sourceAccountId, setSourceAccountId] = useState("");

  const [beneficiaryId, setBeneficiaryId] = useState("");

  const [amount, setAmount] = useState("");

  const [description, setDescription] = useState("");

  const [isLoading, setIsLoading] = useState(true);

  const [isSubmitting, setIsSubmitting] = useState(false);

  const [error, setError] = useState("");

  const [success, setSuccess] = useState(false);

  const [transaction, setTransaction] = useState<TransactionResponse | null>(
    null,
  );

  useEffect(() => {
    const loadTransferData = async () => {
      setIsLoading(true);
      setError("");

      try {
        const [accountData, beneficiaryData] = await Promise.all([
          getAccounts(),
          getBeneficiaries(),
        ]);

        const activeAccounts = accountData.filter(
          (account) => account.status === "ACTIVE",
        );

        setAccounts(activeAccounts);
        setBeneficiaries(beneficiaryData);

        if (activeAccounts.length > 0) {
          setSourceAccountId(String(activeAccounts[0].id));
        }
      } catch {
        setError("Unable to load transfer information.");
      } finally {
        setIsLoading(false);
      }
    };

    void loadTransferData();
  }, []);

  const selectedSourceAccount = useMemo(
    () => accounts.find((account) => account.id === Number(sourceAccountId)),
    [accounts, sourceAccountId],
  );

  const selectedBeneficiary = useMemo(
    () =>
      beneficiaries.find(
        (beneficiary) => beneficiary.id === Number(beneficiaryId),
      ),
    [beneficiaries, beneficiaryId],
  );

  const handleTransfer = async () => {
    setError("");
    setSuccess(false);
    setTransaction(null);

    if (!sourceAccountId) {
      setError("Please select a source account.");
      return;
    }

    if (!beneficiaryId) {
      setError("Please select a beneficiary.");
      return;
    }

    const numericAmount = Number(amount);

    if (!amount || Number.isNaN(numericAmount) || numericAmount <= 0) {
      setError("Please enter a valid transfer amount.");
      return;
    }

    if (
      selectedSourceAccount &&
      numericAmount > selectedSourceAccount.balance
    ) {
      setError("Transfer amount exceeds the available balance.");
      return;
    }

    if (!selectedBeneficiary) {
      setError("Please select a valid beneficiary.");
      return;
    }

    setIsSubmitting(true);

    /*
     * Generate ONE idempotency key for this
     * logical transfer attempt.
     */
    const idempotencyKey = crypto.randomUUID();

    try {
      const response = await createTransfer(
        {
          sourceAccountId: Number(sourceAccountId),

          /*
           * Important:
           * Your backend expects the destination
           * account ID, while the beneficiary UI
           * stores the beneficiary account number.
           *
           * We need to resolve that account before
           * sending the transfer.
           */
          destinationAccountId: selectedBeneficiary.beneficiaryAccountId,

          amount: numericAmount,

          currency: selectedSourceAccount?.currency || "INR",

          description: description.trim() || undefined,
        },
        idempotencyKey,
      );

      setTransaction(response);

      if (response.status === "COMPLETED") {
        setSuccess(true);

        setAmount("");
        setDescription("");
        setBeneficiaryId("");
      } else {
        setError("The transfer was not completed.");
      }
    } catch (error) {
      setError(getApiErrorMessage(error, "Transfer failed. Please try again."));
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
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

  if (error && accounts.length === 0) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Transfer Money
      </Typography>

      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Transfer money to one of your beneficiaries.
      </Typography>

      {accounts.length === 0 && (
        <Alert severity="warning" sx={{ mb: 3 }}>
          You don't have any active accounts.
        </Alert>
      )}

      {beneficiaries.length === 0 && (
        <Alert severity="warning" sx={{ mb: 3 }}>
          You don't have any beneficiaries. Add a beneficiary before making a
          transfer.
        </Alert>
      )}

      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      <Card>
        <CardContent>
          <Grid container spacing={2}>
            <Grid size={12}>
              <FormControl fullWidth>
                <InputLabel>From Account</InputLabel>

                <Select
                  value={sourceAccountId}
                  label="From Account"
                  onChange={(event) => setSourceAccountId(event.target.value)}
                >
                  {accounts.map((account) => (
                    <MenuItem key={account.id} value={String(account.id)}>
                      {account.accountType} - {account.accountNumber}
                      {" — "}
                      {account.currency} {account.balance.toFixed(2)}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            <Grid size={12}>
              <FormControl fullWidth>
                <InputLabel>Beneficiary</InputLabel>

                <Select
                  value={beneficiaryId}
                  label="Beneficiary"
                  onChange={(event) => setBeneficiaryId(event.target.value)}
                >
                  {beneficiaries.map((beneficiary) => (
                    <MenuItem
                      key={beneficiary.id}
                      value={String(beneficiary.id)}
                    >
                      {beneficiary.nickname}
                      {" — "}
                      {beneficiary.beneficiaryAccountNumber}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            <Grid size={12}>
              <TextField
                fullWidth
                label="Amount"
                type="number"
                value={amount}
                onChange={(event) => setAmount(event.target.value)}
                slotProps={{
                  htmlInput: {
                    min: 0.01,
                    step: 0.01,
                  },
                }}
              />
            </Grid>

            <Grid size={12}>
              <TextField
                fullWidth
                label="Description"
                value={description}
                onChange={(event) => setDescription(event.target.value)}
                slotProps={{
                  htmlInput: {
                    maxLength: 255,
                  },
                }}
              />
            </Grid>

            {selectedSourceAccount && (
              <Grid size={12}>
                <Alert severity="info">
                  Available balance: {selectedSourceAccount.currency}{" "}
                  {selectedSourceAccount.balance.toFixed(2)}
                </Alert>
              </Grid>
            )}

            <Grid size={12}>
              <Button
                variant="contained"
                size="large"
                fullWidth
                disabled={
                  isSubmitting ||
                  accounts.length === 0 ||
                  beneficiaries.length === 0
                }
                onClick={() => void handleTransfer()}
              >
                {isSubmitting ? "Processing..." : "Transfer Money"}
              </Button>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {transaction && (
        <Card sx={{ mt: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Transfer Result
            </Typography>

            <Typography>Status: {transaction.status}</Typography>

            <Typography>
              Reference: {transaction.transactionReference}
            </Typography>

            <Typography>
              Amount: {transaction.currency} {transaction.amount.toFixed(2)}
            </Typography>
          </CardContent>
        </Card>
      )}

      <Snackbar
        open={success}
        autoHideDuration={4000}
        onClose={() => setSuccess(false)}
        message="Transfer completed successfully."
      />
    </Box>
  );
}
