function windowOnload(sliderCount) {
    brandSlider();
    loadHomeView(sliderCount);
}

function sliderAction(sliderTrack, buttonAction, id) {
    const {back, next} = latestProductSlider(sliderTrack, 4, "productNext" + id, "productBack" + id, 2);
    if (buttonAction === "left") {
        back();
    } else {
        next();
    }
}


function brandSlider() {
    const sliderTrack = document.getElementById("sliderTrack");
    const slides = Array.from(sliderTrack.children);
    const slideWidth = slides[0].offsetWidth; // Width of one slide
    let currentMargin = 0;
    let lastTime = 0;
    let speed = 6;
    let animationFrameId;

    const animate = (time) => {
        if (!lastTime)
            lastTime = time; // Initialize lastTime on the first call
        const deltaTime = time - lastTime; // Calculate time elapsed
        lastTime = time;

        // Move the slider based on time elapsed
        currentMargin -= (speed * deltaTime) / 100; // Adjust the multiplier for speed
        sliderTrack.style.marginLeft = currentMargin + "px";

        // Check if the first slide has completely exited the view
        if (Math.abs(currentMargin) >= slideWidth) {
            const firstSlide = sliderTrack.firstElementChild;
            sliderTrack.appendChild(firstSlide); // Append to the end
            currentMargin += slideWidth + 10; // Reset margin for a seamless loop
            sliderTrack.style.marginLeft = currentMargin + "px"; // Snap back to the start
        }

        animationFrameId = requestAnimationFrame(animate); // Request the next frame
    };

    // Start the animation
    animationFrameId = requestAnimationFrame(animate);

    // Slow down the animation when mouse is over the slider
    sliderTrack.addEventListener("mouseover", () => {
        speed = 4; // Slow speed
    });

    // Resume normal speed when mouse leaves the slider
    sliderTrack.addEventListener("mouseout", () => {
        speed = 6; // Normal speed
    });
}

function latestProductSlider(sliderID, slideCount, back, next, blurCount) {
    const sliderTrack = document.getElementById(sliderID);
    const slides = Array.from(sliderTrack.children);
    const prevBtn = document.getElementById(back);
    const nextBtn = document.getElementById(next);

    const slidesToShow = slideCount;
    const slideWidth = sliderTrack.offsetWidth / slidesToShow;

    // Set the width of the slides
    slides.forEach(slide => {
        slide.style.minWidth = `${slideWidth}px`;
        makeBlur(1, 2, 3);
    });

    function makeBlur(one, two, three) {
        slides.forEach(slide => {
            slide.style.transition = 'transform 0.5s ease-in-out';
            slide.style.filter = "blur(2px)";
            slide.style.transform = "scale(0.9)";
        });

        const secondElement = sliderTrack.children[one];
        const thirdElement = sliderTrack.children[two];
        const fourthElement = sliderTrack.children[three];

        if (blurCount == 2) {
            secondElement.style.filter = "blur(0px)";
            secondElement.style.transform = "scale(1)";
            thirdElement.style.filter = "blur(0px)";
            thirdElement.style.transform = "scale(1)";
        } else if (blurCount == 3) {
            secondElement.style.filter = "blur(0px)";
            secondElement.style.transform = "scale(1)";
            thirdElement.style.filter = "blur(0px)";
            thirdElement.style.transform = "scale(1)";
            fourthElement.style.filter = "blur(0px)";
            fourthElement.style.transform = "scale(1)";
        }

    }

    // Initialize the current index for tracking visible slides
    let currentIndex = 0;

    function updateSlidePosition() {
        sliderTrack.style.transition = 'transform 0.5s ease-in-out';
        if (currentIndex == 1) {
            sliderTrack.style.transform = `translateX(-${currentIndex * slideWidth}px)`;
            makeBlur(2, 3, 4);
        } else {
            sliderTrack.style.transform = `translateX(+${1 * slideWidth}px)`;
            makeBlur(0, 1, 2);
        }
    }

    function handleSlideTransition(direction) {
        if (direction === "next") {
            const firstSlide = sliderTrack.firstElementChild;
            sliderTrack.appendChild(firstSlide); // Move the first slide to the end
            currentIndex--; // Adjust index since we moved a slide
        } else if (direction === "prev") {
            const lastSlide = sliderTrack.lastElementChild;
            sliderTrack.insertBefore(lastSlide, sliderTrack.firstElementChild); // Move the last slide to the start
            currentIndex++; // Adjust index since we moved a slide
        }

        sliderTrack.style.transition = 'none'; // Disable transition for instant position change
        sliderTrack.style.transform = `translateX(-${currentIndex * slideWidth}px)`; // Correct the position
        setTimeout(() => {
            sliderTrack.style.transition = 'transform 0.5s ease-in-out'; // Re-enable the transition
        }, 0);

        nextBtn.disabled = false;
        prevBtn.disabled = false;
    }

    function backClick() {
        nextBtn.disabled = true;
        prevBtn.disabled = true;
        currentIndex--;
        updateSlidePosition();
        sliderTrack.addEventListener('transitionend', () => handleSlideTransition("prev"), {once: true});
    }

    function nextClick() {
        nextBtn.disabled = true;
        prevBtn.disabled = true;
        currentIndex++;
        updateSlidePosition();
        sliderTrack.addEventListener('transitionend', () => handleSlideTransition("next"), {once: true});
    }

    return {
        back: backClick,
        next: nextClick
    };
}

async function loadHomeView(sliderCount) {
    const response = await fetch("LoadHomeView");
    if (response.ok) {
        const json = await response.json();
        const latestProduct = json.latestProduct;
        const images = json.imageList;
        const productList = json.ProductList;
        let imageCount = 1;
        let imageClone = document.getElementById("productImageSlide");
        let latestProductFeatures = document.getElementById("latestProductFeatures");
        let productSlideClone = document.getElementById("productSlide");

        document.getElementById("latestTitle").innerHTML = latestProduct.title;
        document.getElementById("latestPrice").innerHTML = "LKR. " + latestProduct.price.toLocaleString('en-US') + ".00";

        document.getElementById("productImageContainer").innerHTML = "";
        document.getElementById("productSliderTrack1").innerHTML = "";
        latestProductFeatures.innerHTML = "";

        images.forEach(item => {
            let cloneElement = imageClone.cloneNode(true);

            cloneElement.src = "img/Product/" + item;
            document.getElementById("imgViewer").src = "img/Product/" + latestProduct.title + "_(" + latestProduct.id + ")_(1).jpg";

            document.getElementById("productImageContainer").appendChild(cloneElement);
            imageCount++;
        });

        json.latestFeatureList.forEach(featureItem => {
            let li = document.createElement("li");
            let span1 = document.createElement("span");
            let span2 = document.createElement("span");
            span1.innerHTML = featureItem.feature.feature + ": ";
            span2.innerHTML = featureItem.value;
            span1.classList.add("font-semibold", "text-base");
            span2.classList.add("text-sm");
            li.appendChild(span1);
            li.appendChild(span2);
            latestProductFeatures.appendChild(li);
        });

        productList.forEach(productItem => {
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

            document.getElementById("productSliderTrack1").append(cloneElement);
        });

        for (let index = 1; index <= parseInt(sliderCount); index++) {
            latestProductSlider("productSliderTrack" + index, 4, "productNext" + index, "productBack" + index, 2);
        }

        preLoader();

    }
}

//function preLoader() {
//    const preloader = document.getElementById('preloader');
//
//    setTimeout(() => {
//        preloader.classList.add('hidden');
//    }, 3000);
//}