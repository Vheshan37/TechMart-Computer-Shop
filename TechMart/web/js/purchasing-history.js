document.addEventListener("DOMContentLoaded", function () {
    getDefaultOrders();
});

async function getDefaultOrders() {
    const response = await fetch("loadPurchasingHistory");

    if (response.ok) {
        const json = await response.json();
        arrangeFrontEnd(json);
    } else {
        console.log("Response Error");
    }
}

function arrangeFrontEnd(json) {
    let cloneItem = document.getElementById("purchaseItem");
    document.getElementById("purchasingContainer").innerHTML = "";

    json.orderList.forEach((order) => {

        const dateStr = order.dateTime;
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

        let itemCount = 0;
        json.orderItemList.forEach((orderItem) => {
            if (orderItem.order.id == order.id) {
                itemCount++;
            }
        });

        let totalAmount = 0;
        json.orderItemList.forEach((orderItem) => {
            if (orderItem.order.id == order.id) {
                totalAmount += orderItem.product.price * orderItem.qty;
            }
        });

        let numericValue = parseFloat(totalAmount);
        // Format the number as currency
        let formattedCurrency = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'LKR'
        }).format(numericValue);

        let cloneElement = cloneItem.cloneNode(true);
        cloneElement.querySelector("#col_01").innerHTML = "TM_" + order.id;
        cloneElement.querySelector("#col_02").innerHTML = formattedDate;
        cloneElement.querySelector("#col_03").innerHTML = itemCount + " items";
        cloneElement.querySelector("#col_04").innerHTML = formattedCurrency;
        cloneElement.querySelector("#col_05").innerHTML = order.status.status;

        const upButton = cloneElement.querySelector("#ViewPurchase");
        const downButton = cloneElement.querySelector("#deletePurchase");

        upButton.addEventListener("click", () => {
            viewPurchasedItems(order.id, json);
        });

        downButton.addEventListener("click", () => {

        });

        document.getElementById("purchasingContainer").append(cloneElement);
    });
}

// open the purchased item model
function viewPurchasedItems(orderID, json) {
    let cloneItem = document.getElementById("orderItem");
    document.getElementById("orderContainer").innerHTML = "";

    json.orderItemList.forEach((orderItem) => {
        if (orderItem.order.id == orderID) {

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
            cloneElement.querySelector("#purchase_col_01").innerHTML = formattedDate;
            cloneElement.querySelector("#purchase_orderItemImage").src = "img/Product/" + orderItem.product.title + "_(" + orderItem.product.id + ")_(1).jpg";
            cloneElement.querySelector("#purchase_orderItemTitle").innerHTML = orderItem.product.title;
            cloneElement.querySelector("#purchase_orderItemQty").innerHTML = "Qty: " + orderItem.qty;
            cloneElement.querySelector("#purchase_orderItemUser").innerHTML = orderItem.order.user.first_name + " " + orderItem.order.user.last_name;
            cloneElement.querySelector("#purchase_orderItemAmount").innerHTML = formattedCurrency;
            cloneElement.querySelector("#purchase_orderItemStatus").innerHTML = orderItem.status.status;

            document.getElementById("orderContainer").append(cloneElement);
        }
    });

    let purchasedItemsModel = document.getElementById("purchasedItemModel");
    purchasedItemsModel.classList.remove("hidden");
}

// close the purchased item model
document.getElementById("closeBtn").addEventListener("click", () => {
    let purchasedItemsModel = document.getElementById("purchasedItemModel");
    purchasedItemsModel.classList.add("hidden");
});