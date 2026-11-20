import React from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { Layout } from './layout/Layout';
import { LoginPage } from '@/features/auth/LoginPage';
import { ProtectedRoute, RoleGuard } from '@/features/auth/ProtectedRoute';
import { UsersPage } from '@/features/admin/UsersPage';
import { DepartmentsPage } from '@/features/admin/DepartmentsPage';
import { ConfigPage } from '@/features/admin/ConfigPage';

const PlaceholderPage: React.FC<{ title: string; phase: string }> = ({ title, phase }) => (
  <div className="bg-white rounded-xl border border-slate-200 p-8 shadow-sm">
    <div className="flex items-center justify-between mb-4">
      <h2 className="text-xl font-bold text-slate-800">{title}</h2>
      <span className="text-xs px-2.5 py-1 bg-blue-50 text-blue-700 font-semibold rounded-full border border-blue-200">
        Ships in {phase}
      </span>
    </div>
    <p className="text-slate-600 text-sm">
      Module authenticated with RBAC and department-scope isolation.
    </p>
  </div>
);

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: <ProtectedRoute />,
    children: [
      {
        element: <Layout />,
        children: [
          { index: true, element: <Navigate to="/requisitions" replace /> },
          {
            path: 'requisitions/*',
            element: <PlaceholderPage title="Requisitions & Approval Chains" phase="Phase 2" />,
          },
          {
            path: 'approvals/*',
            element: <PlaceholderPage title="Approval Inbox & Workflow" phase="Phase 2" />,
          },
          {
            path: 'sourcing/*',
            element: <PlaceholderPage title="Suppliers, RFQ & Quotation Matrix" phase="Phase 3" />,
          },
          {
            path: 'orders/*',
            element: <PlaceholderPage title="Purchase Orders & Versioning" phase="Phase 4" />,
          },
          {
            path: 'receiving/*',
            element: <PlaceholderPage title="Goods Receipts & Inventory Quarantine" phase="Phase 4" />,
          },
          {
            path: 'invoices/*',
            element: <PlaceholderPage title="Invoices & Cumulative 3-Way Matching" phase="Phase 5" />,
          },
          {
            path: 'supplier-portal/*',
            element: <PlaceholderPage title="Supplier Self-Service Portal" phase="Phase 3" />,
          },
          {
            path: 'analytics/*',
            element: <PlaceholderPage title="Spend & Budget Analytics" phase="Phase 6" />,
          },
          {
            path: 'alerts/*',
            element: <PlaceholderPage title="Deterministic Anomaly Alerts" phase="Phase 6" />,
          },
          {
            path: 'audit/*',
            element: <PlaceholderPage title="Append-Only Audit Explorer" phase="Phase 6" />,
          },
          {
            path: 'admin',
            element: (
              <RoleGuard allowedRoles={['SUPER_ADMIN']}>
                <div className="space-y-6">
                  <div className="flex border-b border-slate-200 space-x-6 text-sm font-semibold text-slate-600">
                    <a href="#users" className="pb-3 border-b-2 border-blue-600 text-blue-600">Users</a>
                    <a href="#departments" className="pb-3 text-slate-500 hover:text-slate-900">Departments</a>
                    <a href="#config" className="pb-3 text-slate-500 hover:text-slate-900">System Config</a>
                  </div>
                  <div id="users">
                    <UsersPage />
                  </div>
                  <div id="departments" className="pt-8 border-t border-slate-200">
                    <DepartmentsPage />
                  </div>
                  <div id="config" className="pt-8 border-t border-slate-200">
                    <ConfigPage />
                  </div>
                </div>
              </RoleGuard>
            ),
          },
        ],
      },
    ],
  },
]);
