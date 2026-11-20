import React, { useState, useEffect } from 'react';
import { apiClient } from '@/lib/api';
import { User } from '@/types';
import { Button } from '@/components/ui/button';
import { Lock, Unlock, UserPlus, Trash2, CheckCircle2 } from 'lucide-react';

export const UsersPage: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  // New user form state
  const [email, setEmail] = useState('');
  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('Procura2026!Demo');
  const [roleId, setRoleId] = useState<number>(2); // Default EMPLOYEE
  const [departmentId, setDepartmentId] = useState<number | undefined>(1); // Engineering

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await apiClient<{ data: { content: User[] } }>('/users?size=50');
      setUsers(res.data.content);
    } catch (err) {
      console.error('Failed to fetch users', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await apiClient('/users', {
        method: 'POST',
        body: JSON.stringify({ email, fullName, password, roleId, departmentId }),
      });
      setShowCreateModal(false);
      setMessage('User created successfully');
      fetchUsers();
    } catch (err: any) {
      alert(err?.error?.message || 'Failed to create user');
    }
  };

  const handleUnlock = async (userId: number) => {
    try {
      await apiClient(`/users/${userId}/unlock`, { method: 'POST' });
      setMessage('User account unlocked successfully');
      fetchUsers();
    } catch (err: any) {
      alert(err?.error?.message || 'Failed to unlock user');
    }
  };

  const handleDelete = async (userId: number) => {
    if (!confirm('Are you sure you want to soft-delete this user? (BR-18)')) return;
    try {
      await apiClient(`/users/${userId}`, { method: 'DELETE' });
      setMessage('User deleted (soft-delete)');
      fetchUsers();
    } catch (err: any) {
      alert(err?.error?.message || 'Failed to delete user');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900">User Management</h1>
          <p className="text-xs text-slate-500">Configure enterprise identities, RBAC roles and account locks</p>
        </div>
        <Button onClick={() => setShowCreateModal(true)} className="flex items-center space-x-1.5 text-xs">
          <UserPlus className="w-4 h-4" />
          <span>New User</span>
        </Button>
      </div>

      {message && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-lg flex items-center space-x-2 text-xs text-emerald-800">
          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
          <span>{message}</span>
        </div>
      )}

      {/* Users Table */}
      <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 border-b border-slate-200 text-slate-600 font-semibold uppercase">
            <tr>
              <th className="py-3 px-4">User</th>
              <th className="py-3 px-4">Role</th>
              <th className="py-3 px-4">Department / Scope</th>
              <th className="py-3 px-4">Status</th>
              <th className="py-3 px-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {loading ? (
              <tr>
                <td colSpan={5} className="py-8 text-center text-slate-400">Loading users...</td>
              </tr>
            ) : users.map((u: any) => (
              <tr key={u.id} className="hover:bg-slate-50/60 transition">
                <td className="py-3 px-4">
                  <div className="font-semibold text-slate-900">{u.fullName}</div>
                  <div className="text-slate-400 text-[11px]">{u.email}</div>
                </td>
                <td className="py-3 px-4">
                  <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-blue-50 text-blue-700 border border-blue-200">
                    {u.role}
                  </span>
                </td>
                <td className="py-3 px-4 text-slate-600">
                  {u.departmentName || u.warehouseCode || 'Global Scope'}
                </td>
                <td className="py-3 px-4">
                  {u.status === 'LOCKED' ? (
                    <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-red-100 text-red-700 flex items-center w-fit space-x-1">
                      <Lock className="w-3 h-3" />
                      <span>Locked (5 fails)</span>
                    </span>
                  ) : (
                    <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-50 text-emerald-700">
                      Active
                    </span>
                  )}
                </td>
                <td className="py-3 px-4 text-right space-x-2">
                  {u.status === 'LOCKED' && (
                    <Button variant="outline" size="sm" onClick={() => handleUnlock(u.id)} className="h-7 text-xs">
                      <Unlock className="w-3.5 h-3.5 mr-1 text-emerald-600" />
                      Unlock
                    </Button>
                  )}
                  <Button variant="ghost" size="sm" onClick={() => handleDelete(u.id)} className="h-7 text-xs text-red-600 hover:bg-red-50">
                    <Trash2 className="w-3.5 h-3.5" />
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Create User Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-slate-900/40 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl shadow-xl max-w-md w-full p-6 border border-slate-200">
            <h3 className="font-bold text-slate-900 mb-4 text-base">Provision New Enterprise User</h3>
            <form onSubmit={handleCreateUser} className="space-y-3 text-xs">
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Corporate Email</label>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full p-2 border border-slate-300 rounded-md"
                  placeholder="name@procura.local"
                />
              </div>
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Full Name</label>
                <input
                  type="text"
                  required
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="w-full p-2 border border-slate-300 rounded-md"
                  placeholder="e.g. John Doe"
                />
              </div>
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Temporary Password (BR-20, min 10 chars)</label>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full p-2 border border-slate-300 rounded-md"
                />
              </div>
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Role Assignment</label>
                <select
                  value={roleId}
                  onChange={(e) => setRoleId(Number(e.target.value))}
                  className="w-full p-2 border border-slate-300 rounded-md"
                >
                  <option value={1}>SUPER_ADMIN</option>
                  <option value={2}>EMPLOYEE</option>
                  <option value={3}>DEPARTMENT_MANAGER</option>
                  <option value={4}>FINANCE_MANAGER</option>
                  <option value={5}>PROCUREMENT_OFFICER</option>
                  <option value={6}>WAREHOUSE_MANAGER</option>
                  <option value={7}>DIRECTOR</option>
                  <option value={8}>AUDITOR</option>
                  <option value={9}>SUPPLIER</option>
                </select>
              </div>
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Department Scope</label>
                <select
                  value={departmentId || ''}
                  onChange={(e) => setDepartmentId(e.target.value ? Number(e.target.value) : undefined)}
                  className="w-full p-2 border border-slate-300 rounded-md"
                >
                  <option value="">Global / Unassigned</option>
                  <option value={1}>Engineering</option>
                  <option value={2}>Operations</option>
                  <option value={3}>Marketing</option>
                  <option value={4}>Human Resources</option>
                  <option value={5}>Finance</option>
                  <option value={6}>Procurement</option>
                  <option value={7}>Information Technology</option>
                  <option value={8}>Facilities</option>
                </select>
              </div>
              <div className="flex justify-end space-x-2 pt-4">
                <Button variant="outline" type="button" onClick={() => setShowCreateModal(false)}>Cancel</Button>
                <Button type="submit">Create User</Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
