<template>
	<div v-if="list">
		<Input
			v-model="list.name"
			:id="list.id.toString()"
			:label="`List Name: ${list.name}`"
			:placeholder="`Enter new name for list ${list.name}`"
			:mode="'h2'"
			@blur="renameList"
		></Input>
		<ul id="list-of-items">
			<li v-for="item in list.items" :key="item.tempId || item.id">
				<label :for="`item${item.id}`">
					<input
						type="checkbox"
						:id="`item${item.id}`"
						v-model="item.status"
						@change="changeItemStatus(item)"
					/>
					<Input
						v-model="item.text"
						:id="item.id ? item.id.toString() : ''"
						:placeholder="'Enter new item into your shopping list'"
						:mode="'p'"
						:disabled="item.status"
						:ref="
							item.id
								? setInputRef(item.id)
								: setInputRef(item.tempId)
						"
						@blur="updateItem(item)"
					></Input>
				</label>
				<span @click="removeItem(item.id)">
					<i
						class="fa-solid fa-trash"
						style="
							cursor: pointer;
							margin-left: 10px;
							color: #d9534f;
						"
					></i>
				</span>
			</li>
		</ul>
		<Button @click="addItem">Add Item</Button>
		<p>Owner: {{ list.owner }}</p>
		<p>Users: {{ list.users.map((user) => user).join(", ") }}</p>
	</div>
	<div v-else>
		<p>List not found.</p>
	</div>
</template>


<script setup>
	import { defineProps, inject, computed, nextTick, reactive } from "vue";
	import "@fortawesome/fontawesome-free/css/all.css";
	import Button from "@/components/Button.vue";
	import Input from "@/components/Input.vue";
	import { Item } from "@/models/Item";
	import axios from "axios";

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

	const inputRefs = reactive({});

	const setInputRef = (id) => (el) => {
		inputRefs[id] = el;
	};

	const addItem = async () => {
		if (list.value) {
			const tempId = Date.now();
			const newItem = new Item(null, "", false);
			newItem.tempId = tempId;
			list.value.items.push(newItem);
			await nextTick();
			const newActualId = await saveNewItem(newItem);
			console.log(newActualId);
			if (newActualId) {
				newItem.id = newActualId;

				if (inputRefs[tempId]) {
					inputRefs[newActualId] = inputRefs[tempId];
					delete inputRefs[tempId];
				}

				if (inputRefs[newActualId]) {
					inputRefs[newActualId].focus();
				}
			}
		}
	};

	const removeItem = async (itemId) => {
		const index = list.value.items.findIndex((i) => i.id === itemId);
		if (index !== -1) {
			try {
				await axios.delete(
					`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/deleteItemById/${itemId}?userId=1`
				);
				list.value.items.splice(index, 1);
				delete inputRefs[itemId];
			} catch (error) {
				console.error("Error deleting item:", error);
			}
		}
	};

	const renameList = async () => {
		if (list.value) {
			try {
				await axios.put(
					`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/renameList/${list.value.id}?userId=1`,
					list.value.name,
					{
						headers: {
							"Content-Type": "text/plain",
						},
					}
				);
			} catch (error) {
				console.error("Error renaming list:", error);
			}
		}
	};

	const saveNewItem = async (item) => {
		try {
			const response = await axios.post(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/addNewItem/${list.value.id}?userId=1`,
				{
					data: item.text,
					status: item.status,
				}
			);
			const newId = response.data.items[response.data.items.length - 1].id;
			return newId;
		} catch (error) {
			console.error("Error adding new item:", error);
			return null;
		}
	};

	const updateItem = async (item) => {
		const exists = list.value.items.some((i) => i.id === item.id);
		if (!exists) {
			console.warn(
				`Item with id ${item.id} does not exist. Skipping update.`
			);
			return;
		}

		if (!item.id) {
			console.warn("Item does not have a valid id. Skipping update.");
			return;
		}

		if (!item.text.trim()) {
			console.warn("Item text is empty. Removing the item.");
			await removeItem(item.id);
			return;
		}

		try {
			await axios.put(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/changeValueOfItem/${item.id}?userId=1`,
				item.text,
				{
					headers: {
						"Content-Type": "text/plain",
					},
				}
			);
		} catch (error) {
			console.error("Error updating item:", error);
		}
	};

	const changeItemStatus = async (item) => {
		try {
			await axios.put(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/changeStateOfItem/${item.id}?userId=1`
			);
		} catch (error) {
			console.error("Error changing item status:", error);
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
