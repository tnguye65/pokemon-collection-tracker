import type { UserCollection, AddToCollectionRequest, UpdateCollectionRequest } from '../types/CollectionTypes';

const API_BASE_URL = 'http://localhost:8080/api';

class CollectionService {
    private async fetchWithAuth(url: string, options: RequestInit = {}) {
        const response = await fetch(url, {
            ...options,
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                ...options.headers,
            },
        });

        if (!response.ok) {
            if (response.status === 401) {
                // Redirect to login or handle unauthorized
                window.location.href = '/login';
                throw new Error('Unauthorized');
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return response;
    }

    async getUserCollection(): Promise<UserCollection[]> {
        try {
            const response = await this.fetchWithAuth(`${API_BASE_URL}/collection`);
            if (response.status === 204) {
                return [];
            }
            const data = await response.json();

            if (!data || data.length === 0) {
                return [];
            }
            
            // Transform backend data to frontend format if needed
            return data.map((item: UserCollection) => ({
                id: item.id,
                tcgId: item.tcgId,
                quantity: item.quantity,
                condition: item.condition,
                variant: item.variant,
                notes: item.notes,
                dateAdded: item.dateAdded,
                cardDetails: item.cardDetails ? {
                    id: item.cardDetails.id,
                    name: item.cardDetails.name,
                    image: item.cardDetails.image,
                    rarity: item.cardDetails.rarity,
                    set: item.cardDetails.set,
                    types: item.cardDetails.types,
                    hp: item.cardDetails.hp,
                    number: item.cardDetails.number
                } : null
            }));
        } catch (error) {
            // If it's a 404 (no collection found), return empty array
            if (error instanceof Error && error.message.includes('204')) {
                return [];
            }
            // Re-throw other errors (like authentication errors)
            throw error;
        }
    }

    async addToCollection(request: AddToCollectionRequest): Promise<UserCollection> {
        const response = await this.fetchWithAuth(`${API_BASE_URL}/collection`, {
            method: 'POST',
            body: JSON.stringify(request),
        });
        
        return response.json();
    }

    async updateCollectionItem(id: number, request: UpdateCollectionRequest): Promise<UserCollection> {
        const response = await this.fetchWithAuth(`${API_BASE_URL}/collection/${id}`, {
            method: 'PUT',
            body: JSON.stringify(request),
        });
        
        return response.json();
    }

    async removeFromCollection(id: number): Promise<void> {
        await this.fetchWithAuth(`${API_BASE_URL}/collection/${id}`, {
            method: 'DELETE',
         });
    }

    async getCollectionStats(): Promise<{
        totalCards: number;
        uniqueCards: number;
        totalSets: number;
        conditionBreakdown: Record<string, number>;
        rarityBreakdown: Record<string, number>;
    }> {
        const response = await this.fetchWithAuth(`${API_BASE_URL}/collection/stats`);
        return response.json();
    }

    // async exportCollection(format: 'csv' | 'json'): Promise<Blob> {
    //     const response = await this.fetchWithAuth(`${API_BASE_URL}/user-collections/export?format=${format}`);
    //     return response.blob();
    // }
    // NEED TO IMPLEMENT IN BACKEND
}

export const collectionService = new CollectionService();