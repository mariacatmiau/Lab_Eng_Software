const API_URL = "http://localhost:8080/api";

function redirectIfNotLogged() {
  if (!localStorage.getItem("userType")) {
    window.location.href = "login.html";
  }
}
