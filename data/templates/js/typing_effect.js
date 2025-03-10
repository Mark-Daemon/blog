class TypingEffect {
    constructor(element, speed = 50) {
        this.element = element;
        this.text = element.textContent;
        this.speed = speed;
        this.currentChar = 0;
        element.textContent = '';
        
        // Create cursor element
        this.cursor = document.createElement('span');
        this.cursor.className = 'cursor';
        this.cursor.textContent = '█';
        element.appendChild(this.cursor);
    }

    async type() {
        while (this.currentChar < this.text.length) {
            if (this.element.classList.contains('typing-effect')) {
                this.element.classList.remove('typing-effect');
            }
            // Insert text before the cursor
            this.cursor.insertAdjacentText('beforebegin', this.text.charAt(this.currentChar));
            this.currentChar++;
            await new Promise(resolve => setTimeout(resolve, this.speed));
        }
    }
}

function initializeTypingEffects() {
    const elements = document.querySelectorAll('.typing-effect');
    
    // Start all typing effects simultaneously
    elements.forEach(element => {
        const effect = new TypingEffect(element);
        effect.type(); // Don't await, let them all run independently
    });
}

// Start typing effects when the DOM is loaded
document.addEventListener('DOMContentLoaded', initializeTypingEffects);
