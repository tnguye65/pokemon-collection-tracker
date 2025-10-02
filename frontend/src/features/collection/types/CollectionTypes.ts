// Unified Collection Types - matches backend UserCollection entity

export interface UserCollection {
  id: number;
  tcgId: string;  // This matches your backend's tcgId field
  quantity: number;
  condition: string;
  variant?: string;
  notes?: string;
  dateAdded: string;  // This matches your backend's dateAdded field
  dateUpdated?: string;
  cardDetails?: PokemonCardDetails;
}

export interface PokemonCardDetails {
  id: string;
  name: string;
  image: string;
  highQualityImageUrl: string;
  thumbnailImageUrl: string;
  rarity: string;
  set: {
    id: string;
    name: string;
    series: string;
    printedTotal: number;
    total: number;
    releaseDate: string;
    images: {
      symbol: string;
      logo: string;
    };
  };
  types: string[];
  hp?: number;
  number: string;
  artist?: string;
  flavorText?: string;
  attacks?: Attack[];
  weaknesses?: Weakness[];
  resistances?: Resistance[];
  retreatCost?: string[];
}

export interface Attack {
  name: string;
  cost: string[];
  convertedEnergyCost: number;
  damage: string;
  text: string;
}

export interface Weakness {
  type: string;
  value: string;
}

export interface Resistance {
  type: string;
  value: string;
}

// API Request/Response types that match your backend
export interface AddToCollectionRequest {
  cardId: string; 
  variant: string;
  quantity: number;
  condition: string;
  notes?: string;
}

export interface UpdateCollectionRequest {
  quantity: number;
  condition: string;
  variant?: string;
  notes?: string;
}

// Backend response type (what your UserCollectionController returns)
export interface UserCollectionResponse {
  id: number;
  tcgId: string;
  variant?: string;
  quantity: number;
  condition: string;
  notes?: string;
  dateAdded: string;
  dateUpdated?: string;
  cardDetails?: unknown; // This will be populated by your backend
}

// Condition options that match your backend
export const CARD_CONDITIONS = [
  'Mint',
  'Near Mint',
  'Excellent', 
  'Good',
  'Light Played',
  'Played',
  'Poor'
] as const;

export type CardCondition = typeof CARD_CONDITIONS[number];

// Variant options
export const CARD_VARIANTS = [
  'Normal',
  'Holo',
  'Reverse Holo',
  'First Edition',
  'Shadowless'
] as const;

export type CardVariant = typeof CARD_VARIANTS[number];