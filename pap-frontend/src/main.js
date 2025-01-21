import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import Toast, { POSITION } from "vue-toastification";
import "vue-toastification/dist/index.css";
import axios from "axios";
import "./style.scss";

const app = createApp(App);

axios.defaults.withCredentials = true;

axios.interceptors.response.use(
	(response) => response,
	(error) => {
		if (error.response && error.response.status === 401) {
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
