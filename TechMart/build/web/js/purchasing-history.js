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
    console.log(json);

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

        let invoiceElement = cloneItem.cloneNode(true);
        invoiceElement.querySelector("#col_01").innerHTML = "TM_" + order.id;
        invoiceElement.querySelector("#col_02").innerHTML = formattedDate;
        invoiceElement.querySelector("#col_03").innerHTML = itemCount + " items";
        invoiceElement.querySelector("#col_04").innerHTML = formattedCurrency;
        invoiceElement.querySelector("#col_05").innerHTML = order.status.status;

        const upButton = invoiceElement.querySelector("#ViewPurchase");
        const downButton = invoiceElement.querySelector("#deletePurchase");
        const invoiceButton = invoiceElement.querySelector("#ViewInvoice");

        upButton.addEventListener("click", () => {
            viewPurchasedItems(order.id, json);
        });

        downButton.addEventListener("click", () => {

        });

        invoiceButton.addEventListener("click", () => {
            document.getElementById("invoice_id").innerHTML = "TM_" + order.id;
            document.getElementById("invoice_date_time").innerHTML = formattedDate;
            document.getElementById("invoice_customer").innerHTML = order.user.first_name + " " + order.user.last_name;
            document.getElementById("invoice_address").innerHTML = formattedDate;
            document.getElementById("invoice_city").innerHTML = formattedDate;

            let cloneItem = document.getElementById("invoiceItem");
            document.getElementById("invoiceItemContainer").innerHTML = "";

            let subTotal = 0;
            let deliveryFee = 0;
            json.orderItemList.forEach((orderItem) => {
                if (orderItem.order.id == order.id) {
                    let cloneElement = cloneItem.cloneNode(true);
                    cloneElement.querySelector("#col_01").innerHTML = orderItem.product.id;
                    cloneElement.querySelector("#col_02").innerHTML = orderItem.product.title;
                    cloneElement.querySelector("#col_03").innerHTML = orderItem.product.price;
                    cloneElement.querySelector("#col_04").innerHTML = orderItem.qty;

                    subTotal += orderItem.product.price * orderItem.qty;
                    deliveryFee += orderItem.deliveryFee;

                    document.getElementById("invoiceItemContainer").append(cloneElement);
                }
            });

            document.getElementById("invoice_sub_total").innerHTML = subTotal;
            document.getElementById("invoice_delivery_fee").innerHTML = deliveryFee;
            document.getElementById("invoice_total_amount").innerHTML = subTotal + deliveryFee;

            let invoiceModel = document.getElementById("invoiceModel");
            invoiceModel.classList.remove("hidden");
        });

        document.getElementById("purchasingContainer").append(invoiceElement);
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

// close the invoice model
document.getElementById("closeInvoie").addEventListener("click", () => {
    let invoiceModel = document.getElementById("invoiceModel");
    invoiceModel.classList.add("hidden");
});


document.getElementById('download-pdf').addEventListener('click', async () => {
    const {jsPDF} = window.jspdf;
    const content = document.getElementById('pdf-content');

    // Use html2canvas to capture the HTML content
    const canvas = await html2canvas(content);
    const imgData = canvas.toDataURL('image/png');

    // Create a new jsPDF instance
    const pdf = new jsPDF('p', 'mm', 'a4'); // Portrait, millimeters, A4 size

    // Calculate dimensions to fit content on A4 page
    const pdfWidth = pdf.internal.pageSize.getWidth();
    const pdfHeight = (canvas.height * pdfWidth) / canvas.width;

    // Add image to PDF
    pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);

    // Trigger download
    pdf.save('invoice.pdf');
});