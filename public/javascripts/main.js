$(document).ready(function () {
    cookiesLoad();
    statusLoad();
});

/**
 * Loads cookies of users
 */
function cookiesLoad() {
    $.ajax({
        url: "cookiesLoad",
        method: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({}),
        success: function (data) {
            //Cookies werden aus der Application.java mittels ajax geladen
            document.getElementById('cookies').innerHTML = "Cookies: " + data.points;
        }
    });
}

/**
 * Loads status of user
 */
function statusLoad() {
    $.ajax({
        url: "statusLoad",
        method: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({}),
        success: function (data) {
            document.getElementById('status').innerHTML = "Berufsstatus: " + data.userStatus;
        }
    })
}