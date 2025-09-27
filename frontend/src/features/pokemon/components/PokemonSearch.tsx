import React, { useState } from 'react';
import { type PokemonCardBrief } from '../types/PokemonTypes';
import type { AddToCollectionRequest } from '../../collection/types/CollectionTypes';
import AddToCollectionModal from '../../collection/components/AddToCollectionModal';

const PokemonSearch: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [cards, setCards] = useState<PokemonCardBrief[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [hasSearched, setHasSearched] = useState(false);

  // Add to collection state
  const [showAddModal, setShowAddModal] = useState(false);
  const [selectedCard, setSelectedCard] = useState<PokemonCardBrief | null>(null);
  const [addingToCollection, setAddingToCollection] = useState(false);

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      setCards([]);
      setHasSearched(false);
      return;
    }

    setLoading(true);
    setError('');
    setHasSearched(true);

    try {
      const response = await fetch(`http://localhost:8080/api/pokemon/cards/search?name=${encodeURIComponent(searchQuery)}`, {
        credentials: 'include' // Important for your cookie-based auth
      });

      if (!response.ok) {
        throw new Error('Failed to search Pokemon cards');
      }

      const data: PokemonCardBrief[] = await response.json();
      setCards(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred while searching');
      setCards([]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleAddToCollection = (card: PokemonCardBrief) => {
    setSelectedCard(card);
    setShowAddModal(true);
  };

  const submitAddToCollection = async (request: AddToCollectionRequest) => {
    console.log('submitAddToCollection called with:', request); 

    setAddingToCollection(true);
    
    try {
      console.log('Making API call to /api/collection');
      const response = await fetch('http://localhost:8080/api/collection', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(request)
      });

      console.log('Response status:', response.status);

      if (!response.ok) {
        throw new Error('Failed to add card to collection');
      }

      // Success! Close modal and show success message
      setShowAddModal(false);
      setSelectedCard(null);
      alert(`${selectedCard?.name} added to your collection!`); // You can replace with a toast later
      
    } catch (err) {
      console.error('Error adding card to collection:', err);
      setError(err instanceof Error ? err.message : 'Failed to add card to collection');
    } finally {
      setAddingToCollection(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h2 className="text-2xl font-bold text-gray-900 mb-6">Search Pokemon Cards</h2>
      
      {/* Search Input */}
      <div className="mb-6">
        <div className="flex gap-2">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={handleKeyPress}
            placeholder="Enter Pokemon name (e.g., Pikachu)"
            className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
          <button
            onClick={handleSearch}
            disabled={loading}
            className="px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'Searching...' : 'Search'}
          </button>
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-600">{error}</p>
        </div>
      )}

      {/* Results */}
      {cards.length > 0 && (
        <div>
          <p className="text-gray-600 mb-4">Found {cards.length} cards</p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            {cards.map((card) => (
              <div key={card.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
                <div className="aspect-w-3 aspect-h-4 bg-gray-100">
                  <img
                    src={card.thumbnailImageUrl} // Using your thumbnail method
                    alt={card.name}
                    className="w-full h-48 object-contain p-2"
                    onError={(e) => {
                      // Fallback to high quality if thumbnail fails
                      const target = e.target as HTMLImageElement;
                      target.src = card.highQualityImageUrl;
                    }}
                  />
                </div>
                <div className="p-4">
                  <h3 className="font-semibold text-gray-900 mb-1">{card.name}</h3>
                  <p className="text-sm text-gray-600">ID: {card.id}</p>
                  <p className="text-sm text-gray-600">Local ID: {card.localId}</p>
                  <button
                    onClick={() => handleAddToCollection(card)}
                    className="mt-2 w-full bg-blue-500 hover:bg-blue-600 text-white py-2 px-4 rounded-lg transition-colors text-sm font-medium"
                  >
                    Add to Collection
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* No Results */}
      {cards.length === 0 && hasSearched && !loading && !error && (
        <div className="text-center py-8">
          <p className="text-gray-600">No cards found for "{searchQuery}"</p>
          <p className="text-sm text-gray-500 mt-2">Try a different Pokemon name</p>
        </div>
      )}

      {/* Loading State */}
      {loading && (
        <div className="text-center py-8">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          <p className="text-gray-600 mt-2">Searching...</p>
        </div>
      )}

      {/* Add to Collection Modal */}
      {showAddModal && selectedCard && (
        <AddToCollectionModal
          card={selectedCard}
          onSubmit={submitAddToCollection}
          onClose={() => {
            setShowAddModal(false);
            setSelectedCard(null);
          }}
          loading={addingToCollection}
        />
      )}

    </div>
  );
};

export default PokemonSearch;