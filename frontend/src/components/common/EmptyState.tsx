import { Box, Paper, Typography } from "@mui/material";
import type { ReactNode } from "react";

interface EmptyStateProps {
  title: string;
  description: string;
  action?: ReactNode;
}

export default function EmptyState({
  title,
  description,
  action,
}: EmptyStateProps) {
  return (
    <Paper
      variant="outlined"
      sx={{
        p: 4,
        textAlign: "center",
        borderStyle: "dashed",
      }}
    >
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: 1.5,
        }}
      >
        <Typography variant="h6" sx={{ fontWeight: 600 }}>
          {title}
        </Typography>

        <Typography color="text.secondary">{description}</Typography>

        {action}
      </Box>
    </Paper>
  );
}
