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

	import earthTexture from "@/assets/moonmap4k.jpg";
	import getStarfield from "./getStarfield.js";

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

			const hemilight = new THREE.HemisphereLight(0xffffff, 0xffffff, 1);
			scene.add(hemilight);

			function animate(t = 0) {
				requestAnimationFrame(animate);
				earthMesh.rotation.y += 0.001;
				renderer.render(scene, camera);
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
