document.addEventListener("DOMContentLoaded", () => {
  const sidebar = document.querySelector(".sidebar");
  const overlay = document.getElementById("menuOverlay");
  const toggle = document.getElementById("menuToggle");

  if (toggle && sidebar) {
    toggle.addEventListener("click", () => {
      sidebar.classList.toggle("active");
      if (overlay) overlay.classList.toggle("active");
    });
  }

  if (overlay && sidebar) {
    overlay.addEventListener("click", () => {
      sidebar.classList.remove("active");
      overlay.classList.remove("active");
    });
  }

  document.querySelectorAll("[data-logout]").forEach((node) => {
    node.addEventListener("click", (e) => {
      e.preventDefault();
      localStorage.removeItem("usuario");
      localStorage.removeItem("token");
      window.location.replace("login.html");
    });
  });

  const user = JSON.parse(localStorage.getItem("usuario") || "null");
  if (user?.nome) {
    document.querySelectorAll("[data-user-name]").forEach((node) => {
      node.textContent = user.nome;
    });
  }

  if (window.feather) {
    window.feather.replace();
  }

  if (user?.id) {
    _initNotificacoes(user.id);
  }
});

// ── Notificações ────────────────────────────────

const _notifBase = (() => {
  const o = window.location.origin;
  return o && o.startsWith("http") ? o : "http://localhost:8080";
})();

function _notifToken() {
  return localStorage.getItem("token") || "";
}

async function _notifFetch(path, method = "GET") {
  const res = await fetch(_notifBase + path, {
    method,
    headers: { Authorization: "Bearer " + _notifToken() },
  });
  if (!res.ok) throw new Error("notif fetch error " + res.status);
  return res.json();
}

function _notifEscape(str) {
  const d = document.createElement("div");
  d.textContent = String(str);
  return d.innerHTML;
}

function _initNotificacoes(usuarioId) {
  // Inject bell into the header flex container that holds menuToggle
  const menuToggle = document.getElementById("menuToggle");
  if (!menuToggle) return;
  const container = menuToggle.parentElement;
  if (!container) return;

  const wrapper = document.createElement("div");
  wrapper.className = "notif-wrapper";
  wrapper.innerHTML = `
    <button class="notif-bell" id="notif-bell" aria-label="Notificações" aria-haspopup="true" aria-expanded="false">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
           stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
        <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
      </svg>
      <span class="notif-badge" id="notif-badge" style="display:none">0</span>
    </button>
    <div class="notif-panel" id="notif-panel" role="dialog" aria-label="Notificações">
      <div class="notif-panel-header">
        <span>Notificações</span>
        <button class="notif-mark-all" id="notif-mark-all">Marcar todas como lidas</button>
      </div>
      <div id="notif-list"></div>
    </div>
  `;

  // Insert wrapper before the menuToggle button
  container.insertBefore(wrapper, menuToggle);

  const bell = document.getElementById("notif-bell");
  const panel = document.getElementById("notif-panel");
  const badge = document.getElementById("notif-badge");
  const markAllBtn = document.getElementById("notif-mark-all");

  // Toggle panel on bell click
  bell.addEventListener("click", async (e) => {
    e.stopPropagation();
    const isOpen = panel.classList.contains("open");
    panel.classList.toggle("open");
    bell.setAttribute("aria-expanded", String(!isOpen));
    if (!isOpen) {
      await _notifRenderList();
      _notifMarkAllRead(badge);
    }
  });

  // Close panel on outside click
  document.addEventListener("click", (e) => {
    if (!wrapper.contains(e.target)) {
      panel.classList.remove("open");
      bell.setAttribute("aria-expanded", "false");
    }
  });

  // Mark all as read button
  markAllBtn.addEventListener("click", async (e) => {
    e.stopPropagation();
    await _notifMarkAllRead(badge);
    await _notifRenderList();
  });

  // Initial count + poll every 30 s
  _notifUpdateCount(badge);
  setInterval(() => _notifUpdateCount(badge), 30000);
}

async function _notifUpdateCount(badge) {
  try {
    const data = await _notifFetch("/api/notificacoes/count");
    const count = data.naoLidas || 0;
    if (count > 0) {
      badge.textContent = count > 99 ? "99+" : String(count);
      badge.style.display = "flex";
    } else {
      badge.style.display = "none";
    }
  } catch {
    // silently ignore if user is not authenticated yet
  }
}

async function _notifRenderList() {
  const list = document.getElementById("notif-list");
  if (!list) return;
  list.innerHTML = '<div class="notif-empty">Carregando...</div>';
  try {
    const items = await _notifFetch("/api/notificacoes");
    if (!items.length) {
      list.innerHTML = '<div class="notif-empty">Nenhuma notificação ainda.</div>';
      return;
    }
    list.innerHTML = items
      .map(
        (n) => `
        <div class="notif-item ${n.lida ? "" : "unread"}">
          <div class="notif-msg">${_notifEscape(n.mensagem)}</div>
          <div class="notif-time">${_notifEscape(n.data)}</div>
        </div>`
      )
      .join("");
  } catch {
    list.innerHTML = '<div class="notif-empty">Erro ao carregar notificações.</div>';
  }
}

async function _notifMarkAllRead(badge) {
  try {
    await _notifFetch("/api/notificacoes/todas-lidas", "PUT");
    if (badge) badge.style.display = "none";
  } catch {
    // ignore
  }
}
