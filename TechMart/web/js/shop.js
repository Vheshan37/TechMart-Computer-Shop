function shopOnload() {
    loadShopView("");
}

function doSearch() {
    const searchInput = document.getElementById("searchInput").value;
    loadShopView(searchInput);
}

async function loadShopView(text) {
    const response = await fetch("LoadShopView?q=" + text);

    if (response.ok) {
        const json = await response.json();


        let productSlideClone = document.getElementById("productSlide");
        document.getElementById("productContainer").innerHTML = "";

        json.ProductList.forEach(productItem => {
            let cloneElement = productSlideClone.cloneNode(true);
            cloneElement.querySelector("#blurBackground").style.backgroundImage = "url('img/Product/" + productItem.title + "_(" + productItem.id + ")_(1).jpg')";
            cloneElement.querySelector("#productImage").src = "img/Product/" + productItem.title + "_(" + productItem.id + ")_(1).jpg";
            cloneElement.querySelector("#productPrice").innerHTML = "LKR. " + productItem.price.toLocaleString('en-US') + ".00";
            cloneElement.querySelector("#productTitle").innerHTML = productItem.title;
            cloneElement.querySelector("#productTitle").title = productItem.title;

            cloneElement.querySelector("#addToCart").addEventListener("click", async () => {
                const cartResponse = await fetch("AddToCart?id=" + productItem.id);

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

                if (cartResponse.ok) {
                    const cartJson = await cartResponse.json();
                    if (cartJson.success) {
                        Toast.fire({
                            timer: 2000,
                            icon: "success",
                            title: cartJson.content
                        });
                    } else {
                        Toast.fire({
                            timer: 4000,
                            icon: "info",
                            title: cartJson.content
                        });
                    }
                } else {
                    Toast.fire({
                        timer: 4000,
                        icon: "error",
                        title: "Something went wrong! Please try again later"
                    });
                }
            });

            cloneElement.querySelector("#productRead").onclick = function () {
                window.location = "single-product.html?id=" + productItem.id;
            }

            document.getElementById("productContainer").append(cloneElement);
        });

        preLoader();

    } else {
        console.log("Response Error");
    }
}