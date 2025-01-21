<template>
	<div id="content" v-if="list">
		<section class="list-header">
			<Input
				v-model="list.name"
				:id="list.id.toString()"
				:label="`List Name: ${list.name}`"
				:placeholder="`Enter new name for list ${list.name}`"
				mode="h2"
				@focus="syncSlow()"
				@blur="
					() => {
						renameList();
						syncFast();
					}
				"
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
							v-model="item.data"
							:id="item.id ? item.id.toString() : ''"
							placeholder="Enter item name"
							mode="p"
							:disabled="item.status"
							:ref="setInputRef(item.id || item.tempId, 'text')"
							@focus="syncSlow()"
							@blur="
								() => {
									updateItem(item);
									syncFast();
								}
							"
						/>

						<Input
							class="input-unit"
							v-model="item.unit"
							:id="item.id ? item.id.toString() : ''"
							placeholder="Unit"
							mode="p"
							:disabled="item.status"
							:ref="setInputRef(item.id || item.tempId, 'unit')"
							@focus="syncSlow()"
							@blur="
								() => {
									renameList();
									updateItem(item);
								}
							"
						/>

						<Input
							class="input-amount"
							type="number"
							v-model="item.quantity"
							:id="item.id ? item.id.toString() : ''"
							placeholder="Amount"
							mode="p"
							:disabled="item.status"
							:ref="setInputRef(item.id || item.tempId, 'amount')"
							@focus="syncSlow()"
							@blur="
								() => {
									renameList();
									updateItem(item);
								}
							"
							@change="updateItem(item)"
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
			<Button class="add-item-button" @click="addItem"
				>Dodaj Przedmiot</Button
			>
		</section>

		<section class="list-info">
			<!-- <p>Owner: {{ list.owner }}</p> -->
			<p>
				Użytkownicy listy:
				{{ list.users.map((user) => user).join(", ") }}
			</p>

			<div class="share-section">
				<h3>Udostępnij listę</h3>

				<select
					v-model="selectedUserToShare"
					class="share-select"
					@focus="syncSlow"
					@blur="syncFast"
				>
					<option disabled value="">-- Wybierz użytkownika --</option>
					<option
						v-for="email in filteredAllUsers"
						:key="email"
						:value="email"
					>
						{{ email }}
					</option>
				</select>
				<Button type="primary" class="share-button" @click="shareWith">
					Udostępnij
				</Button>
			</div>

			<div class="unshare-section" v-if="list.users.length > 0">
				<h3>Usuń współdzielenie</h3>

				<select
					v-model="selectedUserToUnshare"
					class="share-select"
					@focus="syncSlow"
					@blur="syncFast"
				>
					<option disabled value="">-- Wybierz użytkownika --</option>
					<option
						v-for="email in list.users"
						:key="email"
						:value="email"
					>
						{{ email }}
					</option>
				</select>
				<Button
					type="secondary"
					class="share-button"
					@click="unshareWith"
				>
					Usuń współdzielenie
				</Button>
			</div>
		</section>
	</div>

	<div v-else class="no-list">
		<p>List not found.</p>
	</div>
