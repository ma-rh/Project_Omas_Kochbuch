var currentWare = "";

$(document).ready(function () {

    /**
     * Ingredient is visible in supermarket, if user can buy ingredient
     * Ingredient is hidden in supermarket, if user already owns ingredient and can not buy it
     */
    $('.zutat').each(function (index, item) {

        var zutat = item.id;
        var wareprice = "";

        $.ajax({
            url: "userCanBuyIngredient",
            method: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "zutat": zutat,
                "wareprice": wareprice
            }),
            success: function (data) {
                if (data.canBuy) {
                    item.style.visibility = 'visible';
                    document.getElementsByClassName("wareprice")[index].innerHTML = "Preis: " + data.price + " Cookie";
                } else {
                    item.style.visibility = 'hidden';
                }
            },
            error: function () {
            }
        })

    });

});

/**
 * When checkbox of a ware is clicked, id is safed as current ware
 * @param id
 */
function getWare(id) {
    currentWare = id;
}

/**
 * Current ware is bought from supermarket
 * If user does not have enough cookies, a window alert appears
 * If user has enough cookies, the ware is added to his list and the ware is hidden in supermarket
 */
function buyFromSupermarket() {
    $.ajax({
        url: "supermarketBuy",
        method: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            "currentWare": currentWare
        }),
        success: function (data) {
            if (data.canBuy) {
                cookiesLoad();
                document.getElementById(currentWare).style.visibility = 'hidden';

            } else {
                window.alert("Du besitzt nicht genügend Cookies!");
            }
        }
    });
}