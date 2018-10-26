var memory_array = ['reis', 'reis', 'Kuerbis', 'Kuerbis', 'Nudeln-Penne', 'Nudeln-Penne',
    'salat', 'salat', 'Tomate', 'Tomate', 'putenstreifen', 'putenstreifen', 'hackfleischbulette', 'hackfleischbulette',
    'hamburgerbroetchen', 'hamburgerbroetchen'];
// Array with values of currently flipped tiles
var memory_values = [];
// Array with ids of currently flipped tiles
var memory_tile_ids = [];
var tiles_flipped = 0;

/**
 * Shuffles memory array
 */
Array.prototype.memory_tile_shuffle = function () {
    var i = this.length, j, temp;
    while (--i > 0) {
        j = Math.floor(Math.random() * (i + 1));
        temp = this[j];
        this[j] = this[i];
        this[i] = temp;
    }
};

/**
 * Generates new Memory Board
 */
function newBoard() {
    tiles_flipped = 0;
    var output = '';
    memory_array.memory_tile_shuffle();
    for (var i = 0; i < memory_array.length; i++) {
        output += '<div id="tile_' + i + '"onclick="memoryFlipTile(this,\'' + memory_array[i] + '\')"></div>';
    }
    document.getElementById('memory_board').innerHTML = output;
}
//tile: card, val: value of card
/**
 * When tile is flipped, tile background is changed
 * @param tile
 * @param val
 */
function memoryFlipTile(tile, val) {
    if (memory_values.length < 2) {
        tile.style.background = 'url(../assets/images/' + val + '.jpg)';
        tile.style.backgroundSize = "100% 100%";
        if (memory_values.length == 0) {
            memory_values.push(val);
            memory_tile_ids.push(tile.id);
        } else if (memory_values.length == 1) {
            memory_values.push(val);
            memory_tile_ids.push(tile.id);
            if (memory_values[0] == memory_values[1]) {
                tiles_flipped += 2;
                //Clear both arrays
                memory_values = [];
                memory_tile_ids = [];
                // Check to see if the whole board is cleared
                if (tiles_flipped == memory_array.length) {
                    alert("Herzlichen Glückwunsch, du hast das ganze Memory gelöst! Dafür bekommst du 1 Cookie :)");
                    document.getElementById('memory_board').innerHTML = "";

                    $.ajax({
                        url: "userWonMemory",
                        method: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify({}),
                        success: function (data) {
                            cookiesLoad();
                        },
                        error: function () {

                        }
                    });

                    newBoard();
                }
            } else {
                // Flip tiles over if they don't match
                function flip2Back() {
                    //Flip the 2 tiles back over
                    var tile_1 = document.getElementById(memory_tile_ids[0]);
                    var tile_2 = document.getElementById(memory_tile_ids[1]);
                    tile_1.style.background = 'url(../assets/images/cookies.jpg)';
                    tile_1.style.backgroundSize = "50% 50%";
                    tile_2.style.background = 'url(../assets/images/cookies.jpg)';
                    tile_2.style.backgroundSize = "50% 50%";
                    // Clear both arrays
                    memory_values = [];
                    memory_tile_ids = [];
                }

                //After timeout tile is flipped back
                setTimeout(flip2Back, 1000);
            }
        }
    }

}