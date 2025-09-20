// src/components/WelcomeToggle.tsx
interface WelcomeToggleProps {
  showWelcome: boolean
  onToggle: () => void
}

function WelcomeToggle({ showWelcome, onToggle }: WelcomeToggleProps) {
  return (
    <button 
      className="bg-blue-500 text-white px-4 py-2 rounded"
      onClick={onToggle}
    >
      {showWelcome ? 'Hide Welcome' : 'Show Welcome'}
    </button>
  )
}

export default WelcomeToggle