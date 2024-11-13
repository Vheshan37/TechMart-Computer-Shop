document.addEventListener("DOMContentLoaded", function () {
    loadNavigationMenu();
});

function changeTopProductImg(evt) {
    const mainImage = document.getElementById("imgViewer");
    mainImage.src = evt.src;
}


async function loadNavigationMenu() {
    const navigationContainer = document.getElementById("navigationMenu");

    const response = await fetch("components/navigation-menu.html");
    if (response.ok) {
        const navigationMenu = await response.text();
        navigationContainer.innerHTML = navigationMenu;
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