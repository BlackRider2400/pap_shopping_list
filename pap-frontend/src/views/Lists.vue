<template>
	<div id="lists-container">
		<div id="lists-menu">
			<div v-for="list in lists.lists" :key="list.id">
				{{ list.name }}
			</div>
		</div>
		<div id="lists-window">Reszta</div>
	</div>
	<div></div>
</template>

<script setup>
	import { reactive } from "vue";

	class ReactiveBase {
		constructor(initialState) {
			return reactive(initialState);
		}
	}

	class List extends ReactiveBase {
		constructor(id, name, items, owner, users) {
			super({ name, items, owner, users });
			this.id = id;
		}
	}

	class Lists extends ReactiveBase {
		constructor(lists) {
			super({ lists });
		}
	}

	const list1 = new List(1, "Lista 1", ["item1", "item2"], "owner1", [
		"user1",
		"user2",
	]);

	const list2 = new List(2, "Lista 2", ["item1", "item2"], "owner1", [
		"user1",
		"user2",
	]);

	const list3 = new List(3, "Lista 3", ["item1", "item2"], "owner1", [
		"user1",
		"user2",
	]);

	const lists = new Lists([]);
	lists.lists.push(list1);
	lists.lists.push(list2);
	lists.lists.push(list3);
</script>

<style scoped lang="scss">
	#lists-container {
		display: flex;
		flex-wrap: nowrap;
		background-color: #f0f0f0;
	}

	#lists-menu {
		height: 100%;
		background-color: #f0f0f0;
	}

	#lists-window {
		height: 100%;
		background-color: #f0f0f0;
	}
</style>
