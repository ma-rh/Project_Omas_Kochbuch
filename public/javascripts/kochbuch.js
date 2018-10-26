$(document).ready(function () {
    /**
     * Loads all recipe names for table of contents of the cookbook
     */
    $.ajax({
        url: "recipeNamesForCookbook",
        method: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({}),
        success: function (result) {
            for (var i = 0; i < 9; i++) {
                if (i < result.length) {
                    document.getElementById("rezept" + i).innerHTML = result[i];
                } else {
                    document.getElementById("rezept" + i).innerHTML = "";
                }
            }
        }
    });
});

/**
 * When user selects one recipe, the instruction and link are loaded
 * @param idRecipe: id of recipe
 */
function recipeForCookbook(idRecipe) {
    $.ajax({
        url: "recipeForCookbook",
        method: "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            "recipeId": idRecipe
        }),
        success: function (result) {
            document.getElementById("headline").innerHTML = result.recipeName;
            document.getElementById("instruction").innerHTML = result.instruction;
            document.getElementById("link").innerHTML = result.linktext;
            document.getElementById("link").href = result.linkHref;
        }
    });
}