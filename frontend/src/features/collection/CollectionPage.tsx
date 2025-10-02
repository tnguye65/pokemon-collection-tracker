import React, { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../auth/hooks/useAuth';
import { CollectionGrid } from './components/CollectionGrid'; // Add this import
import { CollectionFilters } from './components/CollectionFilters';
import { CollectionSearch } from './components/CollectionSearch';
import { CollectionStats } from './components/CollectionStats';
import PokemonSearch from '../pokemon/components/PokemonSearch';
import { type UserCollection } from './types/CollectionTypes';
import { collectionService } from './services/CollectionService'; // You'll need to implement the service methods

interface FilterState {
  condition: string;
  set: string;
  rarity: string;
  type: string;
}

interface SortState {
  field: 'name' | 'dateAdded' | 'quantity' | 'condition';
  direction: 'asc' | 'desc';
}

export const CollectionPage: React.FC = () => {
  const { user } = useAuth();
  const [collection, setCollection] = useState<UserCollection[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // UI State
  const [searchQuery, setSearchQuery] = useState('');
  const [filters, setFilters] = useState<FilterState>({
    condition: '',
    set: '',
    rarity: '',
    type: ''
  });
  const [sort, setSort] = useState<SortState>({
    field: 'dateAdded',
    direction: 'desc'
  });
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [currentPage, setCurrentPage] = useState(1);
  const [showAddModal, setShowAddModal] = useState(false);
  
  const itemsPerPage = 20;

  // Load collection data
  useEffect(() => {
    const loadCollection = async () => {
      if (!user) return;
      
      try {
        setLoading(true);
        setError(null);
        const data = await collectionService.getUserCollection();
        setCollection(data);
      } catch (err) {
        setError('Failed to load collection. Please try again.');
        console.error('Collection load error:', err);
      } finally {
        setLoading(false);
      }
    };

    loadCollection();
  }, [user]);

  // Filter and sort collection
  const filteredAndSortedCollection = useMemo(() => {
    const filtered = collection.filter(item => {
      // Search filter
      if (searchQuery) {
        const query = searchQuery.toLowerCase();
        const matchesName = item.cardDetails?.name?.toLowerCase().includes(query);
        const matchesSet = item.cardDetails?.set?.name?.toLowerCase().includes(query);
        if (!matchesName && !matchesSet) return false;
      }

      // Other filters...
      if (filters.condition && item.condition !== filters.condition) return false;
      if (filters.set && item.cardDetails?.set?.name !== filters.set) return false;
      if (filters.rarity && item.cardDetails?.rarity !== filters.rarity) return false;
      if (filters.type && !item.cardDetails?.types?.includes(filters.type)) return false;

      return true;
    });

    // Sort logic...
    filtered.sort((a, b) => {
      let aValue: string | Date | number, bValue: string | Date | number;
      
      switch (sort.field) {
        case 'name':
          aValue = a.cardDetails?.name || '';
          bValue = b.cardDetails?.name || '';
          break;
        case 'dateAdded':
          aValue = new Date(a.dateAdded || 0);
          bValue = new Date(b.dateAdded || 0);
          break;
        case 'quantity':
          aValue = a.quantity;
          bValue = b.quantity;
          break;
        case 'condition':
          aValue = a.condition;
          bValue = b.condition;
          break;
        default:
          return 0;
      }

      if (aValue < bValue) return sort.direction === 'asc' ? -1 : 1;
      if (aValue > bValue) return sort.direction === 'asc' ? 1 : -1;
      return 0;
    });

    return filtered;
  }, [collection, searchQuery, filters, sort]);

  // Pagination
  const totalPages = Math.ceil(filteredAndSortedCollection.length / itemsPerPage);
  const paginatedCollection = filteredAndSortedCollection.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  // Reset page when filters change
  useEffect(() => {
    setCurrentPage(1);
  }, [searchQuery, filters, sort]);

  const handleCollectionUpdate = (updatedItem: UserCollection) => {
    setCollection(prev => 
      prev.map(item => 
        item.id === updatedItem.id ? updatedItem : item
      )
    );
  };

  const handleCollectionDelete = (itemId: number) => {
    setCollection(prev => prev.filter(item => item.id !== itemId));
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <span className="ml-3 text-lg">Loading your collection...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
        <h3 className="text-lg font-semibold text-red-800 mb-2">Error Loading Collection</h3>
        <p className="text-red-600 mb-4">{error}</p>
        <button 
          onClick={() => window.location.reload()}
          className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">My Collection</h1>
          <p className="text-gray-600 mt-1">
            {filteredAndSortedCollection.length} of {collection.length} cards
            {searchQuery && ` matching "${searchQuery}"`}
          </p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => setShowAddModal(true)}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center gap-2"
          >
            <span>+</span> Add Cards
          </button>
        </div>
      </div>

      {/* Stats */}
      <CollectionStats collection={collection} className="mb-6" />

      {/* Search and Filters */}
      <div className="bg-white rounded-lg shadow-sm border p-4 mb-6">
        <div className="flex flex-wrap gap-4 items-center">
          <div className="flex-1 min-w-64">
            <CollectionSearch 
              value={searchQuery}
              onChange={setSearchQuery}
              placeholder="Search your collection..."
            />
          </div>
          
          <CollectionFilters
            filters={filters}
            onFiltersChange={setFilters}
            collection={collection}
          />
          
          <div className="flex items-center gap-2">
            <label className="text-sm font-medium text-gray-700">Sort:</label>
            <select
              value={`${sort.field}-${sort.direction}`}
              onChange={(e) => {
                const [field, direction] = e.target.value.split('-');
                setSort({ field: field as 'name' | 'dateAdded' | 'quantity' | 'condition', direction: direction as 'asc' | 'desc' });
              }}
              className="border border-gray-300 rounded px-3 py-1 text-sm"
            >
              <option value="dateAdded-desc">Newest First</option>
              <option value="dateAdded-asc">Oldest First</option>
              <option value="name-asc">Name A-Z</option>
              <option value="name-desc">Name Z-A</option>
              <option value="quantity-desc">Most Copies</option>
              <option value="quantity-asc">Fewest Copies</option>
            </select>
          </div>

          <div className="flex bg-gray-100 rounded-lg p-1">
            <button
              onClick={() => setViewMode('grid')}
              className={`px-3 py-1 text-sm rounded ${
                viewMode === 'grid' 
                  ? 'bg-white shadow-sm text-gray-900' 
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Grid
            </button>
            <button
              onClick={() => setViewMode('list')}
              className={`px-3 py-1 text-sm rounded ${
                viewMode === 'list' 
                  ? 'bg-white shadow-sm text-gray-900' 
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              List
            </button>
          </div>
        </div>
      </div>

      {/* Collection Grid/List */}
      {filteredAndSortedCollection.length === 0 ? (
        <div className="text-center py-12">
          <div className="text-gray-400 text-6xl mb-4">📱</div>
          <h3 className="text-xl font-semibold text-gray-900 mb-2">
            {collection.length === 0 ? 'No cards in collection' : 'No cards match your filters'}
          </h3>
          <p className="text-gray-600 mb-4">
            {collection.length === 0 
              ? 'Start building your collection by adding some Pokemon cards!'
              : 'Try adjusting your search or filter criteria.'
            }
          </p>
          {collection.length === 0 && (
            <button
              onClick={() => setShowAddModal(true)}
              className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700"
            >
              Add Your First Card
            </button>
          )}
        </div>
      ) : (
        <>
          <CollectionGrid
            collection={paginatedCollection}
            viewMode={viewMode}
            onUpdate={handleCollectionUpdate}
            onDelete={handleCollectionDelete}
          />

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-4 mt-8">
              <button
                onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                disabled={currentPage === 1}
                className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                ← Previous
              </button>
              
              <span className="text-gray-600">
                Page {currentPage} of {totalPages}
              </span>
              
              <button
                onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                disabled={currentPage === totalPages}
                className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                Next →
              </button>
            </div>
          )}
        </>
      )}

      {/* Add Card Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Add Cards to Collection</h2>
                <button
                  onClick={() => setShowAddModal(false)}
                  className="text-gray-500 hover:text-gray-700 text-2xl"
                >
                  ×
                </button>
              </div>
              <PokemonSearch />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};