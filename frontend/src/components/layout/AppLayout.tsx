import {
  AppBar,
  Avatar,
  Box,
  Button,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";

import DashboardOutlinedIcon from "@mui/icons-material/DashboardOutlined";
import AccountBalanceOutlinedIcon from "@mui/icons-material/AccountBalanceOutlined";
import { PeopleOutlined as PeopleOutlineIcon } from "@mui/icons-material";
import SwapHorizOutlinedIcon from "@mui/icons-material/SwapHorizOutlined";
import ReceiptLongOutlinedIcon from "@mui/icons-material/ReceiptLongOutlined";
import AdminPanelSettingsOutlinedIcon from "@mui/icons-material/AdminPanelSettingsOutlined";
import MenuIcon from "@mui/icons-material/Menu";

import {
  Link as RouterLink,
  Outlet,
  useLocation,
  useNavigate,
} from "react-router-dom";

import { useState } from "react";

import { alpha } from "@mui/material/styles";

import { useAuth } from "../../context/AuthContext";

const drawerWidth = 240;

interface NavigationItem {
  label: string;
  path: string;
  icon: React.ReactNode;
}

export default function AppLayout() {
  const theme = useTheme();

  const isMobile = useMediaQuery(theme.breakpoints.down("md"));

  const location = useLocation();
  const navigate = useNavigate();

  const { user, logout } = useAuth();

  const [mobileOpen, setMobileOpen] = useState(false);

  const handleLogout = async () => {
    await logout();

    navigate("/login", {
      replace: true,
    });
  };

  const handleNavigation = () => {
    if (isMobile) {
      setMobileOpen(false);
    }
  };

  const navigationItems: NavigationItem[] = [
    {
      label: "Dashboard",
      path: "/dashboard",
      icon: <DashboardOutlinedIcon />,
    },
    {
      label: "Accounts",
      path: "/accounts",
      icon: <AccountBalanceOutlinedIcon />,
    },
    {
      label: "Beneficiaries",
      path: "/beneficiaries",
      icon: <PeopleOutlineIcon />,
    },
    {
      label: "Transfer",
      path: "/transfer",
      icon: <SwapHorizOutlinedIcon />,
    },
    {
      label: "Transactions",
      path: "/transactions",
      icon: <ReceiptLongOutlinedIcon />,
    },
  ];

  if (user?.roles.includes("ROLE_ADMIN")) {
    navigationItems.push({
      label: "Admin",
      path: "/admin/dashboard",
      icon: <AdminPanelSettingsOutlinedIcon />,
    });
  }

  const username = user?.username ?? "User";

  const avatarLetter = username.charAt(0).toUpperCase();

  const drawerContent = (
    <Box
      sx={{
        height: "100%",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <Toolbar />

      <Box
        sx={{
          px: 2.5,
          py: 2.5,
        }}
      >
        <Typography
          variant="h6"
          sx={{
            fontWeight: 700,
            letterSpacing: "-0.02em",
          }}
        >
          SecureBank
        </Typography>

        <Typography
          variant="body2"
          color="text.secondary"
          sx={{
            mt: 0.25,
          }}
        >
          Digital Banking
        </Typography>
      </Box>

      <Divider />

      <Box
        sx={{
          px: 1.25,
          py: 1.5,
        }}
      >
        <Typography
          variant="caption"
          sx={{
            px: 1.5,
            color: "text.secondary",
            fontWeight: 700,
            letterSpacing: "0.08em",
            textTransform: "uppercase",
          }}
        >
          Banking
        </Typography>
      </Box>

      <List
        disablePadding
        sx={{
          px: 1,
        }}
      >
        {navigationItems.map((item) => {
          const isActive =
            location.pathname === item.path ||
            location.pathname.startsWith(`${item.path}/`);

          return (
            <ListItemButton
              key={item.path}
              component={RouterLink}
              to={item.path}
              selected={isActive}
              onClick={handleNavigation}
              sx={{
                minHeight: 46,
                mb: 0.5,
                px: 1.5,
                borderRadius: 2,

                color: isActive ? "primary.main" : "text.secondary",

                backgroundColor: isActive
                  ? alpha(theme.palette.primary.main, 0.1)
                  : "transparent",

                "&:hover": {
                  backgroundColor: alpha(theme.palette.primary.main, 0.06),
                  color: "primary.main",
                },

                "&.Mui-selected": {
                  backgroundColor: alpha(theme.palette.primary.main, 0.1),
                  color: "primary.main",
                },

                "&.Mui-selected:hover": {
                  backgroundColor: alpha(theme.palette.primary.main, 0.14),
                },

                "&::before": {
                  content: '""',
                  width: 3,
                  height: 24,
                  borderRadius: 999,
                  backgroundColor: isActive
                    ? theme.palette.primary.main
                    : "transparent",
                  mr: 1,
                  ml: -0.5,
                },
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: 38,
                  color: "inherit",
                }}
              >
                {item.icon}
              </ListItemIcon>

              <ListItemText
                primary={item.label}
                sx={{
                  "& .MuiListItemText-primary": {
                    fontWeight: isActive ? 600 : 500,
                    fontSize: "0.95rem",
                  },
                }}
              />
            </ListItemButton>
          );
        })}
      </List>

      <Box sx={{ flexGrow: 1 }} />

      <Divider />

      <Box
        sx={{
          p: 2,
          display: "flex",
          alignItems: "center",
          gap: 1.25,
        }}
      >
        <Avatar
          sx={{
            width: 36,
            height: 36,
            fontSize: "0.9rem",
            bgcolor: "primary.main",
          }}
        >
          {avatarLetter}
        </Avatar>

        <Box
          sx={{
            minWidth: 0,
          }}
        >
          <Typography
            variant="body2"
            sx={{
              fontWeight: 600,
              overflow: "hidden",
              textOverflow: "ellipsis",
              whiteSpace: "nowrap",
            }}
          >
            {username}
          </Typography>

          <Typography variant="caption" color="text.secondary">
            {user?.roles.includes("ROLE_ADMIN") ? "Administrator" : "Customer"}
          </Typography>
        </Box>
      </Box>
    </Box>
  );

  return (
    <Box
      sx={{
        minHeight: "100vh",
        backgroundColor: "background.default",
      }}
    >
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          zIndex: theme.zIndex.drawer + 1,
          backgroundColor: "background.paper",
          color: "text.primary",
          borderBottom: `1px solid ${theme.palette.divider}`,
        }}
      >
        <Toolbar
          sx={{
            minHeight: {
              xs: 64,
              md: 72,
            },
          }}
        >
          {isMobile && (
            <IconButton
              edge="start"
              onClick={() => setMobileOpen((current) => !current)}
              sx={{
                mr: 1,
                color: "text.primary",
              }}
              aria-label="open navigation menu"
            >
              <MenuIcon />
            </IconButton>
          )}

          <Typography
            variant="h6"
            sx={{
              flexGrow: 1,
              fontWeight: 700,
              letterSpacing: "-0.02em",
            }}
          >
            SecureBank
          </Typography>

          {!isMobile && (
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                gap: 1,
                mr: 2,
              }}
            >
              <Avatar
                sx={{
                  width: 34,
                  height: 34,
                  fontSize: "0.85rem",
                  bgcolor: "primary.main",
                }}
              >
                {avatarLetter}
              </Avatar>

              <Typography
                variant="body2"
                sx={{
                  fontWeight: 600,
                }}
              >
                {username}
              </Typography>
            </Box>
          )}

          <Button
            variant="outlined"
            color="inherit"
            onClick={() => void handleLogout()}
            sx={{
              borderColor: "divider",
              color: "text.primary",
              "&:hover": {
                borderColor: "text.secondary",
                backgroundColor: alpha(theme.palette.text.primary, 0.04),
              },
            }}
          >
            Logout
          </Button>
        </Toolbar>
      </AppBar>

      {isMobile ? (
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={() => setMobileOpen(false)}
          ModalProps={{
            keepMounted: true,
          }}
          sx={{
            "& .MuiDrawer-paper": {
              width: drawerWidth,
              boxSizing: "border-box",
              borderRight: `1px solid ${theme.palette.divider}`,
            },
          }}
        >
          {drawerContent}
        </Drawer>
      ) : (
        <Drawer
          variant="permanent"
          sx={{
            width: drawerWidth,
            flexShrink: 0,
            "& .MuiDrawer-paper": {
              width: drawerWidth,
              boxSizing: "border-box",
              borderRight: `1px solid ${theme.palette.divider}`,
            },
          }}
        >
          {drawerContent}
        </Drawer>
      )}

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          ml: isMobile ? 0 : `${drawerWidth}px`,
          minHeight: "100vh",
          p: {
            xs: 2,
            sm: 3,
            md: 4,
          },
          pt: {
            xs: 10,
            sm: 11,
            md: 12,
          },
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
}
