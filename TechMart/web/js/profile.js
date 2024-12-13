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
        document.getElementById("first_name").value = json.user.first_name;
        document.getElementById("last_name").value = json.user.last_name;
        document.getElementById("mobile").value = json.user.mobile;
        document.getElementById("email").value = json.user.email;
        document.getElementById("line1").value = json.address.line1;
        document.getElementById("line2").value = json.address.line2;
        document.getElementById("postal_code").value = json.address.postalCode;
    } else {
        console.log("Response error!");
    }
}