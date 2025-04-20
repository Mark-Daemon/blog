import * as THREE from 'three';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { BacklightFlicker } from '../scanner/BacklightFlicker.js';

class NostremoShip {
    constructor() {
        this.frameInterval = 1000 / 24; // 24 fps
        this.lastFrameTime = 0;
        
        this.scene = new THREE.Scene();
        
        // Update camera setup
        const container = document.querySelector('.ship-container');
        const aspect = container.clientWidth / container.clientHeight;
        this.camera = new THREE.PerspectiveCamera(30, aspect, 0.1, 1000);
        
        // Create a separate scene for the CRT effect
        this.overlayScene = new THREE.Scene();
        this.overlayCamera = new THREE.OrthographicCamera(
            -container.clientWidth/2, 
            container.clientWidth/2, 
            container.clientHeight/2, 
            -container.clientHeight/2, 
            0.1, 
            10
        );
        this.overlayCamera.position.z = 5;
        
        this.renderer = new THREE.WebGLRenderer({
            canvas: document.getElementById('shipCanvas'),
            alpha: true,
            antialias: true
        });
        
        // Set renderer size to match container
        this.renderer.setSize(container.clientWidth, container.clientHeight, false);
        this.renderer.setPixelRatio(window.devicePixelRatio);
        
        // Add lighting
        const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
        this.scene.add(ambientLight);
        
        const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
        directionalLight.position.set(5, 5, 5);
        this.scene.add(directionalLight);
        
        // Add backlight flicker to overlay scene
        this.backlightFlicker = new BacklightFlicker(
            container.clientWidth,
            container.clientHeight,
            false  // Keep as rectangular (default)
        );
        this.backlightFlicker.addToScene(this.overlayScene);
        
        // Adjust backlight colors for ship view
        this.backlightFlicker.baseGlow.material.color.setHex(0x00ffaa);
        this.backlightFlicker.baseCircle.material.opacity = 0.15;
        
        // Initialize IndexedDB
        this.initializeCache().then(() => {
            this.loadModel();
        });
    }
    
    async initializeCache() {
        const request = indexedDB.open('NostremoCache', 1);
        
        request.onupgradeneeded = (event) => {
            const db = event.target.result;
            if (!db.objectStoreNames.contains('models')) {
                db.createObjectStore('models');
            }
        };

        return new Promise((resolve, reject) => {
            request.onerror = () => reject(request.error);
            request.onsuccess = () => {
                this.db = request.result;
                resolve();
            };
        });
    }

    async loadModel() {
        try {
            // Try to load from cache first
            const cachedModel = await this.getFromCache('shipModel');
            if (cachedModel) {
                this.createModelFromGeometry(cachedModel);
                return;
            }

            // If not in cache, load from file
            const loader = new GLTFLoader();
            loader.load(
                '/blog/js/ship/scene.gltf',
                (gltf) => {
                    // Create container for wireframe model
                    const container = new THREE.Group();
                    const geometries = [];

                    // Convert model to outlines and collect geometries
                    gltf.scene.traverse((child) => {
                        if (child.isMesh) {
                            // Store the position attribute data
                            const geometry = child.geometry;
                            const positions = geometry.attributes.position.array;
                            geometries.push({
                                positions: Array.from(positions), // Convert TypedArray to regular array for JSON storage
                                index: geometry.index ? Array.from(geometry.index.array) : null
                            });
                            
                            const edges = new THREE.EdgesGeometry(child.geometry, 30);
                            const line = new THREE.LineSegments(
                                edges,
                                new THREE.LineBasicMaterial({ 
                                    color: 0x00ffff,
                                    transparent: true,
                                    opacity: 0.8,
                                    linewidth: 1
                                })
                            );
                            container.add(line);
                        }
                    });

                    // Cache the processed geometries
                    this.saveToCache('shipModel', geometries);

                    this.finalizeModelSetup(container);
                },
                (xhr) => {
                    console.log((xhr.loaded / xhr.total * 100) + '% loaded');
                },
                (error) => {
                    console.error('An error happened:', error);
                }
            );
        } catch (error) {
            console.error('Error loading model:', error);
        }
    }

    async getFromCache(key) {
        return new Promise((resolve, reject) => {
            const transaction = this.db.transaction(['models'], 'readonly');
            const store = transaction.objectStore('models');
            const request = store.get(key);

            request.onsuccess = () => resolve(request.result);
            request.onerror = () => reject(request.error);
        });
    }

    async saveToCache(key, data) {
        const transaction = this.db.transaction(['models'], 'readwrite');
        const store = transaction.objectStore('models');
        store.put(data, key);
    }

    createModelFromGeometry(geometries) {
        const container = new THREE.Group();
        
        geometries.forEach(geometryData => {
            // Create new buffer geometry
            const bufferGeometry = new THREE.BufferGeometry();
            
            // Set position attribute
            bufferGeometry.setAttribute('position', 
                new THREE.Float32BufferAttribute(geometryData.positions, 3)
            );
            
            // Set index if it exists
            if (geometryData.index) {
                bufferGeometry.setIndex(geometryData.index);
            }
            
            // Create edges from the buffer geometry
            const edges = new THREE.EdgesGeometry(bufferGeometry, 30);
            
            const line = new THREE.LineSegments(
                edges,
                new THREE.LineBasicMaterial({ 
                    color: 0x00ffff,
                    transparent: true,
                    opacity: 0.8,
                    linewidth: 1
                })
            );
            container.add(line);
        });

        this.finalizeModelSetup(container);
    }

    finalizeModelSetup(container) {
        // Calculate bounds
        const box = new THREE.Box3().setFromObject(container);
        const center = box.getCenter(new THREE.Vector3());
        
        // Center the container
        container.position.set(-center.x, -center.y, -center.z);
        
        // Create and setup pivot
        const pivot = new THREE.Group();
        pivot.add(container);
        
        // Rotate to stand upright and show front
        pivot.rotation.x = -Math.PI / 2;
        pivot.rotation.z = -Math.PI / 2 + (Math.random() * Math.PI * 2);
        
        // Scale the pivot
        pivot.scale.set(0.8, 0.8, 0.8);
        
        this.model = pivot;
        this.scene.add(pivot);
        
        // Adjust camera position
        this.camera.position.set(0, 2, 4);
        this.camera.lookAt(0, -0.5, 0);
        
        this.animate();
    }

    animate = () => {
        requestAnimationFrame(this.animate);
        
        // Check if enough time has passed for next frame
        const currentTime = Date.now();
        if (currentTime - this.lastFrameTime < this.frameInterval) {
            return;
        }
        this.lastFrameTime = currentTime;
        
        // Update effects
        const time = currentTime * 0.001;
        this.backlightFlicker.update();
        
        // Rotate the model if it exists
        if (this.model) {
            this.model.rotation.z += 0.03;
        }
        
        // Render main scene
        this.renderer.render(this.scene, this.camera);
        
        // Render overlay scene on top
        this.renderer.autoClear = false;
        this.renderer.render(this.overlayScene, this.overlayCamera);
        this.renderer.autoClear = true;
    }
}

// Initialize ship when document is loaded
document.addEventListener('DOMContentLoaded', () => {
    new NostremoShip();
}); 