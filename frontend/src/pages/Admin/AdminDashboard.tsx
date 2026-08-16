import { Container, Paper, Typography } from "@mui/material";

import { useAuth } from "../../context/AuthContext";

export default function AdminDashboard() {
  const { user } = useAuth();

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Admin Dashboard
        </Typography>

        <Typography color="text.secondary">
          Welcome, {user?.username}.
        </Typography>

        <Typography sx={{ mt: 2 }}>Role: {user?.roles.join(", ")}</Typography>
      </Paper>
    </Container>
  );
}
