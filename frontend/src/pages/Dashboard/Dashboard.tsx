import {
  Alert,
  Box,
  Button,
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
import { useNavigate } from "react-router-dom";

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

import { getAccounts, getFundingStatus } from "../../api/accountApi";
import { getCurrentCustomer } from "../../api/customerApi";
import { getBeneficiaries } from "../../api/beneficiaryApi";
import { getAccountTransactions } from "../../api/transactionApi";

import type { Account } from "../../types/account";
import type { Customer } from "../../types/customer";
import type { TransactionResponse } from "../../types/transaction";

import { getTransactionSummary } from "../../utils/transactionAnalytics";

export default function Dashboard() {
  const [customer, setCustomer] = useState<Customer | null>(null);

  const [accounts, setAccounts] = useState<Account[]>([]);

  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);

  const [isLoading, setIsLoading] = useState(true);

  const [error, setError] = useState("");

  const [hasFunding, setHasFunding] = useState(false);

  const [beneficiaryCount, setBeneficiaryCount] = useState(0);

  const navigate = useNavigate();

  useEffect(() => {
    const loadDashboard = async () => {
      setIsLoading(true);
      setError("");

      try {
        let customerData: Customer | null = null;

        try {
          customerData = await getCurrentCustomer();
        } catch (err: any) {
          /*
           * A newly registered authenticated user may not
           * have a customer profile yet.
           *
           * Treat 404 as an onboarding state instead
           * of showing a generic dashboard error.
           */
          if (err?.response?.status !== 404) {
            throw err;
          }
        }

        /*
         * No customer profile yet.
         *
         * Keep the dashboard usable so onboarding can
         * guide the user to the Accounts page where the
         * existing profile dialog is displayed.
         */
        if (!customerData) {
          setCustomer(null);
          setAccounts([]);
          setTransactions([]);
          setHasFunding(false);
          setBeneficiaryCount(0);
          return;
        }

        setCustomer(customerData);

        const [accountData, fundingStatus, beneficiaryData] = await Promise.all(
          [getAccounts(), getFundingStatus(), getBeneficiaries()],
        );

        setAccounts(accountData);
        setHasFunding(fundingStatus);
        setBeneficiaryCount(beneficiaryData.length);

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

  const profileComplete = customer !== null;

  const accountCreated = accounts.length > 0;

  const firstTransferCompleted = transactions.some(
    (transaction) =>
      transaction.status === "COMPLETED" &&
      accounts.some((account) => account.id === transaction.sourceAccountId),
  );

  const onboardingSteps = [
    {
      key: "profile",
      label: "Complete your profile",
      description: "Add your personal details.",
      completed: profileComplete,
      path: "/accounts",
      action: "Complete profile",
    },
    {
      key: "account",
      label: "Open an account",
      description: "Open your first Savings or Current account.",
      completed: accountCreated,
      path: "/accounts",
      action: "Open account",
    },
    {
      key: "funding",
      label: "Fund an account",
      description: "Add funds so you can make transfers.",
      completed: hasFunding,
      path: "/accounts",
      action: "Fund account",
    },
    {
      key: "beneficiary",
      label: "Add a beneficiary",
      description: "Add a recipient account for transfers.",
      completed: beneficiaryCount > 0,
      path: "/beneficiaries",
      action: "Add beneficiary",
    },
    {
      key: "transfer",
      label: "Make your first transfer",
      description: "Complete your first successful transfer.",
      completed: firstTransferCompleted,
      path: "/transfer",
      action: "Make transfer",
    },
  ];

  const nextOnboardingIndex = onboardingSteps.findIndex(
    (step) => !step.completed,
  );

  const onboardingComplete = nextOnboardingIndex === -1;

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
        {customer ? `Welcome, ${customer.firstName}` : "Welcome to SecureBank"}
      </Typography>

      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Here's an overview of your banking activity.
      </Typography>

      {!onboardingComplete && (
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h5">Complete your banking setup</Typography>

            <Typography color="text.secondary" sx={{ mt: 0.5, mb: 2 }}>
              Follow these steps to get your account ready for everyday banking.
            </Typography>

            <Stack spacing={1.5}>
              {onboardingSteps.map((step, index) => {
                const isNextStep = index === nextOnboardingIndex;

                return (
                  <Box
                    key={step.key}
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      gap: 2,
                      p: 1.5,
                      borderRadius: 2,
                      border: "1px solid",
                      borderColor: isNextStep ? "primary.main" : "divider",
                      bgcolor: step.completed
                        ? "action.hover"
                        : "background.paper",
                    }}
                  >
                    <Typography
                      sx={{
                        minWidth: 28,
                        fontSize: "1.2rem",
                        fontWeight: 700,
                        color: step.completed
                          ? "success.main"
                          : isNextStep
                            ? "primary.main"
                            : "text.disabled",
                      }}
                    >
                      {step.completed ? "✓" : isNextStep ? "→" : "○"}
                    </Typography>

                    <Box sx={{ flex: 1 }}>
                      <Typography
                        sx={{
                          fontWeight: 600,
                        }}
                      >
                        {step.label}
                      </Typography>

                      <Typography variant="body2" color="text.secondary">
                        {step.description}
                      </Typography>
                    </Box>

                    {isNextStep && (
                      <Button
                        variant="contained"
                        size="small"
                        onClick={() => navigate(step.path)}
                      >
                        {step.action}
                      </Button>
                    )}
                  </Box>
                );
              })}
            </Stack>
          </CardContent>
        </Card>
      )}

      {onboardingComplete && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Your banking setup is complete. You're ready to use all core banking
          features.
        </Alert>
      )}

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
