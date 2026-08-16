import {
  AppBar,
  Box,
  Button,
  Drawer,
  List,
  ListItemButton,
  ListItemText,
  Toolbar,
  Typography,
} from "@mui/material";

import { Link as RouterLink, Outlet, useNavigate } from "react-router-dom";

import { useAuth } from "../../context/AuthContext";

const drawerWidth = 220;

export default function AppLayout() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate("/login", { replace: true });
  };

  return (
    <Box sx={{ minHeight: "100vh" }}>
      <AppBar
        position="fixed"
        sx={{
          zIndex: (theme) => theme.zIndex.drawer + 1,
        }}
      >
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Digital Banking
          </Typography>

          <Typography variant="body2" sx={{ mr: 2 }}>
            {user?.username}
          </Typography>

          <Button color="inherit" onClick={() => void handleLogout()}>
            Logout
          </Button>
        </Toolbar>
      </AppBar>

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
        <Toolbar />

        <List>
          <ListItemButton component={RouterLink} to="/dashboard">
            <ListItemText primary="Dashboard" />
          </ListItemButton>

          <ListItemButton component={RouterLink} to="/accounts">
            <ListItemText primary="Accounts" />
          </ListItemButton>

          <ListItemButton component={RouterLink} to="/beneficiaries">
            <ListItemText primary="Beneficiaries" />
          </ListItemButton>

          <ListItemButton component={RouterLink} to="/transfer">
            <ListItemText primary="Transfer" />
          </ListItemButton>

          <ListItemButton component={RouterLink} to="/transactions">
            <ListItemText primary="Transactions" />
          </ListItemButton>

          {user?.roles.includes("ROLE_ADMIN") && (
            <ListItemButton component={RouterLink} to="/admin/dashboard">
              <ListItemText primary="Admin" />
            </ListItemButton>
          )}
        </List>
      </Drawer>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          ml: `${drawerWidth}px`,
          p: 3,
          pt: 11,
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
}
