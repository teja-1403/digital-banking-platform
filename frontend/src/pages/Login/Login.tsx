import {
  Alert,
  Box,
  Button,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import { useState } from "react";
import type { FormEvent } from "react";

import { Link, useNavigate } from "react-router-dom";

import { useAuth } from "../../context/AuthContext";

import AuthLayout from "../../components/auth/AuthLayout";

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setError("");
    setIsSubmitting(true);

    try {
      await login({
        username,
        password,
      });

      navigate("/dashboard");
    } catch {
      setError("Invalid username or password.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AuthLayout>
      <Paper
        elevation={3}
        sx={{
          width: "100%",
          p: {
            xs: 3,
            sm: 4,
          },
          borderRadius: 3,
        }}
      >
        <Typography
          variant="h4"
          gutterBottom
          sx={{
            color: "text.primary",
            fontWeight: 700,
          }}
        >
          Sign in
        </Typography>

        <Typography
          color="text.secondary"
          sx={{
            mb: 3,
          }}
        >
          Sign in to manage your accounts and banking activity.
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit}>
          <TextField
            fullWidth
            label="Username"
            margin="normal"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            required
            autoComplete="username"
          />

          <TextField
            fullWidth
            label="Password"
            type="password"
            margin="normal"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
            autoComplete="current-password"
          />

          <Button
            fullWidth
            type="submit"
            variant="contained"
            size="large"
            disabled={isSubmitting}
            sx={{
              mt: 2,
              py: 1.4,
              borderRadius: 2,
              fontWeight: 600,
            }}
          >
            {isSubmitting ? "Signing in..." : "Sign in"}
          </Button>
        </Box>

        <Typography
          sx={{
            mt: 3,
            textAlign: "center",
          }}
        >
          Don't have an account? <Link to="/register">Create an account</Link>
        </Typography>
      </Paper>
    </AuthLayout>
  );
}
