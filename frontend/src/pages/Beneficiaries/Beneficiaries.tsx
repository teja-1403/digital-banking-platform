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
import { getApiErrorMessage } from "../../utils/apiError";

import PageHeader from "../../components/common/PageHeader";
import EmptyState from "../../components/common/EmptyState";

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

  const [deleteTarget, setDeleteTarget] = useState<Beneficiary | null>(null);

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
    } catch (error) {
      setFormError(getApiErrorMessage(error, "Unable to add beneficiary."));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (beneficiaryId: number) => {
    setError("");
    setDeletingId(beneficiaryId);

    try {
      await deleteBeneficiary(beneficiaryId);

      setBeneficiaries((current) =>
        current.filter((beneficiary) => beneficiary.id !== beneficiaryId),
      );

      setDeleteTarget(null);
    } catch (error) {
      setFormError(getApiErrorMessage(error, "Unable to delete beneficiary."));
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
      <PageHeader
        title="Beneficiaries"
        subtitle="Manage accounts you can transfer money to."
        action={
          <Button variant="contained" onClick={() => setDialogOpen(true)}>
            Add Beneficiary
          </Button>
        }
      />

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
                    alignItems: {
                      xs: "stretch",
                      sm: "flex-start",
                    },
                    gap: 2,
                    flexDirection: {
                      xs: "column",
                      sm: "row",
                    },
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
                    onClick={() => setDeleteTarget(beneficiary)}
                    sx={{
                      alignSelf: {
                        xs: "flex-end",
                        sm: "flex-start",
                      },
                    }}
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
            <EmptyState
              title="No beneficiaries yet"
              description="Add a beneficiary to make transfers faster and easier."
              action={
                <Button variant="contained" onClick={() => setDialogOpen(true)}>
                  Add Beneficiary
                </Button>
              }
            />
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

        <DialogContent dividers>
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

        <DialogActions
          sx={{
            px: 3,
            pb: 2,
            gap: 1,
            flexDirection: {
              xs: "column-reverse",
              sm: "row",
            },
            "& > button": {
              width: {
                xs: "100%",
                sm: "auto",
              },
            },
          }}
        >
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
      <Dialog
        open={Boolean(deleteTarget)}
        onClose={() => !deletingId && setDeleteTarget(null)}
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle>Delete Beneficiary?</DialogTitle>

        <DialogContent dividers>
          <Typography>
            Are you sure you want to remove{" "}
            <strong>{deleteTarget?.nickname}</strong> from your beneficiaries?
          </Typography>
        </DialogContent>

        <DialogActions
          sx={{
            px: 3,
            pb: 2,
            gap: 1,
            flexDirection: {
              xs: "column-reverse",
              sm: "row",
            },
            "& > button": {
              width: {
                xs: "100%",
                sm: "auto",
              },
            },
          }}
        >
          <Button
            onClick={() => setDeleteTarget(null)}
            disabled={deletingId !== null}
          >
            Cancel
          </Button>

          <Button
            color="error"
            variant="contained"
            onClick={() => deleteTarget && void handleDelete(deleteTarget.id)}
            disabled={deletingId !== null}
          >
            {deletingId !== null ? "Deleting..." : "Delete"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
