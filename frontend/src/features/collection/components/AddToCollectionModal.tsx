import { useState } from 'react';
import type { PokemonCardBrief } from '../../pokemon/types/PokemonTypes';
import { type AddToCollectionRequest, CARD_CONDITIONS, CARD_VARIANTS, type CardCondition, type CardVariant } from '../types/CollectionTypes';

// Add to Collection Modal Component
interface AddToCollectionModalProps {
  card: PokemonCardBrief;
  onSubmit: (request: AddToCollectionRequest) => Promise<void>;
  onClose: () => void;
  loading: boolean;
}

const AddToCollectionModal: React.FC<AddToCollectionModalProps> = ({
  card,
  onSubmit,
  onClose,
  loading
}) => {
  const [variant, setVariant] = useState<CardVariant>('Normal');
  const [quantity, setQuantity] = useState(1);
  const [condition, setCondition] = useState<CardCondition>('Near Mint');
  const [notes, setNotes] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Form submitted');
    
    const request: AddToCollectionRequest = {
      cardId: card.id,
      variant,
      quantity,
      condition,
      notes: notes.trim() || undefined
    };

    console.log('Request payload:', request); // Add this
    await onSubmit(request);
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold text-gray-900">Add to Collection</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600"
              disabled={loading}
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Card Preview */}
          <div className="flex items-center mb-4 p-3 bg-gray-50 rounded-lg">
            <img
              src={card.image + '/low.webp'}
              alt={card.name}
              className="w-12 h-16 object-contain rounded mr-3"
            />
            <div>
              <h3 className="font-medium text-gray-900">{card.name}</h3>
              <p className="text-sm text-gray-600">ID: {card.localId}</p>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Variant */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Variant
              </label>
              <select
                value={variant}
                onChange={(e) => setVariant(e.target.value as CardVariant)}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                disabled={loading}
              >
                {CARD_VARIANTS.map(v => (
                  <option key={v} value={v}>
                    {v.split('_').map(word => 
                      word.charAt(0).toUpperCase() + word.slice(1)
                    ).join(' ')}
                  </option>
                ))}
              </select>
            </div>

            {/* Quantity */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Quantity
              </label>
              <input
                type="number"
                min="1"
                max="100"
                value={quantity}
                onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                disabled={loading}
              />
            </div>

            {/* Condition */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Condition
              </label>
              <select
                value={condition}
                onChange={(e) => setCondition(e.target.value as CardCondition)}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                disabled={loading}
              >
                {CARD_CONDITIONS.map(c => (
                  <option key={c} value={c}>
                    {c.split('_').map(word => 
                      word.charAt(0).toUpperCase() + word.slice(1)
                    ).join(' ')}
                  </option>
                ))}
              </select>
            </div>

            {/* Notes */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Notes (Optional)
              </label>
              <textarea
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="Add any notes about this card..."
                rows={3}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                disabled={loading}
              />
            </div>

            {/* Action Buttons */}
            <div className="flex space-x-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 py-2 px-4 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
                disabled={loading}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="flex-1 py-2 px-4 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                disabled={loading}
              >
                {loading ? 'Adding...' : 'Add to Collection'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddToCollectionModal;
