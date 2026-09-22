import { Chip } from "@mui/material";

interface StatusChipProps {
  status: string;
}

const statusColors: Record<
  string,
  "success" | "error" | "warning" | "info" | "default"
> = {
  ACTIVE: "success",
  BLOCKED: "warning",
  CLOSED: "default",

  COMPLETED: "success",
  FAILED: "error",
  PENDING: "warning",
};

export default function StatusChip({ status }: StatusChipProps) {
  return (
    <Chip
      label={status}
      color={statusColors[status] ?? "default"}
      size="small"
      variant="outlined"
    />
  );
}
