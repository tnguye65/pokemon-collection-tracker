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

export type { PokemonCardBrief, PokemonCard }
