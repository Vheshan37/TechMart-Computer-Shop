async function cartOnLoad() {

    const response = await fetch("CartLoad");

    if (response.ok) {
        const json = await response.json();

        const cloneItem = document.getElementById("cartCloneItem");
        document.getElementById("cartItemContainer").innerHTML = "";

        if (json.success) {

            const checkoutTotal = document.getElementById("cartTotal");
            let cartTotal = 0;
            json.content.cartList.forEach(cartItem => {
                let cloneElement = cloneItem.cloneNode(true);
                cloneElement.querySelector("#cartBlurImage").src = "img/Product/" + cartItem.product.title + "_(" + cartItem.product.id + ")_(1).jpg";
                cloneElement.querySelector("#cartImage").src = "img/Product/" + cartItem.product.title + "_(" + cartItem.product.id + ")_(1).jpg";
                cloneElement.querySelector("#cartTitle").innerHTML = cartItem.product.title;
                cloneElement.querySelector("#cartPrice").innerHTML = "LKR. " + cartItem.product.price.toLocaleString('en-US') + ".00";
                cloneElement.querySelector("#quantityInput").value = cartItem.quantity;


                let itemSubTotal = parseInt(cartItem.product.price) * parseInt(cartItem.quantity);
                cartTotal += parseInt(itemSubTotal);
                checkoutTotal.innerHTML = "LKR. " + cartTotal.toLocaleString('en-US') + ".00";

                cloneElement.querySelector("#itemSubTotal").innerHTML = "LKR. " + parseInt(itemSubTotal).toLocaleString('en-US') + ".00";

                cloneElement.querySelector("#buyNow").addEventListener("click", () => {
                    alert(cartTotal);
                });

                // Quantity Decrease
                cloneElement.querySelector("#decreaseBtn").addEventListener("click", () => {
                    const quantityInput = cloneElement.querySelector("#quantityInput");
                    let currentValue = parseInt(quantityInput.value);
                    if (currentValue > 1) {
                        currentValue--;
                        quantityInput.value = currentValue;

                        cartTotal -= parseInt(cartItem.product.price);
                        checkoutTotal.innerHTML = "LKR. " + cartTotal.toLocaleString('en-US') + ".00";

                        let currentTotal = parseInt(cartItem.product.price) * parseInt(currentValue);
                        cloneElement.querySelector("#itemSubTotal").innerHTML = "LKR. " + parseInt(currentTotal).toLocaleString('en-US') + ".00";
                    }
                });

                // Quantity Increase
                cloneElement.querySelector("#increaseBtn").addEventListener("click", () => {
                    const quantityInput = cloneElement.querySelector("#quantityInput");
                    let currentValue = parseInt(quantityInput.value);
                    if (currentValue < cartItem.product.quantity) {
                        currentValue++;
                        quantityInput.value = currentValue;

                        cartTotal += parseInt(cartItem.product.price);
                        checkoutTotal.innerHTML = "LKR. " + cartTotal.toLocaleString('en-US') + ".00";

                        let currentTotal = parseInt(cartItem.product.price) * parseInt(currentValue);
                        cloneElement.querySelector("#itemSubTotal").innerHTML = "LKR. " + parseInt(currentTotal).toLocaleString('en-US') + ".00";
                    }
                });

                // Remove items from cart
                cloneElement.querySelector("#cartRemove").addEventListener("click", async () => {
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


                    const remove = await fetch("RemoveCart?id=" + (cartItem.id ?? cartItem.product.id));
                    if (remove.ok) {
                        const removeJson = await remove.json();

                        if (removeJson.success) {
                            Toast.fire({
                                timer: 2000,
                                icon: "success",
                                title: removeJson.content
                            });

                            removeCartItem();

                            checkCartView();

                        } else {
                            Toast.fire({
                                timer: 4000,
                                icon: "warning",
                                title: json.content
                            });
                        }
                    } else {
                        Toast.fire({
                            timer: 4000,
                            icon: "error",
                            title: "Something went wrong! Please try again later."
                        });
                    }
                });

                document.getElementById("cartItemContainer").appendChild(cloneElement);

                function removeCartItem() {
                    const quantityInput = cloneElement.querySelector("#quantityInput");
                    let itemSubTotal = parseInt(cartItem.product.price) * parseInt(quantityInput.value);
                    cartTotal -= itemSubTotal;
                    checkoutTotal.innerHTML = "LKR. " + parseInt(cartTotal).toLocaleString('en-US') + ".00";

                    document.getElementById("cartItemContainer").removeChild(cloneElement);
                }

            });

        } else {
            checkCartView();
        }

        preLoader();
    }

}

function checkCartView() {
    if (document.getElementById("cartItemContainer").childElementCount === 0) {
        document.getElementById("emptyCart").classList.remove("hidden");
        document.getElementById("mainContent").classList.add("hidden");
    }
}