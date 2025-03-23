# Shopping List Application - PAP Project

This project is a collaborative effort developed by a two-person team as part of the **Programowanie Aplikacyjne (PAP)** course at the university. The goal of this application is to provide users with a convenient, interactive platform for managing their shopping lists.

## Team Members
- Krzysztof Barałkiewicz
- Rafał Mironko

## Live Demo
Explore the live version of our application hosted here:
[https://mylovelyserver.fun/pap_shopping_list/](https://mylovelyserver.fun/pap_shopping_list/)

## Features
- **User Authentication:** Secure registration, login, and session management.
- **Dynamic Shopping Lists:** Create, edit, and manage multiple shopping lists seamlessly.
- **Item Management:** Add, update, delete, and categorize items effectively.
- **Responsive Design:** Fully accessible on desktop and mobile devices.

## Technology Stack
### Frontend
- HTML5, CSS3, JavaScript
- Vue.js

### Backend
- Java
- Spring Boot
- Hibernate
- MySQL Database

### Hosting
- Private Ubuntu Server
- Apache2 web server

## Installation Instructions
To set up this project locally:

1. **Clone the repository**:
```bash
git clone https://github.com/BlackRider2400/pap_shopping_list.git
```

2. **Backend Setup:**
Navigate to the backend directory and build the project.
```bash
cd backend
./mvnw clean install
```
Start the backend:
```bash
./mvnw spring-boot:run
```

3. **Frontend Setup:**
Navigate to the frontend directory and install dependencies.
```bash
cd ../frontend
npm install
npm run serve
```

Access the application locally at:
```
http://localhost:8080
```
---

For further details, visit the project's repository:  
[https://github.com/BlackRider2400/pap_shopping_list](https://github.com/BlackRider2400/pap_shopping_list)

