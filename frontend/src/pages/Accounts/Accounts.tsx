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
  TextField,
  Typography,
} from "@mui/material";

import { useEffect, useState } from "react";

import {
  activateAccount,
  closeAccount,
  createAccount,
  freezeAccount,
  fundAccount,
  getAccounts,
} from "../../api/accountApi";

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

  const [fundAccountDialogOpen, setFundAccountDialogOpen] = useState(false);

  const [fundingAccount, setFundingAccount] = useState<Account | null>(null);

  const [fundAmount, setFundAmount] = useState("");

  const [isFundingAccount, setIsFundingAccount] = useState(false);

  const [fundingError, setFundingError] = useState("");

  const [fundingSuccess, setFundingSuccess] = useState("");

  const [lifecycleAccount, setLifecycleAccount] = useState<Account | null>(
    null,
  );

  const [lifecycleAction, setLifecycleAction] = useState<
    "FREEZE" | "CLOSE" | "ACTIVATE" | null
  >(null);

  const [isUpdatingLifecycle, setIsUpdatingLifecycle] = useState(false);

  const [lifecycleError, setLifecycleError] = useState("");

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

      // Automatically open funding for the newly created account.
      setFundingAccount(createdAccount);
      setFundAmount("");
      setFundingError("");
      setFundingSuccess("");
      setFundAccountDialogOpen(true);
    } catch (error) {
      setAccountError(
        getApiErrorMessage(error, "Unable to create the account."),
      );
    } finally {
      setIsCreatingAccount(false);
    }
  };

  const handleFundAccount = async () => {
    setFundingError("");
    setFundingSuccess("");

    const amount = Number(fundAmount);

    if (!Number.isFinite(amount) || amount <= 0) {
      setFundingError("Enter a valid funding amount greater than zero.");
      return;
    }

    if (!/^\d+(\.\d{1,2})?$/.test(fundAmount)) {
      setFundingError("Funding amount can have at most 2 decimal places.");
      return;
    }

    if (!fundingAccount) {
      setFundingError("No account selected.");
      return;
    }

    setIsFundingAccount(true);

    try {
      const result = await fundAccount(fundingAccount.id, amount);

      setAccounts((current) =>
        current.map((account) =>
          account.id === result.accountId
            ? {
                ...account,
                balance: result.balance,
              }
            : account,
        ),
      );

      setFundingSuccess(
        `Funding successful. Reference: ${result.fundingReference}`,
      );

      setFundAmount("");
    } catch (error) {
      setFundingError(getApiErrorMessage(error, "Unable to fund the account."));
    } finally {
      setIsFundingAccount(false);
    }
  };

  const openLifecycleDialog = (
    account: Account,
    action: "FREEZE" | "CLOSE" | "ACTIVATE",
  ) => {
    setLifecycleAccount(account);
    setLifecycleAction(action);
    setLifecycleError("");
  };

  const handleLifecycleAction = async () => {
    if (!lifecycleAccount || !lifecycleAction) {
      return;
    }

    setLifecycleError("");
    setIsUpdatingLifecycle(true);

    try {
      let updatedAccount: Account;

      if (lifecycleAction === "FREEZE") {
        updatedAccount = await freezeAccount(lifecycleAccount.id);
      } else if (lifecycleAction === "ACTIVATE") {
        updatedAccount = await activateAccount(lifecycleAccount.id);
      } else {
        updatedAccount = await closeAccount(lifecycleAccount.id);
      }

      setAccounts((current) =>
        current.map((account) =>
          account.id === updatedAccount.id ? updatedAccount : account,
        ),
      );

      setLifecycleAccount(null);
      setLifecycleAction(null);
    } catch (error) {
      setLifecycleError(
        getApiErrorMessage(error, "Unable to update the account status."),
      );
    } finally {
      setIsUpdatingLifecycle(false);
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
                <Box
                  sx={{
                    mt: 2,
                    display: "flex",
                    flexDirection: "column",
                    gap: 1,
                  }}
                >
                  <Button
                    variant="outlined"
                    fullWidth
                    disabled={account.status !== "ACTIVE"}
                    onClick={() => {
                      setFundingAccount(account);
                      setFundAmount("");
                      setFundingError("");
                      setFundingSuccess("");
                      setFundAccountDialogOpen(true);
                    }}
                  >
                    Fund Account
                  </Button>

                  {account.status === "ACTIVE" && (
                    <>
                      <Button
                        variant="outlined"
                        color="warning"
                        fullWidth
                        onClick={() => openLifecycleDialog(account, "FREEZE")}
                      >
                        Freeze Account
                      </Button>

                      <Button
                        variant="outlined"
                        color="error"
                        fullWidth
                        onClick={() => openLifecycleDialog(account, "CLOSE")}
                      >
                        Close Account
                      </Button>
                    </>
                  )}

                  {account.status === "BLOCKED" && (
                    <Button
                      variant="outlined"
                      color="success"
                      fullWidth
                      onClick={() => openLifecycleDialog(account, "ACTIVATE")}
                    >
                      Activate Account
                    </Button>
                  )}
                </Box>
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

      <Dialog
        open={accountDialogOpen}
        onClose={() => {
          if (!isCreatingAccount) {
            setAccountDialogOpen(false);
            setAccountError("");
          }
        }}
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
            <InputLabel id="account-type-label">Account Type</InputLabel>

            <Select
              labelId="account-type-label"
              value={accountType}
              label="Account Type"
              onChange={(event) =>
                setAccountType(event.target.value as "SAVINGS" | "CURRENT")
              }
              disabled={isCreatingAccount}
            >
              <MenuItem value="SAVINGS">Savings</MenuItem>
              <MenuItem value="CURRENT">Current</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>

        <DialogActions>
          <Button
            onClick={() => {
              setAccountDialogOpen(false);
              setAccountError("");
            }}
            disabled={isCreatingAccount}
          >
            Cancel
          </Button>

          <Button
            variant="contained"
            onClick={() => void handleCreateAccount()}
            disabled={isCreatingAccount}
          >
            {isCreatingAccount ? (
              <CircularProgress size={22} />
            ) : (
              "Open Account"
            )}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={fundAccountDialogOpen}
        onClose={() => {
          if (!isFundingAccount) {
            setFundAccountDialogOpen(false);
            setFundingAccount(null);
            setFundAmount("");
            setFundingError("");
            setFundingSuccess("");
          }
        }}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>Fund Account</DialogTitle>

        <DialogContent>
          {fundingAccount && (
            <Alert severity="info" sx={{ mb: 2 }}>
              {fundingAccount.accountType} — {fundingAccount.accountNumber}
            </Alert>
          )}

          {fundingError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {fundingError}
            </Alert>
          )}

          {fundingSuccess && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {fundingSuccess}
            </Alert>
          )}

          <TextField
            fullWidth
            label="Funding Amount"
            value={fundAmount}
            onChange={(event) => {
              setFundAmount(event.target.value);
              setFundingError("");
              setFundingSuccess("");
            }}
            type="text"
            inputMode="decimal"
            placeholder="Enter amount"
            disabled={isFundingAccount}
            sx={{ mt: 1 }}
          />
        </DialogContent>

        <DialogActions>
          <Button
            onClick={() => {
              setFundAccountDialogOpen(false);
              setFundingAccount(null);
              setFundAmount("");
              setFundingError("");
              setFundingSuccess("");
            }}
            disabled={isFundingAccount}
          >
            Close
          </Button>

          <Button
            variant="contained"
            onClick={() => void handleFundAccount()}
            disabled={isFundingAccount || !fundingAccount}
          >
            {isFundingAccount ? <CircularProgress size={22} /> : "Fund Account"}
          </Button>
        </DialogActions>
      </Dialog>

      <CustomerProfileDialog
        open={profileDialogOpen}
        onCreated={() => void handleProfileCreated()}
      />

      <Dialog
        open={lifecycleAccount !== null && lifecycleAction !== null}
        onClose={() => {
          if (!isUpdatingLifecycle) {
            setLifecycleAccount(null);
            setLifecycleAction(null);
            setLifecycleError("");
          }
        }}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>
          {lifecycleAction === "FREEZE"
            ? "Freeze Account"
            : lifecycleAction === "ACTIVATE"
              ? "Activate Account"
              : "Close Account"}
        </DialogTitle>

        <DialogContent>
          {lifecycleAccount && (
            <Alert severity="info" sx={{ mb: 2 }}>
              {lifecycleAccount.accountType} — {lifecycleAccount.accountNumber}
            </Alert>
          )}

          {lifecycleError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {lifecycleError}
            </Alert>
          )}

          <Typography>
            {lifecycleAction === "FREEZE" &&
              "Freezing this account will prevent funding and transfers until it is activated again."}

            {lifecycleAction === "ACTIVATE" &&
              "This will reactivate the account and allow normal banking operations again."}

            {lifecycleAction === "CLOSE" &&
              "This account will be permanently closed and cannot be reactivated."}
          </Typography>

          {lifecycleAction === "CLOSE" && lifecycleAccount?.balance !== 0 && (
            <Alert severity="warning" sx={{ mt: 2 }}>
              This account must have a zero balance before it can be closed.
            </Alert>
          )}
        </DialogContent>

        <DialogActions>
          <Button
            onClick={() => {
              setLifecycleAccount(null);
              setLifecycleAction(null);
              setLifecycleError("");
            }}
            disabled={isUpdatingLifecycle}
          >
            Cancel
          </Button>

          <Button
            variant="contained"
            color={
              lifecycleAction === "CLOSE"
                ? "error"
                : lifecycleAction === "FREEZE"
                  ? "warning"
                  : "success"
            }
            onClick={() => void handleLifecycleAction()}
            disabled={
              isUpdatingLifecycle ||
              (lifecycleAction === "CLOSE" && lifecycleAccount?.balance !== 0)
            }
          >
            {isUpdatingLifecycle
              ? "Updating..."
              : lifecycleAction === "FREEZE"
                ? "Freeze Account"
                : lifecycleAction === "ACTIVATE"
                  ? "Activate Account"
                  : "Close Account"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
