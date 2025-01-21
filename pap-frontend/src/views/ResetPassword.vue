<template>
	<div id="reset-password-container">
		<h1>Ustaw nowe hasło</h1>
		<form @submit.prevent="handleResetPassword">
			<Input
				id="new-password"
				label="Nowe hasło:"
				type="password"
				placeholder="Wprowadź nowe hasło"
				v-model="newPassword"
				:errorMessage="passwordError"
			/>
			<Input
				id="confirm-password"
				label="Powtórz nowe hasło:"
				type="password"
				placeholder="Wprowadź ponownie nowe hasło"
				v-model="confirmPassword"
				:errorMessage="confirmPasswordError"
			/>
			<div class="error-message" v-if="apiError">
				{{ apiError }}
			</div>
			<div class="success-message" v-if="successMessage">
				{{ successMessage }}
			</div>
			<Button type="primary" :disabled="isLoading">
				{{ isLoading ? "Resetowanie..." : "Zresetuj Hasło" }}
			</Button>
		</form>
	</div>
</template>

  <script setup>
	import { ref, onMounted } from "vue";
	import { useRoute, useRouter } from "vue-router";
	import axios from "axios";
	import Input from "@/components/Input.vue";
	import Button from "@/components/Button.vue";

	const route = useRoute();
	const router = useRouter();

	const newPassword = ref("");
	const confirmPassword = ref("");
	const passwordError = ref("");
	const confirmPasswordError = ref("");
	const apiError = ref("");
	const successMessage = ref("");
	const isLoading = ref(false);

	let token = "";

	onMounted(() => {
		token = route.query.token || "";
	});

	const handleResetPassword = async () => {
		passwordError.value = "";
		confirmPasswordError.value = "";
		apiError.value = "";
		successMessage.value = "";

		if (!newPassword.value) {
			passwordError.value = "Hasło jest wymagane!";
			return;
		} else if (newPassword.value.length < 6) {
			passwordError.value = "Hasło musi mieć co najmniej 6 znaków!";
			return;
		}

		if (!confirmPassword.value) {
			confirmPasswordError.value = "Potwierdź hasło!";
			return;
		}

		if (newPassword.value !== confirmPassword.value) {
			confirmPasswordError.value = "Hasła nie są zgodne!";
			return;
		}

		if (!token) {
			apiError.value = "Brak tokenu resetującego w adresie URL.";
			return;
		}

		isLoading.value = true;

		try {
			const response = await axios.post(
				"https://mylovelyserver.fun:8443/pap_shopping_list/api/auth/reset-password",
				null,
				{
					params: {
						token: token,
						newPassword: newPassword.value,
					},
					withCredentials: false,
				}
			);

			if (response.status === 200) {
				successMessage.value = "Hasło zostało pomyślnie zmienione!";
				setTimeout(() => {
					router.push({ name: "LogIn" });
				}, 2000);
			}
		} catch (error) {
			if (error.response) {
				if (error.response.status === 400) {
					apiError.value = "Nieprawidłowy lub wygasły token.";
				} else {
					apiError.value = "Wystąpił błąd podczas resetowania hasła.";
				}
			} else {
				apiError.value = "Brak połączenia z serwerem.";
			}
		} finally {
			isLoading.value = false;
		}
	};
</script>

  <style scoped lang="scss">
	#reset-password-container {
		max-width: 400px;
		margin: 50px auto;
		padding: 20px;
		border: 1px solid #ccc;
		border-radius: 5px;
		background-color: #f9f9f9;
		box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

		button {
			margin-top: 20px;
		}

		h1 {
			text-align: center;
			color: #333;
			margin-bottom: 20px;
		}

		.error-message {
			color: red;
			font-size: 14px;
			margin-top: 10px;
			text-align: center;
		}

		.success-message {
			color: green;
			font-size: 14px;
			margin-top: 10px;
			text-align: center;
		}
	}
</style>
