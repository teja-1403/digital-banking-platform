import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";
import Dashboard from "./pages/Dashboard/Dashboard";

import ProtectedRoute from "./routes/ProtectedRoute";
import AdminRoute from "./routes/AdminRoute";
import AdminDashboard from "./pages/Admin/AdminDashboard";
import AppLayout from "./components/layout/AppLayout";

import Accounts from "./pages/Accounts/Accounts";
import Beneficiaries from "./pages/Beneficiaries/Beneficiaries";
import Transfer from "./pages/Transfer/Transfer";
import Transactions from "./pages/Transactions/Transactions";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />

        <Route path="/login" element={<Login />} />

        <Route path="/register" element={<Register />} />

        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<Dashboard />} />

            <Route path="/accounts" element={<Accounts />} />

            <Route path="/beneficiaries" element={<Beneficiaries />} />

            <Route path="/transfer" element={<Transfer />} />

            <Route path="/transactions" element={<Transactions />} />
          </Route>
        </Route>

        <Route element={<AdminRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/admin/dashboard" element={<AdminDashboard />} />
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
