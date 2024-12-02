<template>
	<div id="app">
		<div id="main">
			<Nav id="nav" />
			<router-view />
			<Footer id="footer" />
		</div>
	</div>
</template>

<script setup>
	import Nav from "./components/Nav.vue";
	import Footer from "./components/Footer.vue";
	import axios from "axios";
	import { provide, reactive } from "vue";

	const state = reactive({
		endpoint: "https://mylovelyserver.fun:8443/pap_shopping_list/api/",
	});
	provide("state", state);

	const fetchDataFromApi = async () => {
		let ep2 = state.endpoint + "lists/getAllLists?userId=1";
		const response = await axios.get(ep2);
		console.log(JSON.stringify(response.data, null, 2));
	};

	fetchDataFromApi();
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
