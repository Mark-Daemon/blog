import * as THREE from 'three';

export class CRTInterference {
    constructor(width = 100, height = 100, lineCount = 20, isCircular = false) {
        this.width = width;
        this.height = height;
        this.lineCount = lineCount;
        
        // Create the scanlines mesh
        const scanlineGeometry = new THREE.BufferGeometry();
        const positions = new Float32Array(lineCount * 6);
        
        // Create horizontal lines across the entire area
        for (let i = 0; i < lineCount; i++) {
            const y = (i / lineCount) * height - height/2;
            
            // Each line needs 2 points
            const index = i * 6;
            positions[index] = -width/2;     // x1
            positions[index + 1] = y;        // y1
            positions[index + 2] = 0;        // z1
            positions[index + 3] = width/2;  // x2
            positions[index + 4] = y;        // y2
            positions[index + 5] = 0;        // z2
        }
        
        scanlineGeometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
        
        const scanlineMaterial = new THREE.LineBasicMaterial({
            color: 0x00ffaa,
            transparent: true,
            opacity: 0.25,
            blending: THREE.AdditiveBlending
        });
        
        this.scanlines = new THREE.LineSegments(scanlineGeometry, scanlineMaterial);
        
        // Create a mask for the scanlines
        let maskGeometry;
        if (isCircular) {
            maskGeometry = new THREE.CircleGeometry(width/2, 64);
        } else {
            maskGeometry = new THREE.PlaneGeometry(width, height);
        }
        
        const maskMaterial = new THREE.MeshBasicMaterial({
            color: 0x000000,
            transparent: true,
            opacity: 0.3,
            side: THREE.DoubleSide
        });
        this.mask = new THREE.Mesh(maskGeometry, maskMaterial);
        
        // Position the scanlines and mask in front of everything
        this.scanlines.renderOrder = 999;
        this.scanlines.position.z = 1;
        
        this.mask.renderOrder = 998;
        this.mask.position.z = 0.9;
        
        // Make materials always render on top
        this.scanlines.material.depthTest = false;
        this.mask.material.depthTest = false;
    }
    
    addToScene(scene) {
        scene.add(this.scanlines);
        scene.add(this.mask);
    }
    
    update(time) {
        const scanlinePositions = this.scanlines.geometry.attributes.position.array;
        const scanlineSpeed = 15; // Adjust speed of scanline movement
        
        for (let i = 1; i < scanlinePositions.length; i += 6) {
            scanlinePositions[i] = ((((scanlinePositions[i] + this.height/2 + time * scanlineSpeed) % this.height) - this.height/2)); // y1
            scanlinePositions[i + 3] = scanlinePositions[i]; // y2
        }
        
        this.scanlines.geometry.attributes.position.needsUpdate = true;
        
        // Vary scanline opacity slightly with larger range and higher base opacity
        this.scanlines.material.opacity = 0.2 + Math.sin(time * 2) * 0.06;
    }
} 