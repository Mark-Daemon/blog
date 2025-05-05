// Global variable to store the search index
let searchIndex = null;

// Load the search index when the page loads
async function loadSearchIndex() {
    try {
        const response = await fetch('/blog/search.json');
        searchIndex = await response.json();
    } catch (error) {
        console.error('Error loading search index:', error);
    }
}

// Initialize search when the page loads
document.addEventListener('DOMContentLoaded', loadSearchIndex);

// Function to get auto-complete suggestions
function getAutoCompleteSuggestions(searchText) {
    if (!searchIndex || !searchIndex._prefixes) return [];
    
    // Split the search text into words
    const words = searchText.toLowerCase().split(/\s+/);
    const lastWord = words[words.length - 1];
    
    // If the last word is empty or just whitespace, return no suggestions
    if (!lastWord.trim()) return [];
    
    // Get suggestions for the last word
    const suggestions = searchIndex._prefixes[lastWord] || [];
    
    // If there are previous words, filter suggestions to only include words that haven't been used
    if (words.length > 1) {
        const previousWords = new Set(words.slice(0, -1));
        return suggestions.filter(word => !previousWords.has(word));
    }
    
    return suggestions;
}

// Function to display auto-complete suggestions
function displayAutoCompleteSuggestions(suggestions, searchText) {
    const suggestionsContainer = document.getElementById('search-suggestions');
    const searchInput = document.getElementById('search-input');
    if (!suggestionsContainer || !searchInput) return;
    
    suggestionsContainer.innerHTML = '';
    
    if (suggestions.length === 0) {
        suggestionsContainer.style.display = 'none';
        return;
    }
    
    // Position the suggestions container
    const inputRect = searchInput.getBoundingClientRect();
    suggestionsContainer.style.top = `${inputRect.bottom}px`;
    suggestionsContainer.style.left = `${inputRect.left}px`;
    suggestionsContainer.style.width = `${inputRect.width}px`;
    
    const suggestionsList = document.createElement('ul');
    suggestionsList.className = 'suggestions-list';
    
    suggestions.forEach(suggestion => {
        const listItem = document.createElement('li');
        listItem.className = 'suggestion-item';
        listItem.textContent = suggestion;
        
        // Add click handler to select the suggestion
        listItem.addEventListener('click', () => {
            const words = searchText.split(/\s+/);
            words[words.length - 1] = suggestion;
            const newSearchText = words.join(' ');
            
            if (searchInput) {
                searchInput.value = newSearchText;
                // Trigger the search
                const event = new Event('input');
                searchInput.dispatchEvent(event);
            }
            
            suggestionsContainer.style.display = 'none';
        });
        
        suggestionsList.appendChild(listItem);
    });
    
    suggestionsContainer.appendChild(suggestionsList);
    suggestionsContainer.style.display = 'block';
}

// Function to perform the search
function performSearch(searchText) {
    if (!searchIndex) {
        console.error('Search index not loaded');
        return [];
    }

    // Convert search text to lowercase and split into words
    const searchTerms = searchText.toLowerCase().split(/\s+/).filter(term => term.length > 0);
    
    // Create a map to store document scores and counts
    const documentScores = new Map();
    
    // For each search term, find matching documents
    searchTerms.forEach(term => {
        // Check if the term exists in the search index
        if (searchIndex[term]) {
            searchIndex[term].forEach(docId => {
                // Get or initialize the document score object
                const currentScore = documentScores.get(docId) || { score: 0, count: 0 };
                
                // Update the score and count
                documentScores.set(docId, {
                    score: currentScore.score + 1,
                    count: currentScore.count + 1
                });
            });
        }
    });
    
    // Convert scores to array and sort by score (descending)
    const results = Array.from(documentScores.entries())
        .map(([docId, { score, count }]) => {
            // Get the document info from _ids
            const docInfo = searchIndex._ids[docId];
            if (!docInfo) {
                console.warn(`Missing document info for ID: ${docId}`);
                return null;
            }
            
            return {
                ...docInfo,
                score,
                matchCount: count
            };
        })
        .filter(result => result !== null) // Remove any null results
        .sort((a, b) => b.score - a.score);
    
    return results;
}

// Function to display search results
function displaySearchResults(results) {
    const resultsContainer = document.getElementById('search-results');
    if (!resultsContainer) return;
    
    resultsContainer.innerHTML = '';
    
    // Update the entry count
    const entryCount = document.getElementById('entry-count');
    if (entryCount) {
        entryCount.textContent = results.length;
    }
    
    if (results.length === 0) {
        resultsContainer.innerHTML = '<p>No results found</p>';
        return;
    }
    
    const resultsList = document.createElement('div');
    resultsList.className = 'search-results-list';
    
    results.forEach(result => {
        const listItem = document.createElement('div');
        listItem.className = 'search-result-item';
        
        const link = document.createElement('a');
        link.href = result.link;
        
        const blogPost = document.createElement('div');
        blogPost.className = 'blog-post';
        
        const title = document.createElement('div');
        title.className = 'post-title';
        title.textContent = result.title.toUpperCase();
        
        const meta = document.createElement('div');
        meta.className = 'post-meta';
        meta.textContent = result.date.toUpperCase();
        
        const excerpt = document.createElement('div');
        excerpt.className = 'post-excerpt';
        excerpt.textContent = result.excerpt;
        
        const matchCount = document.createElement('div');
        matchCount.className = 'post-matches';
        matchCount.textContent = `${result.matchCount} match${result.matchCount !== 1 ? 'es' : ''}`;
        
        blogPost.appendChild(title);
        blogPost.appendChild(meta);
        blogPost.appendChild(excerpt);
        blogPost.appendChild(matchCount);
        
        link.appendChild(blogPost);
        listItem.appendChild(link);
        
        resultsList.appendChild(listItem);
    });
    
    resultsContainer.appendChild(resultsList);
}

// Add event listeners for search input
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('search-input');
    if (searchInput) {
        // Create suggestions container if it doesn't exist
        let suggestionsContainer = document.getElementById('search-suggestions');
        if (!suggestionsContainer) {
            suggestionsContainer = document.createElement('div');
            suggestionsContainer.id = 'search-suggestions';
            suggestionsContainer.className = 'search-suggestions';
            searchInput.parentNode.insertBefore(suggestionsContainer, searchInput.nextSibling);
        }
        
        // Handle input changes
        searchInput.addEventListener('input', (e) => {
            const searchText = e.target.value.trim();
            
            // Get and display auto-complete suggestions
            const suggestions = getAutoCompleteSuggestions(searchText);
            displayAutoCompleteSuggestions(suggestions, searchText);
            
            // Perform search if there's text
            if (searchText.length > 0) {
                const results = performSearch(searchText);
                displaySearchResults(results);
            } else {
                const entryCount = document.getElementById('entry-count');
                const resultsContainer = document.getElementById('search-results');
                if (resultsContainer) {
                    resultsContainer.innerHTML = '';
                }
                if (entryCount) {
                    entryCount.textContent = 0;
                }
                suggestionsContainer.style.display = 'none';
            }
        });
        
        // Hide suggestions when clicking outside
        document.addEventListener('click', (e) => {
            if (!searchInput.contains(e.target) && !suggestionsContainer.contains(e.target)) {
                suggestionsContainer.style.display = 'none';
            }
        });
    }
});
