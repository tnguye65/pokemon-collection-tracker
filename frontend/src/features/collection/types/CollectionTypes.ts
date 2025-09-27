// Collection API types

export interface AddToCollectionRequest {
  cardId: string;
  variant: string;
  quantity: number;
  condition: string;
  notes?: string;
}

export interface UserCollectionResponse {
  id: number;
  cardId: string;
  variant: string;
  quantity: number;
  condition: string;
  notes?: string;
  addedDate: string;
  updatedDate: string;
  cardDetails: unknown; // This would be your TCGdexCard, but we can keep it flexible for now
}

// Common condition options
export const CARD_CONDITIONS = [
  'mint',
  'near_mint', 
  'lightly_played',
  'moderately_played',
  'heavily_played',
  'damaged'
] as const;

export type CardCondition = typeof CARD_CONDITIONS[number];

// Common variant options  
export const CARD_VARIANTS = [
  'normal',
  'holofoil',
  'reverse_holofoil',
  'first_edition'
] as const;

export type CardVariant = typeof CARD_VARIANTS[number];