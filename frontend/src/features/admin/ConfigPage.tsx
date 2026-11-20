import React, { useState, useEffect } from 'react';
import { apiClient } from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Sliders, CheckCircle2, ShieldCheck } from 'lucide-react';

interface ConfigItem {
  key: string;
  value: string;
  description: string;
}

export const ConfigPage: React.FC = () => {
  const [settings, setSettings] = useState<ConfigItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingKey, setEditingKey] = useState<string | null>(null);
  const [editValue, setEditValue] = useState<string>('');
  const [message, setMessage] = useState<string | null>(null);

  const fetchConfig = async () => {
    try {
      setLoading(true);
      const res = await apiClient<{ data: ConfigItem[] }>('/config');
      setSettings(res.data);
    } catch (err) {
      console.error('Failed to load settings', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchConfig();
  }, []);

  const handleSave = async (key: string) => {
    try {
      await apiClient('/config', {
        method: 'PUT',
        body: JSON.stringify({ key, value: editValue }),
      });
      setMessage(`Configuration '${key}' updated successfully`);
      setEditingKey(null);
      fetchConfig();
    } catch (err: any) {
      alert(err?.error?.message || 'Failed to update setting');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-2">
        <Sliders className="w-5 h-5 text-blue-600" />
        <div>
          <h1 className="text-xl font-bold text-slate-900">System Configuration & Governance</h1>
          <p className="text-xs text-slate-500">Tolerances, escalation SLA hours, and matching thresholds</p>
        </div>
      </div>

      {message && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-lg flex items-center space-x-2 text-xs text-emerald-800">
          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
          <span>{message}</span>
        </div>
      )}

      <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
        <div className="p-4 bg-slate-50 border-b border-slate-200 flex items-center space-x-2 text-xs font-semibold text-slate-700">
          <ShieldCheck className="w-4 h-4 text-blue-600" />
          <span>Platform Parameters (Audit trail recorded on change)</span>
        </div>

        <div className="divide-y divide-slate-100 text-xs">
          {loading ? (
            <div className="p-8 text-center text-slate-400">Loading parameters...</div>
          ) : settings.map((item) => (
            <div key={item.key} className="p-4 flex items-center justify-between hover:bg-slate-50/50 transition">
              <div className="max-w-xl">
                <span className="font-mono font-semibold text-slate-800 text-xs">{item.key}</span>
                <p className="text-slate-500 text-[11px] mt-0.5">{item.description}</p>
              </div>

              <div className="flex items-center space-x-3">
                {editingKey === item.key ? (
                  <div className="flex items-center space-x-2">
                    <input
                      type="text"
                      value={editValue}
                      onChange={(e) => setEditValue(e.target.value)}
                      className="p-1.5 border border-slate-300 rounded text-xs w-28 font-mono"
                    />
                    <Button size="sm" onClick={() => handleSave(item.key)} className="h-7 text-xs">Save</Button>
                    <Button variant="outline" size="sm" onClick={() => setEditingKey(null)} className="h-7 text-xs">Cancel</Button>
                  </div>
                ) : (
                  <div className="flex items-center space-x-3">
                    <span className="font-mono font-bold text-slate-900 bg-slate-100 px-2.5 py-1 rounded">
                      {item.value}
                    </span>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => {
                        setEditingKey(item.key);
                        setEditValue(item.value);
                      }}
                      className="h-7 text-xs"
                    >
                      Edit
                    </Button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
