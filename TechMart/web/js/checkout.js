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

async function checkout() {
    const response = await fetch("Checkout");

    if (response.ok) {
        const json = await response.json();
        if (json.payment_status) {
            payhere.startPayment(json.payhere_data);
        } else {
            Toast.fire({
                timer: 3000,
                icon: "warning",
                title: "Unable to Process Payments"
            });
        }
    } else {
        console.log("Response Error");
    }
}

async function updatePayment(orderId) {
    const response = await fetch("UpdateOrder?id=" + orderId);
    if (response.ok) {
        const json = await response.json();
        if (json.success) {
            Toast.fire({
                timer: 3000,
                icon: "success",
                title: "Payment Completed."
            }).then(() => {
                window.location = "invoice.html?id=" + orderId;
            });
        } else {
            console.log("Success: FALSE");
        }
    } else {
        console.log("Response Error");
    }
}