import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  TextField,
  Typography,
} from "@mui/material";

import DeleteIcon from "@mui/icons-material/Delete";

import { useEffect, useState } from "react";

import {
  createBeneficiary,
  deleteBeneficiary,
  getBeneficiaries,
} from "../../api/beneficiaryApi";

import type { Beneficiary } from "../../types/beneficiary";

export default function Beneficiaries() {
  const [beneficiaries, setBeneficiaries] = useState<Beneficiary[]>([]);

  const [isLoading, setIsLoading] = useState(true);

  const [error, setError] = useState("");

  const [dialogOpen, setDialogOpen] = useState(false);

  const [beneficiaryAccountNumber, setBeneficiaryAccountNumber] = useState("");

  const [nickname, setNickname] = useState("");

  const [formError, setFormError] = useState("");

  const [isSubmitting, setIsSubmitting] = useState(false);

  const [deletingId, setDeletingId] = useState<number | null>(null);

  const loadBeneficiaries = async () => {
    setIsLoading(true);
    setError("");

    try {
      const data = await getBeneficiaries();

      setBeneficiaries(data);
    } catch {
      setError("Unable to load beneficiaries.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    void loadBeneficiaries();
  }, []);

  const resetForm = () => {
    setBeneficiaryAccountNumber("");
    setNickname("");
    setFormError("");
  };

  const handleCloseDialog = () => {
    if (isSubmitting) {
      return;
    }

    setDialogOpen(false);
    resetForm();
  };

  const handleCreate = async () => {
    setFormError("");

    if (!beneficiaryAccountNumber.trim()) {
      setFormError("Beneficiary account number is required.");
      return;
    }

    if (!nickname.trim()) {
      setFormError("Nickname is required.");
      return;
    }

    setIsSubmitting(true);

    try {
      const beneficiary = await createBeneficiary({
        beneficiaryAccountNumber: beneficiaryAccountNumber.trim(),
        nickname: nickname.trim(),
      });

      setBeneficiaries((current) => [...current, beneficiary]);

      handleCloseDialog();
    } catch (err: any) {
      const message =
        err?.response?.data?.message || "Unable to add beneficiary.";

      setFormError(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (beneficiaryId: number) => {
    setError("");

    const confirmed = window.confirm(
      "Are you sure you want to delete this beneficiary?",
    );

    if (!confirmed) {
      return;
    }

    setDeletingId(beneficiaryId);

    try {
      await deleteBeneficiary(beneficiaryId);

      setBeneficiaries((current) =>
        current.filter((beneficiary) => beneficiary.id !== beneficiaryId),
      );
    } catch (err: any) {
      setError(err?.response?.data?.message || "Unable to delete beneficiary.");
    } finally {
      setDeletingId(null);
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
          flexDirection: {
            xs: "column",
            sm: "row",
          },
          gap: 2,
          mb: 3,
        }}
      >
        <Box>
          <Typography variant="h4">Beneficiaries</Typography>

          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            Manage accounts you can transfer money to.
          </Typography>
        </Box>

        <Button variant="contained" onClick={() => setDialogOpen(true)}>
          Add Beneficiary
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={2}>
        {beneficiaries.map((beneficiary) => (
          <Grid
            size={{
              xs: 12,
              md: 6,
            }}
            key={beneficiary.id}
          >
            <Card>
              <CardContent>
                <Box
                  sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "flex-start",
                  }}
                >
                  <Box>
                    <Typography variant="h6" gutterBottom>
                      {beneficiary.nickname}
                    </Typography>

                    <Typography variant="body2" color="text.secondary">
                      Account Number
                    </Typography>

                    <Typography>
                      {beneficiary.beneficiaryAccountNumber}
                    </Typography>

                    {beneficiary.createdAt && (
                      <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{ mt: 1 }}
                      >
                        Added:{" "}
                        {new Date(beneficiary.createdAt).toLocaleString()}
                      </Typography>
                    )}
                  </Box>

                  <IconButton
                    color="error"
                    disabled={deletingId === beneficiary.id}
                    onClick={() => void handleDelete(beneficiary.id)}
                  >
                    <DeleteIcon />
                  </IconButton>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}

        {beneficiaries.length === 0 && (
          <Grid size={12}>
            <Alert severity="info">You don't have any beneficiaries yet.</Alert>
          </Grid>
        )}
      </Grid>

      <Dialog
        open={dialogOpen}
        onClose={handleCloseDialog}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>Add Beneficiary</DialogTitle>

        <DialogContent>
          {formError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {formError}
            </Alert>
          )}

          <TextField
            fullWidth
            label="Beneficiary Account Number"
            margin="normal"
            value={beneficiaryAccountNumber}
            onChange={(event) =>
              setBeneficiaryAccountNumber(event.target.value)
            }
            slotProps={{
              htmlInput: {
                maxLength: 20,
              },
            }}
            required
          />

          <TextField
            fullWidth
            label="Nickname"
            margin="normal"
            value={nickname}
            onChange={(event) => setNickname(event.target.value)}
            slotProps={{
              htmlInput: {
                maxLength: 50,
              },
            }}
            required
          />
        </DialogContent>

        <DialogActions>
          <Button onClick={handleCloseDialog} disabled={isSubmitting}>
            Cancel
          </Button>

          <Button
            variant="contained"
            onClick={() => void handleCreate()}
            disabled={isSubmitting}
          >
            {isSubmitting ? "Adding..." : "Add Beneficiary"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
