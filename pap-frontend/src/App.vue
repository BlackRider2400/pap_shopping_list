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
	import { provide } from "vue";
	import { Lists } from "./models/Lists";

	const lists = Lists.fromJSON([]);

	const getAuthHeaders = () => {
		const email = localStorage.getItem("authEmail");
		const password = localStorage.getItem("authPassword");
		let headers = { "Content-Type": "application/json" };
		if (email && password) {
			const credentials = btoa(`${email}:${password}`);
			headers.Authorization = `Basic ${credentials}`;
		}
		return headers;
	};

	const fetchDataFromApi = async () => {
		try {
			const endpoint =
				"https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/getAllLists";
			const response = await axios.get(endpoint, {
				headers: getAuthHeaders(),
				withCredentials: true,
			});
			lists.updateFromJSON(response.data);
		} catch (error) {
			console.error("Błąd podczas pobierania danych z API:", error);
		}
	};

	provide("lists", lists);
	provide("fetchDataFromApi", fetchDataFromApi);
	provide("getAuthHeaders", getAuthHeaders);
</script>

<style lang="scss">
	* {
		box-sizing: border-box;
	}
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
