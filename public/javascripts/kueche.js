var step = 0;
var currentAction = "noAction";
var currentIngredient = "";

$(document).ready(function () {
    /**
     * Ware is visible in kitchen, if user owns ware
     * Ware is hidden in kitchen, if user does not own ware
     */
    $('.ware').each(function (index, item) {
        var ware = item.id;
        $.ajax({
            url: "userOwnsIngredient",
            method: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "warename": ware
            }),
            success: function (data) {
                if (data.userOwnsWare) {
                    item.style.visibility = 'visible';
                } else {
                    item.style.visibility = 'hidden';
                }
            },
            error: function () {
            }
        })

    });

    /**
     * Warename is shown when mouse is on ware
     */
    $('.ware').hover(function () {
        var flavor_text;
        var ware = $(this).attr('id');

        flavor_text = ware.toString();

        $(this).removeAttr('title');
        $('<p class="tooltip"></p>').html(flavor_text).appendTo('body').fadeIn("slow");
        $(this).css({"opacity": "1"});
    }, function () {

        $('.tooltip').remove();
        $(this).css({"opacity": "0.9"});
    }).mousemove(function (e) {
        var mousex = e.pageX + 20;  //Get X coordinates
        var mousey = e.pageY + 20; //Get Y coordinates
        $('.tooltip')
            .css({top: mousey, left: mousex})
    });


});

/**
 * Safes the selected action
 * @param id
 */
function getAction(id) {
    currentAction = id;
}

/**
 * When ware is dropped on "Arbeitsfläche",
 * cookRecipe tests if the user selected right action and ingredient for the current step
 */
function ablegen(ev) {
    ev.preventDefault();
    currentIngredient = ev.dataTransfer.getData('text');
    ev.target.appendChild(document.getElementById(currentIngredient));
    if (currentAction == "noAction") {
        document.getElementById("sprechblase").innerHTML = "Bitte wähle links eine Aktion aus";
    } else {
        $.ajax({
            url: "cookRecipe",
            method: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "step": step,
                "currentAction": currentAction,
                "currentIngredient": currentIngredient
            }),
            error: function (req, error) {
                console.log(req, error);
            },
            success: function (data) {
                document.getElementById(currentAction).checked = false;
                currentAction = "noAction";
                step = data.newStep;
                if (data.rightAction) {
                    document.getElementById("sprechblase").innerHTML = "Super, das war richtig! Wähle nun die nächste Aktion und Zutat aus.";
                    if (data.newStep == 0) {
                        cookiesLoad();
                        document.getElementById("sprechblase").innerHTML = "Herzlichen Glückwunsch, du hast das Rezept richtig zubereitet! Dafür bekommst du 5 Cookies :)";
                    }
                } else {
                    document.getElementById("sprechblase").innerHTML = "Das war leider falsch. Vielleicht wirfst du lieber nochmal einen Blick in das Kochbuch und beginnst von vorne.";
                }
            }
        });
    }
}

/**
 * For drag and drop
 */
function ziehen(ev) {
    ev.dataTransfer.setData('text', ev.target.id);
}

function ablegenErlauben(ev) {
    ev.preventDefault();
}