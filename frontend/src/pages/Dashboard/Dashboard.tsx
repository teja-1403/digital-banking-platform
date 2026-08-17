import {
  Alert,
  Box,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Grid,
  Paper,
  Stack,
  Typography,
} from "@mui/material";

import { useEffect, useMemo, useState } from "react";

import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

import { getAccounts } from "../../api/accountApi";
import { getCurrentCustomer } from "../../api/customerApi";
import { getAccountTransactions } from "../../api/transactionApi";

import type { Account } from "../../types/account";
import type { Customer } from "../../types/customer";
import type { TransactionResponse } from "../../types/transaction";

import {
  getTransactionSummary,
} from "../../utils/transactionAnalytics";

export default function Dashboard() {
  const [customer, setCustomer] = useState<Customer | null>(null);

  const [accounts, setAccounts] = useState<Account[]>([]);

  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);

  const [isLoading, setIsLoading] = useState(true);

  const [error, setError] = useState("");

  useEffect(() => {
    const loadDashboard = async () => {
      setIsLoading(true);
      setError("");

      try {
        const [customerData, accountData] = await Promise.all([
          getCurrentCustomer(),
          getAccounts(),
        ]);

        setCustomer(customerData);
        setAccounts(accountData);

        /*
         * Load transaction history for all of the
         * user's accounts.
         */
        const transactionResponses = await Promise.all(
          accountData.map((account) => getAccountTransactions(account.id)),
        );

        const mergedTransactions = transactionResponses
          .flat()
          .filter(
            (transaction, index, array) =>
              array.findIndex((item) => item.id === transaction.id) === index,
          )
          .sort(
            (a, b) =>
              new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
          );

        setTransactions(mergedTransactions);
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

  const summary = useMemo(
    () => getTransactionSummary(transactions),
    [transactions],
  );

  /*
   * Build chart data across all accounts.
   */
  const chartData = useMemo(() => {
    const monthMap = new Map<
      string,
      {
        month: string;
        credits: number;
        debits: number;
      }
    >();

    transactions
      .filter((transaction) => transaction.status === "COMPLETED")
      .forEach((transaction) => {
        const date = new Date(transaction.createdAt);

        const key = `${date.getFullYear()}-${String(
          date.getMonth() + 1,
        ).padStart(2, "0")}`;

        const label = date.toLocaleDateString("en-IN", {
          month: "short",
          year: "numeric",
        });

        if (!monthMap.has(key)) {
          monthMap.set(key, {
            month: label,
            credits: 0,
            debits: 0,
          });
        }

        const point = monthMap.get(key)!;

        const sourceOwned = accounts.some(
          (account) => account.id === transaction.sourceAccountId,
        );

        const destinationOwned = accounts.some(
          (account) => account.id === transaction.destinationAccountId,
        );

        if (sourceOwned) {
          point.debits += transaction.amount;
        }

        if (destinationOwned) {
          point.credits += transaction.amount;
        }
      });

    return Array.from(monthMap.entries())
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([, value]) => value);
  }, [transactions, accounts]);

  const recentTransactions = transactions.slice(0, 5);

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
        Here's an overview of your banking activity.
      </Typography>

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Card>
            <CardContent>
              <Typography variant="body2" color="text.secondary">
                Total Balance
              </Typography>

              <Typography variant="h4" sx={{ mt: 1 }}>
                ₹{totalBalance.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Card>
            <CardContent>
              <Typography variant="body2" color="text.secondary">
                Total Transactions
              </Typography>

              <Typography variant="h4" sx={{ mt: 1 }}>
                {summary.totalTransactions}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Card>
            <CardContent>
              <Typography variant="body2" color="text.secondary">
                Completed
              </Typography>

              <Typography variant="h5" sx={{ mt: 1 }}>
                {summary.completedTransactions}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Card>
            <CardContent>
              <Typography variant="body2" color="text.secondary">
                Failed
              </Typography>

              <Typography variant="h5" sx={{ mt: 1 }}>
                {summary.failedTransactions}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Card>
            <CardContent>
              <Typography variant="body2" color="text.secondary">
                Transaction Volume
              </Typography>

              <Typography variant="h5" sx={{ mt: 1 }}>
                ₹{summary.totalTransferred.toFixed(2)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Grid container spacing={2} sx={{ mt: 1 }}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Typography variant="h5" sx={{ mb: 2 }}>
            Your Accounts
          </Typography>

          <Stack spacing={2}>
            {accounts.map((account) => (
              <Card key={account.id}>
                <CardContent>
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "flex-start",
                    }}
                  >
                    <Box>
                      <Typography variant="h6">
                        {account.accountType}
                      </Typography>

                      <Typography color="text.secondary" variant="body2">
                        {account.accountNumber}
                      </Typography>
                    </Box>

                    <Chip
                      label={account.status}
                      color={
                        account.status === "ACTIVE" ? "success" : "default"
                      }
                      size="small"
                    />
                  </Box>

                  <Typography variant="h5" sx={{ mt: 2 }}>
                    {account.currency} {account.balance.toFixed(2)}
                  </Typography>
                </CardContent>
              </Card>
            ))}
          </Stack>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Typography variant="h5" sx={{ mb: 2 }}>
            Transaction Activity
          </Typography>

          <Paper
            sx={{
              p: 2,
              height: 350,
            }}
          >
            {chartData.length === 0 ? (
              <Box
                sx={{
                  height: "100%",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                <Alert severity="info">
                  No transactions yet. Once you make or receive a transfer,
                  recent activity will appear here.
                </Alert>
              </Box>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />

                  <XAxis dataKey="month" />

                  <YAxis />

                  <Tooltip />

                  <Legend />

                  <Bar dataKey="credits" name="Credits" fill="#2e7d32" />

                  <Bar dataKey="debits" name="Debits" fill="#1565c0" />
                </BarChart>
              </ResponsiveContainer>
            )}
          </Paper>
        </Grid>
      </Grid>

      <Typography variant="h5" sx={{ mt: 4, mb: 2 }}>
        Recent Transactions
      </Typography>

      {recentTransactions.length === 0 ? (
        <Alert severity="info">No transactions yet.</Alert>
      ) : (
        <Stack spacing={1.5}>
          {recentTransactions.map((transaction) => (
            <Paper key={transaction.id} sx={{ p: 2 }}>
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  gap: 2,
                }}
              >
                <Box>
                  <Typography sx={{ fontWeight: 600 }}>
                    {transaction.description || transaction.type}
                  </Typography>

                  <Typography variant="body2" color="text.secondary">
                    {transaction.transactionReference}
                  </Typography>
                </Box>

                <Box
                  sx={{
                    textAlign: "right",
                  }}
                >
                  <Typography sx={{ fontWeight: 600 }}>
                    {transaction.currency} {transaction.amount.toFixed(2)}
                  </Typography>

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
                </Box>
              </Box>
            </Paper>
          ))}
        </Stack>
      )}
    </Box>
  );
}
