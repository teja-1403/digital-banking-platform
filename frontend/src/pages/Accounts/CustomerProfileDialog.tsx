import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";

import { useState } from "react";

import {
  createCustomer,
  type CreateCustomerRequest,
} from "../../api/customerApi";

interface CustomerProfileDialogProps {
  open: boolean;
  onCreated: () => void;
}

export default function CustomerProfileDialog({
  open,
  onCreated,
}: CustomerProfileDialogProps) {
  const [form, setForm] = useState<CreateCustomerRequest>({
    firstName: "",
    lastName: "",
    phoneNumber: "",
  });

  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange =
    (field: keyof CreateCustomerRequest) =>
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setForm((current) => ({
        ...current,
        [field]: event.target.value,
      }));
    };

  const handleSubmit = async () => {
    setError("");
    setIsSubmitting(true);

    try {
      await createCustomer(form);
      onCreated();

      setForm({
        firstName: "",
        lastName: "",
        phoneNumber: "",
      });
    } catch {
      setError("Unable to create your customer profile.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} fullWidth maxWidth="sm">
      <DialogTitle>Complete Your Profile</DialogTitle>

      <DialogContent>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <TextField
          fullWidth
          label="First Name"
          margin="normal"
          value={form.firstName}
          onChange={handleChange("firstName")}
          required
        />

        <TextField
          fullWidth
          label="Last Name"
          margin="normal"
          value={form.lastName}
          onChange={handleChange("lastName")}
          required
        />

        <TextField
          fullWidth
          label="Phone Number"
          margin="normal"
          value={form.phoneNumber}
          onChange={handleChange("phoneNumber")}
          required
        />
      </DialogContent>

      <DialogActions>
        <Button
          variant="contained"
          onClick={() => void handleSubmit()}
          disabled={isSubmitting}
        >
          {isSubmitting ? "Saving..." : "Create Profile"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
