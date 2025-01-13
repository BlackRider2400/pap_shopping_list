// main.js
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import Toast, { POSITION } from "vue-toastification";
import "vue-toastification/dist/index.css";
import axios from "axios";
import "./style.scss";
import Cookies from "js-cookie"; // Dodano import js-cookie

const app = createApp(App);

// Ustawienie podstawowej ścieżki API
axios.defaults.baseURL = "/api/auth";

// Ustawienie Axios do wysyłania ciasteczek z żądaniami
axios.defaults.withCredentials = true;

// Dodanie interceptora do sprawdzania odpowiedzi i obsługi błędów globalnych
axios.interceptors.response.use(
	(response) => response,
	(error) => {
		if (error.response && error.response.status === 401) {
			// Możesz dodać globalną obsługę błędów, np. przekierowanie do logowania
			router.push({ name: "LogIn" });
		}
		return Promise.reject(error);
	}
);

app.use(router);

app.use(Toast, {
	position: POSITION.BOTTOM_LEFT,
	timeout: 3000,
	closeOnClick: true,
	pauseOnFocusLoss: true,
	pauseOnHover: true,
	draggable: true,
	draggablePercent: 0.6,
	showCloseButtonOnHover: false,
	hideProgressBar: false,
	closeButton: "button",
	icon: true,
	rtl: false,
});

app.mount("#app");
