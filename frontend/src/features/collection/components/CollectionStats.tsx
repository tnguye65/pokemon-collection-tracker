import React, { useMemo } from 'react';
import { type UserCollection } from '../types/CollectionTypes';

interface CollectionStatsProps {
    collection: UserCollection[],
    className?: string;
}

export const CollectionStats: React.FC<CollectionStatsProps> = ({
    collection,
    className = ""
}) => {
    const stats = useMemo(() => {
        const totalCards = collection.reduce((sum, item) => sum + item.quantity, 0);
        const uniqueCards = collection.length;

        // Condition breakdown
        const conditionCounts = collection.reduce((acc, item) => {
            const condition = item.condition;
            acc[condition] = (acc[condition] || 0) + item.quantity;
            return acc;
        }, {} as Record<string, number>);

        const rarityCounts = collection.reduce((acc, item) => {
            const rarity = item.cardDetails?.rarity || "Unknown";
            acc[rarity] = (acc[rarity] || 0) + item.quantity;
            return acc;
        }, {} as Record<string, number>);

        const setCounts = collection.reduce((acc, item) => {
            const setName = item.cardDetails?.set?.name || "Unknown";
            acc[setName] = (acc[setName] || 0) + 1;
            return acc;
        }, {} as Record<string, number>);
        
        // Most common condition
        const mostCommonCondition = Object.entries(conditionCounts)
            .sort(([,a], [,b]) => b - a)[0];

        // Most common rarity
        const mostCommonRarity = Object.entries(rarityCounts)
            .sort(([,a], [,b]) => b - a)[0];

        return {
            totalCards,
            uniqueCards,
            conditionCounts,
            rarityCounts,
            setCounts,
            totalSets: Object.keys(setCounts).length,
            mostCommonCondition: mostCommonCondition?.[0] || 'N/A',
            mostCommonRarity: mostCommonRarity?.[0] || 'N/A'
        };
    }, [collection]);

    const getConditionColor = (condition: string) => {
        switch (condition.toLowerCase()) {
            case 'mint': return 'bg-green-100 text-green-800';
            case 'near mint': return 'bg-green-50 text-green-700';
            case 'excellent': return 'bg-blue-100 text-blue-800';
            case 'good': return 'bg-yellow-100 text-yellow-800';
            case 'light played': return 'bg-orange-100 text-orange-800';
            case 'played': return 'bg-red-100 text-red-800';
            case 'poor': return 'bg-red-200 text-red-900';
            default: return 'bg-gray-100 text-gray-800';
        }
    };

    const getRarityColor = (rarity: string) => {
        switch (rarity.toLowerCase()) {
            case 'common': return 'bg-gray-100 text-gray-800';
            case 'uncommon': return 'bg-green-100 text-green-800';
            case 'rare': return 'bg-blue-100 text-blue-800';
            case 'rare holo': return 'bg-purple-100 text-purple-800';
            case 'ultra rare': return 'bg-yellow-100 text-yellow-800';
            case 'secret rare': return 'bg-pink-100 text-pink-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    };

    if (collection.length === 0) {
        return null;
    }

    return (
        <div className={`bg-white rounded-lg shadow-sm border p-6 ${className}`}>
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Collection Overview</h2>

            {/* Main Stats */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                <div className="text-center">
                    <div className="text-2xl font-bold text-blue-600">{stats.totalCards}</div>
                    <div className="text-sm text-gray-600">Total Cards</div>
                </div>
                <div className="text-center">
                    <div className="text-2xl font-bold text-green-600">{stats.uniqueCards}</div>
                    <div className="text-sm text-gray-600">Unique Cards</div>
                </div>
                <div className="text-center">
                    <div className="text-2xl font-bold text-purple-600">{stats.totalSets}</div>
                    <div className="text-sm text-gray-600">Sets</div>
                </div>
                <div className="text-center">
                    <div className="text-2xl font-bold text-orange-600">
                        {Math.round((stats.totalCards / stats.uniqueCards) * 10) / 10}
                    </div>
                    <div className="text-sm text-gray-600">Avg Copies</div>
                </div>
            </div>

            <div className="mb-6">
                <h3 className="text-sm font-medium text-gray-700 mb-3">Condition Breakdown</h3>
                <div className="flex flex-wrap gap-2">
                    {Object.entries(stats.conditionCounts)
                        .sort(([,a], [,b]) => b - a)
                        .map(([condition, count]) => (
                            <span
                                key={condition}
                                className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${getConditionColor(condition)}`}
                            >
                                {condition}: {count}
                            </span>
                        ))}
                </div>
            </div>

            {/* Rarity Breakdown */}
            <div className="mb-6">
                <h3 className="text-sm font-medium text-gray-700 mb-3">Rarity Breakdown</h3>
                <div className="flex flex-wrap gap-2">
                {Object.entries(stats.rarityCounts)
                    .sort(([,a], [,b]) => b - a)
                    .slice(0, 6) // Show top 6 rarities
                    .map(([rarity, count]) => (
                    <span
                        key={rarity}
                        className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${getRarityColor(rarity)}`}
                    >
                        {rarity}: {count}
                    </span>
                    ))}
                </div>
            </div>

            {/* Top Sets */}
            <div>
                <h3 className="text-sm font-medium text-gray-700 mb-3">Top Sets</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                {Object.entries(stats.setCounts)
                    .sort(([,a], [,b]) => b - a)
                    .slice(0, 6) // Show top 6 sets
                    .map(([setName, count]) => (
                        <div key={setName} className="flex justify-between items-center py-1">
                            <span className="text-sm text-gray-700 truncate">{setName}</span>
                            <span className="text-sm font-medium text-gray-900">{count}</span>
                        </div>
                    ))}
                </div>
            </div>

        </div>
    )
}