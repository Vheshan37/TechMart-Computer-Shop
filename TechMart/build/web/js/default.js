document.addEventListener("DOMContentLoaded", function () {
    loadDefaultComponents();
});

function changeTopProductImg(evt) {
    const mainImage = document.getElementById("imgViewer");
    mainImage.src = evt.src;
}


async function loadDefaultComponents() {
    const navigationContainer = document.getElementById("navigationMenu");
    const footerContainer = document.getElementById("footerContainer");

    const navigationResponse = await fetch("components/navigation-menu.html");
    if (navigationResponse.ok) {
        const navigationContent = await navigationResponse.text();
        navigationContainer.innerHTML = navigationContent;
    }
    
    const footerResponse = await fetch("components/footer.html");
    if (footerResponse.ok) {
        const footerContent = await footerResponse.text();
        footerContainer.innerHTML = footerContent;
    }
}

function goToCart() {
    window.location = "cart.html";
}

function goToProfile() {
    window.location = "profile.html";
}

function preLoader() {
    const preloader = document.getElementById('preloader');

    setTimeout(() => {
        preloader.classList.add('hidden');
    }, 3000);
}