payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    updatePayment(orderId);
};

payhere.onDismissed = function onDismissed() {
    console.log("Payment dismissed");
};

payhere.onError = function onError(error) {
    console.log("Error:" + error);
};

const Toast = Swal.mixin({
    toast: true,
    position: "top-end",
    showConfirmButton: false,
    timerProgressBar: true,
    didOpen: (toast) => {
        toast.onmouseenter = Swal.stopTimer;
        toast.onmouseleave = Swal.resumeTimer;
    }
});

document.getElementById('payhere-payment').onclick = async function (e) {

    const parameters = new URLSearchParams(window.location.search);
    const response = await fetch("BuyNow?id=" + parameters.get("id"));

    if (response.ok) {
        const json = await response.json();
        if (json.payment_status) {
            payhere.startPayment(json.payhere_data);
        } else {
            Toast.fire({
                timer: 3000,
                icon: "warning",
                title: "Unable to Process Payments: " + json.login_status
            });
        }
    } else {
        console.log("Response Error: " + response);
    }
};

async function updatePayment(orderId) {
    const response = await fetch("UpdateOrder?id=" + orderId);
    if (response.ok) {
        const json = await response.json();
        if (json.success) {
            console.log("Success: TRUE");
        } else {
            console.log("Success: FALSE");
        }
    } else {
        console.log("Response Error");
    }
    Toast.fire({
        timer: 3000,
        icon: "success",
        title: "Payment Completed."
    }).then(() => {
        window.location.reload();
    });
}