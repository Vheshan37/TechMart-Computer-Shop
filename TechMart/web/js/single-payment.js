payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    updatePayment(orderId);
};

payhere.onDismissed = function onDismissed() {
    console.log("Payment dismissed. OrderID: " + orderID);
};

payhere.onError = function onError(error) {
    console.log("Error:" + error);
};

var orderID;
document.getElementById('payhere-payment').onclick = async function (e) {
    const parameters = new URLSearchParams(window.location.search);
    const response = await fetch("BuyNow?id=" + parameters.get("id"));

    if (response.ok) {
        const json = await response.json();
        orderID = json.payhere_data.orderID;
        payhere.startPayment(json.payhere_data);
    } else {
        console.log("Response Error");
    }
};

async function updatePayment(orderId) {
    const response = await fetch("UpdateOrder?id=" + orderId);
    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.success) {
            console.log("Success: TRUE")
        } else {
            console.log("Success: FALSE")
        }
    } else {
        console.log("Response Error");
    }
}