document.addEventListener("DOMContentLoaded", function () {
    getInvoiceDetails();
});

async function getInvoiceDetails() {
    const parameters = new URLSearchParams(window.location.search);
    const response = await fetch("LoadInvoice?id=" + parameters.get("id"));

    if (response.ok) {
        const json = await response.json();

        const dateStr = json.order.dateTime;
        const dateObj = new Date(dateStr);

        // Use Intl.DateTimeFormat to format the date
        const formattedDate = new Intl.DateTimeFormat('en-US', {
            month: 'short', // Short month name (e.g., "Dec")
            day: 'numeric', // Numeric day (e.g., "15")
            year: 'numeric', // Numeric year (e.g., "2024")
            hour: '2-digit', // 2-digit hour (e.g., "03")
            minute: '2-digit', // 2-digit minute (e.g., "12")
            hour12: true      // 12-hour format (AM/PM)
        }).format(dateObj);

        document.getElementById("invoice_id").innerHTML = "TM_" + json.order.id;
        document.getElementById("invoice_date_time").innerHTML = formattedDate;
        document.getElementById("invoice_user").innerHTML = json.order.user.first_name + " " + json.order.user.last_name + ",";
        document.getElementById("invoice_user_address").innerHTML = json.address.line1 + ", " + json.address.line2 + ", " + json.address.city.city + ",";
        document.getElementById("invoice_user_city").innerHTML = json.address.city.district.district + ".";

        let cloneItem = document.getElementById("invoice_item");
        document.getElementById("invoice_container").innerHTML = "";

        let shipping_cost = 0;
        let sub_total = 0;

        function getFormattedCurrency(amount) {
            let formattedCurrency = new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'LKR'
            }).format(amount);
            return formattedCurrency;
        }

        json.orderItems.forEach((orderItem) => {
            let invoiceElement = cloneItem.cloneNode(true);
            invoiceElement.querySelector("#invoice_product_id").innerHTML = orderItem.product.id;
            invoiceElement.querySelector("#invoice_product_name").innerHTML = orderItem.product.title;
            invoiceElement.querySelector("#invoice_product_price").innerHTML = getFormattedCurrency(orderItem.product.price);
            invoiceElement.querySelector("#invoice_product_qty").innerHTML = orderItem.qty;
            invoiceElement.querySelector("#invoice_product_amount").innerHTML = getFormattedCurrency(orderItem.qty * orderItem.product.price);
            invoiceElement.querySelector("#invoice_product_delivery").innerHTML = getFormattedCurrency(orderItem.deliveryFee);

            sub_total += (orderItem.qty * orderItem.product.price);
            shipping_cost += orderItem.deliveryFee;

            document.getElementById("invoice_container").append(invoiceElement);
        });

        document.getElementById("invoice_sub_total").innerHTML = getFormattedCurrency(sub_total);
        document.getElementById("invoice_shiping_cost").innerHTML = getFormattedCurrency(shipping_cost);
        document.getElementById("invoice_total").innerHTML = getFormattedCurrency(sub_total + shipping_cost);

    } else {
        console.log("Response Error: " + response.status);
    }
}