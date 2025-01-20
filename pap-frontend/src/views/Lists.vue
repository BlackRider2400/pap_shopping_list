<template>
	<div id="lists-container">
		<aside id="lists-menu">
			<header class="menu-header">
				<h2>Twoje Listy</h2>
				<Button class="add-list" @click="addList">
					<i class="fa-solid fa-plus"></i> Dodaj Listę
				</Button>
			</header>
			<ul class="list-items">
				<li
					v-for="list in lists.lists"
					:key="list.id"
					@click="setCurrentList(list)"
					class="list-item"
				>
					<span class="list-name">{{ list.name }}</span>
					<button
						@click.stop="removeList(list.id)"
						class="remove-button"
						aria-label="Usuń Listę"
					>
						<i class="fa-solid fa-trash"></i>
					</button>
				</li>
			</ul>
		</aside>
		<main id="lists-window">
			<router-view />
		</main>
	</div>
</template>

<script setup>
	import { inject } from "vue";
	import { useRouter } from "vue-router";
	import Button from "@/components/Button.vue";
	import axios from "axios";
	import { useToast } from "vue-toastification";

	const lists = inject("lists");
	const fetchDataFromApi = inject("fetchDataFromApi");
	const getAuthHeaders = inject("getAuthHeaders");
	const router = useRouter();
	const toast = useToast();

	const setCurrentList = (list) => {
		router.push({ name: "ListDetail", params: { id: list.id } });
	};

	const addList = async () => {
		try {
			const email = localStorage.getItem("authEmail");
			const password = localStorage.getItem("authPassword");
			const credentials = btoa(`${email}:${password}`);

			const response = await axios.post(
				"https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/addNewList",
				null,
				{
					params: {
						name: "Nowa Lista",
					},
					headers: {
						"Content-Type": "application/json",
						Authorization: `Basic ${credentials}`,
					},
					withCredentials: true,
				}
			);

			toast.success("Lista została dodana.");
			await fetchDataFromApi();
			const newList = lists.lists.find((list) => list.name === "Nowa Lista");
			if (newList) {
				setCurrentList(newList);
			}
		} catch (error) {
			console.error("Error adding new list:", error);
		}
	};

	const removeList = async (listId) => {
		try {
			await axios.delete(
				`https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/deleteListById/${listId}`,
				{
					params: {
						userId: 1,
						id: listId,
					},
					headers: getAuthHeaders(),
					withCredentials: true,
				}
			);
			toast.error("Lista została usunięta.");
			await fetchDataFromApi();
		} catch (error) {
			console.error("Error deleting list:", error);
		}
	};

	fetchDataFromApi();
</script>

<style scoped lang="scss">
	#lists-container {
		display: flex;
		min-height: 100vh;
		background-color: #f0f0f0;
	}

	#lists-menu {
		width: 300px;
		background-color: #ffffff;
		border-right: 1px solid #ddd;
		display: flex;
		flex-direction: column;
		padding: 20px;
		box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
	}

	.menu-header {
		display: flex;
		flex-direction: column;
		align-items: space-between;
		justify-content: center;
		margin-bottom: 20px;

		h2 {
			font-size: 1.5rem;
			color: #333;
		}

		.add-list {
			display: flex;
			align-items: center;
			background-color: #28a745;
			color: #fff;
			border: none;
			padding: 10px 15px;
			border-radius: 5px;
			cursor: pointer;
			font-size: 0.9rem;
			transition: background-color 0.3s;

			i {
				margin-right: 5px;
			}

			&:hover {
				background-color: #218838;
			}
		}
	}

	.list-items {
		list-style: none;
		padding: 0;
		margin: 0;
		flex-grow: 1;
		overflow-y: auto;
	}

	.list-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 12px 10px;
		border-bottom: 1px solid #eee;
		cursor: pointer;
		transition: background-color 0.2s;

		&:hover {
			background-color: #f9f9f9;
		}

		.list-name {
			font-size: 1rem;
			color: #555;
			flex-grow: 1;
		}

		.remove-button {
			background: none;
			border: none;
			color: #d9534f;
			cursor: pointer;
			font-size: 1rem;
			padding: 5px;
			transition: color 0.2s;

			&:hover {
				color: #c9302c;
			}

			&:focus {
				outline: none;
			}
		}
	}

	#lists-window {
		flex-grow: 1;
		padding: 30px;
		background-color: #fafafa;
		overflow-y: auto;
	}

	@media (max-width: 768px) {
		#lists-container {
			flex-direction: column;
		}

		#lists-menu {
			width: 100%;
			flex-direction: row;
			overflow-x: auto;
			padding: 10px;
			box-shadow: none;
			border-right: none;
			border-bottom: 1px solid #ddd;

			header {
				flex: 2;
			}
			ul {
				flex: 4;
			}
		}

		.menu-header {
			flex-direction: column;
			align-items: flex-start;

			h2 {
				margin-bottom: 10px;
			}

			.add-list {
				width: 100%;
				justify-content: center;
			}
		}

		.list-items {
			display: flex;
			flex-direction: row;
			overflow-x: auto;
		}

		.list-item {
			flex-direction: column;
			align-items: flex-start;
		}

		#lists-window {
			padding: 5px;
		}
	}
</style>
