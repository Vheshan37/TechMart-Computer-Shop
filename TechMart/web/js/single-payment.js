payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
};

payhere.onDismissed = function onDismissed() {
    console.log("Payment dismissed");
};

payhere.onError = function onError(error) {
    console.log("Error:" + error);
};

document.getElementById('payhere-payment').onclick = async function (e) {
    const parameters = new URLSearchParams(window.location.search);
    const response = await fetch("BuyNow?id=" + parameters.get("id"));

    if (response.ok) {
        const json = await response.json();
        console.log(json.payhere_data);

        payhere.startPayment(json.payhere_data);

    } else {
        console.log("Response Error");
    }
};

//        var payment = {
//            "sandbox": true,
//            "merchant_id": 1221196,
//            "return_url": "index.html",
//            "cancel_url": "index.html",
//            "notify_url": "index.html",
//            "order_id": json.payhere_data.order_id,
//            "items": json.payhere_data.items,
//            "amount": json.payhere_data.amount,
//            "currency": "LKR",
//            "hash": json.payhere_data.hash,
//            "first_name": json.payhere_data.first_name,
//            "last_name": json.payhere_data.last_name,
//            "email": json.payhere_data.email,
//            "phone": json.user.mobile,
//            "address": json.payhere_data.address,
//            "city": json.payhere_data.city,
//            "country": "Sri Lanka",
//            "delivery_address": json.payhere_data.address,
//            "delivery_city": json.payhere_data.city,
//            "delivery_country": "Sri Lanka"
//        };