import { ParkingCircle, LogOut } from 'lucide-react';

interface HeaderProps {
  onLogout: () => void;
}

function Header({ onLogout }: HeaderProps) {
  return (
    <header className="bg-white shadow-sm border-b border-slate-200">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="bg-slate-800 p-2 rounded-lg">
              <ParkingCircle className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-xl font-bold text-slate-800">ParkSpot</h1>
              <p className="text-xs text-slate-500">Platformă de Rezervări</p>
            </div>
          </div>

          <nav className="hidden md:flex items-center gap-6">
            <a href="#" className="text-slate-600 hover:text-slate-800 transition-colors">
              Locuri Disponibile
            </a>
            <a href="#" className="text-slate-600 hover:text-slate-800 transition-colors">
              Rezervările Mele
            </a>
            <a href="#" className="text-slate-600 hover:text-slate-800 transition-colors">
              Istoric
            </a>
          </nav>

          <button
            onClick={onLogout}
            className="flex items-center gap-2 bg-slate-800 text-white px-4 py-2 rounded-lg hover:bg-slate-700 transition-colors"
          >
            <LogOut className="w-4 h-4" />
            <span className="hidden sm:inline">Deconectare</span>
          </button>
        </div>
      </div>
    </header>
  );
}

export default Header;
