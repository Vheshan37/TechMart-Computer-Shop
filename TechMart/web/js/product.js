const scrollContainer = document.querySelector('.x-wheel-scroll');

scrollContainer.addEventListener('wheel', (e) => {
    e.preventDefault();
    scrollContainer.scrollLeft += e.deltaY;
});

function SpcDetailAndReviews(direction) {
    if (direction === "left") {
        document.getElementById("detailsAndReviews").style.transform = "translateX(0%)";
    } else if (direction === "right") {
        document.getElementById("detailsAndReviews").style.transform = "translateX(-100%)";
    } else {
        document.getElementById("detailsAndReviews").style.transform = "translateX(0%)";
    }
}

function FaqAction(faqElement) {
    const bodyElement = faqElement.querySelector('.body');
    const iconElement = faqElement.querySelector('.icon');

    if (parseInt(getComputedStyle(bodyElement).height) != 0) {
        bodyElement.style.height = 0 + "px";
        bodyElement.style.overflow = "hidden";
        iconElement.icon = "mingcute:down-line";
    } else {
        bodyElement.style.height = bodyElement.scrollHeight + "px";
        bodyElement.style.overflow = "visible";
        iconElement.icon = "mingcute:up-line";
    }
}

async function singleProductLoad() {

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

    const parameters = new URLSearchParams(window.location.search);
    const response = await fetch("ProductLoad?id=" + parameters.get("id"));
    if (response.ok) {
        const json = await response.json();


        const images = json.imageList;
        let imageCount = 1;


        let imageClone = document.getElementById("productImageSlide");
        document.getElementById("productImageContainer").innerHTML = "";

        images.forEach(item => {
            let cloneElement = imageClone.cloneNode(true);

            cloneElement.querySelector("#imageSlide").src = "img/Product/" + item;
            document.getElementById("imgViewer").src = "img/Product/" + json.title + "_(" + json.id + ")_(1).jpg";

            document.getElementById("productImageContainer").appendChild(cloneElement);
            imageCount++;
        });

        document.getElementById("breadCrumbTitle").innerHTML = json.title;
        document.getElementById("productTitle").innerHTML = json.title;
        document.getElementById("productPrice").innerHTML = "LKR. " + json.price.toLocaleString('en-US') + ".00";
        document.getElementById("productStock").innerHTML = json.stock + " items available";

        let similarClone = document.getElementById("similarProduct");
        document.getElementById("similarProductContainer").innerHTML = "";

        if (json.similarProductList.length == 0) {
            document.getElementById("similerProductSection").style.display = "none";
        } else {
            json.similarProductList.forEach(similarItem => {
                let cloneElement = similarClone.cloneNode(true);
                cloneElement.querySelector("#blurImage").style.backgroundImage = "url('img/Product/" + similarItem.title + "_(" + similarItem.id + ")_(1).jpg')";
                cloneElement.querySelector("#similarImg").src = "img/Product/" + similarItem.title + "_(" + similarItem.id + ")_(1).jpg";
                cloneElement.querySelector("#similarTitle").innerHTML = similarItem.title;
                cloneElement.querySelector("#similarPrice").innerHTML = "LKR. " + similarItem.price.toLocaleString('en-US') + ".00";
                cloneElement.querySelector("#similarStock").innerHTML = similarItem.quantity + " items available";
                cloneElement.querySelector("#similarLink").href = "?id=" + similarItem.id;

                document.getElementById("similarProductContainer").appendChild(cloneElement);
            });
        }


        let featureClone = document.getElementById("featureClone");
        document.getElementById("featureListContainer").innerHTML = "";

        json.featureList.forEach(featureItem => {
            let cloneElement = featureClone.cloneNode(true);

            let feature = featureItem.feature;
            let value = featureItem.value;

            cloneElement.querySelector("#feature").innerHTML = feature;
            cloneElement.querySelector("#value").innerHTML = value;

            document.getElementById("featureListContainer").appendChild(cloneElement);
        });

        preLoader();

    } else {
        console.log("Response Error");
    }

}