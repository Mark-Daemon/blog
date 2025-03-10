import * as THREE from 'three';

export class BacklightFlicker {
    constructor(width = 100, height = 100, isCircular = false) {
        // Create base with flickering
        let baseGeometry;
        if (isCircular) {
            baseGeometry = new THREE.CircleGeometry(width/2, 64);
        } else {
            baseGeometry = new THREE.PlaneGeometry(width, height);
        }
        
        const baseMaterial = new THREE.MeshBasicMaterial({
            color: 0x001410,
            transparent: true,
            opacity: 0.3
        });
        this.baseCircle = new THREE.Mesh(baseGeometry, baseMaterial);
        
        // Add a glow layer for the backlight
        let glowGeometry;
        if (isCircular) {
            glowGeometry = new THREE.CircleGeometry(width/2, 64);
        } else {
            glowGeometry = new THREE.PlaneGeometry(width, height);
        }
        
        const glowMaterial = new THREE.MeshBasicMaterial({
            color: 0x00ffaa,
            transparent: true,
            opacity: 0.05
        });
        this.baseGlow = new THREE.Mesh(glowGeometry, glowMaterial);
    }
    
    addToScene(scene) {
        scene.add(this.baseCircle);
        scene.add(this.baseGlow);
    }
    
    update() {
        // Add subtle backlight flicker
        const flickerAmount = Math.random();
        this.baseGlow.material.opacity = 0.03 + (flickerAmount * 0.04);
        this.baseCircle.material.opacity = 0.25 + (flickerAmount * 0.1);
        
        // Occasionally add a stronger flicker pulse
        if (Math.random() < 0.02) { // 2% chance each frame
            this.baseGlow.material.opacity = 0.1 + (Math.random() * 0.05);
            this.baseCircle.material.opacity = 0.4;
        }
    }
} 