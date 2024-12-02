<template>
	<div id="lists-container">
		<div id="lists-menu">
			<div
				v-for="list in lists.lists"
				:key="list.id"
				@click="setCurrentList(list)"
				class="list-item"
			>
				{{ list.name }}
			</div>
		</div>
		<div id="lists-window">
			<router-view />
		</div>
	</div>
</template>

<script setup>
	import { reactive, provide } from "vue";
	import { useRouter } from "vue-router";
	import testList from "../assets/testList.json";
	import { Lists } from "../models/Lists";

	const lists = Lists.fromJSON(testList);

	provide("lists", lists);

	const router = useRouter();

	const setCurrentList = (list) => {
		router.push({ name: "ListDetail", params: { id: list.id } });
	};
</script>

<style scoped lang="scss">
	#lists-container {
		display: flex;
		flex-wrap: nowrap;
		background-color: #f0f0f0;
	}

	#lists-menu {
		flex: 1;
		background-color: #ffffff;
		border-right: 1px solid #ccc;
	}

	.list-item {
		padding: 10px;
		cursor: pointer;
		border-bottom: 1px solid #eee;
	}

	.list-item:hover {
		background-color: #f9f9f9;
	}

	#lists-window {
		flex: 4;
		padding: 20px;
		background-color: #fafafa;
	}
</style>
