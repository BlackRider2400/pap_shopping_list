<template>
	<div id="content" v-if="list">
		<section class="list-header">
			<Input
				v-model="list.name"
				:id="list.id.toString()"
				:label="`List Name: ${list.name}`"
				:placeholder="`Enter new name for list ${list.name}`"
				mode="h2"
				@blur="renameList"
			/>
		</section>

		<section class="items-section">
			<ul class="list-of-items">
				<li
					v-for="item in list.items"
					:key="item.tempId || item.id"
					class="item"
				>
					<div class="item-left">
						<input
							type="checkbox"
							:id="`item${item.id}`"
							v-model="item.status"
							@change="changeItemStatus(item)"
							class="item-checkbox"
						/>

						<Input
							class="input-text"
							v-model="item.text"
							:id="item.id ? item.id.toString() : ''"
							placeholder="Enter item name"
							mode="p"
							:disabled="item.status"
							:ref="setInputRef(item.id || item.tempId, 'text')"
							@blur="updateItem(item)"
						/>

						<Input
							class="input-unit"
							v-model="item.unit"
							:id="item.id ? item.id.toString() : ''"
							placeholder="Unit"
							mode="p"
							:disabled="item.status"
							:ref="setInputRef(item.id || item.tempId, 'unit')"
							@blur="updateItem(item)"
						/>

						<Input
							class="input-amount"
							type="number"
							v-model="item.amount"
							:id="item.id ? item.id.toString() : ''"
							placeholder="Amount"
							mode="p"
							:disabled="item.status"
							:ref="setInputRef(item.id || item.tempId, 'amount')"
							@blur="updateItem(item)"
						/>
					</div>
					<div class="item-actions">
						<button
							@click="removeItem(item.id)"
							class="remove-button"
							aria-label="Remove Item"
						>
							<i class="fa-solid fa-trash"></i>
						</button>
					</div>
				</li>
			</ul>
			<Button class="add-item-button" @click="addItem">Add Item</Button>
		</section>

		<section class="list-info">
			<p>Owner: {{ list.owner }}</p>
			<p>Users: {{ list.users.map((user) => user).join(", ") }}</p>
		</section>
	</div>

	<div v-else class="no-list">
		<p>List not found.</p>
	</div>
</template>

