import {
  Alert,
  Box,
  Card,
  CardContent,
  CircularProgress,
  Grid,
  Typography,
} from "@mui/material";

import { useEffect, useMemo, useState } from "react";

import { getAccounts } from "../../api/accountApi";
import { getCurrentCustomer } from "../../api/customerApi";

import type { Account } from "../../types/account";
import type { Customer } from "../../types/customer";

export default function Dashboard() {
  const [customer, setCustomer] = useState<Customer | null>(null);

  const [accounts, setAccounts] = useState<Account[]>([]);

  const [isLoading, setIsLoading] = useState(true);

  const [error, setError] = useState("");

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        setIsLoading(true);
        setError("");

        const [customerData, accountData] = await Promise.all([
          getCurrentCustomer(),
          getAccounts(),
        ]);

        setCustomer(customerData);
        setAccounts(accountData);
      } catch {
        setError("Unable to load your banking information.");
      } finally {
        setIsLoading(false);
      }
    };

    void loadDashboard();
  }, []);

  const totalBalance = useMemo(
    () => accounts.reduce((total, account) => total + account.balance, 0),
    [accounts],
  );

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

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Welcome, {customer?.firstName}
      </Typography>

      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Here's an overview of your banking accounts.
      </Typography>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="body2" color="text.secondary">
            Total Balance
          </Typography>

          <Typography variant="h4" sx={{ mt: 1 }}>
            ₹{totalBalance.toFixed(2)}
          </Typography>
        </CardContent>
      </Card>

      <Typography variant="h5" sx={{ mb: 2 }}>
        Your Accounts
      </Typography>

      <Grid container spacing={2}>
        {accounts.map((account) => (
          <Grid
            size={{
              xs: 12,
              md: 6,
            }}
            key={account.id}
          >
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {account.accountType}
                </Typography>

                <Typography color="text.secondary" variant="body2">
                  Account Number
                </Typography>

                <Typography sx={{ mb: 2 }}>{account.accountNumber}</Typography>

                <Typography color="text.secondary" variant="body2">
                  Balance
                </Typography>

                <Typography variant="h5" sx={{ mt: 0.5 }}>
                  {account.currency} {account.balance.toFixed(2)}
                </Typography>

                <Typography
                  color="text.secondary"
                  variant="body2"
                  sx={{ mt: 1 }}
                >
                  Status: {account.status}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}

        {accounts.length === 0 && (
          <Grid size={12}>
            <Alert severity="info">You don't have any accounts yet.</Alert>
          </Grid>
        )}
      </Grid>
    </Box>
  );
}
