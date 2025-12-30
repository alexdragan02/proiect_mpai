import { useEffect, useMemo, useState } from 'react';
import { X, Clock, MapPin, CreditCard, Download, Info } from 'lucide-react';
import type { PayReservationRequest, ReservationResponse, PaymentResponse } from '../api';
import { ParkingSpot } from '../App';

interface ReservationModalProps {
  spot: ParkingSpot;
  onClose: () => void;

  currentUsername: string;

  onLoadSpotReservation: (spotId: number) => Promise<ReservationResponse | null>;

  onLoadReservationPayment: (reservationId: number) => Promise<PaymentResponse | null>;

  onCreateReservation: (spotId: number, hours: number) => Promise<ReservationResponse>;
  onPayReservation: (reservationId: number, payload: PayReservationRequest) => Promise<PaymentResponse>;
  onCancelReservation: (reservationId: number) => Promise<void>;
  onReleaseReservation: (reservationId: number) => Promise<void>;
  onDownloadReceipt: (paymentId: number) => Promise<void>;
}

export default function ReservationModal({
  spot,
  onClose,
  currentUsername,
  onLoadSpotReservation,
  onLoadReservationPayment,
  onCreateReservation,
  onPayReservation,
  onCancelReservation,
  onReleaseReservation,
  onDownloadReceipt,
}: ReservationModalProps) {
  const [hours, setHours] = useState(2);

  const [reservation, setReservation] = useState<ReservationResponse | null>(null);
  const [payment, setPayment] = useState<PaymentResponse | null>(null);

  const [loading, setLoading] = useState(false);
  const [loadingReservation, setLoadingReservation] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [cardNumber, setCardNumber] = useState('1234 5678 9012 3456');
  const [exp, setExp] = useState('12/30');
  const [cvv, setCvv] = useState('123');
  const [holderName, setHolderName] = useState('Ion Popescu');

  const totalPrice = useMemo(() => spot.pricePerHour * hours, [spot.pricePerHour, hours]);

  const isOwner = reservation ? reservation.username === currentUsername : false;

  useEffect(() => {
    let cancelled = false;

    const load = async () => {
      setError(null);
      setPayment(null);
      setReservation(null);

      if (spot.status === 'available') return;

      setLoadingReservation(true);
      try {
        const r = await onLoadSpotReservation(spot.id);

        if (cancelled) return;
        setReservation(r);

        if (r && r.status === 'PAID') {
          const p = await onLoadReservationPayment(r.id);
          if (!cancelled) setPayment(p);
        }
      } catch (e: any) {
        if (!cancelled) setError(e?.message ?? 'Eroare la încărcarea detaliilor');
      } finally {
        if (!cancelled) setLoadingReservation(false);
      }
    };

    load();
    return () => {
      cancelled = true;
    };
  }, [spot.id, spot.status, onLoadSpotReservation, onLoadReservationPayment]);

  const doReserve = async () => {
    setError(null);
    setLoading(true);
    try {
      const r = await onCreateReservation(spot.id, hours);
      setReservation(r);
      setPayment(null);
      alert('Rezervare creată. Locul este acum rezervat (galben).');
    } catch (e: any) {
      setError(e?.message ?? 'Eroare la rezervare');
    } finally {
      setLoading(false);
    }
  };

  const doCancel = async () => {
    if (!reservation) return;
    setError(null);
    setLoading(true);
    try {
      await onCancelReservation(reservation.id);
      alert('Rezervarea a fost anulată. Locul este din nou disponibil (verde).');
      onClose();
    } catch (e: any) {
      setError(e?.message ?? 'Eroare la anulare');
    } finally {
      setLoading(false);
    }
  };

  const doPay = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!reservation) return;

    setError(null);
    setLoading(true);
    try {
      const payload: PayReservationRequest = { method: 'MOCK' };
     
      const p = await onPayReservation(reservation.id, payload);
      setPayment(p);

      setReservation({ ...reservation, status: 'PAID' });

      alert('Plata OK. Locul este acum ocupat (roșu).');
    } catch (e: any) {
      setError(e?.message ?? 'Eroare la plată');
    } finally {
      setLoading(false);
    }
  };

  const doRelease = async () => {
    if (!reservation) return;
    setError(null);
    setLoading(true);
    try {
      await onReleaseReservation(reservation.id);
      alert('Locul a fost eliberat. Devine din nou disponibil (verde).');
      onClose();
    } catch (e: any) {
      setError(e?.message ?? 'Eroare la eliberare');
    } finally {
      setLoading(false);
    }
  };

  const doDownload = async () => {
    if (!payment) return;
    setError(null);
    setLoading(true);
    try {
      await onDownloadReceipt(payment.id);
    } catch (e: any) {
      setError(e?.message ?? 'Eroare la descărcarea receipt-ului');
    } finally {
      setLoading(false);
    }
  };

  const headerTitle =
    spot.status === 'available' ? 'Loc disponibil' :
    spot.status === 'reserved' ? 'Loc rezervat' :
    'Loc ocupat';

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-slate-800">{headerTitle}</h2>
            <button
              onClick={onClose}
              disabled={loading}
              className="text-slate-400 hover:text-slate-600 transition-colors disabled:opacity-60"
            >
              <X className="w-6 h-6" />
            </button>
          </div>

          {error && (
            <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <div className="bg-slate-50 border border-slate-200 rounded-lg p-4 mb-6">
            <div className="flex items-center gap-3 mb-2">
              <MapPin className="w-5 h-5 text-slate-700" />
              <span className="font-semibold text-slate-800">
                {spot.number} - {spot.zone}
              </span>
            </div>
            <p className="text-slate-600 text-sm">
              Preț: <span className="font-semibold">{spot.pricePerHour} RON/oră</span>
            </p>
            <p className="text-slate-600 text-sm">
              Status: <span className="font-semibold">{spot.status}</span>
            </p>
          </div>

          {(spot.status !== 'available') && (
            <div className="mb-6">
              {loadingReservation ? (
                <div className="text-slate-600 text-sm">Se încarcă detaliile rezervării...</div>
              ) : reservation ? (
                <div className="rounded-lg border border-slate-200 bg-white p-4">
                  <p className="text-sm text-slate-600">Rezervare #{reservation.id}</p>
                  <p className="text-sm text-slate-600">
                    Utilizator (owner): <span className="font-semibold">{reservation.username}</span>
                  </p>
                  <p className="text-sm text-slate-600">
                    Status rezervare: <span className="font-semibold">{reservation.status}</span>
                  </p>
                  <p className="text-sm text-slate-600">
                    Total: <span className="font-semibold">{reservation.totalPrice} RON</span>
                  </p>

                  {!isOwner && (
                    <div className="mt-3 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-xs text-amber-800 flex gap-2">
                      <Info className="w-4 h-4 mt-[1px]" />
                      <div>
                        Acest loc este gestionat de <b>{reservation.username}</b>. Doar owner-ul poate plăti / anula / elibera.
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                <div className="text-slate-600 text-sm">
                  Nu am găsit rezervare asociată acestui loc (backend-ul trebuie să o furnizeze).
                </div>
              )}
            </div>
          )}

          {spot.status === 'available' && (
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  <Clock className="w-4 h-4 inline mr-1" />
                  Durată rezervare (ore)
                </label>
                <input
                  type="number"
                  min="1"
                  max="24"
                  value={hours}
                  onChange={(e) => setHours(Number(e.target.value))}
                  className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="bg-emerald-50 border border-emerald-200 rounded-lg p-4">
                <div className="flex justify-between items-center">
                  <span className="text-slate-700">Total:</span>
                  <span className="text-2xl font-bold text-emerald-600">{totalPrice} RON</span>
                </div>
              </div>

              <button
                onClick={doReserve}
                disabled={loading}
                className="w-full bg-emerald-600 text-white py-3 rounded-lg font-semibold hover:bg-emerald-700 disabled:opacity-60"
              >
                {loading ? 'Se rezervă...' : 'REZERVĂ'}
              </button>
            </div>
          )}

          {spot.status === 'reserved' && reservation && reservation.status === 'CREATED' && isOwner && (
            <div className="space-y-4">
              <form onSubmit={doPay} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">
                    <CreditCard className="w-4 h-4 inline mr-1" />
                    Card (UI demo)
                  </label>
                  <input
                    type="text"
                    value={cardNumber}
                    onChange={(e) => setCardNumber(e.target.value)}
                    className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-2">Exp</label>
                    <input
                      type="text"
                      value={exp}
                      onChange={(e) => setExp(e.target.value)}
                      className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-2">CVV</label>
                    <input
                      type="text"
                      value={cvv}
                      onChange={(e) => setCvv(e.target.value)}
                      className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Nume</label>
                  <input
                    type="text"
                    value={holderName}
                    onChange={(e) => setHolderName(e.target.value)}
                    className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-emerald-600 text-white py-3 rounded-lg font-semibold hover:bg-emerald-700 disabled:opacity-60"
                >
                  {loading ? 'Se procesează...' : 'PLĂTEȘTE'}
                </button>
              </form>

              <button
                onClick={doCancel}
                disabled={loading}
                className="w-full bg-slate-200 text-slate-800 py-3 rounded-lg font-semibold hover:bg-slate-300 disabled:opacity-60"
              >
                ANULEAZĂ REZERVAREA
              </button>
            </div>
          )}

          {spot.status === 'occupied' && reservation && isOwner && (
            <div className="space-y-4">
              {}
              {payment && (
                <button
                  onClick={doDownload}
                  disabled={loading}
                  className="w-full flex items-center justify-center gap-2 bg-white border border-slate-200 py-3 rounded-lg font-semibold hover:bg-slate-50 disabled:opacity-60"
                >
                  <Download className="w-4 h-4" />
                  Descarcă Receipt
                </button>
              )}

              <button
                onClick={doRelease}
                disabled={loading}
                className="w-full bg-slate-800 text-white py-3 rounded-lg font-semibold hover:bg-slate-700 disabled:opacity-60"
              >
                {loading ? 'Se eliberează...' : 'ELIBEREAZĂ LOCUL'}
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
