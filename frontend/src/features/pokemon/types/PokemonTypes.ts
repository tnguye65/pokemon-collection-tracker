interface PokemonCardBrief {
  id: string;
  localId: string;
  name: string;
  image: string;
  highQualityImageUrl: string;
  thumbnailImageUrl: string;
}

interface PokemonCard extends PokemonCardBrief {
  category?: string;
  rarity?: string;
  set?: {
    name: string;
    id?: string;
  };
  hp?: number;
  types?: string[];
  // Only add fields as you need them in your UI
}

interface UserCollectionItem {
    id: number;
    cardId: string;
    quantity: number;
    condition: string;
    variant: string;
    notes: string | null;
    addedDate: string;
    updatedDate: string;
    cardDetails: {
        id: string;
        name: string;
        thumbnailImageUrl: string;
        highQualityImageUrl: string;
        set: {
            id: string;
            name: string;
        };
        hp?: number;
        types: string[];
        rarity: string;
        // Add other properties as needed
    };
}

export type { PokemonCardBrief, PokemonCard, UserCollectionItem }