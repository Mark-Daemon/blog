import * as THREE from 'three';
import { CRTInterference } from './CRTInterference.js';
import { BacklightFlicker } from './BacklightFlicker.js';

class NostremoScanner {
    constructor() {
        this.frameInterval = 1000 / 60; // 24 fps
        this.lastFrameTime = 0;
        
        this.scene = new THREE.Scene();
        
        // Setup camera
        const container = document.querySelector('.scanner-container');
        const aspect = container.clientWidth / container.clientHeight;
        this.camera = new THREE.PerspectiveCamera(45, aspect, 0.1, 1000);
        this.camera.position.z = 100;
        
        // Setup renderer
        this.renderer = new THREE.WebGLRenderer({
            canvas: document.getElementById('scannerCanvas'),
            alpha: true,
            antialias: true
        });
        this.renderer.setSize(container.clientWidth, container.clientHeight);
        
        // Add static interference
        this.createStaticInterference();
        
        // Add CRT scanlines
        this.crtInterference = new CRTInterference(100, 100, 20, true);
        this.crtInterference.addToScene(this.scene);
        
        // Add backlight flicker
        this.backlightFlicker = new BacklightFlicker(100, 100, true);
        this.backlightFlicker.addToScene(this.scene);
        
        // Create grid lines
        this.createGridLines();
        
        // Create radar sweep
        this.createRadarSweep();
        
        // Create blips
        this.blips = this.createBlips();
        
        // Create rings
        this.createRings();
        
        // Start animation
        this.animate();
    }
    
    createGridLines() {
        const material = new THREE.LineBasicMaterial({
            color: 0x00ffaa,
            transparent: true,
            opacity: 0.4
        });
        
        // Vertical line
        const verticalGeometry = new THREE.BufferGeometry().setFromPoints([
            new THREE.Vector3(0, -50, 0),
            new THREE.Vector3(0, 50, 0)
        ]);
        const verticalLine = new THREE.Line(verticalGeometry, material);
        this.scene.add(verticalLine);
        
        // Horizontal line
        const horizontalGeometry = new THREE.BufferGeometry().setFromPoints([
            new THREE.Vector3(-50, 0, 0),
            new THREE.Vector3(50, 0, 0)
        ]);
        const horizontalLine = new THREE.Line(horizontalGeometry, material);
        this.scene.add(horizontalLine);
    }
    
