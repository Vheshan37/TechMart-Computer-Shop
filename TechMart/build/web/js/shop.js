function shopOnload() {
    doSearch(true);
    productSlideClone = document.getElementById("productSlide");
}

async function doSearch(condition) {
    let searchInput = "";
    if (condition) {
        searchInput = "";
    } else {
        searchInput = document.getElementById("searchInput").value;
    }

    const response = await fetch("LoadShopView?q=" + searchInput);
    if (response.ok) {
        const json = await response.json();
        loadCategoryList("categorySelect", json.categoryList, ["id", "category"]);
        loadShopView(json);
    } else {
        console.log("response error: " + response.status);
    }
}

async function advancedSearch() {
    const category = document.getElementById("categorySelect").value;
    const minPrice = document.getElementById("minPrice").value;
    const maxPrice = document.getElementById("maxPrice").value;

    const formData = new FormData();
    formData.append("category", category);
    formData.append("minPrice", minPrice);
    formData.append("maxPrice", maxPrice);

    const response = await fetch("LoadAdvanceSearch", {
        method: "POST",
        body: formData
    });

    if (response.ok) {
        const json = await response.json();
        loadShopView(json);
    } else {
        console.log("response error: " + response.status);
    }
}

function loadCategoryList(selector, list, propertyArray) {
    const selectorElement = document.getElementById(selector);
    selectorElement.innerHTML = "";
    let option = document.createElement("option");
    option.classList = "text-black";
    option.innerHTML = "All Category";
    option.value = "";
    selectorElement.appendChild(option);
    list.forEach(item => {
        let option = document.createElement("option");
        option.classList = "text-black";
        option.innerHTML = item[propertyArray[1]];
        option.value = item[propertyArray[0]];
        selectorElement.appendChild(option);
    });
}

let productSlideClone;
async function loadShopView(json) {
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
}