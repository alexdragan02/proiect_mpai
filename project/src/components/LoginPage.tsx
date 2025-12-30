import { useState } from 'react';
import { ParkingCircle } from 'lucide-react';

interface LoginPageProps {
  onLogin: (username: string, password: string) => Promise<void>;
}

function LoginPage({ onLogin }: LoginPageProps) {
  const [username, setUsername] = useState('ion');
  const [password, setPassword] = useState('parola');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await onLogin(username, password);
    } catch (err: any) {
      setError(err?.message ?? 'Login failed');
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-xl shadow-lg p-8">
          <div className="flex items-center justify-center mb-8">
            <div className="bg-slate-800 p-3 rounded-lg">
              <ParkingCircle className="w-8 h-8 text-white" />
            </div>
            <div className="ml-3">
              <h1 className="text-2xl font-bold text-slate-800">ParkSpot</h1>
              <p className="text-xs text-slate-500">Platformă de Rezervări</p>
            </div>
          </div>

          <h2 className="text-3xl font-bold text-slate-800 mb-2 text-center">
            Bun venit
          </h2>
          <p className="text-slate-600 text-center mb-8">
            Conectează-te (HTTP Basic) pentru a accesa locurile de parcare
          </p>

          {error && (
            <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-slate-700 mb-2">
                Username
              </label>
              <input
                id="username"
                type="text"
                placeholder="ion"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full px-4 py-3 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-colors"
                required
              />
              <p className="text-xs text-slate-500 mt-1">Ex: ion / parola sau admin / admin</p>
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-slate-700 mb-2">
                Parolă
              </label>
              <input
                id="password"
                type="password"
                placeholder="parola"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-3 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-colors"
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-emerald-600 text-white py-3 rounded-lg font-semibold hover:bg-emerald-700 transition-colors mt-6 disabled:opacity-60"
            >
              {loading ? 'Se conectează...' : 'Conectează-te'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
