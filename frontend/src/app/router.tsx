import React from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { Layout } from './layout/Layout';

// Placeholder views for Foundation Phase 0
const PlaceholderPage: React.FC<{ title: string; phase: string }> = ({ title, phase }) => (
  <div className="bg-white rounded-xl border border-slate-200 p-8 shadow-sm">
    <div className="flex items-center justify-between mb-4">
      <h2 className="text-xl font-bold text-slate-800">{title}</h2>
      <span className="text-xs px-2.5 py-1 bg-blue-50 text-blue-700 font-semibold rounded-full border border-blue-200">
        Ships in {phase}
      </span>
    </div>
    <p className="text-slate-600 text-sm">
      This module is wired to the vertical slice architecture and ready for Phase implementation.
    </p>
  </div>
);

export const router = createBrowserRouter([
  {
    path: '/',
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
        path: 'admin/*',
        element: <PlaceholderPage title="Administration & Configuration" phase="Phase 1" />,
      },
    ],
  },
  {
    path: '/login',
    element: <PlaceholderPage title="Authentication & Role Selection" phase="Phase 1" />,
  },
]);
