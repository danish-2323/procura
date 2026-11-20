import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { useAuth } from '@/features/auth/AuthContext';
import {
  FileText,
  CheckSquare,
  Truck,
  Building2,
  Package,
  Receipt,
  BarChart3,
  AlertTriangle,
  History,
  Settings,
  Bot,
  LogOut,
  User as UserIcon,
} from 'lucide-react';

export const Layout: React.FC = () => {
  const location = useLocation();
  const { user, logout } = useAuth();

  const allNavItems = [
    { label: 'Requisitions', path: '/requisitions', icon: FileText, roles: ['SUPER_ADMIN', 'EMPLOYEE', 'DEPARTMENT_MANAGER', 'AUDITOR'] },
    { label: 'Approvals', path: '/approvals', icon: CheckSquare, roles: ['SUPER_ADMIN', 'DEPARTMENT_MANAGER', 'FINANCE_MANAGER', 'DIRECTOR', 'AUDITOR'] },
    { label: 'Suppliers & RFQ', path: '/sourcing', icon: Building2, roles: ['SUPER_ADMIN', 'PROCUREMENT_OFFICER', 'AUDITOR'] },
    { label: 'Purchase Orders', path: '/orders', icon: Package, roles: ['SUPER_ADMIN', 'PROCUREMENT_OFFICER', 'WAREHOUSE_MANAGER', 'FINANCE_MANAGER', 'AUDITOR'] },
    { label: 'Receiving & Stock', path: '/receiving', icon: Truck, roles: ['SUPER_ADMIN', 'WAREHOUSE_MANAGER', 'AUDITOR'] },
    { label: 'Invoices & Match', path: '/invoices', icon: Receipt, roles: ['SUPER_ADMIN', 'FINANCE_MANAGER', 'PROCUREMENT_OFFICER', 'AUDITOR'] },
    { label: 'Supplier Portal', path: '/supplier-portal', icon: Package, roles: ['SUPPLIER'] },
    { label: 'Analytics', path: '/analytics', icon: BarChart3, roles: ['SUPER_ADMIN', 'DEPARTMENT_MANAGER', 'FINANCE_MANAGER', 'PROCUREMENT_OFFICER', 'DIRECTOR', 'AUDITOR'] },
    { label: 'Alerts', path: '/alerts', icon: AlertTriangle, roles: ['SUPER_ADMIN', 'FINANCE_MANAGER', 'AUDITOR'] },
    { label: 'Audit Logs', path: '/audit', icon: History, roles: ['SUPER_ADMIN', 'AUDITOR'] },
    { label: 'Admin', path: '/admin', icon: Settings, roles: ['SUPER_ADMIN'] },
  ];

  const visibleNavItems = allNavItems.filter((item) => {
    if (!user) return false;
    return item.roles.includes(user.role);
  });

  return (
    <div className="flex h-screen bg-slate-50 overflow-hidden">
      {/* Sidebar */}
      <aside className="w-64 bg-slate-900 text-slate-100 flex flex-col">
        <div className="p-4 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center font-bold text-white text-lg">
              P
            </div>
            <div>
              <h1 className="font-semibold text-white tracking-wide text-sm">Procura 2.0</h1>
              <span className="text-xs text-slate-400">Procure-to-Pay</span>
            </div>
          </div>
          <span className="px-1.5 py-0.5 rounded text-[10px] font-semibold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
            v2.0
          </span>
        </div>

        {/* User Badge */}
        {user && (
          <div className="p-3 border-b border-slate-800/80 bg-slate-950/40">
            <div className="flex items-center space-x-2.5">
              <div className="w-7 h-7 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-slate-300">
                <UserIcon className="w-3.5 h-3.5" />
              </div>
              <div className="overflow-hidden">
                <div className="text-xs font-semibold text-white truncate">{user.fullName}</div>
                <div className="text-[10px] text-blue-400 font-mono tracking-tight font-semibold">{user.role}</div>
              </div>
            </div>
          </div>
        )}

        <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
          {visibleNavItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname.startsWith(item.path);
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center space-x-3 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-blue-600 text-white'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                }`}
              >
                <Icon className="w-4 h-4 shrink-0" />
                <span>{item.label}</span>
              </Link>
            );
          })}
        </nav>

        {/* AI Assistant Mode */}
        <div className="p-3 border-t border-slate-800">
          <div className="p-2.5 rounded-lg bg-slate-800/60 border border-slate-700/60 flex items-center justify-between text-xs">
            <div className="flex items-center space-x-2">
              <Bot className="w-4 h-4 text-blue-400" />
              <span className="text-slate-300 font-medium">AI Copilot</span>
            </div>
            <span className="text-[10px] text-slate-400">Advisory</span>
          </div>
        </div>
      </aside>

      {/* Main Area */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top Header */}
        <header className="h-14 border-b border-slate-200 bg-white flex items-center justify-between px-6">
          <div className="flex items-center space-x-3">
            <span className="text-xs font-semibold text-slate-600 uppercase tracking-wide">
              {user?.departmentId ? `Dept Scope: Dept #${user.departmentId}` : 'Global Enterprise Scope'}
            </span>
          </div>

          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2 text-xs bg-slate-100 px-2.5 py-1 rounded-full border border-slate-200">
              <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
              <span className="text-slate-600 font-medium">INR / ₹ Default</span>
            </div>

            <button
              onClick={logout}
              className="flex items-center space-x-1.5 text-xs font-medium text-slate-600 hover:text-red-600 transition"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Sign Out</span>
            </button>
          </div>
        </header>

        {/* Main Content */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
