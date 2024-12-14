// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    // Note: validate the payment and show success or failure page to the customer
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:" + error);
};

// Show the payhere.js popup, when "PayHere Pay" is clicked
document.getElementById('payhere-payment').onclick = async function (e) {

    const parameters = new URLSearchParams(window.location.search);
    const response = await fetch("BuyNow?id=" + parameters.get("id"));

    if (response.ok) {
        const json = await response.json();
        console.log(json);

        var payment = {
            "sandbox": true,
            "merchant_id": 1221196, // Replace your Merchant ID
            "return_url": "index.html", // Important
            "cancel_url": "index.html", // Important
            "notify_url": "index.html",
            "order_id": json.payhere_data.order_id,
            "items": json.payhere_data.items,
            "amount": json.payhere_data.amount,
            "currency": "LKR",
            "hash": json.payhere_data.hash, // *Replace with generated hash retrieved from backend
            "first_name": json.payhere_data.first_name,
            "last_name": json.payhere_data.last_name,
            "email": json.payhere_data.email,
            "phone": "0771234567",
            "address": "No.1, Galle Road",
            "city": "Colombo",
            "country": "Sri Lanka",
            "delivery_address": "No. 46, Galle road, Kalutara South",
            "delivery_city": "Kalutara",
            "delivery_country": "Sri Lanka",
            "custom_1": "",
            "custom_2": ""
        };

        payhere.startPayment(payment);

    } else {
        console.log("Response Error");
    }
};