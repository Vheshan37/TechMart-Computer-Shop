document.getElementById("profile_change_btn").addEventListener("click", () => {
    alert("It's clicked");
});

document.addEventListener("DOMContentLoaded", function () {
    getProfileDetails();
});

async function getProfileDetails() {
    const response = await fetch("Profile?action=get");
    if (response.ok) {
        console.log(response);
        const json = await response.json();
    } else {
        console.log("Response error!");
    }
}