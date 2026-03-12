window.AddressForm = (() => {
  function readFields(prefix = "") {
    return {
      rua: document.getElementById(`${prefix}rua`)?.value.trim() || "",
      numero: document.getElementById(`${prefix}numero`)?.value.trim() || "",
      bairro: document.getElementById(`${prefix}bairro`)?.value.trim() || "",
      cidade: document.getElementById(`${prefix}cidade`)?.value.trim() || "",
      estado: (document.getElementById(`${prefix}estado`)?.value || "").trim().toUpperCase(),
    };
  }

  function buildFromFields(prefix = "") {
    const fields = readFields(prefix);
    const valid = !!(fields.rua && fields.numero && fields.bairro && fields.cidade && fields.estado);

    return {
      valid,
      fields,
      address: valid
        ? `${fields.rua}, ${fields.numero}, ${fields.bairro}, ${fields.cidade} - ${fields.estado}`
        : "",
    };
  }

  function parseAddress(address) {
    const text = String(address || "").trim();
    if (!text) {
      return { rua: "", numero: "", bairro: "", cidade: "", estado: "" };
    }

    const parts = text.split(",").map((part) => part.trim()).filter(Boolean);
    if (parts.length < 4) {
      return { rua: text, numero: "", bairro: "", cidade: "", estado: "" };
    }

    const rua = parts[0] || text;
    const numero = parts[1] || "";
    const bairro = parts[2] || "";
    const cityAndState = parts.slice(3).join(", ");
    let cidade = "";
    let estado = "";

    if (cityAndState.includes(" - ")) {
      const chunks = cityAndState.split(" - ");
      cidade = chunks[0]?.trim() || "";
      estado = chunks.slice(1).join(" - ").trim().toUpperCase();
    } else {
      cidade = cityAndState;
    }

    return { rua, numero, bairro, cidade, estado };
  }

  function fillFields(address, prefix = "") {
    const parsed = parseAddress(address);
    const mappings = Object.entries(parsed);
    mappings.forEach(([key, value]) => {
      const field = document.getElementById(`${prefix}${key}`);
      if (field) {
        field.value = value;
      }
    });
  }

  return {
    readFields,
    buildFromFields,
    parseAddress,
    fillFields,
  };
})();