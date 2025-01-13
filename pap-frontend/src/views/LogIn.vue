<template>
	<div id="login-container">
		<h1>Logowanie</h1>
		<form @submit.prevent="handleLogin">
			<Input
				id="email"
				label="Email:"
				type="email"
				placeholder="Wprowadź email"
				v-model="email"
				:errorMessage="emailError"
			/>
			<Input
				id="password"
				type="password"
				label="Hasło:"
				placeholder="Wprowadź hasło"
				v-model="password"
				:errorMessage="passwordError"
			/>
			<div class="error-message" v-if="apiError">
				{{ apiError }}
			</div>
			<Button type="primary" :disabled="isLoading">
				{{ isLoading ? "Logowanie..." : "Zaloguj" }}
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
	const password = ref("");

	const emailError = ref("");
	const passwordError = ref("");
	const apiError = ref("");

	const isLoading = ref(false);
	const router = useRouter();

	const validateEmail = (email) => {
		const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return re.test(email);
	};

	const handleLogin = async () => {
		emailError.value = "";
		passwordError.value = "";
		apiError.value = "";

		let hasError = false;

		if (!email.value) {
			emailError.value = "Email jest wymagany!";
			hasError = true;
		} else if (!validateEmail(email.value)) {
			emailError.value = "Nieprawidłowy format email!";
			hasError = true;
		}

		if (!password.value) {
			passwordError.value = "Hasło jest wymagane!";
			hasError = true;
		}

		if (hasError) return;

		isLoading.value = true;

		try {
			const response = await axios.post(
				"https://mylovelyserver.fun:8443/pap_shopping_list/api/auth/login",
				null,
				{
					params: {
						email: email.value,
						password: password.value,
					},
					withCredentials: false,
				}
			);

			if (response.status === 200) {
				// Ustawienie stanu logowania w localStorage
				localStorage.setItem("isAuthenticated", "true");
				router.push({ name: "Home" });
			}
		} catch (error) {
			if (error.response) {
				if (error.response.status === 401) {
					apiError.value = "Nieprawidłowe dane logowania.";
				} else {
					apiError.value = "Wystąpił błąd podczas logowania.";
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
	#login-container {
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
