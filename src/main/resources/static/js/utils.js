const API_URL = "http://44.198.34.216:8081/api";

function redirectIfNotLogged() {
  if (!localStorage.getItem("userType")) {
    window.location.href = "login.html";
  }
}
