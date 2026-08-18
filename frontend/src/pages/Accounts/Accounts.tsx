import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  Typography,
} from "@mui/material";

import { useEffect, useState } from "react";

import { createAccount, getAccounts } from "../../api/accountApi";

import { getCurrentCustomer } from "../../api/customerApi";

import type { Account } from "../../types/account";
import type { Customer } from "../../types/customer";

import CustomerProfileDialog from "./CustomerProfileDialog";
import { getApiErrorMessage } from "../../utils/apiError";

export default function Accounts() {
  const [accounts, setAccounts] = useState<Account[]>([]);

  const [customer, setCustomer] = useState<Customer | null>(null);

  const [isLoading, setIsLoading] = useState(true);

  const [error, setError] = useState("");

  const [profileDialogOpen, setProfileDialogOpen] = useState(false);

  const [accountDialogOpen, setAccountDialogOpen] = useState(false);

  const [accountType, setAccountType] = useState<"SAVINGS" | "CURRENT">(
    "SAVINGS",
  );

  const [isCreatingAccount, setIsCreatingAccount] = useState(false);

  const [accountError, setAccountError] = useState("");

  const loadData = async () => {
    setIsLoading(true);
    setError("");

    try {
      const [customerData, accountData] = await Promise.all([
        getCurrentCustomer(),
        getAccounts(),
      ]);

      setCustomer(customerData);
      setAccounts(accountData);
    } catch (err: any) {
      /*
       * Your backend returns 404 when the customer
       * profile does not exist.
       */
      if (err?.response?.status === 404) {
        setCustomer(null);
        setAccounts([]);
        setProfileDialogOpen(true);
      } else {
        setError("Unable to load account information.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    void loadData();
  }, []);

  const handleCreateAccount = async () => {
    setAccountError("");
    setIsCreatingAccount(true);

    try {
      const createdAccount = await createAccount(accountType);

      setAccounts((current) => [...current, createdAccount]);

      setAccountDialogOpen(false);
    } catch (error) {
      setAccountError(
        getApiErrorMessage(error, "Unable to create the account."),
      );
    } finally {
      setIsCreatingAccount(false);
    }
  };

  const handleProfileCreated = async () => {
    setProfileDialogOpen(false);
    await loadData();
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

  return (
    <Box>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: {
            xs: "flex-start",
            sm: "center",
          },
          gap: 2,
          mb: 3,
          flexDirection: {
            xs: "column",
            sm: "row",
          },
        }}
      >
        <Box>
          <Typography variant="h4">Accounts</Typography>

          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            Manage your banking accounts.
          </Typography>
        </Box>

        <Button
          variant="contained"
          onClick={() => setAccountDialogOpen(true)}
          disabled={!customer}
        >
          Open Account
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {customer && (
        <Alert severity="info" sx={{ mb: 3 }}>
          Customer: {customer.firstName} {customer.lastName}
        </Alert>
      )}

      <Grid container spacing={2}>
        {accounts.map((account) => (
          <Grid size={{ xs: 12, md: 6 }} key={account.id}>
            <Card>
              <CardContent>
                <Box
                  sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    mb: 2,
                  }}
                >
                  <Typography variant="h6">{account.accountType}</Typography>

                  <Chip
                    label={account.status}
                    color={account.status === "ACTIVE" ? "success" : "default"}
                    size="small"
                  />
                </Box>

                <Typography variant="body2" color="text.secondary">
                  Account Number
                </Typography>

                <Typography sx={{ mb: 2 }}>{account.accountNumber}</Typography>

                <Typography variant="body2" color="text.secondary">
                  Available Balance
                </Typography>

                <Typography variant="h5">
                  {account.currency} {account.balance.toFixed(2)}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}

        {accounts.length === 0 && customer && (
          <Grid size={12}>
            <Alert severity="info">
              You don't have any accounts yet. Open your first account to get
              started.
            </Alert>
          </Grid>
        )}
      </Grid>

      <CustomerProfileDialog
        open={profileDialogOpen}
        onCreated={() => void handleProfileCreated()}
      />

      <Dialog
        open={accountDialogOpen}
        onClose={() => !isCreatingAccount && setAccountDialogOpen(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>Open New Account</DialogTitle>

        <DialogContent>
          {accountError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {accountError}
            </Alert>
          )}

          <FormControl fullWidth sx={{ mt: 1 }}>
            <InputLabel>Account Type</InputLabel>

            <Select
              value={accountType}
              label="Account Type"
              onChange={(event) =>
                setAccountType(event.target.value as "SAVINGS" | "CURRENT")
              }
            >
              <MenuItem value="SAVINGS">Savings</MenuItem>

              <MenuItem value="CURRENT">Current</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>

        <DialogActions>
          <Button
            onClick={() => setAccountDialogOpen(false)}
            disabled={isCreatingAccount}
          >
            Cancel
          </Button>

          <Button
            variant="contained"
            onClick={() => void handleCreateAccount()}
            disabled={isCreatingAccount}
          >
            {isCreatingAccount ? "Creating..." : "Create Account"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
