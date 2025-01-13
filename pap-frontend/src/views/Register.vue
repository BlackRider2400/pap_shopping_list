<template>
	<div id="register-container">
		<h1>Rejestracja</h1>
		<form @submit.prevent="handleRegister">
			<Input
				id="email"
				label="Email:"
				type="email"
				placeholder="Wprowadź email"
				v-model="email"
				:errorMessage="emailError"
			/>
			<Input
				id="name"
				label="Imię:"
				placeholder="Wprowadź swoje imię"
				v-model="name"
				:errorMessage="nameError"
			/>
			<Input
				id="password"
				type="password"
				label="Hasło:"
				placeholder="Wprowadź hasło"
				v-model="password"
				:errorMessage="passwordError"
			/>
			<Input
				id="confirm-password"
				type="password"
				label="Potwierdź hasło:"
				placeholder="Wprowadź hasło ponownie"
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
				{{ isLoading ? "Rejestrowanie..." : "Zarejestruj" }}
			</Button>
		</form>
	</div>
</template>

<script setup>
	import { ref } from "vue";
	import { useRouter } from "vue-router";
	import axios from "axios";
	import Input from "../components/Input.vue";
	import Button from "../components/Button.vue";

	const email = ref("");
	const name = ref("");
	const password = ref("");
	const confirmPassword = ref("");

	const emailError = ref("");
	const nameError = ref("");
	const passwordError = ref("");
	const confirmPasswordError = ref("");
	const apiError = ref("");
	const successMessage = ref("");

	const isLoading = ref(false);
	const router = useRouter();

	const validateEmail = (email) => {
		const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return re.test(email);
	};

	const handleRegister = async () => {
		// Resetowanie komunikatów błędów
		emailError.value = "";
		nameError.value = "";
		passwordError.value = "";
		confirmPasswordError.value = "";
		apiError.value = "";
		successMessage.value = "";

		let hasError = false;

		if (!email.value) {
			emailError.value = "Email jest wymagany!";
			hasError = true;
		} else if (!validateEmail(email.value)) {
			emailError.value = "Nieprawidłowy format email!";
			hasError = true;
		}

		if (!name.value) {
			nameError.value = "Imię jest wymagane!";
			hasError = true;
		}

		if (!password.value) {
			passwordError.value = "Hasło jest wymagane!";
			hasError = true;
		} else if (password.value.length < 6) {
			passwordError.value = "Hasło musi mieć co najmniej 6 znaków!";
			hasError = true;
		}

		if (!confirmPassword.value) {
			confirmPasswordError.value = "Potwierdzenie hasła jest wymagane!";
			hasError = true;
		} else if (password.value !== confirmPassword.value) {
			confirmPasswordError.value = "Hasła nie są zgodne!";
			hasError = true;
		}

		if (hasError) return;

		const payload = {
			email: email.value,
			password: password.value,
			name: name.value,
		};

		isLoading.value = true;

		try {
			const response = await axios.post(
				"https://mylovelyserver.fun:8443/pap_shopping_list/api/auth/register",
				payload,
				{ withCredentials: true }
			);

			if (response.status === 200) {
				successMessage.value = `Rejestracja udana dla użytkownika: ${response.data.name}`;
				await handleLogin(email.value, password.value);
			}
		} catch (error) {
			if (error.response) {
				if (error.response.status === 400) {
					apiError.value = "Email jest już w użyciu.";
				} else {
					apiError.value = "Wystąpił błąd podczas rejestracji.";
				}
			} else {
				apiError.value = "Brak połączenia z serwerem.";
			}
		} finally {
			isLoading.value = false;
		}
	};

	const handleLogin = async (email, password) => {
		try {
			const response = await axios.post(
				"https://mylovelyserver.fun:8443/pap_shopping_list/api/auth/login",
				{ email, password },
				{ withCredentials: true }
			);

			if (response.status === 200) {
				router.push({ name: "Home" });
			}
		} catch (error) {
			apiError.value =
				"Automatyczne logowanie po rejestracji nie powiodło się.";
		}
	};
</script>

  <style scoped lang="scss">
	#register-container {
		max-width: 400px;
		margin: 50px auto;
		padding: 20px;
		border: 1px solid #ccc;
		border-radius: 5px;
		background-color: #f9f9f9;
		box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
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
</style>
