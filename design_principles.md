# Retro Terminal Design Principles

## Core Display Characteristics

### Base Aesthetics
- Monochrome green phosphor display (reminiscent of 1970s CRT monitors)
- Primary color: #0f0 (pure green) with varying opacity levels
- Dark background (#000) for maximum contrast
- Courier New monospace font for authentic terminal feel

### Visual Effects
1. **Screen Effects**
   - Subtle screen flicker (0.15s animation)
   - Vertical scan lines with continuous movement
   - Visible phosphor decay simulation
   - CRT-style screen artifacts

2. **Text Characteristics**
   - Monospace typography
   - Character-by-character typing effects
   - Blinking cursors (underscore style)
   - Text glow/bloom effects using text-shadow

## Component Design

### Terminal Window
- Bordered container with green outline
- Subtle inner glow
- Overall screen flicker effect
- Vertical scanning line animation

### Motion Tracker/Scanner
- Circular display with grid overlay
- Concentric distance rings
- Rotating scan line with gradient
- Sweeping radar effect
- Pulsing blips for detected objects
- Distance markers (10M, 20M, 30M)
- Glowing effects for enhanced visibility

### Interactive Elements
- Hover states with subtle background illumination
- Command-line style navigation (> prefix)
- Blinking cursors for active states
- ASCII-style borders and dividers

## Animation Principles

### Timing Guidelines
- Screen flicker: 0.15s cycle
- Scan lines: 6s per screen cycle
- Cursor blink: 1s intervals
- Text typing: ~3.5s for full line
- Scanner rotation: 4s per 360°
- Blip pulse: 2s cycle

### Animation Types
1. **Continuous Animations**
   - Screen scan lines
   - Scanner rotation
   - Base screen flicker
   
2. **State Animations**
   - Hover effects
   - Cursor blinks
   - Blip pulses
   - Text typing

## Interface Guidelines

### Layout Structure
- Header with system identification
- Navigation using command-line style
- Content blocks with green borders
- Status indicators in ASCII style
- System status in bottom data block

### Text Hierarchy
1. **Primary Text**
   - Full brightness (#0f0)
   - Optional glow effects
   
2. **Secondary Text**
   - Reduced opacity (0.7-0.8)
   - Smaller font size
   
3. **System Text**
   - Lowest opacity (0.4-0.6)
   - Smallest font size
   - Often used for status/metrics

### Interactive States
- Hover: Increase in brightness/glow
- Active: Blinking cursor or indicator
- Disabled: Reduced opacity
- Warning: Increased pulse rate

## Technical Considerations

### Performance
- Use CSS animations for smooth performance
- Minimize DOM updates
- Use transform and opacity for animations
- Layer effects using pseudo-elements

### Accessibility
- Maintain readable contrast ratios
- Ensure interactive elements are clearly visible
- Provide sufficient text size
- Consider animation reduction preferences

### Browser Compatibility
- Use standard CSS properties
- Provide fallbacks for advanced effects
- Test across major browsers
- Consider mobile device limitations

## Implementation Notes

### CSS Best Practices
- Use CSS custom properties for theme colors
- Organize animations by type
- Maintain consistent timing variables
- Layer effects using z-index appropriately

### HTML Structure
- Semantic markup where possible
- Clear component hierarchy
- Consistent class naming
- Proper use of containers for effects

This design system aims to create an authentic retro terminal experience while maintaining modern web development best practices and user experience considerations. 