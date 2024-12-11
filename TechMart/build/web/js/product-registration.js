function loadProductImagetoPreview() {
    const input = document.getElementById('imageUploader');
    const files = input.files;
    const previewImages = document.querySelectorAll('#imagePreviewGrid img');

    for (let i = 0; i < files.length && i < previewImages.length; i++) {
        const reader = new FileReader();
        reader.onload = function (e) {
            previewImages[i].src = e.target.result;
        }
        reader.readAsDataURL(files[i]);
    }
}

document.addEventListener("DOMContentLoaded", function () {
    loadProductFeatures();
});

function loadSelectItems(selector, list, propertyArray) {
    const selectorElement = document.getElementById(selector);
    selectorElement.innerHTML = "";
    list.forEach(item => {
        let option = document.createElement("option");
        option.classList = "text-black";
        option.innerHTML = item[propertyArray[1]];
        option.value = item[propertyArray[0]];
        selectorElement.appendChild(option);
    });
}

function loadFeaturesByCategory(categoryId, categoryFeatureList) {
    const filteredFeatures = categoryFeatureList.filter(item => item.category.id == categoryId);
    const featureList = filteredFeatures.map(item => item.feature);
    loadSelectItems("featureSelector", featureList, ["id", "feature"]);
}

function loadBrandsByCategory(categoryId, categoryBrandList) {
    const filteredbrands = categoryBrandList.filter(item => item.category.id == categoryId);
    const brandList = filteredbrands.map(item => item.brand);
    loadSelectItems("brandSelector", brandList, ["id", "brand"]);
}

async function loadProductFeatures() {

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

    const response = await fetch("LoadRegistrationFeature");

    if (response.ok) {
        const json = await response.json();
        loadSelectItems("categorySelector", json.categoryList, ["id", "category"]);
        loadSelectItems("brandSelector", json.brandList, ["id", "brand"]);
        loadSelectItems("colorSelector", json.colorList, ["id", "color"]);
        loadSelectItems("districtSelector", json.districtList, ["id", "district"]);


        document.getElementById("categorySelector").addEventListener("change", function () {
            const selectedCategoryId = this.value;
            loadFeaturesByCategory(selectedCategoryId, json.categoryFeatureList);
        });
        
        document.getElementById("categorySelector").addEventListener("change", function () {
            const selectedCategoryId = this.value;
            loadBrandsByCategory(selectedCategoryId, json.categoryBrandList);
        });

    } else {
        Toast.fire({
            timer: 5000,
            icon: "warning",
            title: "Something went wrong! Please reload the page"
        });
    }
    
    preLoader();
}

window.onload = function () {
    loadProductFeatures();
};

var featureArray = [];
function addFeature() {

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

    const featureSelector = document.getElementById('featureSelector');
    const selectedFeature = featureSelector.options[featureSelector.selectedIndex].text;

    const featureValueInput = document.getElementById("featureValue");
    const featureValue = featureValueInput.value;

    if (selectedFeature !== "Select" && featureValue !== "") {
        const existingFeature = this.featureArray.find(feature => feature.id == featureSelector.value);

        if (!existingFeature) {

            const tableBody = document.querySelector('tbody');
            const newRow = document.createElement('tr');

            const featureCell = document.createElement('td');
            featureCell.className = "p-2 border border-slate-600";
            featureCell.textContent = selectedFeature;
            newRow.appendChild(featureCell);

            const valueCell = document.createElement('td');
            valueCell.className = "p-2 border border-slate-600";
            valueCell.textContent = featureValue;
            newRow.appendChild(valueCell);

            tableBody.appendChild(newRow);

            const featureItem = {
                id: featureSelector.value,
                value: featureValue
            };
            this.featureArray.push(featureItem);

            featureSelector.selectedIndex = 0;
            featureValueInput.value = ""; // Reset feature value input field
            document.getElementById("categorySelector").disabled = true;
        } else {
            Toast.fire({
                timer: 5000,
                icon: "warning",
                title: "This feature is already added"
            });

        }
    } else {
        Toast.fire({
            timer: 5000,
            icon: "warning",
            title: "Please select a feature and enter a value."
        });
    }
}


async function productRegistration() {

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

    const formData = new FormData();
    formData.append("category", document.getElementById('categorySelector').value);
    formData.append("brand", document.getElementById('brandSelector').value);
    formData.append("color", document.getElementById('colorSelector').value);
    formData.append("title", document.getElementById('productTitle').value);
    formData.append("qty", document.getElementById('productQty').value);
    formData.append("price", document.getElementById('productPrice').value);
    formData.append("deliveryCity", document.getElementById('districtSelector').value);
    formData.append("deliveryIn", document.getElementById('deliveryIn').value);
    formData.append("deliveryOut", document.getElementById('deliveryOut').value);
    formData.append("featureList", JSON.stringify(this.featureArray));

    const imageFiles = document.getElementById('imageUploader').files;
    for (let i = 0; i < imageFiles.length; i++) {
        formData.append('images', imageFiles[i]);
    }

    const response = await fetch(
            "ProductRegistration",
            {
                method: "POST",
                body: formData
            }
    );

    if (response.ok) {
        const json = await response.json();
        if (json.success) {
            Toast.fire({
                timer: 2000,
                icon: "success",
                title: json.content
            }).then(() => {
                window.location.reload();
            });
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
            title: "Something went wrong! Please try again later"
        });
    }
}