<template>
	<footer class="footer">
		<div class="footer-content">
			<div class="footer-section">
				<h3>O nas</h3>
				<p>
					Jesteśmy firmą tworzącą innowacyjne rozwiązania dla Twoich
					projektów.
				</p>
			</div>
			<div class="footer-section">
				<h3>Nawigacja</h3>
				<ul>
					<li><router-link to="/">Strona główna</router-link></li>
					<li>
						<router-link to="/register">Rejestracja</router-link>
					</li>
					<li>
						<router-link to="/lists">Listy Zakupowe</router-link>
					</li>
					<li
						v-if="isAuthenticated"
						@click="handleLogout"
						class="pointer-class"
					>
						Logout
					</li>
					<li
						v-if="isAuthenticated"
						@click="handleDeleteAccount"
						class="pointer-class"
					>
						Usuń konto
					</li>
				</ul>
			</div>
			<div class="footer-section">
				<h3>Kontakt</h3>
				<p>Email: kontakt@firma.pl</p>
				<p>Telefon: +48 123 456 789</p>
			</div>
		</div>
		<div class="footer-bottom">
			<p>
				&copy; {{ new Date().getFullYear() }} Firma. Wszelkie prawa
				zastrzeżone.
			</p>
		</div>
	</footer>
</template>

  <script setup>
	import { ref, watch } from "vue";
	import { useRouter, useRoute } from "vue-router";
	import axios from "axios";
	import { useToast } from "vue-toastification";

	const route = useRoute();
	const router = useRouter();
	const toast = useToast();

	const isAuthenticated = ref(localStorage.getItem("isAuthenticated") === "true");

	watch(
		() => route.fullPath,
		() => {
			setTimeout(() => {
				isAuthenticated.value =
					localStorage.getItem("isAuthenticated") === "true";
			}, 1000);
		}
	);

	const handleLogout = () => {
		localStorage.removeItem("isAuthenticated");
		localStorage.removeItem("authEmail");
		localStorage.removeItem("authPassword");
		isAuthenticated.value = false;
		router.push({ name: "LogIn" });
	};

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

	const handleDeleteAccount = async () => {
		const confirmation = window.confirm(
			"Czy na pewno chcesz usunąć swoje konto? Tej operacji nie można cofnąć."
		);
		if (!confirmation) return;

		try {
			const response = await axios.delete(
				"https://mylovelyserver.fun:8443/pap_shopping_list/api/lists/deleteUser",
				{
					headers: getAuthHeaders(),
					withCredentials: true,
				}
			);

			if (response.status === 204) {
				toast.success("Konto zostało usunięte.");
				localStorage.removeItem("isAuthenticated");
				localStorage.removeItem("authEmail");
				localStorage.removeItem("authPassword");
				isAuthenticated.value = false;
				router.push({ name: "LogIn" });
			}
		} catch (error) {
			console.error("Błąd podczas usuwania konta:", error);
			toast.error(
				"Wystąpił błąd podczas usuwania konta. Spróbuj jeszcze raz."
			);
		}
	};
</script>

  <style scoped lang="scss">
	.footer {
		background-color: #333;
		color: white;
		padding: 20px 10px;
		text-align: center;

		.footer-content {
			display: flex;
			justify-content: space-around;
			flex-wrap: wrap;
			margin-bottom: 20px;

			.footer-section {
				flex: 1;
				min-width: 200px;
				margin: 10px;

				h3 {
					margin-bottom: 10px;
					font-size: 18px;
					color: #007bff;
				}

				p {
					font-size: 14px;
					line-height: 1.5;
				}

				ul {
					list-style: none;
					padding: 0;

					li.pointer-class {
						cursor: pointer;
					}

					li {
						margin: 5px 0;

						a {
							color: white;
							text-decoration: none;
							transition: color 0.3s;

							&:hover {
								color: #007bff;
							}
						}
					}
				}
			}
		}

		.footer-bottom {
			font-size: 12px;
			border-top: 1px solid #555;
			padding-top: 10px;
		}
	}
</style>
