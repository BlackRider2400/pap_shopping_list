import { createRouter, createWebHistory } from "vue-router";

const routes = [
	{
		path: "/",
		name: "Home",
		component: () => import("../views/Home.vue"),
	},
	{
		path: "/login",
		name: "LogIn",
		component: () => import("../views/LogIn.vue"),
	},
	{
		path: "/register",
		name: "Register",
		component: () => import("../views/Register.vue"),
	},
	{
		path: "/lists",
		redirect: "/lists/1",
		name: "Lists",
		component: () => import("../views/Lists.vue"),
		children: [
			{
				path: ":id",
				name: "ListDetail",
				component: () => import("../views/Lists/ListBody.vue"),
				props: true,
			},
		],
	},
];

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes,
});

export default router;
