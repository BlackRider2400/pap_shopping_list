<template>
	<div v-if="list">
		<Input
			v-model="list.name"
			:id="list.id.toString()"
			:label="`List Name: ${list.name}`"
			:placeholder="`Enter new name for list ${list.name}`"
			:mode="'h2'"
		></Input>
		<ul id="list-of-items">
			<li v-for="item in list.items" :key="item.id">
				<label :for="`item${item.id}`">
					<input
						type="checkbox"
						:id="`item${item.id}`"
						v-model="item.status" />
					<Input
						v-model="item.text"
						:id="item.id.toString()"
						:placeholder="`Enter new item into your shoping list`"
						:mode="'p'"
						:disabled="item.status"
						:crossed="item.status"
					></Input
				></label>
				<span @click="removeItem(item.id)">
					<i
						class="fa-solid fa-trash"
						style="
							cursor: pointer;
							margin-left: 10px;
							color: #d9534f;
						"
					>
					</i
				></span>
			</li>
		</ul>
		<Button @click="addItem">Add Item</Button>
		<p>Owner: {{ list.owner }}</p>
		<p>Users: {{ list.users.join(", ") }}</p>
	</div>
	<div v-else>
		<p>List not found.</p>
	</div>
</template>

<script setup>
	import { defineProps, inject, computed, ref, nextTick } from "vue";
	import "@fortawesome/fontawesome-free/css/all.css";

	import { Item } from "@/models/Item";
	import Button from "@/components/Button.vue";
	import Input from "@/components/Input.vue";

	const props = defineProps({
		id: {
			type: String,
			required: true,
		},
	});

	const lists = inject("lists");

	const list = computed(() => {
		const listId = Number(props.id);
		return lists.lists.find((l) => l.id === listId) || null;
	});
	const changeListNameState = ref(false);

	const addItem = async () => {
		if (list.value) {
			const newId = list.value.items.length + 1;
			const newItem = new Item(newId, "", true);
			list.value.items.push(newItem);
			await nextTick(() => {
				list.value.items[newId - 1].status = false;
			});
		}
	};

	const removeItem = (itemId) => {
		const index = list.value.items.findIndex((i) => i.id === itemId);
		if (index !== -1) {
			list.value.items.splice(index, 1);
			list.value.items.forEach((item, idx) => {
				item.id = idx + 1;
			});
		}
	};
</script>


<style scoped lang="scss">
	.change-name {
		width: 20%;
	}

	h2 {
		font-size: 24px;
		font-weight: bold;
		border: none;
		border-bottom: 2px solid #007bff;
		padding: 5px;
		margin-bottom: 10px;
		outline: none;
		transition: border-color 0.3s;
	}

	#list-of-items {
		list-style-type: none;
		padding: 0;

		li {
			display: flex;
			align-items: center;
			margin-bottom: 10px;

			label {
				display: flex;
				align-items: center;
			}

			input[type="checkbox"] {
				margin-right: 10px;
				scale: 1.5;
			}
		}
	}
</style>
