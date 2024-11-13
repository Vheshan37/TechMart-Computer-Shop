async function loadProductHome() {
    const response = await fetch("LoadProductHome");

    if (response.ok) {
        const json = await response.json();


        let productSlideClone = document.getElementById("product");
        document.getElementById("ProductContainer").innerHTML = "";

        json.ProductList.forEach(productItem => {
            let cloneElement = productSlideClone.cloneNode(true);
            cloneElement.querySelector("#blurBackground").style.backgroundImage = "url('img/Product/" + productItem.title + "_(" + productItem.id + ")_(1).jpg')";
            cloneElement.querySelector("#productImage").src = "img/Product/" + productItem.title + "_(" + productItem.id + ")_(1).jpg";
            cloneElement.querySelector("#productTitle").innerHTML = productItem.title;

            cloneElement.querySelector("#manageProductBtn").onclick = function () {
                window.location = "product-management.html?id=" + productItem.id;
            }

            document.getElementById("ProductContainer").append(cloneElement);
        });

        preLoader();

    } else {
        console.log("Response Error");
    }
}