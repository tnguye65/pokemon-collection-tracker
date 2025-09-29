import { useEffect, useState } from 'react'
import { useAuth } from '../../features/auth/hooks/useAuth'
import type { UserCollectionItem } from '../../features/pokemon/types/PokemonTypes'
import EditCollectionModal from '../../features/collection/components/EditCollectionModal'

function UserProfile() {
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const { logout } = useAuth()

    // Adding user collection info
    const [collection, setCollection] = useState<UserCollectionItem[]>([]);

    // Modal state
    const [showEditModal, setShowEditModal] = useState(false)
    const [selectedCard, setSelectedCard] = useState<UserCollectionItem | null>(null)

    const uniqueCards = collection.length;
    const totalCards = collection.reduce((sum, card) => sum + card.quantity, 0);

    const fetchUserCollection = async () => {
        setLoading(true)
        setError('')

        try {
            const response = await fetch('http://localhost:8080/api/collection', {
                credentials: 'include'
            })

            if (response.ok) {
                const data = await response.json()
                console.log('Collection data:', data)
                setCollection(data)
            } else {
                setError('Failed to fetch collection')
            }
        } catch (error) {
            console.error('Error fetching collection:', error)
            setError('An error occurred while fetching collection')
        } finally {
            setLoading(false)
        }
    }

    const handleEditCard = (card: UserCollectionItem) => {
        setSelectedCard(card)
        setShowEditModal(true)
    }

    const handleModalSave = () => {
        // Refresh the collection after save/delete
        fetchUserCollection()
    }

    const handleLogout = async () => {
        setLoading(true)
        try {
            await logout()
        } catch (err) {
            setError('Logout failed')
            console.error('Logout error:', err)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        fetchUserCollection()
    }, [])

    return (
        <>
        <div className="max-w-4xl mx-auto p-6">

            {/* Display collection if user has any */}
            {collection.length > 0 && (
                <div>
                    <h3 className="text-lg font-semibold mb-2">Your Collection</h3>
                    
                    {/* Stats cards */}
                    <div className="grid grid-cols-2 gap-4 mb-6">
                        <div className="bg-blue-50 rounded-lg p-4 text-center">
                            <div className="text-2xl font-bold text-blue-600">{uniqueCards}</div>
                            <div className="text-sm text-blue-600">Unique Cards</div>
                        </div>
                        <div className="bg-green-50 rounded-lg p-4 text-center">
                            <div className="text-2xl font-bold text-green-600">{totalCards}</div>
                            <div className="text-sm text-green-600">Total Cards</div>
                        </div>
                    </div>

                    {/* Collection grid */}
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                        {collection.map((card) => (
                            <div key={card.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
                                <div className="aspect-w-3 aspect-h-4 bg-gray-100">
                                    <img
                                        src={card.cardDetails.thumbnailImageUrl}
                                        alt={card.cardDetails.name}
                                        className="w-full h-48 object-contain p-2"
                                        onError={(e) => {
                                            const target = e.target as HTMLImageElement;
                                            target.src = card.cardDetails.highQualityImageUrl;
                                        }}
                                    />
                                </div>
                                {/* Add card name and details */}
                                <div className="p-3">
                                    <h4 className="font-semibold text-sm truncate">{card.cardDetails.name}</h4>
                                    <h4 className="text-xs text-gray-500 truncate">ID: {card.cardId}</h4>
                                    <h4 className="text-xs text-gray-500 truncate">Set: {card.cardDetails.set.name}</h4>
                                    <h4 className="font-semibold text-sm truncate">x{card.quantity}</h4>

                                    {<button
                                        onClick={() => handleEditCard(card)}
                                        className="mt-2 w-full bg-blue-600 text-white p-1 rounded hover:bg-blue-700 text-xs"
                                    >
                                        Edit
                                    </button> }
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Add loading state */}
            {loading && (
                <div className="text-center py-8">
                    <p>Loading collection...</p>
                </div>
            )}

            {/* Update empty state */}
            {!loading && collection.length === 0 && (
                <div className="text-center py-8">
                    <p className="text-gray-500">No cards in your collection yet.</p>
                    <p className="text-sm text-gray-400">Start adding some Pokémon cards!</p>
                </div>
            )}

            {/* Error */}
            {error && collection.length != 0 && (
                <div className="text-center py-8">
                    <p className="text-red-500">{error}</p>
                </div>
            )}

            {/* Logout Button */}   
            <button
                onClick={handleLogout}
                className="w-full bg-red-600 text-white p-2 rounded hover:bg-red-700"
            >
                {loading ? 'Logging out...' : 'Logout'}
            </button>

        </div>

        {/* Edit Modal */}
        <EditCollectionModal
            isOpen={showEditModal}
            onClose={() => setShowEditModal(false)}
            collectionItem={selectedCard}
            onSave={handleModalSave}
        />
        </>
    )
}

export default UserProfile