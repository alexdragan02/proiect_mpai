import { ParkingSpot } from '../App';

interface ParkingGridProps {
  spots: ParkingSpot[];
  onSpotClick: (spot: ParkingSpot) => void;

  ownersBySpotId: Record<number, string | undefined>;
}

function ParkingGrid({ spots, onSpotClick, ownersBySpotId }: ParkingGridProps) {
  const getStatusColor = (status: ParkingSpot['status']) => {
    switch (status) {
      case 'available':
        return 'bg-emerald-500 hover:bg-emerald-600 cursor-pointer';
      case 'occupied':
        return 'bg-red-500 hover:bg-red-600 cursor-pointer';
      case 'reserved':
        return 'bg-amber-500 hover:bg-amber-600 cursor-pointer';
    }
  };

  const zones = ['Zona A', 'Zona B', 'Zona C'];

  const statusLabel = (s: ParkingSpot) => {
    const owner = ownersBySpotId[s.id];
    if (s.status === 'available') return 'Disponibil';
    if (s.status === 'reserved') return owner ? `Rezervat de ${owner}` : 'Rezervat';
    return owner ? `Ocupat de ${owner}` : 'Ocupat';
  };

  return (
    <div className="space-y-8">
      {zones.map((zone) => {
        const zoneSpots = spots.filter((spot) => spot.zone === zone);
        return (
          <div key={zone} className="bg-white rounded-xl shadow-sm p-6">
            <h2 className="text-xl font-semibold text-slate-800 mb-4">{zone}</h2>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
              {zoneSpots.map((spot) => (
                <button
                  key={spot.id}
                  onClick={() => onSpotClick(spot)}
                  className={`
                    ${getStatusColor(spot.status)}
                    p-6 rounded-lg transition-all duration-200 transform
                    hover:scale-105 shadow-md hover:shadow-lg
                  `}
                >
                  <div className="text-white text-center">
                    <div className="text-lg font-bold mb-1">{spot.number}</div>
                    <div className="text-xs opacity-90">{spot.pricePerHour} RON/oră</div>
                    <div className="text-[10px] opacity-90 mt-1">
                      {statusLabel(spot)}
                    </div>
                  </div>
                </button>
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default ParkingGrid;
