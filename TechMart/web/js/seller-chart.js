const ctx1 = document.getElementById('myChart1');

new Chart(ctx1, {
    type: 'line',
    data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
        datasets: [
            {
                label: 'Sale',
                data: [8, 25, 7, 15, 22, 13, 15, 14, 5, 7, 5, 4],
                borderWidth: 1,
                backgroundColor: 'rgba(0, 51, 111, 0.75)',
                borderColor: 'rgba(0, 50, 255, 1)',
            },
            {
                label: 'Revenue',
                data: [12, 19, 3, 5, 2, 3, 7, 5, 4, 8, 5, 2],
                borderWidth: 1,
                backgroundColor: 'rgba(0, 85, 184, 0.75)',
                borderColor: 'rgba(0, 118, 255, 1)',
            }
        ]
    },
    options: {
        scales: {
            y: {
                beginAtZero: true
            }
        }, animations: {
            tension: {
                duration: 1000,
                easing: 'linear',
                from: 1,
                to: 0,
                loop: false
            }
        }
    }
});

const ctx2 = document.getElementById('myChart2');

new Chart(ctx2, {
    type: 'doughnut',
    data: {
        labels: ['All Clients', 'My Clients'],
        datasets: [
            {
                label: 'Count',
                data: [12, 19],
                borderWidth: 1,
                backgroundColor: [
                    'rgba(0, 51, 111, 0.75)',
                    'rgba(0, 85, 184, 0.75)'
                ],
                borderColor: [
                    'rgba(0, 118, 255, 1)',
                    'rgba(0, 118, 255, 1)'
                ],
            }
        ]
    },
    options: {
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});

const ctx3 = document.getElementById('myChart3');

new Chart(ctx3, {
    type: 'bar',
    data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
        datasets: [
            {
                label: 'Purchases',
                data: [8, 25, 7, 15, 22, 13, 15, 14, 5, 7, 5, 4],
                borderWidth: 1,
                backgroundColor: 'rgba(0, 51, 111, 0.75)',
            }, {
                label: 'Product',
                data: [12, 19, 3, 5, 2, 3, 7, 5, 4, 8, 5, 2],
                borderWidth: 1,
                backgroundColor: 'rgba(0, 85, 184, 0.75)',
            }
        ]
    },
    options: {
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});