    createRadarSweep() {
        // Create thick line geometry
        const width = 2.0; // Line thickness
        const points = [
            new THREE.Vector3(0, -width/2, 0),
            new THREE.Vector3(50, -width/2, 0),
            new THREE.Vector3(50, width/2, 0),
            new THREE.Vector3(0, width/2, 0)
        ];
        
        const sweepGeometry = new THREE.BufferGeometry();
        const vertices = new Float32Array([
            ...points[0].toArray(),
            ...points[1].toArray(),
            ...points[2].toArray(),
            ...points[0].toArray(),
            ...points[2].toArray(),
            ...points[3].toArray()
        ]);
        
        // Create gradient using vertex colors
        const colors = new Float32Array([
            1, 1, 1,  // Full opacity at center
            0, 0, 0,  // Fade to transparent at tip
            0, 0, 0,  // Fade to transparent at tip
            1, 1, 1,  // Full opacity at center
            0, 0, 0,  // Fade to transparent at tip
            1, 1, 1   // Full opacity at center
        ]);
        
        sweepGeometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3));
        sweepGeometry.setAttribute('color', new THREE.BufferAttribute(colors, 3));
        
        const sweepMaterial = new THREE.MeshBasicMaterial({
            color: 0x00ffaa,
            transparent: true,
            opacity: 0.8,
            vertexColors: true,
            side: THREE.DoubleSide
        });
        
        this.sweepLine = new THREE.Mesh(sweepGeometry, sweepMaterial);
        this.scene.add(this.sweepLine);
        
        // Create sweep area with gradient
        const sweepAreaVertexShader = `
            varying vec2 vUv;
            void main() {
                vUv = uv;
                gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
            }
        `;
        
        const sweepAreaFragmentShader = `
            varying vec2 vUv;
            void main() {
                // Calculate angle from center point (0.5, 0.5)
                vec2 centered = vUv - vec2(0.5, 0.5);
                float angle = atan(centered.y, centered.x);
                
                // Normalize angle to 0-1 range for first quadrant
                float normalizedAngle = angle / (PI * 0.5);
                
                // Calculate opacity with longer fade
                float opacity = 1.0 - normalizedAngle;
                opacity = pow(opacity, 0.5); // Use square root for longer fade
                
                // Increase the base opacity but maintain fade
                vec4 color = vec4(0.0, 1.0, 0.67, opacity * 0.3);
                gl_FragColor = color;
            }
        `;
        
        const sweepShape = new THREE.Shape();
        sweepShape.moveTo(0, 0);
        sweepShape.lineTo(50, 0);
        sweepShape.absarc(0, 0, 50, 0, Math.PI/2, false);
        sweepShape.lineTo(0, 0);
        
        const sweepAreaGeometry = new THREE.ShapeGeometry(sweepShape);
        const sweepAreaMaterial = new THREE.ShaderMaterial({
            vertexShader: sweepAreaVertexShader,
            fragmentShader: sweepAreaFragmentShader,
            transparent: true,
            side: THREE.DoubleSide,
            defines: {
                PI: Math.PI
            }
        });
        
        this.sweepArea = new THREE.Mesh(sweepAreaGeometry, sweepAreaMaterial);
        this.scene.add(this.sweepArea);
    }
    
    positionBlip(blip, isInitial = false) {
        if (isInitial) {
            // Initial random position
            const angle = Math.random() * Math.PI * 2;
            const radius = 20 + Math.random() * 15;
            blip.position.x = Math.cos(angle) * radius;
            blip.position.y = Math.sin(angle) * radius;
        } else {
            // Move slightly from current position
            const randomAngle = Math.random() * Math.PI * 2;
            const randomDistance = 5;
            
            // Calculate new position
            const newX = blip.position.x + Math.cos(randomAngle) * randomDistance;
            const newY = blip.position.y + Math.sin(randomAngle) * randomDistance;
            
            // Calculate distance from center
            const distanceFromCenter = Math.sqrt(newX * newX + newY * newY);
            
            // If new position would be outside valid range, try again
            if (distanceFromCenter < 20 || distanceFromCenter > 35) {
                this.positionBlip(blip, false);
                return;
            }
            
            blip.position.x = newX;
            blip.position.y = newY;
        }
    }
    
    createBlips() {
        const blips = [];
        // Make blips slightly larger
        const blipGeometry = new THREE.CircleGeometry(1.5, 16);
        const blipMaterial = new THREE.MeshBasicMaterial({
            color: 0x00ffaa,
            transparent: true,
            opacity: 0
        });
        
        // Add glow effect
        const glowGeometry = new THREE.CircleGeometry(3, 16);
        const glowMaterial = new THREE.MeshBasicMaterial({
            color: 0x00ffaa,
            transparent: true,
            opacity: 0
        });
        
        // Create 3 blips with random positions
        for (let i = 0; i < 3; i++) {
            const blip = new THREE.Mesh(blipGeometry, blipMaterial.clone());
            const glow = new THREE.Mesh(glowGeometry, glowMaterial.clone());
            
            this.positionBlip(blip, true); // Initial random position
            glow.position.copy(blip.position); // Match glow position to blip
            
            this.scene.add(blip);
            this.scene.add(glow);
            
            blips.push({
                mesh: blip,
                glow: glow,
                fadeStart: null,
                isVisible: false
            });
        }
        
        return blips;
    }
    
    createRings() {
        const ringMaterial = new THREE.LineBasicMaterial({
            color: 0x00ffaa,
            transparent: true,
            opacity: 0.3
        });
        
        [15, 30, 45].forEach(radius => {
            // Create points for the ring
            const segments = 64;
            const points = [];
            for (let i = 0; i <= segments; i++) {
                const theta = (i / segments) * Math.PI * 2;
                points.push(new THREE.Vector3(
                    Math.cos(theta) * radius,
                    Math.sin(theta) * radius,
                    0
                ));
            }
            
            const ringGeometry = new THREE.BufferGeometry().setFromPoints(points);
            const ring = new THREE.LineLoop(ringGeometry, ringMaterial);
            this.scene.add(ring);
        });
    }
    
    createStaticInterference() {
        const particleCount = 300;
        const particles = new THREE.BufferGeometry();
        const positions = new Float32Array(particleCount * 3);
        const opacities = new Float32Array(particleCount);
        
        for (let i = 0; i < particleCount; i++) {
            // Random position within a circle
            const angle = Math.random() * Math.PI * 2;
            const radius = Math.random() * 50;
            positions[i * 3] = Math.cos(angle) * radius;
            positions[i * 3 + 1] = Math.sin(angle) * radius;
            positions[i * 3 + 2] = 0;
            
            opacities[i] = Math.random();
        }
        
        particles.setAttribute('position', new THREE.BufferAttribute(positions, 3));
        particles.setAttribute('opacity', new THREE.BufferAttribute(opacities, 1));
        
        const particleMaterial = new THREE.PointsMaterial({
            color: 0x00ffaa,
            size: 1.5,
            transparent: true,
            opacity: 0.5,
            blending: THREE.AdditiveBlending
        });
        
        this.staticParticles = new THREE.Points(particles, particleMaterial);
        this.scene.add(this.staticParticles);
    }
    
    animate = () => {
        requestAnimationFrame(this.animate);
        
        // Check if enough time has passed for next frame
        const currentTime = Date.now();
        if (currentTime - this.lastFrameTime < this.frameInterval) {
            return;
        }
        this.lastFrameTime = currentTime;
        
        const time = currentTime * 0.001;
        const sweepAngle = -time % (Math.PI * 2);
        this.sweepLine.rotation.z = sweepAngle;
        this.sweepArea.rotation.z = sweepAngle;
        
        // Update blips
        this.blips.forEach(blip => {
            const blipAngle = Math.atan2(blip.mesh.position.y, blip.mesh.position.x);
            const normalizedBlipAngle = blipAngle < 0 ? blipAngle + Math.PI * 2 : blipAngle;
            const normalizedSweepAngle = sweepAngle < 0 ? sweepAngle + Math.PI * 2 : sweepAngle;
            
            // Check if sweep line just passed over the blip
            if (!blip.isVisible && 
                Math.abs(normalizedSweepAngle - normalizedBlipAngle) < 0.1) {
                blip.isVisible = true;
                blip.mesh.material.opacity = 1.0; // Increased brightness
                blip.glow.material.opacity = 0.4; // Add glow
                blip.fadeStart = time;
            }
            
            // Handle fading
            if (blip.isVisible && blip.fadeStart) {
                const fadeTime = time - blip.fadeStart;
                if (fadeTime > 5.0) {
                    blip.isVisible = false;
                    blip.fadeStart = null;
                    blip.mesh.material.opacity = 0;
                    blip.glow.material.opacity = 0;
                    this.positionBlip(blip.mesh, false);
                    blip.glow.position.copy(blip.mesh.position);
                } else {
                    // Slower fade over 5 seconds
                    const fadeRatio = fadeTime / 5.0;
                    blip.mesh.material.opacity = 1.0 * (1 - fadeRatio);
                    blip.glow.material.opacity = 0.4 * (1 - fadeRatio);
                }
            }
        });
        
        // Animate static interference
        const positions = this.staticParticles.geometry.attributes.position.array;
        const opacities = this.staticParticles.geometry.attributes.opacity.array;
        
        for (let i = 0; i < positions.length; i += 3) {
            // Increased movement amount
            positions[i] += (Math.random() - 0.5) * 0.3;
            positions[i + 1] += (Math.random() - 0.5) * 0.3;
            
            // Keep particles within bounds
            const x = positions[i];
            const y = positions[i + 1];
            const distance = Math.sqrt(x * x + y * y);
            
            if (distance > 50) {
                const angle = Math.atan2(y, x);
                positions[i] = Math.cos(angle) * 49;
                positions[i + 1] = Math.sin(angle) * 49;
            }
            
            // Increased opacity range
            const opacityIndex = i / 3;
            opacities[opacityIndex] = 0.2 + Math.random() * 0.6;
        }
        
        this.staticParticles.geometry.attributes.position.needsUpdate = true;
        this.staticParticles.geometry.attributes.opacity.needsUpdate = true;
        
        // Update backlight flicker
        this.backlightFlicker.update();
        
        // Replace scanline animation code with:
        this.crtInterference.update(time);
        
        this.renderer.render(this.scene, this.camera);
    }
}

// Initialize scanner when document is loaded
document.addEventListener('DOMContentLoaded', () => {
    new NostremoScanner();
}); 