document.addEventListener("DOMContentLoaded", function () {
    getInvoiceDetails();
});

async function getInvoiceDetails() {
    const parameters = new URLSearchParams(window.location.search);
    const response = await fetch("LoadInvoice?id=" + parameters.get("id"));

    if (response.ok) {
        const json = await response.json();
        console.log(json);
    } else {
        console.log("Response Error");
    }
}