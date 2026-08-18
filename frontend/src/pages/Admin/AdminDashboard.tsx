import {
  Alert,
  Box,
  Card,
  CardContent,
  CircularProgress,
  Grid,
  Typography,
} from "@mui/material";

import { useEffect, useState } from "react";

import {
  getAdminAccountStats,
  getAdminTransactionStats,
  getAdminUserStats,
} from "../../api/adminApi";

import type {
  AdminAccountStats,
  AdminTransactionStats,
  AdminUserStats,
} from "../../types/admin";

export default function AdminDashboard() {
  const [userStats, setUserStats] = useState<AdminUserStats | null>(null);

  const [accountStats, setAccountStats] = useState<AdminAccountStats | null>(
    null,
  );

  const [transactionStats, setTransactionStats] =
    useState<AdminTransactionStats | null>(null);

  const [isLoading, setIsLoading] = useState(true);

  const [error, setError] = useState("");

  useEffect(() => {
    const loadStats = async () => {
      setIsLoading(true);
      setError("");

      try {
        const [users, accounts, transactions] = await Promise.all([
          getAdminUserStats(),
          getAdminAccountStats(),
          getAdminTransactionStats(),
        ]);

        setUserStats(users);
        setAccountStats(accounts);
        setTransactionStats(transactions);
      } catch {
        setError("Unable to load admin dashboard data.");
      } finally {
        setIsLoading(false);
      }
    };

    void loadStats();
  }, []);

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
        Admin Dashboard
      </Typography>

      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Platform-wide banking statistics.
      </Typography>

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard label="Users" value={userStats?.totalUsers ?? 0} />
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard
            label="Customers"
            value={accountStats?.totalCustomers ?? 0}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard label="Accounts" value={accountStats?.totalAccounts ?? 0} />
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard
            label="Active Accounts"
            value={accountStats?.activeAccounts ?? 0}
          />
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <StatCard
            label="Total Active Balance"
            value={`₹${(accountStats?.totalBalance ?? 0).toFixed(2)}`}
          />
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <StatCard
            label="Transaction Volume"
            value={`₹${(transactionStats?.totalTransactionVolume ?? 0).toFixed(
              2,
            )}`}
          />
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <StatCard
            label="Transactions"
            value={transactionStats?.totalTransactions ?? 0}
          />
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <StatCard
            label="Completed"
            value={transactionStats?.completedTransactions ?? 0}
          />
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <StatCard
            label="Failed"
            value={transactionStats?.failedTransactions ?? 0}
          />
        </Grid>
      </Grid>
    </Box>
  );
}

interface StatCardProps {
  label: string;
  value: string | number;
}

function StatCard({ label, value }: StatCardProps) {
  return (
    <Card>
      <CardContent>
        <Typography variant="body2" color="text.secondary">
          {label}
        </Typography>

        <Typography
          variant="h4"
          sx={{
            mt: 1,
            fontWeight: 600,
          }}
        >
          {value}
        </Typography>
      </CardContent>
    </Card>
  );
}
