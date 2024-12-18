document.addEventListener("DOMContentLoaded", function () {
    getDefaultOrders();
});

async function getDefaultOrders() {
    const response = await fetch("Orders");

    if (response.ok) {
        const json = await response.json();
        arrangeFrontEnd(json);
    } else {
        console.log("Response Error");
    }
}

function increaseItemStatus(orderItemID) {
    updateOrderItemStatus(orderItemID, "up");
}

function decreaseItemStatus(orderItemID) {
    updateOrderItemStatus(orderItemID, "down");
}

async function updateOrderItemStatus(orderItemID, action) {
    // Create a new FormData object
    const formData = new FormData();

    // Append the values to the FormData object
    formData.append("orderItemID", orderItemID);
    formData.append("action", action);

    // Send the FormData via a POST request
    const response = await fetch("UpdateOrderItemStatus", {
        method: 'POST',
        body: formData  // Use the formData object as the body
    });

    // Check if the response is successful
    if (response.ok) {
        const json = await response.json();
        arrangeFrontEnd(json);
    } else {
        console.log("Response Error");
    }
}


function arrangeFrontEnd(json) {
    let cloneItem = document.getElementById("orderItem");
    document.getElementById("orderContainer").innerHTML = "";

    json.orderItemList.sort((a, b) => {
        // Convert date strings to Date objects for comparison
        return new Date(a.order.dateTime) - new Date(b.order.dateTime);
    });


    json.orderItemList.forEach(orderItem => {

        const dateStr = orderItem.order.dateTime;
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

        let numericValue = parseFloat(orderItem.qty * orderItem.product.price);
        // Format the number as currency
        let formattedCurrency = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'LKR'
        }).format(numericValue);

        let cloneElement = cloneItem.cloneNode(true);
        cloneElement.querySelector("#col_01").innerHTML = formattedDate;
        cloneElement.querySelector("#orderItemImage").src = "img/Product/" + orderItem.product.title + "_(" + orderItem.product.id + ")_(1).jpg";
        cloneElement.querySelector("#orderItemTitle").innerHTML = orderItem.product.title;
        cloneElement.querySelector("#orderItemQty").innerHTML = "Qty: " + orderItem.qty;
        cloneElement.querySelector("#orderItemUser").innerHTML = orderItem.order.user.first_name + " " + orderItem.order.user.last_name;
        cloneElement.querySelector("#orderItemAmount").innerHTML = formattedCurrency;
        cloneElement.querySelector("#orderItemStatus").innerHTML = orderItem.status.status;

        const upButton = cloneElement.querySelector("#orderItemStatusUp");
        const downButton = cloneElement.querySelector("#orderItemStatusDown");

        upButton.addEventListener("click", () => {
            if (orderItem.status.id < 5) {
                increaseItemStatus(orderItem.id);
            }
        });

        downButton.addEventListener("click", () => {
            if (orderItem.status.id > 1) {
                decreaseItemStatus(orderItem.id);
            }
        });

        document.getElementById("orderContainer").append(cloneElement);
    });
}