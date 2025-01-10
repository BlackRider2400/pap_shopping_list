<template>
	<div id="home-page">
		<section class="hero">
			<div class="hero-content">
				<h1>Lista zakupowa zawsze pod ręką</h1>
				<p>
					Zarządzaj swoimi zakupami w prosty i intuicyjny sposób.
					Twórz listy, udostępniaj je innym i synchronizuj w czasie
					rzeczywistym.
				</p>
				<Button class="cta-button" @click="navigateToLists"
					>Rozpocznij teraz</Button
				>
			</div>
		</section>

		<section class="features">
			<h2>Dlaczego warto wybrać nasz produkt?</h2>
			<div class="features-container">
				<div
					class="feature-item"
					v-for="feature in features"
					:key="feature.id"
				>
					<i :class="feature.icon" class="feature-icon"></i>
					<h3>{{ feature.title }}</h3>
					<p>{{ feature.description }}</p>
				</div>
			</div>
		</section>

		<div ref="threeContainer" class="three-container"></div>
		<div ref="threeContainerSecond" class="three-container"></div>

		<section class="cta">
			<h2>Dołącz do tysięcy zadowolonych użytkowników!</h2>
			<Button class="cta-button" @click="navigateToSignup"
				>Zarejestruj się teraz</Button
			>
		</section>
	</div>
</template>

  <script setup>
	import { ref, onMounted } from "vue";
	import { useRouter } from "vue-router";
	import Button from "@/components/Button.vue";
	import * as THREE from "three";
	import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";

	import { EffectComposer } from "three/examples/jsm/postprocessing/EffectComposer.js";
	import { RenderPass } from "three/examples/jsm/postprocessing/RenderPass.js";
	import { UnrealBloomPass } from "three/examples/jsm/postprocessing/UnrealBloomPass.js";

	import earthTexture from "@/assets/moonmap4k.jpg";
	import getStarfield from "./getStarfield.js";
	import spline from "./spline.js";
	import { update } from "three/examples/jsm/libs/tween.module.js";

	const features = [
		{
			id: 1,
			icon: "fa-solid fa-list-check",
			title: "Intuicyjny interfejs",
			description:
				"Łatwe tworzenie i zarządzanie listami zakupowymi dzięki przejrzystemu i intuicyjnemu interfejsowi.",
		},
		{
			id: 2,
			icon: "fa-solid fa-share-nodes",
			title: "Udostępnianie",
			description:
				"Dziel się swoimi listami zakupów z rodziną i znajomymi w czasie rzeczywistym.",
		},
		{
			id: 3,
			icon: "fa-solid fa-sync-alt",
			title: "Synchronizacja",
			description:
				"Twoje listy są synchronizowane na wszystkich urządzeniach, dzięki czemu masz do nich dostęp zawsze i wszędzie.",
		},
		{
			id: 4,
			icon: "fa-solid fa-lock",
			title: "Bezpieczeństwo",
			description:
				"Zapewniamy najwyższy poziom bezpieczeństwa danych, aby Twoje informacje były zawsze chronione.",
		},
	];

	const router = useRouter();

	const navigateToLists = () => {
		router.push("/lists");
	};

	const navigateToSignup = () => {
		router.push("/register");
	};

	// Three.js code //

	const threeContainer = ref(null);
	const threeContainerSecond = ref(null);

	onMounted(() => {
		if (threeContainer.value) {
			const w = threeContainer.value.clientWidth;
			const h = threeContainer.value.clientHeight;
			const renderer = new THREE.WebGLRenderer({ antialias: true });
			renderer.setSize(w, h);
			threeContainer.value.appendChild(renderer.domElement);

			const fov = 75;
			const aspect = w / h;
			const near = 0.1;
			const far = 1000;
			const camera = new THREE.PerspectiveCamera(fov, aspect, near, far);
			camera.position.z = 2.5;

			const scene = new THREE.Scene();

			const earthGroup = new THREE.Group();
			earthGroup.rotation.z = -23.5 * (Math.PI / 180);
			scene.add(earthGroup);

			const controls = new OrbitControls(camera, renderer.domElement);
			controls.enableDamping = true;
			controls.dampingFactor = 0.02;

			const loader = new THREE.TextureLoader();
			const geometry = new THREE.IcosahedronGeometry(1, 16);
			const material = new THREE.MeshStandardMaterial({
				map: loader.load(earthTexture),
			});
			const earthMesh = new THREE.Mesh(geometry, material);
			earthGroup.add(earthMesh);

			const stars = getStarfield();
			scene.add(stars);

			// const hemilight = new THREE.HemisphereLight(0xffffff, 0xffffff, 1);
			// scene.add(hemilight);

			const sunLight = new THREE.DirectionalLight(0xffffff);
			sunLight.position.set(-2, 0.5, 1.5);
			scene.add(sunLight);

			function animate(t = 0) {
				requestAnimationFrame(animate);
				earthMesh.rotation.y += 0.001;
				renderer.render(scene, camera);
				controls.update();
			}

			animate();
		}

		if (threeContainerSecond.value) {
			const w = threeContainerSecond.value.clientWidth;
			const h = threeContainerSecond.value.clientHeight;

			// Renderer
			const renderer = new THREE.WebGLRenderer({ antialias: true });
			renderer.setSize(w, h);
			renderer.toneMapping = THREE.ACESFilmicToneMapping;
			renderer.outputColorSpace = THREE.SRGBColorSpace;
			threeContainerSecond.value.appendChild(renderer.domElement);

			// Camera
			const fov = 75;
			const aspect = w / h;
			const near = 0.1;
			const far = 1000;
			const camera = new THREE.PerspectiveCamera(fov, aspect, near, far);
			camera.position.z = 30;

			// Scene
			const scene = new THREE.Scene();
			scene.fog = new THREE.FogExp2(0x000000, 0.3);

			// Controls
			const controls = new OrbitControls(camera, renderer.domElement);
			controls.enableDamping = true;
			controls.dampingFactor = 0.03;

			// Post processing
			const renderScene = new RenderPass(scene, camera);
			const bloomPass = new UnrealBloomPass(
				new THREE.Vector2(w, h),
				1.5,
				0.4,
				100
			);
			bloomPass.threshold = 0.002;
			bloomPass.strength = 3.5;
			bloomPass.radius = 0;
			const composer = new EffectComposer(renderer);
			composer.addPass(renderScene);
			composer.addPass(bloomPass);

			// scene.add(line);

			// create a tube geometry from the spline
			const tubeGeo = new THREE.TubeGeometry(spline, 222, 0.65, 16, true);

			// create edges geometry from the spline
			const edges = new THREE.EdgesGeometry(tubeGeo, 0.2);
			const lineMat = new THREE.LineBasicMaterial({ color: 0xff0000 });
			const tubeLines = new THREE.LineSegments(edges, lineMat);
			scene.add(tubeLines);

			// Add boxes
			const numBoxes = 55;
			const size = 0.075;
			const boxGeo = new THREE.BoxGeometry(size, size, size);
			for (let i = 0; i < numBoxes; i += 1) {
				const boxMat = new THREE.MeshBasicMaterial({
					color: 0xffffff,
					wireframe: true,
				});
				const box = new THREE.Mesh(boxGeo, boxMat);
				const p = (i / numBoxes + Math.random() * 0.1) % 1;
				const pos = tubeGeo.parameters.path.getPointAt(p);
				pos.x += Math.random() - 0.4;
				pos.z += Math.random() - 0.4;
				box.position.copy(pos);
				const rote = new THREE.Vector3(
					Math.random() * Math.PI,
					Math.random() * Math.PI,
					Math.random() * Math.PI
				);
				box.rotation.set(rote.x, rote.y, rote.z);
				const edges = new THREE.EdgesGeometry(boxGeo, 0.2);
				const color = new THREE.Color().setHSL(0.7 - p, 1, 0.5);
				const lineMat = new THREE.LineBasicMaterial({ color });
				const boxLines = new THREE.LineSegments(edges, lineMat);
				boxLines.position.copy(pos);
				boxLines.rotation.set(rote.x, rote.y, rote.z);
				// scene.add(box);
				scene.add(boxLines);
			}

			function updateCamera(tm) {
				const time = tm * 0.2;
				const looptime = 8 * 1000;
				const t = (time % looptime) / looptime;
				const pos = tubeGeo.parameters.path.getPointAt(t);
				const lookAt = tubeGeo.parameters.path.getPointAt((t + 0.01) % 1);
				camera.position.copy(pos);
				camera.lookAt(lookAt);
			}

			// Animation Loop
			function animate(t = 0) {
				requestAnimationFrame(animate);
				updateCamera(t);

				// Render Scene
				composer.render(scene, camera);

				// Update Controls
				controls.update();
			}

			animate();
		}
	});