</template>

  <script setup>
	import {
		defineProps,
		inject,
		computed,
		nextTick,
		reactive,
		onMounted,
		onBeforeUnmount,
		ref,
	} from "vue";
	import "@fortawesome/fontawesome-free/css/all.css";
	import Button from "@/components/Button.vue";
	import Input from "@/components/Input.vue";
	import { Item } from "@/models/Item";
	import axios from "axios";
	import { useToast } from "vue-toastification";

	const props = defineProps({
		id: {
			type: String,
			required: true,
		},
	});

	const lists = inject("lists");
	const toast = useToast();

	const allUsers = ref([]);
	const selectedUserToShare = ref("");
	const selectedUserToUnshare = ref("");

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

	const listId = computed(() => Number(props.id));

	const list = computed(() => {
		return lists.lists.find((l) => l.id === listId.value) || null;
	});

	onMounted(async () => {
		await fetchAllUsers();
		syncFast();
	});

	async function fetchAllUsers() {
		try {
			const resp = await axios.get(
				"https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/getAllUsers",
				{
					headers: getAuthHeaders(),
					withCredentials: true,
				}
			);
			allUsers.value = resp.data;
		} catch (error) {
			console.error(
				"Błąd podczas pobierania listy wszystkich użytkowników:",
				error
			);
		}
	}

	const filteredAllUsers = computed(() => {
		if (!list.value) return [];
		return allUsers.value.filter(
			(email) =>
				email !== list.value.owner && !list.value.users.includes(email)
		);
	});

	const shareWith = async () => {
		if (!selectedUserToShare.value) {
			toast.error("Wybierz użytkownika, któremu chcesz udostępnić!");
			return;
		}
		if (!list.value) {
			toast.error("Nie znaleziono listy – odśwież stronę.");
			return;
		}
		try {
			const emailToShare = selectedUserToShare.value;
			await axios.post(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/addSharedUser/${list.value.id}?email=${emailToShare}`,
				null,
				{
					headers: {
						"Content-Type": "application/json",
						...getAuthHeaders(),
					},
					withCredentials: true,
				}
			);
			toast.success(
				`Użytkownik ${emailToShare} został dodany do współdzielenia!`
			);
			selectedUserToShare.value = "";
			await refreshListData();
		} catch (error) {
			console.error("Error trying to share this list", error);
			toast.error("Błąd podczas udostępniania listy.");
		}
	};

	const unshareWith = async () => {
		if (!selectedUserToUnshare.value) {
			toast.error(
				"Wybierz użytkownika, którego chcesz usunąć ze współdzielenia!"
			);
			return;
		}
		if (!list.value) {
			toast.error("Nie znaleziono listy – odśwież stronę.");
			return;
		}
		try {
			const emailToRemove = selectedUserToUnshare.value;
			await axios.delete(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/removeSharedUser/${list.value.id}`,
				{
					headers: getAuthHeaders(),
					params: {
						email: emailToRemove,
					},
					withCredentials: true,
				}
			);
			toast.success(
				`Użytkownik ${emailToRemove} został usunięty ze współdzielenia!`
			);
			selectedUserToUnshare.value = "";
			await refreshListData();
		} catch (error) {
			console.error("Error trying to unshare this list", error);
			toast.error("Błąd podczas usuwania współdzielenia.");
		}
	};

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

	async function saveNewItem(item) {
		try {
			const response = await axios.post(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/addNewItem/${list.value.id}`,
				{
					data: item.data,
					status: item.status,
					unit: item.unit,
					quantity: item.quantity,
				},
				{
					headers: {
						"Content-Type": "application/json",
						...getAuthHeaders(),
					},
					withCredentials: true,
				}
			);
			const newId = response.data.items[response.data.items.length - 1].id;
			return newId;
		} catch (error) {
			console.error("Error adding new item:", error);
			return null;
		}
	}

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

	async function updateItem(item) {
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

		if (!item.data.trim()) {
			console.warn("Item text is empty. Removing the item.");
			await removeItem(item.id);
			return;
		}

		try {
			await axios.put(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/changeItem/${item.id}`,
				item,
				{
					headers: {
						"Content-Type": "application/json",
						...getAuthHeaders(),
					},
					withCredentials: true,
				}
			);
		} catch (error) {
			console.error("Error updating item:", error);
		}
	}

	async function changeItemStatus(item) {
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
	}

	async function renameList() {
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
						params: {
							newName: list.value.name,
						},
						withCredentials: true,
					}
				);
			} catch (error) {
				console.error("Error renaming list:", error);
			}
		}
	}

	const refreshListData = async () => {
		console.log("Wywołanie refreshListData dla listy ID:", listId.value);
		try {
			const response = await axios.get(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/getListById/${listId.value}`,
				{ headers: getAuthHeaders(), withCredentials: true }
			);
			updateListInStore(response.data);
		} catch (error) {
			console.error("Błąd podczas synchronizacji listy:", error);
		}
	};

	function updateListInStore(freshData) {
		const index = lists.lists.findIndex((l) => l.id === freshData.id);
		if (index !== -1) {
			lists.lists[index].name = freshData.name;
			lists.lists[index].owner = freshData.owner;
			lists.lists[index].users = reactive(
				freshData.sharedUsers?.map((u) => u.email) ?? []
			);
			lists.lists[index].items = reactive(
				freshData.items.map(
					(it) =>
						new Item(it.id, it.data, it.status, it.unit, it.quantity)
				)
			);
			console.log(`Lista ID ${freshData.id} zaktualizowana.`);
		} else {
			console.warn(
				"Lista nieznaleziona w store podczas synchronizacji. Dodaję nową listę."
			);
		}
	}

	let syncInterval = null;

	function startSync(intervalMs) {
		if (syncInterval) {
			clearInterval(syncInterval);
		}
		syncInterval = setInterval(refreshListData, intervalMs);
	}

	function syncFast() {
		console.log("Blur detected - switching to fast sync (500ms)");
		startSync(500);
	}

	function syncSlow() {
		console.log("Focus detected - switching to slow sync (20s)");
		startSync(20000);
	}

	onBeforeUnmount(() => {
		if (syncInterval) {
			clearInterval(syncInterval);
		}
	});
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

		.share-section,
		.unshare-section {
			margin-top: 20px;

			h3 {
				margin-bottom: 8px;
				color: var(--text-color);
			}

			.share-select {
				min-width: 180px;
				margin-right: 10px;
				padding: 6px;
				border-radius: 4px;
				border: 1px solid var(--border-color);
				outline: none;
				background-color: #fff;
				font-size: 14px;
				color: var(--text-color);
			}

			.share-button {
				width: auto;
				min-width: 120px;
			}
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

			.share-section,
			.unshare-section {
				display: flex;
				flex-direction: column;

				.share-select {
					margin-bottom: 10px;
				}

				.share-button {
					width: 100%;
				}
			}
		}
	}
</style>
