document.addEventListener("DOMContentLoaded", function () {
    getDefaultOrders();
});

async function getDefaultOrders() {
    const response = await fetch("Orders");

    if (response.ok) {
        const json = await response.json();
        console.log(json);
    } else {
        console.log("Response Error");
    }
}