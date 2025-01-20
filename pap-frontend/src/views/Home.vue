<template>
	<div id="home-page">
		<!-- Sekcja Hero -->
		<section class="hero">
			<div class="hero-overlay"></div>
			<div class="hero-content">
				<h2>Dla wszystkich ludzi, którzy zapominają wszystko kupić</h2>
				<h1>
					W końcu! Przełomowa Metoda Dla Zapominalskich, Żeby Już
					Nigdy Nie Zapomnieć O Niczym Na Zakupach, Gwarantowane!
				</h1>
			</div>
		</section>

		<!-- Sekcja "Co można zrobić" -->
		<section class="features">
			<div class="container">
				<h2>
					Też masz czasem tak, że idziesz na zakupy, ale zapominasz,
					co chciałeś kupić?
				</h2>
				<h2>
					Zwykle kończy się to tym, że przypomnisz sobie o tym dopiero
					po powrocie do domu...
				</h2>
				<Button class="cta-button" @click="navigateToLists"
					>Tak, chcę pozbyć się tego problemu!</Button
				>
			</div>
		</section>
		<section class="features">
			<div class="container">
				<h2>Możesz zrobić kilka rzeczy.</h2>
				<div class="features-container">
					<div
						class="feature-item"
						v-for="amplify in amplifies"
						:key="amplify.id"
					>
						<i :class="amplify.icon" class="feature-icon"></i>
						<h3>{{ amplify.title }}</h3>
						<p v-html="amplify.description"></p>
					</div>
				</div>
				<h2>
					Ale, no szczerze, żadna z tych opcji nie jest zbyt dobra.
				</h2>
				<h2>Właśnie dlatego stworzyliśmy Listę Zakupową 2000!</h2>
				<h2>
					Od teraz, kiedy nie chcesz o czymś zapomnieć, możesz to
					sobie zapisać!
				</h2>
				<p>
					Ta przełomowa metoda opracowana przez naszych najlepszych
					naukowców z działu R&B sprawiła, że wielu ludzi, robiących
					na co dzień zakupy, odwróciło swoje życie o 360 stopni!
				</p>
				<Button class="cta-button" @click="navigateToLists"
					>Rozpocznij teraz</Button
				>
			</div>
		</section>

		<!-- Sekcja "Dlaczego my" -->
		<section class="features why-us">
			<div class="container">
				<h2>Dlaczego powienieneś korzystać z naszej listy?</h2>
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
			</div>
		</section>

		<!-- Kontenery Three.js (wkomponowane w layout) -->
		<section class="three-section">
			<h2 class="three-title">
				Lista tak dobra, że wywali Cię na księżyc<br />(lub do tunelu
				czasoprzestrzennego)
			</h2>
			<div ref="threeContainer" class="three-container"></div>
			<div ref="threeContainerSecond" class="three-container"></div>
		</section>

		<!-- Sekcja CTA na dole strony -->
		<section class="cta">
			<div class="container">
				<h2>Dołącz do tysięcy zadowolonych użytkowników!</h2>
				<Button class="cta-button" @click="navigateToSignup"
					>Zarejestruj się teraz</Button
				>
			</div>
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

	const amplifies = [
		{
			id: 1,
			icon: "fa-solid fa-list-check",
			title: "Ucz się na pamięć",
			description:
				"Przed pójściem na zakupy uczyć się na pamięć tego, czego potrzebujesz.<br />Jednak to rozwiązanie jest dość niewygodne i zajmuje sporo czasu.",
		},
		{
			id: 2,
			icon: "fa-solid fa-share-nodes",
			title: "Zatrudnij kogoś",
			description:
				"Możesz też zapłacić komuś, żeby myślał za Ciebie.<br />Niestety, nie każdy może sobie na to pozwolić.",
		},
	];

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

	// Three.js kontenery
	const threeContainer = ref(null);
	const threeContainerSecond = ref(null);

	onMounted(() => {
		// Pierwszy kontener
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

			const sunLight = new THREE.DirectionalLight(0xffffff);
			sunLight.position.set(-2, 0.5, 1.5);
			scene.add(sunLight);

			function animate() {
				requestAnimationFrame(animate);
				earthMesh.rotation.y += 0.001;
				renderer.render(scene, camera);
				controls.update();
			}

			animate();
		}

		// Drugi kontener
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

			// Tworzenie geometrii Tube
			const tubeGeo = new THREE.TubeGeometry(spline, 222, 0.65, 16, true);

			// Krawędzie rurki
			const edges = new THREE.EdgesGeometry(tubeGeo, 0.2);
			const lineMat = new THREE.LineBasicMaterial({ color: 0xff0000 });
			const tubeLines = new THREE.LineSegments(edges, lineMat);
			scene.add(tubeLines);

			// Dodanie kostek
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

			// Animacja
			function animate(t = 0) {
				requestAnimationFrame(animate);
				updateCamera(t);
				composer.render(scene, camera);
				controls.update();
			}

			animate();
		}
	});
