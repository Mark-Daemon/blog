const importMap = {
    imports: {
        "three": "https://unpkg.com/three@0.160.0/build/three.module.js",
        "three/addons/": "https://unpkg.com/three@0.160.0/examples/jsm/"
    }
};

const importMapElement = document.createElement('script');
importMapElement.type = 'importmap';
importMapElement.textContent = JSON.stringify(importMap);
document.head.appendChild(importMapElement); 