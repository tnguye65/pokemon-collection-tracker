import React, { useMemo } from 'react';
import { type UserCollection } from '../types/CollectionTypes';

interface FilterState {
    condition: string;
    set: string;
    rarity: string;
    type: string;
}

interface CollectionFilterProps {
    filters: FilterState;
    onFiltersChange: (filters: FilterState) => void;
    collection: UserCollection[]
}

export const CollectionFilters: React.FC<CollectionFilterProps> = ({
    filters,
    onFiltersChange,
    collection
}) => {

    const filterOptions = useMemo(() => {
        // Extract unique values from collection for filter options
        const conditions = new Set<string>();
        const sets = new Set<string>();
        const rarities = new Set<string>();
        const types = new Set<string>();

        collection.forEach(item => {
            if (item.condition) conditions.add(item.condition);
            if (item.cardDetails?.set?.name) sets.add(item.cardDetails.set.name);
            if (item.cardDetails?.rarity) rarities.add(item.cardDetails.rarity);
            if (item.cardDetails?.types) {
                item.cardDetails.types.forEach(type => types.add(type))
            }
        });

        return {
            conditions: Array.from(conditions).sort(),
            sets: Array.from(sets).sort(),
            rarities: Array.from(rarities).sort(),
            types: Array.from(types).sort()
        };
    }, [collection]);

    const handleFilterChange = (key: keyof FilterState, value: string) => {
        onFiltersChange({
            ...filters,
            [key]: value
        });
    };

    const clearFilters = () => {
        onFiltersChange({
        condition: '',
        set: '',
        rarity: '',
        type: ''
        });
    };

    const hasActiveFilters = Object.values(filters).some(value => value !== '');

    return (
        <div className="flex flex-wrap gap-3 items-center">
            
            {/* Condition Filter */}
            <div className="flex items-center gap-2">
                <label className="text-sm font-medium text-gray-700">Condition:</label>
                <select
                    value={filters.condition}
                    onChange={(e) => handleFilterChange('condition', e.target.value)}
                    className="border border-gray-300 rounded px-3 py-1 text-sm focus:ring-blue-500 focus:border-blue-500"
                >
                    <option value="">All</option>
                    {filterOptions.conditions.map(condition => (
                        <option key={condition} value={condition}>
                            {condition}
                        </option>
                    ))}
                </select>
            </div>

            {/* Set Filter */}
            <div className="flex items-center gap-2">
                <label className="text-sm font-medium text-gray-700">Set:</label>
                <select
                    value={filters.set}
                    onChange={(e) => handleFilterChange('set', e.target.value)}
                    className="border border-gray-300 rounded px-3 py-1 text-sm focus:ring-blue-500 focus:border-blue-500 max-w-48"
                >
                    <option value="">All Sets</option>
                    {filterOptions.sets.map(set => (
                        <option key={set} value={set}>
                            {set}
                        </option>
                    ))}
                </select>
            </div>

            {/* Rarity Filter */}
            <div className="flex items-center gap-2">
                <label className="text-sm font-medium text-gray-700">Rarity:</label>
                <select
                    value={filters.rarity}
                    onChange={(e) => handleFilterChange('rarity', e.target.value)}
                    className="border border-gray-300 rounded px-3 py-1 text-sm focus:ring-blue-500 focus:border-blue-500"
                >
                    <option value="">All</option>
                    {filterOptions.rarities.map(rarity => (
                        <option key={rarity} value={rarity}>
                            {rarity}
                        </option>
                    ))}
                </select>
            </div>

            {/* Type Filter */}
            <div className="flex items-center gap-2">
                <label className="text-sm font-medium text-gray-700">Type:</label>
                <select
                    value={filters.type}
                    onChange={(e) => handleFilterChange('type', e.target.value)}
                    className="border border-gray-300 rounded px-3 py-1 text-sm focus:ring-blue-500 focus:border-blue-500"
                >
                    <option value="">All Types</option>
                    {filterOptions.types.map(type => (
                        <option key={type} value={type}>
                            {type}
                        </option>
                    ))}
                </select>
            </div>

            {/* Clear Filters */}
            {hasActiveFilters && (
                <button
                    onClick={clearFilters}
                    className="text-sm text-gray-500 hover:text-gray-700 underline"
                >
                Clear Filters
                </button>
            )}
        </div>
    );
}