</script>

<style scoped lang="scss">
	#home-page {
		font-family: "Arial, sans-serif";
		color: #333;
	}

	.three-container {
		width: 100%;
		height: 1600px;
		margin-top: 20px;
		margin-bottom: 20px;
	}

	.hero {
		background: url("@/assets/shopper.png") center center/cover no-repeat;
		color: #fff;
		padding: 100px 20px;
		text-align: center;

		.hero-content {
			max-width: 800px;
			margin: 0 auto;

			h1 {
				font-size: 3rem;
				margin-bottom: 20px;
				text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
			}

			p {
				font-size: 1.5rem;
				margin-bottom: 30px;
			}

			.cta-button {
				padding: 15px 30px;
				font-size: 1.2rem;
			}
		}
	}

	.features {
		padding: 60px 20px;
		background-color: #f9f9f9;
		text-align: center;

		h2 {
			font-size: 2.5rem;
			margin-bottom: 40px;
		}

		.features-container {
			display: flex;
			flex-wrap: wrap;
			justify-content: center;
			gap: 40px;

			.feature-item {
				background-color: #fff;
				padding: 30px;
				border-radius: 10px;
				width: 250px;
				box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
				transition: transform 0.3s;

				&:hover {
					transform: translateY(-10px);
				}

				.feature-icon {
					font-size: 3rem;
					color: #007bff;
					margin-bottom: 20px;
				}

				h3 {
					font-size: 1.5rem;
					margin-bottom: 15px;
				}

				p {
					font-size: 1rem;
					color: #666;
				}
			}
		}
	}

	.cta {
		padding: 60px 20px;
		text-align: center;
		background-color: #007bff;
		color: #fff;

		h2 {
			font-size: 2.5rem;
			margin-bottom: 30px;
		}

		.cta-button {
			padding: 15px 30px;
			font-size: 1.2rem;
			background-color: #fff;
			color: #007bff;
			border: none;
			border-radius: 5px;
			cursor: pointer;
			transition: background-color 0.3s, color 0.3s;

			&:hover {
				background-color: #e6e6e6;
			}
		}
	}

	@media (max-width: 768px) {
		.features-container {
			flex-direction: column;
			align-items: center;
		}

		.feature-item {
			width: 80%;
		}

		.hero-content h1 {
			font-size: 2.5rem;
		}

		.hero-content p {
			font-size: 1.2rem;
		}

		.cta h2 {
			font-size: 2rem;
		}
	}
</style>
