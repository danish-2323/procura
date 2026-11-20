import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext';
import { Lock, Mail, ShieldAlert, Sparkles } from 'lucide-react';
import { Button } from '@/components/ui/button';

const DEMO_USERS = [
  { role: 'SUPER_ADMIN', email: 'admin@procura.local', label: 'Super Admin', desc: 'Platform configuration' },
  { role: 'EMPLOYEE', email: 'employee@procura.local', label: 'Employee', desc: 'Create requisitions' },
  { role: 'DEPARTMENT_MANAGER', email: 'manager.eng@procura.local', label: 'Dept Manager', desc: 'Approve < 50k & own dept' },
  { role: 'FINANCE_MANAGER', email: 'finance@procura.local', label: 'Finance Manager', desc: 'Approve 50k+ & 3-way match' },
  { role: 'PROCUREMENT_OFFICER', email: 'procurement@procura.local', label: 'Procurement', desc: 'RFQ, POs, Suppliers' },
  { role: 'WAREHOUSE_MANAGER', email: 'warehouse@procura.local', label: 'Warehouse Lead', desc: 'Goods receipts & stock' },
  { role: 'DIRECTOR', email: 'director@procura.local', label: 'Director', desc: 'Approvals > 500k' },
  { role: 'AUDITOR', email: 'auditor@procura.local', label: 'Auditor', desc: 'Read-only trail & ledger' },
  { role: 'SUPPLIER', email: 'supplier.dell@procura.local', label: 'Supplier Portal', desc: 'Quotations & Invoices' },
];

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('admin@procura.local');
  const [password, setPassword] = useState('Procura2026!Demo');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(email, password);
      navigate('/');
    } catch (err: any) {
      setError(err?.error?.message || err?.message || 'Authentication failed. Please verify credentials.');
    } finally {
      setSubmitting(false);
    }
  };

  const selectDemoUser = (userEmail: string) => {
    setEmail(userEmail);
    setPassword('Procura2026!Demo');
    setError(null);
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="max-w-4xl w-full grid grid-cols-1 md:grid-cols-2 bg-white rounded-2xl shadow-xl overflow-hidden border border-slate-200">
        {/* Left Side: Login Form */}
        <div className="p-8 flex flex-col justify-center">
          <div className="mb-6">
            <div className="flex items-center space-x-2 mb-2">
              <div className="w-9 h-9 rounded-xl bg-blue-600 flex items-center justify-center font-bold text-white text-xl">
                P
              </div>
              <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Procura 2.0</h1>
            </div>
            <p className="text-slate-500 text-sm">Enterprise Procure-to-Pay Platform</p>
          </div>

          {error && (
            <div className="mb-4 p-3 rounded-lg bg-red-50 border border-red-200 flex items-start space-x-2 text-xs text-red-700">
              <ShieldAlert className="w-4 h-4 shrink-0 mt-0.5 text-red-500" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleLogin} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Corporate Email</label>
              <div className="relative">
                <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className="w-full pl-9 pr-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="user@procura.local"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Password</label>
              <div className="relative">
                <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  className="w-full pl-9 pr-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Min 10 characters (BR-20)"
                />
              </div>
            </div>

            <Button type="submit" disabled={submitting} className="w-full py-2.5 text-sm">
              {submitting ? 'Authenticating...' : 'Sign In'}
            </Button>
          </form>

          <p className="mt-6 text-center text-xs text-slate-400">
            Enforces BR-20: 5 failed attempts trigger a 15-minute lockout.
          </p>
        </div>

        {/* Right Side: Demo Quick-Select Roster */}
        <div className="bg-slate-900 text-slate-200 p-8 flex flex-col justify-between">
          <div>
            <div className="flex items-center space-x-2 mb-3">
              <Sparkles className="w-4 h-4 text-amber-400" />
              <h2 className="text-sm font-semibold tracking-wide uppercase text-slate-300">
                Quick Demo Role Switcher
              </h2>
            </div>
            <p className="text-xs text-slate-400 mb-4">
              Click any persona below to autofill credentials and explore role-scoped views:
            </p>

            <div className="space-y-1.5 max-h-[380px] overflow-y-auto pr-1">
              {DEMO_USERS.map((u) => {
                const isSelected = email === u.email;
                return (
                  <button
                    key={u.role}
                    type="button"
                    onClick={() => selectDemoUser(u.email)}
                    className={`w-full text-left p-2.5 rounded-lg border transition-all text-xs flex items-center justify-between ${
                      isSelected
                        ? 'bg-blue-600/30 border-blue-500 text-white'
                        : 'bg-slate-800/60 border-slate-700/60 text-slate-300 hover:bg-slate-800'
                    }`}
                  >
                    <div>
                      <div className="font-semibold">{u.label}</div>
                      <div className="text-[11px] text-slate-400">{u.desc}</div>
                    </div>
                    <span className="text-[10px] px-1.5 py-0.5 rounded bg-slate-700 font-mono text-slate-300">
                      {u.role.split('_')[0]}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>

          <div className="pt-4 border-t border-slate-800 text-[11px] text-slate-500 flex justify-between">
            <span>Procura 2.0 Identity Slice</span>
            <span>JWT + Rotation</span>
          </div>
        </div>
      </div>
    </div>
  );
};
