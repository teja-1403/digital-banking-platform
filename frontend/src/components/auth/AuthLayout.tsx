import { Box, Typography } from "@mui/material";
import type { ReactNode } from "react";

interface AuthLayoutProps {
  children: ReactNode;
}

const AuthLayout = ({ children }: AuthLayoutProps) => {
  return (
    <Box
      sx={{
        position: "relative",
        minHeight: "100vh",
        width: "100%",
        overflow: "hidden",
        backgroundImage: "url('/images/background img.png')",
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      {/* Background overlay */}
      <Box
        sx={{
          position: "absolute",
          inset: 0,
          background:
            "linear-gradient(90deg, rgba(4, 20, 52, 0.72) 0%, rgba(4, 20, 52, 0.48) 42%, rgba(4, 20, 52, 0.35) 100%)",
          zIndex: 0,
        }}
      />

      {/* Main content */}
      <Box
        sx={{
          position: "relative",
          zIndex: 1,
          width: "100%",
          maxWidth: 1400,
          minHeight: "100vh",
          mx: "auto",
          px: {
            xs: 2,
            sm: 4,
            md: 6,
            lg: 8,
          },
          py: {
            xs: 4,
            md: 6,
          },
          display: "flex",
          alignItems: "center",
          justifyContent: {
            xs: "center",
            md: "space-between",
          },
          gap: {
            xs: 4,
            md: 8,
          },
          flexDirection: {
            xs: "column",
            md: "row",
          },
        }}
      >
        {/* Left branding */}
        <Box
          sx={{
            flex: 1,
            width: "100%",
            maxWidth: {
              xs: 500,
              md: 600,
            },
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            color: "white",
            textAlign: {
              xs: "center",
              md: "left",
            },
          }}
        >
          <Typography
            variant="h2"
            sx={{
              fontWeight: 700,
              lineHeight: 1.1,
              mb: 2,
              fontSize: {
                xs: "2.5rem",
                sm: "3.5rem",
                md: "4rem",
                lg: "4.5rem",
              },
            }}
          >
            Welcome to
            <br />
            SecureBank
          </Typography>

          <Typography
            variant="h6"
            sx={{
              fontWeight: 400,
              maxWidth: 520,
              opacity: 0.95,
              fontSize: {
                xs: "1rem",
                sm: "1.15rem",
                md: "1.25rem",
              },
            }}
          >
            Bank smarter. Bank securely.
          </Typography>

          <Typography
            sx={{
              mt: 2,
              maxWidth: 500,
              opacity: 0.8,
              fontSize: {
                xs: "0.9rem",
                sm: "1rem",
              },
              display: {
                xs: "none",
                sm: "block",
              },
            }}
          >
            Secure digital banking for managing your accounts, transfers,
            beneficiaries, and financial activity.
          </Typography>
        </Box>

        {/* Login/Register content */}
        <Box
          sx={{
            width: "100%",
            maxWidth: 460,
            flexShrink: 0,
          }}
        >
          {children}
        </Box>
      </Box>
    </Box>
  );
};

export default AuthLayout;
