import { useState, useEffect } from 'react'
import type { UserCollectionItem } from '../../pokemon/types/PokemonTypes'

interface EditCollectionModalProps {
  isOpen: boolean
  onClose: () => void
  collectionItem: UserCollectionItem | null
  onSave: (updatedItem: UserCollectionItem) => void
}

function EditCollectionModal({ isOpen, onClose, collectionItem, onSave }: EditCollectionModalProps) {
  const [quantity, setQuantity] = useState(1)
  const [condition, setCondition] = useState('near_mint')
  const [variant, setVariant] = useState('normal')
  const [notes, setNotes] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  // Populate form when modal opens with different item
  useEffect(() => {
    if (collectionItem) {
      setQuantity(collectionItem.quantity)
      setCondition(collectionItem.condition)
      setVariant(collectionItem.variant)
      setNotes(collectionItem.notes || '')
    }
  }, [collectionItem])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!collectionItem) return

    setLoading(true)
    setError('')

    try {
      const response = await fetch(`http://localhost:8080/api/collection/${collectionItem.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
          quantity,
          condition,
          variant,
          notes: notes || null
        })
      })

      if (response.ok) {
        const updatedItem = await response.json()
        onSave(updatedItem)
        onClose()
      } else {
        const errorData = await response.json()
        setError(errorData.message || 'Failed to update collection item')
      }
    } catch (err) {
      setError('Network error. Please try again.')
      console.error('Error updating collection item:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async () => {
    if (!collectionItem || !confirm('Are you sure you want to remove this card from your collection?')) return

    setLoading(true)
    setError('')

    try {
      const response = await fetch(`http://localhost:8080/api/collection/${collectionItem.id}`, {
        method: 'DELETE',
        credentials: 'include'
      })

      if (response.ok) {
        onSave(collectionItem) // Trigger refresh
        onClose()
      } else {
        setError('Failed to delete collection item')
      }
    } catch (err) {
      setError('Network error. Please try again.')
      console.error('Error deleting collection item:', err)
    } finally {
      setLoading(false)
    }
  }

  if (!isOpen || !collectionItem) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          {/* Header */}
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold">Edit Collection Item</h3>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-xl"
            >
              ×
            </button>
          </div>

          {/* Card Info */}
          <div className="mb-4 p-3 bg-gray-50 rounded-lg">
            <div className="flex items-center space-x-3">
              <img
                src={collectionItem.cardDetails.thumbnailImageUrl}
                alt={collectionItem.cardDetails.name}
                className="w-16 h-16 object-contain"
              />
              <div>
                <h4 className="font-semibold">{collectionItem.cardDetails.name}</h4>
                <p className="text-sm text-gray-600">{collectionItem.cardDetails.set.name}</p>
              </div>
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {error}
            </div>
          )}

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Quantity */}
            <div>
              <label htmlFor="quantity" className="block text-sm font-medium text-gray-700 mb-1">
                Quantity
              </label>
              <input
                type="number"
                id="quantity"
                min="1"
                value={quantity}
                onChange={(e) => setQuantity(parseInt(e.target.value))}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            {/* Condition */}
            <div>
              <label htmlFor="condition" className="block text-sm font-medium text-gray-700 mb-1">
                Condition
              </label>
              <select
                id="condition"
                value={condition}
                onChange={(e) => setCondition(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="mint">Mint</option>
                <option value="near_mint">Near Mint</option>
                <option value="excellent">Excellent</option>
                <option value="good">Good</option>
                <option value="light_played">Light Played</option>
                <option value="played">Played</option>
                <option value="poor">Poor</option>
              </select>
            </div>

            {/* Variant */}
            <div>
              <label htmlFor="variant" className="block text-sm font-medium text-gray-700 mb-1">
                Variant
              </label>
              <select
                id="variant"
                value={variant}
                onChange={(e) => setVariant(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="normal">Normal</option>
                <option value="reverse">Reverse Holo</option>
                <option value="holo">Holo</option>
                <option value="first_edition">First Edition</option>
              </select>
            </div>

            {/* Notes */}
            <div>
              <label htmlFor="notes" className="block text-sm font-medium text-gray-700 mb-1">
                Notes (Optional)
              </label>
              <textarea
                id="notes"
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Any additional notes about this card..."
              />
            </div>

            {/* Buttons */}
            <div className="flex space-x-3 pt-4">
              <button
                type="submit"
                disabled={loading}
                className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
              >
                {loading ? 'Saving...' : 'Save Changes'}
              </button>
              <button
                type="button"
                onClick={handleDelete}
                disabled={loading}
                className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50"
              >
                Delete
              </button>
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-500"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default EditCollectionModal