async function signIn() {
    const data = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

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

    if (response.ok) {
        const json = await response.json();
        if (json.success) {
            if (json.content == "verification_method") {
                document.getElementById("verificationModel").classList.remove("hidden");
            } else {
                Toast.fire({
                    timer: 3000,
                    icon: "success",
                    title: "Login Success"
                }).then(() => {
                    window.location = "index.html";
                });
            }
        } else {
            Toast.fire({
                timer: 5000,
                icon: "warning",
                title: json.content
            });
        }
    } else {
        Toast.fire({
            timer: 5000,
            icon: "warning",
            title: "Response Error. Please try again later"
        });
    }

}

function verificationClose() {
    document.getElementById("verificationModel").classList.add("hidden");
}

async function verifyAccount() {

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

    const verification = document.getElementById("verificationInput").value;
    const response = await fetch("AccountVerification?code=" + verification);
    if (response.ok) {
        const json = await response.json();
        if (json.success) {
            window.location = "index.html";
        } else {
            Toast.fire({
                timer: 5000,
                icon: "warning",
                title: json.content
            });
        }
    }
}