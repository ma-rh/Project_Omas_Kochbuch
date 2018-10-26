//Wird beim laden der seite ausgeführt
$(document).ready(function () {

    //Erste Frage die geladen wird beim Aufruf der Quiz Seite.
    quizNext();

    //wird beim click auf den OK button ausgeführt
    $('#submitOK').click(function () {

        //ohne preventDefault werden nach jedem submit click die Werte auf die Standard values des HTML view gesetzt.
        event.preventDefault();

        //Ein radio Button muss gewählt sein
        if (document.getElementById('radioA').checked || document.getElementById('radioB').checked ||
            document.getElementById('radioC').checked || document.getElementById('radioD').checked) {
            //Welcher radio button ist gewählt?
            var answer = document.querySelector('input[name = "radioAnswer"]:checked').value;

            $.ajax({
                url: "quizOK",
                method: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({
                    //Hier wird die gewählte antwort an die Application zur Auswertung geschickt
                    "Answer": answer
                }),
                success: function (data) {
                    //kommt keine richtige Antwort aus der Application.java zurück war die Frage Richtig
                    if (data.rightAnswer == null) {
                        document.getElementById('labelNote').innerHTML = "Richtig !! :)";
                        cookiesLoad();
                    } else {
                        document.getElementById('labelNote').innerHTML = "Leider falsch, richtig wäre Antwort "
                            + data.rightAnswer + " gewesen!";
                    }
                    //Sperrt die Radio und den Submit button bis zur nächsten Frage
                    disableForm();
                }
            });
        }
        else {
            document.getElementById('labelNote').innerHTML = "Wähle eine Antwort!";
        }
    });

    //wird beim click auf den Weiter Button ausgeführt
    $('#submitNext').click(function () {

        //Auswahlmöglichkeiten wieder freigegeben
        undisableForm();
        event.preventDefault();
        quizNext();
    });

    function disableForm() {
        document.getElementById("radioA").disabled = true;
        document.getElementById("radioB").disabled = true;
        document.getElementById("radioC").disabled = true;
        document.getElementById("radioD").disabled = true;
        document.getElementById("submitOK").disabled = true;
    }

    function undisableForm() {
        document.getElementById("radioA").disabled = false;
        document.getElementById("radioB").disabled = false;
        document.getElementById("radioC").disabled = false;
        document.getElementById("radioD").disabled = false;
        document.getElementById("submitOK").disabled = false;
        document.getElementById('radioA').checked = false;
        document.getElementById('radioB').checked = false;
        document.getElementById('radioC').checked = false;
        document.getElementById('radioD').checked = false;
    }

    function quizNext() {
        $.ajax({
            url: "quizNext",
            method: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({}),
            success: function (data) {
                //Frage und Antworttexte werden aus der Application.java mittels ajax geladen
                document.getElementById('labelNote').innerHTML = "";
                document.getElementById('areaQuestion').value = data.Question;
                document.getElementById('labelAnswerA').innerHTML = "A: " + data.AnswerA;
                document.getElementById('labelAnswerB').innerHTML = "B: " + data.AnswerB;
                document.getElementById('labelAnswerC').innerHTML = "C: " + data.AnswerC;
                document.getElementById('labelAnswerD').innerHTML = "D: " + data.AnswerD;
            }
        });
    }


});