async function signOut() {
    const response = await fetch("SignOut");
    if (response.ok) {
        window.location.reload();
    }
}