<script setup>
	import { defineProps, inject, computed, nextTick, reactive } from "vue";
	import "@fortawesome/fontawesome-free/css/all.css";
	import Button from "@/components/Button.vue";
	import Input from "@/components/Input.vue";
	import { Item } from "@/models/Item";
	import { useToast } from "vue-toastification";
	import axios from "axios";

	const props = defineProps({
		id: {
			type: String,
			required: true,
		},
	});

	const lists = inject("lists");
	const toast = useToast();

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

	const list = computed(() => {
		const listId = Number(props.id);
		return lists.lists.find((l) => l.id === listId) || null;
	});

	const inputRefs = reactive({});

	const setInputRef = (id, type) => (el) => {
		inputRefs[`${type}-${id}`] = el;
	};

	const addItem = async () => {
		if (list.value) {
			const tempId = Date.now();
			const newItem = new Item(null, "", false, "sztuki", 1);
			newItem.tempId = tempId;
			list.value.items.push(newItem);
			await nextTick();
			const newActualId = await saveNewItem(newItem);
			console.log(newActualId);
			if (newActualId) {
				newItem.id = newActualId;

				if (inputRefs[`text-${tempId}`]) {
					inputRefs[`text-${newActualId}`] = inputRefs[`text-${tempId}`];
					delete inputRefs[`text-${tempId}`];
				}

				if (inputRefs[`text-${newActualId}`]) {
					inputRefs[`text-${newActualId}`].focus();
				}
			}
		}
	};

	const removeItem = async (itemId) => {
		const index = list.value.items.findIndex((i) => i.id === itemId);
		if (index !== -1) {
			try {
				await axios.delete(
					`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/deleteItemById/${itemId}`,
					{
						headers: getAuthHeaders(),
						withCredentials: true,
					}
				);
				list.value.items.splice(index, 1);
				delete inputRefs[`text-${itemId}`];
				delete inputRefs[`unit-${itemId}`];
				delete inputRefs[`amount-${itemId}`];
			} catch (error) {
				console.error("Error deleting item:", error);
			}
		}
	};

	const renameList = async () => {
		if (list.value) {
			try {
				await axios.put(
					`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/renameList/${list.value.id}`,
					list.value.name,
					{
						headers: {
							"Content-Type": "text/plain",
							...getAuthHeaders(),
						},
						withCredentials: true,
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
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/addNewItem/${list.value.id}`,
				{
					data: item.text,
					status: item.status,
				},
				{
					headers: getAuthHeaders(),
					withCredentials: true,
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
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/changeValueOfItem/${item.id}`,
				item.text,
				{
					headers: {
						"Content-Type": "text/plain",
						...getAuthHeaders(),
					},
					withCredentials: true,
				}
			);
		} catch (error) {
			console.error("Error updating item:", error);
		}
	};

	const changeItemStatus = async (item) => {
		try {
			await axios.put(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/changeStateOfItem/${item.id}`,
				null,
				{
					headers: getAuthHeaders(),
					withCredentials: true,
				}
			);

			if (!item.status) {
				inputRefs[`text-${item.id}`]?.focus();
			}
		} catch (error) {
			console.error("Error changing item status:", error);
		}
	};
</script>

<style scoped lang="scss">
	:root {
		--primary-color: #007bff;
		--primary-color-hover: #0056b3;
		--danger-color: #d9534f;
		--danger-color-hover: #c9302c;
		--background-color: #f8f9fa;
		--menu-background: #ffffff;
		--text-color: #333333;
		--text-color-light: #777777;
		--border-color: #dddddd;
		--input-background: #ffffff;
		--input-border: #cccccc;
		--input-disabled: #e9ecef;
		--button-background: #28a745;
		--button-hover: #218838;
	}

	#content {
		padding: 20px;
		background-color: var(--background-color);
		min-height: 100vh;
		width: 100%;
	}

	.list-header {
		margin-bottom: 30px;

		h2 {
			font-size: 2rem;
			color: var(--text-color);
			margin-bottom: 10px;
			border-bottom: 2px solid var(--primary-color);
			padding-bottom: 5px;
		}
	}

	.items-section {
		background-color: var(--menu-background);
		padding: 20px;
		border-radius: 8px;
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
		width: 100%;
	}

	.list-of-items {
		list-style-type: none;
		padding: 0;
		margin: 0;

		.item {
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding: 10px 0;
			border-bottom: 1px solid var(--border-color);
			width: 100%;
			flex-wrap: wrap;
			transition: background-color 0.3s, transform 0.3s;

			&:hover {
				background-color: var(--hover-background);
			}

			.remove-button {
				transition: color 0.3s, transform 0.3s;

				&:hover {
					color: var(--danger-color-hover);
					transform: scale(1.1);
				}
			}

			&:last-child {
				border-bottom: none;
			}

			.item-left {
				display: flex;
				align-items: center;
				gap: 10px;
				flex: 1 1 0;
				min-width: 0;
			}

			.input-text,
			.input-unit,
			.input-amount {
				flex-shrink: 1;
				min-width: 0;
			}

			.item-actions {
				margin-left: 10px;

				.remove-button {
					background: none;
					border: none;
					color: var(--danger-color);
					cursor: pointer;
					font-size: 1.2rem;
					transition: color 0.3s;

					&:hover {
						color: var(--danger-color-hover);
					}

					&:focus {
						outline: none;
					}
				}
			}
		}
	}

	.add-item-button {
		display: block;
		width: 100%;
		margin-top: 20px;
		padding: 10px 0;
		background-color: var(--button-background);
		color: #ffffff;
		border: none;
		border-radius: 4px;
		font-size: 1rem;
		cursor: pointer;
		transition: background-color 0.3s;

		display: flex;
		align-items: center;
		justify-content: center;

		&:hover {
			background-color: var(--button-hover);
		}

		&:focus {
			outline: none;
			box-shadow: 0 0 5px rgba(40, 167, 69, 0.5);
		}

		i {
			margin-right: 5px;
		}
	}

	.list-info {
		margin-top: 30px;
		font-size: 1rem;
		color: var(--text-color-light);

		p {
			margin: 5px 0;
		}
	}

	.no-list {
		padding: 20px;
		font-size: 1.2rem;
		color: var(--text-color-light);
		text-align: center;
	}

	@media (max-width: 768px) {
		#content {
			padding: 0px;
		}

		.list-header h2 {
			font-size: 1.5rem;
		}

		.items-section {
			padding: 5px;
		}

		.add-item-button {
			font-size: 0.9rem;
		}

		.list-info {
			font-size: 0.9rem;
		}
	}
</style>
