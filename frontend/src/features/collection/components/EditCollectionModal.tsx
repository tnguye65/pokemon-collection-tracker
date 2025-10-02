import { useState } from 'react'
import {type UserCollection, CARD_CONDITIONS, CARD_VARIANTS } from '../../collection/types/CollectionTypes'

interface EditCollectionModalProps {
  item: UserCollection;  // This should match what CollectionGrid passes
  onSave: (updatedItem: UserCollection) => Promise<void>;
  onCancel: () => void;
}

export const EditCollectionModal: React.FC<EditCollectionModalProps> = ({
    item,
    onSave,
    onCancel
}) => {
    const [formData, setFormData] = useState({
        quantity: item.quantity,
        condition: item.condition,
        variant: item.variant || '',
        notes: item.notes || ''
    });

    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (formData.quantity < 1) {
            alert('Quantity must be at least 1');
            return;
        }

        try {
            setIsSubmitting(true);
            const updatedItem: UserCollection = {
                ...item,
                quantity: formData.quantity,
                condition: formData.condition,
                variant: formData.variant || undefined,
                notes: formData.notes || undefined
            };

            await onSave(updatedItem);
        } catch (error) {
            console.error('Failed to save: ', error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleInputChange = (field: keyof typeof formData, value: string | number) => {
        setFormData(prev => ({
            ...prev,
            [field]: value
        }))
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg max-w-md w-full">
                <div className="p-6">
                    <div className="flex justify-between items-center mb-4">
                        <h2 className="text-xl font-bold text-gray-900">Edit Card</h2>
                        <button
                            onClick={onCancel}
                            className="text-gray-500 hover:text-gray-700"
                        >
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>

                    {/* Card Info */}
                    <div className="flex items-center mb-4 p-3 bg-gray-50 rounded-lg">
                        <img
                            src={item.cardDetails?.image || '/placeholder-card.png'}
                            alt={item.cardDetails?.name}
                            className="w-12 h-16 object-cover rounded"
                        />
                        <div className="ml-3">
                            <h3 className="font-medium text-gray-900">{item.cardDetails?.name}</h3>
                            <p className="text-sm text-gray-600">{item.cardDetails?.set?.name}</p>
                        </div>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        {/* Quantity */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Quantity
                            </label>
                            <input
                                type="number"
                                min="1"
                                value={formData.quantity}
                                onChange={(e) => handleInputChange('quantity', parseInt(e.target.value) || 1)}
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                                required
                            />
                        </div>

                        {/* Condition */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Condition
                            </label>
                            <select
                                value={formData.condition}
                                onChange={(e) => handleInputChange('condition', e.target.value)}
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                                required
                            >
                                {CARD_CONDITIONS.map(condition => (
                                    <option key={condition} value={condition}>
                                        {condition}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Variant */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Variant (Optional)
                            </label>
                            <select
                                value={formData.variant}
                                onChange={(e) => handleInputChange('variant', e.target.value)}
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                            >
                                <option value="">No Variant</option>
                                {CARD_VARIANTS.map(variant => (
                                    <option key={variant} value={variant}>
                                        {variant}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Notes */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Notes (Optional)
                            </label>
                            <textarea
                                value={formData.notes}
                                onChange={(e) => handleInputChange('notes', e.target.value)}
                                rows={3}
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Add any notes about this card..."
                            />
                        </div>

                        {/* Buttons */}
                        <div className="flex gap-3 pt-4">
                            <button
                                type="button"
                                onClick={onCancel}
                                className="flex-1 px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
                                disabled={isSubmitting}
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
                                disabled={isSubmitting}
                            >
                                {isSubmitting ? 'Saving...' : 'Save Changes'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default EditCollectionModal;