export const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080';

export type BackendParkingSpotStatus = 'AVAILABLE' | 'RESERVED' | 'OCCUPIED';

export interface BackendParkingSpot {
  id: number;
  number: string;
  zone: string;
  status: BackendParkingSpotStatus;
  pricePerHour: number;
}

export interface CreateReservationRequest {
  spotId: number;
  hours: number;
}

export interface ReservationResponse {
  id: number;
  username: string;
  spotId: number;
  spotNumber: string;
  zone: string;
  hours: number;
  totalPrice: number;
  status: 'CREATED' | 'PAID' | 'CANCELLED' | 'EXPIRED';
  createdAt: string;
}

export interface PayReservationRequest {
  method: 'MOCK' | 'CARD';
  cardNumber?: string;
  exp?: string;
  cvv?: string;
  holderName?: string;
}

export interface PaymentResponse {
  id: number;
  reservationId: number;
  amount: number;
  method: 'MOCK' | 'CARD';
  status: 'SUCCESS' | 'FAILED';
  createdAt: string;
  reference: string;
}

export function buildBasicAuth(username: string, password: string) {
  return 'Basic ' + btoa(`${username}:${password}`);
}

async function http<T>(path: string, opts: RequestInit & { auth?: string } = {}): Promise<T> {
  const headers = new Headers(opts.headers);

  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  if (opts.auth) headers.set('Authorization', opts.auth);

  const res = await fetch(`${API_BASE}${path}`, { ...opts, headers });

  if (!res.ok) {
    let msg = `${res.status} ${res.statusText}`;
    try {
      const data = await res.json();
      msg = data?.message ?? data?.error ?? msg;
    } catch {
    }
    throw new Error(msg);
  }

  if (res.status === 204) return undefined as T;

  return res.json() as Promise<T>;
}

export const api = {
  getSpots(auth: string) {
    return http<BackendParkingSpot[]>('/api/spots', { method: 'GET', auth });
  },

  getReservationBySpot(auth: string, spotId: number) {
    return http<ReservationResponse>(`/api/reservations/spot/${spotId}`, { method: 'GET', auth });
  },

  getPaymentByReservation(auth: string, reservationId: number) {
    return http<PaymentResponse>(`/api/reservations/${reservationId}/payment`, { method: 'GET', auth });
  },

  createReservation(auth: string, body: CreateReservationRequest) {
    return http<ReservationResponse>('/api/reservations', {
      method: 'POST',
      auth,
      body: JSON.stringify(body),
    });
  },

  payReservation(auth: string, reservationId: number, body: PayReservationRequest) {
    return http<PaymentResponse>(`/api/reservations/${reservationId}/pay`, {
      method: 'POST',
      auth,
      body: JSON.stringify(body),
    });
  },

  myReservations(auth: string) {
    return http<ReservationResponse[]>('/api/reservations/me', { method: 'GET', auth });
  },

  cancelReservation(auth: string, reservationId: number) {
    return http<void>(`/api/reservations/${reservationId}/cancel`, {
      method: 'POST',
      auth,
    });
  },

  releaseReservation(auth: string, reservationId: number) {
    return http<void>(`/api/reservations/${reservationId}/release`, {
      method: 'POST',
      auth,
    });
  },

  async downloadReceipt(auth: string, paymentId: number) {
    const res = await fetch(`${API_BASE}/api/payments/${paymentId}/receipt`, {
      method: 'GET',
      headers: { Authorization: auth },
    });

    if (!res.ok) {
      throw new Error(`${res.status} ${res.statusText}`);
    }

    const blob = await res.blob();
    const url = URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = `receipt_payment_${paymentId}.txt`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  },
};
