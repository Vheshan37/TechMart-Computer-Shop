async function signUp() {

    const data = {
        first_name: document.getElementById("first_name").value,
        last_name: document.getElementById("last_name").value,
        mobile: document.getElementById("mobile").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

    const response = await fetch(
            "SignUp",
            {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();

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

        if (json.success) {
            Toast.fire({
                timer: 3000,
                icon: "success",
                title: "Registration completed"
            }).then(() => {
                window.location = "sign-in.html";
            });
        } else {
            Toast.fire({
                timer: 5000,
                icon: "warning",
                title: json.content
            });
        }
    } else {
        console.log("Response Error");
    }
}