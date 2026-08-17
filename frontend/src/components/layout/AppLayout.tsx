import {
  AppBar,
  Box,
  Button,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemText,
  Toolbar,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";

import MenuIcon from "@mui/icons-material/Menu";

import {
  Link as RouterLink,
  Outlet,
  useLocation,
  useNavigate,
} from "react-router-dom";

import { useState } from "react";

import { useAuth } from "../../context/AuthContext";

const drawerWidth = 220;

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

  const navigationItems = [
    {
      label: "Dashboard",
      path: "/dashboard",
    },
    {
      label: "Accounts",
      path: "/accounts",
    },
    {
      label: "Beneficiaries",
      path: "/beneficiaries",
    },
    {
      label: "Transfer",
      path: "/transfer",
    },
    {
      label: "Transactions",
      path: "/transactions",
    },
  ];

  if (user?.roles.includes("ROLE_ADMIN")) {
    navigationItems.push({
      label: "Admin",
      path: "/admin/dashboard",
    });
  }

  const drawerContent = (
    <>
      <Toolbar />

      <List>
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
                mx: 1,
                mb: 0.5,
                borderRadius: 1,
              }}
            >
              <ListItemText primary={item.label} />
            </ListItemButton>
          );
        })}
      </List>
    </>
  );

  return (
    <Box sx={{ minHeight: "100vh" }}>
      <AppBar
        position="fixed"
        sx={{
          zIndex: theme.zIndex.drawer + 1,
        }}
      >
        <Toolbar>
          {isMobile && (
            <IconButton
              color="inherit"
              edge="start"
              onClick={() => setMobileOpen((current) => !current)}
              sx={{ mr: 1 }}
            >
              <MenuIcon />
            </IconButton>
          )}

          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Digital Banking
          </Typography>

          {!isMobile && (
            <Typography variant="body2" sx={{ mr: 2 }}>
              {user?.username}
            </Typography>
          )}

          <Button color="inherit" onClick={() => void handleLogout()}>
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
          p: {
            xs: 2,
            sm: 3,
          },
          pt: {
            xs: 9,
            sm: 10,
          },
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
}
