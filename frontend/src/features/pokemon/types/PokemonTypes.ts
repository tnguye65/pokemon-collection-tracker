// Pokemon-specific types (for search/display, not collection)

export interface PokemonCardBrief {
  id: string;
  localId: string;
  name: string;
  image: string;
  highQualityImageUrl: string;
  thumbnailImageUrl: string;
}

export interface PokemonCard extends PokemonCardBrief {
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

// Re-export UserCollection from CollectionTypes to avoid duplication
export type { UserCollection, PokemonCardDetails } from '../../collection/types/CollectionTypes';