<template>
	<div id="app">
		<div id="main">
			<Nav id="nav" />
			<router-view id="view" />
			<Footer -d="footer" />
		</div>
	</div>
</template>

<script setup>
	import Nav from "./components/Nav.vue";
	import Footer from "./components/Footer.vue";
</script>

<style scoped lang="scss">
	#app {
		width: 100vw;
		min-height: 100vh;
	}
	#main {
		display: flex;
		flex-direction: column;
		width: 100%;
		max-width: 1200px;
		margin: auto;
		height: 100%;
		min-height: 100vh;
	}
	#view {
		flex: 1;
	}
	#nav {
		flex: 0 0 auto;
	}
	#footer {
		flex: 0 0 auto;
	}
</style>
