document.getElementById("profile_change_btn").addEventListener("click", () => {
    alert("It's clicked");
});

document.addEventListener("DOMContentLoaded", function () {
    getProfileDetails();
});

async function getProfileDetails() {
    const response = await fetch("Profile?action=get");
    if (response.ok) {
//        console.log(response);
        const json = await response.json();
        document.getElementById("first_name").value = json.user.first_name;
        document.getElementById("last_name").value = json.user.last_name;
        document.getElementById("mobile").value = json.user.mobile;
        document.getElementById("email").value = json.user.email;
        document.getElementById("line1").value = json.address?.line1 || "";
        document.getElementById("line2").value = json.address?.line2 || "";
        document.getElementById("postal_code").value = json.address?.postalCode || "";


        loadSelectItems("districtSelector", json.districtList, ["id", "district"], "default");
//        loadSelectItems("citySelector", json.cityList, ["id", "city"]);

        document.getElementById("districtSelector").addEventListener("change", function () {
            const districtId = this.value;
            loadCitiesByDistrict(districtId, json.cityList);
        });
    } else {
        console.log("Response error!");
    }
}

function loadSelectItems(selector, list, propertyArray, method) {
    const selectorElement = document.getElementById(selector);
    selectorElement.innerHTML = "";

    if (method == "default") {
        let option = document.createElement("option");
        option.classList = "text-black";
        option.innerHTML = "Select";
        option.value = 0;
        selectorElement.appendChild(option);
    }
    list.forEach(item => {
        let option = document.createElement("option");
        option.classList = "text-black";
        option.innerHTML = item[propertyArray[1]];
        option.value = item[propertyArray[0]];
        selectorElement.appendChild(option);
    });
}

function loadCitiesByDistrict(districtId, cityList) {

    const filteredCities = cityList.filter(item => item.district.id == districtId);
    const cityData = filteredCities.map(item => ({
            id: item.id,
            city: item.city
        }));
    console.log(JSON.stringify(cityData));
    loadSelectItems("citySelector", cityData, ["id", "city"]);
}