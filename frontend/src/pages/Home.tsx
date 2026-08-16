import { Container, Typography, Paper } from "@mui/material";

export default function Home() {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Paper sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom>
          Digital Banking Platform
        </Typography>

        <Typography color="text.secondary">
          Frontend foundation is ready.
        </Typography>
      </Paper>
    </Container>
  );
}
