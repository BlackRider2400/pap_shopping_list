<template>
	<nav>
		<div id="logo">
			<router-link to="/">Shoppin' List 2000</router-link>
		</div>
		<div id="burger" @click="toggleMenu">☰</div>
		<div id="link-tree-overlay" :class="{ active: isMenuOpen }">
			<div id="link-tree">
				<router-link to="/" @click="closeMenu"
					>Strona główna</router-link
				>
				<router-link to="/login" @click="closeMenu">Login</router-link>
				<router-link to="/register" @click="closeMenu"
					>Register</router-link
				>
				<router-link to="/lists" @click="closeMenu"
					>Listy Zakupowe</router-link
				>
			</div>
		</div>
	</nav>
</template>

<script setup>
	import { ref } from "vue";

	const isMenuOpen = ref(false);

	const toggleMenu = () => {
		isMenuOpen.value = !isMenuOpen.value;
		document.body.style.overflow = isMenuOpen.value ? "hidden" : "auto";
	};

	const closeMenu = () => {
		isMenuOpen.value = false;
		document.body.style.overflow = "auto";
	};
</script>

<style scoped lang="scss">
	nav {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 10px 20px;
		background-color: #333;
		color: white;
		position: relative;
		z-index: 10;
		user-select: none;

		#logo {
			font-size: 1.5rem;
			font-weight: bold;

			a {
				color: white;
				text-decoration: none;
			}
		}

		#burger {
			font-size: 1.5rem;
			cursor: pointer;
			display: block;
			z-index: 20;
			color: white;
		}
	}

	#link-tree-overlay {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.9);
		display: none;
		justify-content: center;
		align-items: center;
		z-index: 15;

		&.active {
			display: flex;
		}
	}

	#link-tree {
		display: flex;
		flex-direction: column;
		gap: 20px;
		text-align: center;

		a {
			color: white;
			text-decoration: none;
			font-size: 1.5rem;
			transition: color 0.3s;

			&:hover {
				color: #007bff;
			}
		}
	}
</style>
