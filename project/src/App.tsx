import { useEffect, useMemo, useState } from 'react';
import Header from './components/Header';
import ParkingGrid from './components/ParkingGrid';
import ReservationModal from './components/ReservationModal';
import LoginPage from './components/LoginPage';
import { api, buildBasicAuth, type BackendParkingSpot, type PaymentResponse, type ReservationResponse } from './api';

export interface ParkingSpot {
  id: number;
  number: string;
  zone: string;
  status: 'available' | 'occupied' | 'reserved';
  pricePerHour: number;
}

function mapSpot(s: BackendParkingSpot): ParkingSpot {
  return {
    id: s.id,
    number: s.number,
    zone: s.zone,
    pricePerHour: s.pricePerHour,
    status:
      s.status === 'AVAILABLE' ? 'available' :
      s.status === 'OCCUPIED' ? 'occupied' :
      'reserved',
  };
}

function App() {
  const [auth, setAuth] = useState<string | null>(() => localStorage.getItem('auth'));
  const [username, setUsername] = useState<string | null>(() => localStorage.getItem('username'));

  const [spots, setSpots] = useState<ParkingSpot[]>([]);
  const [loadingSpots, setLoadingSpots] = useState(false);
  const [spotsError, setSpotsError] = useState<string | null>(null);

  const [ownersBySpotId, setOwnersBySpotId] = useState<Record<number, string | undefined>>({});

  const [selectedSpot, setSelectedSpot] = useState<ParkingSpot | null>(null);
  const [showModal, setShowModal] = useState(false);

  const isLoggedIn = !!auth;

  const refreshOwnersForSpots = async (token: string, spotsList: ParkingSpot[]) => {
    const need = spotsList.filter(s => s.status !== 'available');

    if (need.length === 0) {
      setOwnersBySpotId({});
      return;
    }

    const results = await Promise.all(
      need.map(async (s) => {
        try {
          const r = await api.getReservationBySpot(token, s.id);
          return { spotId: s.id, owner: r.username };
        } catch {
          return { spotId: s.id, owner: undefined };
        }
      })
    );

    const map: Record<number, string | undefined> = {};
    for (const item of results) map[item.spotId] = item.owner;

    setOwnersBySpotId(map);
  };

  const refreshSpots = async (token: string) => {
    setSpotsError(null);
    setLoadingSpots(true);
    try {
      const data = await api.getSpots(token);
      const mapped = data.map(mapSpot);
      setSpots(mapped);
      await refreshOwnersForSpots(token, mapped);
    } catch (e: any) {
      setSpotsError(e?.message ?? 'Eroare la încărcarea locurilor');
    } finally {
      setLoadingSpots(false);
    }
  };

  useEffect(() => {
    if (auth) refreshSpots(auth);
  }, [auth]);

  const handleSpotClick = (spot: ParkingSpot) => {
    setSelectedSpot(spot);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedSpot(null);
  };

  const handleLogin = async (u: string, p: string) => {
    const token = buildBasicAuth(u, p);
    await api.getSpots(token);

    localStorage.setItem('auth', token);
    localStorage.setItem('username', u);
    setAuth(token);
    setUsername(u);
  };

  const handleLogout = () => {
    localStorage.removeItem('auth');
    localStorage.removeItem('username');
    setAuth(null);
    setUsername(null);
    setSpots([]);
    setOwnersBySpotId({});
    handleCloseModal();
  };

  const modalHandlers = useMemo(() => {
    if (!auth) return null;

    return {
      onLoadSpotReservation: async (spotId: number): Promise<ReservationResponse | null> => {
        try {
          return await api.getReservationBySpot(auth, spotId);
        } catch {
          return null;
        }
      },

      onLoadReservationPayment: async (reservationId: number): Promise<PaymentResponse | null> => {
        try {
          return await api.getPaymentByReservation(auth, reservationId);
        } catch {
          return null;
        }
      },

      onCreateReservation: async (spotId: number, hours: number) => {
        const r = await api.createReservation(auth, { spotId, hours });
        await refreshSpots(auth);
        return r;
      },

      onPayReservation: async (reservationId: number, payload: any): Promise<PaymentResponse> => {
        const payment = await api.payReservation(auth, reservationId, payload);
        await refreshSpots(auth);
        return payment;
      },

      onCancelReservation: async (reservationId: number) => {
        await api.cancelReservation(auth, reservationId);
        await refreshSpots(auth);
      },

      onReleaseReservation: async (reservationId: number) => {
        await api.releaseReservation(auth, reservationId);
        await refreshSpots(auth);
      },

      onDownloadReceipt: async (paymentId: number) => {
        await api.downloadReceipt(auth, paymentId);
      },
    };
  }, [auth]);

  if (!isLoggedIn) {
    return <LoginPage onLogin={handleLogin} />;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      <Header onLogout={handleLogout} />
      <main className="container mx-auto px-4 py-8">
        <div className="mb-8 flex items-end justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold text-slate-800 mb-2">
              Locuri de Parcare Disponibile
            </h1>
            <p className="text-slate-600">
              Autentificat ca <span className="font-semibold">{username}</span>. Apasă pe orice loc pentru detalii / acțiuni.
            </p>
          </div>

          <button
            onClick={() => auth && refreshSpots(auth)}
            className="bg-white border border-slate-200 px-4 py-2 rounded-lg shadow-sm hover:bg-slate-50 transition-colors"
          >
            Refresh
          </button>
        </div>

        <div className="mb-6 flex flex-wrap gap-6 bg-white p-4 rounded-lg shadow-sm">
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded bg-emerald-500"></div>
            <span className="text-sm text-slate-600">Disponibil</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded bg-amber-500"></div>
            <span className="text-sm text-slate-600">Rezervat</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded bg-red-500"></div>
            <span className="text-sm text-slate-600">Ocupat</span>
          </div>
        </div>

        {spotsError && (
          <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {spotsError}
          </div>
        )}

        {loadingSpots ? (
          <div className="text-slate-600">Se încarcă locurile de parcare...</div>
        ) : (
          <ParkingGrid
            spots={spots}
            onSpotClick={handleSpotClick}
            ownersBySpotId={ownersBySpotId}
          />
        )}
      </main>

      {showModal && selectedSpot && modalHandlers && username && (
        <ReservationModal
          spot={selectedSpot}
          currentUsername={username}
          onClose={handleCloseModal}
          onLoadSpotReservation={modalHandlers.onLoadSpotReservation}
          onLoadReservationPayment={modalHandlers.onLoadReservationPayment}
          onCreateReservation={modalHandlers.onCreateReservation}
          onPayReservation={modalHandlers.onPayReservation}
          onCancelReservation={modalHandlers.onCancelReservation}
          onReleaseReservation={modalHandlers.onReleaseReservation}
          onDownloadReceipt={modalHandlers.onDownloadReceipt}
        />
      )}
    </div>
  );
}

export default App;
