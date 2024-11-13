const scrollContainer = document.querySelector('.x-wheel-scroll');

scrollContainer.addEventListener('wheel', (e) => {
    e.preventDefault();
    scrollContainer.scrollLeft += e.deltaY;
});