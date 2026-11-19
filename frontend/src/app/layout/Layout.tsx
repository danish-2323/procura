import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
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
} from 'lucide-react';

export const Layout: React.FC = () => {
  const location = useLocation();

  const navItems = [
    { label: 'Requisitions', path: '/requisitions', icon: FileText },
    { label: 'Approvals', path: '/approvals', icon: CheckSquare },
    { label: 'Suppliers & RFQ', path: '/sourcing', icon: Building2 },
    { label: 'Purchase Orders', path: '/orders', icon: Package },
    { label: 'Receiving & Stock', path: '/receiving', icon: Truck },
    { label: 'Invoices & Match', path: '/invoices', icon: Receipt },
    { label: 'Analytics', path: '/analytics', icon: BarChart3 },
    { label: 'Alerts', path: '/alerts', icon: AlertTriangle },
    { label: 'Audit Logs', path: '/audit', icon: History },
    { label: 'Admin', path: '/admin', icon: Settings },
  ];

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

        <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
          {navItems.map((item) => {
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

        {/* AI Assistant Indicator */}
        <div className="p-3 border-t border-slate-800">
          <div className="p-2.5 rounded-lg bg-slate-800/60 border border-slate-700/60 flex items-center justify-between text-xs">
            <div className="flex items-center space-x-2">
              <Bot className="w-4 h-4 text-blue-400" />
              <span className="text-slate-300 font-medium">AI Copilot</span>
            </div>
            <span className="text-[10px] text-slate-400">Advisory Mode</span>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top Navbar */}
        <header className="h-14 border-b border-slate-200 bg-white flex items-center justify-between px-6">
          <div className="text-sm font-medium text-slate-700">
            Enterprise Procurement System
          </div>
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2 text-xs">
              <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
              <span className="text-slate-600 font-medium">INR / ₹ Default</span>
            </div>
            <Link
              to="/login"
              className="flex items-center space-x-1 text-xs text-slate-500 hover:text-red-600 transition"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Switch User</span>
            </Link>
          </div>
        </header>

        {/* Dynamic Route Content */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
