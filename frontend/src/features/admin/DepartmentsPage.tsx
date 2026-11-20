import React, { useState, useEffect } from 'react';
import { apiClient } from '@/lib/api';
import { Button } from '@/components/ui/button';
import { formatCurrency } from '@/lib/utils';
import { Building2, Edit2, CheckCircle2 } from 'lucide-react';

interface Department {
  id: number;
  name: string;
  budgetTotal: number;
  currency: string;
  fiscalYear: string;
  version: number;
}

export const DepartmentsPage: React.FC = () => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingDept, setEditingDept] = useState<Department | null>(null);
  const [newBudget, setNewBudget] = useState<string>('');
  const [message, setMessage] = useState<string | null>(null);

  const fetchDepartments = async () => {
    try {
      setLoading(true);
      const res = await apiClient<{ data: Department[] }>('/departments');
      setDepartments(res.data);
    } catch (err) {
      console.error('Failed to fetch departments', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDepartments();
  }, []);

  const handleUpdateBudget = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingDept) return;
    try {
      await apiClient(`/departments/${editingDept.id}/budget`, {
        method: 'PUT',
        body: JSON.stringify({ budgetTotal: parseFloat(newBudget) }),
      });
      setMessage(`Budget for ${editingDept.name} updated to ₹${parseFloat(newBudget).toLocaleString('en-IN')}`);
      setEditingDept(null);
      fetchDepartments();
    } catch (err: any) {
      alert(err?.error?.message || 'Failed to update department budget');
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-900">Departments & Fiscal Budgets</h1>
        <p className="text-xs text-slate-500">Departmental organizational units, currencies and allocated caps</p>
      </div>

      {message && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-lg flex items-center space-x-2 text-xs text-emerald-800">
          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
          <span>{message}</span>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {loading ? (
          <div className="col-span-3 py-12 text-center text-xs text-slate-400">Loading departments...</div>
        ) : departments.map((dept) => (
          <div key={dept.id} className="bg-white border border-slate-200 rounded-xl p-5 shadow-sm space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <div className="w-7 h-7 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center">
                  <Building2 className="w-4 h-4" />
                </div>
                <h3 className="font-semibold text-slate-900 text-sm">{dept.name}</h3>
              </div>
              <span className="text-[10px] px-2 py-0.5 rounded font-mono bg-slate-100 text-slate-600">
                FY {dept.fiscalYear}
              </span>
            </div>

            <div className="pt-2 border-t border-slate-100 flex items-end justify-between">
              <div>
                <span className="text-[10px] text-slate-400 block uppercase font-medium">Allocated Budget</span>
                <span className="text-base font-bold text-slate-800">
                  {formatCurrency(dept.budgetTotal, dept.currency)}
                </span>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => {
                  setEditingDept(dept);
                  setNewBudget(String(dept.budgetTotal));
                }}
                className="h-7 text-xs flex items-center space-x-1"
              >
                <Edit2 className="w-3 h-3" />
                <span>Adjust</span>
              </Button>
            </div>
          </div>
        ))}
      </div>

      {/* Edit Budget Modal */}
      {editingDept && (
        <div className="fixed inset-0 bg-slate-900/40 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl shadow-xl max-w-sm w-full p-6 border border-slate-200">
            <h3 className="font-bold text-slate-900 mb-1 text-sm">Update Budget: {editingDept.name}</h3>
            <p className="text-xs text-slate-500 mb-4">Optimistic locking checks version {editingDept.version}</p>
            <form onSubmit={handleUpdateBudget} className="space-y-4 text-xs">
              <div>
                <label className="block font-semibold text-slate-700 mb-1">New Total Budget (INR)</label>
                <input
                  type="number"
                  step="0.01"
                  required
                  value={newBudget}
                  onChange={(e) => setNewBudget(e.target.value)}
                  className="w-full p-2 border border-slate-300 rounded-md text-sm"
                />
              </div>
              <div className="flex justify-end space-x-2 pt-2">
                <Button variant="outline" type="button" onClick={() => setEditingDept(null)}>Cancel</Button>
                <Button type="submit">Save Changes</Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
