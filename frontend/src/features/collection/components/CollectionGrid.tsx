import React, { useState } from 'react';
import { type UserCollection } from '../types/CollectionTypes';
import EditCollectionModal from './EditCollectionModal';
import { collectionService } from '../services/CollectionService';

interface CollectionGridProps {
  collection: UserCollection[];
  viewMode: 'grid' | 'list';
  onUpdate: (item: UserCollection) => void;
  onDelete: (itemId: number) => void;
}

export const CollectionGrid: React.FC<CollectionGridProps> = ({
  collection,
  viewMode,
  onUpdate,
  onDelete
}) => {
  const [editingItem, setEditingItem] = useState<UserCollection | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const handleDelete = async (item: UserCollection) => {
    if (!confirm(`Remove ${item.cardDetails?.name} from your collection?`)) {
      return;
    }

    try {
      setDeletingId(item.id);
      await collectionService.removeFromCollection(item.id);
      alert(`${item.cardDetails?.name} was removed from your collection.`)
      onDelete(item.id);
    } catch (error) {
      console.error('Failed to delete item:', error);
      alert('Failed to remove card. Please try again.');
    } finally {
      setDeletingId(null);
    }
  };

  const handleEdit = (item: UserCollection) => {
    setEditingItem(item);
  };

  const handleEditSave = async (updatedItem: UserCollection) => {
    try {
      const saved = await collectionService.updateCollectionItem(updatedItem.id, {
        quantity: updatedItem.quantity,
        condition: updatedItem.condition,
        variant: updatedItem.variant,
        notes: updatedItem.notes
      });
      onUpdate(saved);
      setEditingItem(null);
    } catch (error) {
      console.error('Failed to update item:', error);
      alert('Failed to update card. Please try again.');
    }
  };

  const getConditionColor = (condition: string) => {
    switch (condition.toLowerCase()) {
      case 'mint': return 'text-green-600 bg-green-50';
      case 'near mint': return 'text-green-500 bg-green-50';
      case 'excellent': return 'text-blue-600 bg-blue-50';
      case 'good': return 'text-yellow-600 bg-yellow-50';
      case 'light played': return 'text-orange-600 bg-orange-50';
      case 'played': return 'text-red-600 bg-red-50';
      case 'poor': return 'text-red-700 bg-red-100';
      default: return 'text-gray-600 bg-gray-50';
    }
  };

  if (viewMode === 'list') {
    return (
      <div className="bg-white rounded-lg shadow-sm border overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Card
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Set
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Condition
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Quantity
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {collection.map((item) => (
              <tr key={item.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="flex items-center">
                    <img
                      src={item.cardDetails?.image + '/low.webp' || '/placeholder-card.png'}
                      alt={item.cardDetails?.name}
                      className="h-16 w-12 object-cover rounded"
                      onError={(e) => {
                        e.currentTarget.src = '/placeholder-card.png';
                      }}
                    />
                    <div className="ml-4">
                      <div className="text-sm font-medium text-gray-900">
                        {item.cardDetails?.name || 'Unknown Card'}
                      </div>
                      <div className="text-sm text-gray-500">
                        {item.cardDetails?.rarity}
                        {item.variant && ` • ${item.variant}`}
                      </div>
                    </div>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {item.cardDetails?.set?.name || 'Unknown Set'}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getConditionColor(item.condition)}`}>
                    {item.condition}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {item.quantity}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <button
                    onClick={() => handleEdit(item)}
                    className="text-blue-600 hover:text-blue-900 mr-3"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(item)}
                    disabled={deletingId === item.id}
                    className="text-red-600 hover:text-red-900 disabled:opacity-50"
                  >
                    {deletingId === item.id ? 'Removing...' : 'Remove'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {editingItem && (
          <EditCollectionModal
            item={editingItem}
            onSave={handleEditSave}
            onCancel={() => setEditingItem(null)}
          />
        )}
      </div>
    );
  }

  // Grid view
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
      {collection.map((item) => (
        <div key={item.id} className="bg-white rounded-lg shadow-sm border hover:shadow-md transition-shadow">
          <div className="aspect-[3/4] relative overflow-hidden rounded-t-lg">
            <img
              src={item.cardDetails?.image + '/low.webp' || '/placeholder-card.png'}
              alt={item.cardDetails?.name}
              className="w-full h-full object-cover"
              onError={(e) => {
                e.currentTarget.src = '/placeholder-card.png';
              }}
            />
            {item.quantity > 1 && (
              <div className="absolute top-2 right-2 bg-blue-600 text-white text-xs font-bold px-2 py-1 rounded-full">
                ×{item.quantity}
              </div>
            )}
          </div>
          
          <div className="p-4">
            <h3 className="font-semibold text-gray-900 text-sm mb-1 line-clamp-2">
              {item.cardDetails?.name || 'Unknown Card'}
            </h3>
            
            <p className="text-xs text-gray-600 mb-2">
              {item.cardDetails?.set?.name || 'Unknown Set'}
            </p>
            
            <div className="flex items-center justify-between mb-3">
              <span className={`text-xs px-2 py-1 rounded-full ${getConditionColor(item.condition)}`}>
                {item.condition}
              </span>
              <span className="text-xs text-gray-500">
                {item.cardDetails?.rarity || 'Unknown'}
              </span>
            </div>

            {item.variant && (
              <p className="text-xs text-gray-600 mb-2 italic">
                {item.variant}
              </p>
            )}
            
            <div className="flex gap-2">
              <button
                onClick={() => handleEdit(item)}
                className="flex-1 text-xs bg-blue-50 text-blue-600 py-2 rounded hover:bg-blue-100"
              >
                Edit
              </button>
              <button
                onClick={() => handleDelete(item)}
                disabled={deletingId === item.id}
                className="flex-1 text-xs bg-red-50 text-red-600 py-2 rounded hover:bg-red-100 disabled:opacity-50"
              >
                {deletingId === item.id ? 'Removing...' : 'Remove'}
              </button>
            </div>
          </div>
        </div>
      ))}

      {editingItem && (
        <EditCollectionModal
          item={editingItem}
          onSave={handleEditSave}
          onCancel={() => setEditingItem(null)}
        />
      )}
    </div>
  );
};