</script>

  <style scoped lang="scss">
	#home-page {
		color: #333;
		overflow-x: hidden;
	}

	.cta-button {
		width: 40%;
	}

	/* Sekcja Hero */
	.hero {
		position: relative;
		background: url("@/assets/shopper.png") center center/cover no-repeat;
		color: #fff;
		min-height: 60vh;
		display: flex;
		align-items: center;
		justify-content: center;

		.hero-overlay {
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background-color: rgba(0, 0, 0, 0.35);
		}

		.hero-content {
			position: relative;
			max-width: 800px;
			margin: 0 auto;
			padding: 60px 20px;
			text-align: center;
			z-index: 1;

			h1 {
				font-size: 3rem;
				margin-bottom: 15px;
				line-height: 1.3;
			}

			h2 {
				font-size: 1.5rem;
				font-weight: normal;
				line-height: 1.4;
			}
		}
	}

	/* Sekcje .features */
	.features {
		padding: 60px 20px;
		background-color: #f9f9f9;
		text-align: center;

		.container {
			width: 80%;
			max-width: 1200px;
			margin: 0 auto;
		}

		h2 {
			font-size: 2rem;
			margin-bottom: 30px;
		}

		.extra-text {
			font-size: 1.1rem;
			margin-top: 20px;
			color: #555;
		}

		.features-container {
			display: flex;
			flex-wrap: wrap;
			justify-content: center;
			gap: 40px;
			margin-top: 40px;

			.feature-item {
				background-color: #fff;
				padding: 30px;
				border-radius: 10px;
				width: 270px;
				box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
				transition: transform 0.3s, box-shadow 0.3s;

				&:hover {
					transform: translateY(-6px);
					box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
				}

				.feature-icon {
					font-size: 3rem;
					color: #007bff;
					margin-bottom: 20px;
				}

				h3 {
					font-size: 1.3rem;
					margin-bottom: 15px;
					color: #333;
				}

				p {
					font-size: 1rem;
					color: #666;
					line-height: 1.4;
				}
			}
		}
	}

	/* Sekcja "Dlaczego my" (możemy dodać inne tło) */
	.why-us {
		background-color: #ffffff;
	}

	/* Sekcja Three.js */
	.three-section {
		background-color: #f9f9f9;
		padding: 60px 20px;
		text-align: center;

		.three-title {
			font-size: 2rem;
			margin-bottom: 30px;
		}

		.three-container {
			width: 100%;
			height: 600px;
			margin: 40px 0;
			border: 1px solid #ccc;
			border-radius: 8px;
			overflow: hidden;
		}
	}

	/* Sekcja CTA */
	.cta {
		padding: 60px 20px;
		background-color: #007bff;
		color: #fff;
		text-align: center;

		.container {
			max-width: 1200px;
			margin: 0 auto;
		}

		h2 {
			font-size: 2rem;
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

	/* Responsywność */
	@media (max-width: 768px) {
		.cta-button {
			width: 100%;
		}
		.hero-content {
			padding: 40px 20px;

			h1 {
				font-size: 2rem;
			}

			h2 {
				font-size: 1.2rem;
			}
		}

		.features-container {
			flex-direction: column;
			align-items: center;
		}

		.feature-item {
			width: 90%;
		}

		.three-container {
			height: 400px;
		}

		.cta h2 {
			font-size: 1.7rem;
		}
	}
</style>
