/**
 * Sends entered username and password to createSessionFor to log user in
 */
function einloggen() {
    var username = document.getElementById("name").value;
    var password = document.getElementById("password").value;

    if (username == "" || password == "") {
        window.alert("Bitte beide Felder ausfüllen");
    } else {
        $.ajax({
            url: "createSessionFor",
            method: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "username": username,
                "password": password
            }),
            success: function (data) {
                if (data.wrong == "nothing") {
                    window.location = '/kochbuch';
                } else if (data.wrong == "Passwort") {
                    window.alert("Falsches Passwort");
                } else {
                    window.alert("Falscher Name");
                }

            },
            error: function () {
            }
        });
    }
}

/**
 * Sends entered username and password to register to register new user
 */
function registrieren() {

    var reg_username = document.getElementById("reg_name").value;
    var reg_password = document.getElementById("reg_password").value;
    var reg_pw_repeat = document.getElementById("reg_pw_repeat").value;

    if (reg_username != "" && reg_password != "" && reg_pw_repeat == reg_password) {
        $.ajax({
            url: "register",
            method: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "username": reg_username,
                "password": reg_password
            }),
            success: function (data) {
                if (data.inDatabase) {
                    window.alert("Dieser Name ist schon vorhanden. Bitte wähle einen anderen.");
                } else {
                    window.location = '/gameinstructions';
                }
            },
            error: function () {
                window.alert("nicht erfolgreich");
            }
        });
    } else if (reg_username == "" || reg_password == "") {
        window.alert("Alle Felder ausfüllen!");
    } else {
        window.alert("Zwei mal gleiches Passwort eingeben!");
    }
}