const API_URL = "http://localhost:8080";

const customerForm = document.getElementById("customerForm");
customerForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const response = await fetch(`${API_URL}/customers`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            name: document.getElementById("customerName").value,
            email: document.getElementById("customerEmail").value,
            document: document.getElementById("customerDocument").value
        })
    });

    const data = await response.json();
    const result = document.getElementById("customerResult");

    if (response.ok) {
        result.innerHTML = `<p>Cliente criado com sucesso!</p><p>ID: ${data.id}</p>`;
        customerForm.reset();
    } else {
        result.innerHTML = `<p>Erro: ${data.message}</p>`;
    }
});

const productForm = document.getElementById("productForm");
productForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const response = await fetch(`${API_URL}/products`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            name: document.getElementById("productName").value,
            description: document.getElementById("productDescription").value,
            price: Number(document.getElementById("productPrice").value)
        })
    });

    const data = await response.json();
    const result = document.getElementById("productResult");

    if (response.ok) {
        result.innerHTML = `<p>Produto criado com sucesso!</p><p>ID: ${data.id}</p>`;
        productForm.reset();
    } else {
        result.innerHTML = `<p>Erro: ${data.message}</p>`;
    }
});

const searchCustomerForm = document.getElementById("searchCustomerForm");
searchCustomerForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const id = document.getElementById("customerId").value;
    const response = await fetch(`${API_URL}/customers/${id}`);
    const data = await response.json();
    const result = document.getElementById("searchCustomerResult");

    if (response.ok) {
        result.innerHTML = `
            <h3>Cliente encontrado</h3>
            <p><strong>ID:</strong> ${data.id}</p>
            <p><strong>Nome:</strong> ${data.name}</p>
            <p><strong>E-mail:</strong> ${data.email}</p>
            <p><strong>Documento:</strong> ${data.document}</p>
        `;
    } else {
        result.innerHTML = `<p>Erro: ${data.message}</p>`;
    }
});
