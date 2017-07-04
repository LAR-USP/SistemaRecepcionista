// Reinicia a página ao seu estado inicial.
function reset(){
    $('#results').text("");
    $('#searchbar').val("");
    $('#searchbar').blur();
    $('#results').css('top', '40%');
    $('#searchdiv').css('top', '28%');
    
    if(acesso == 1){
        $("#laraface").fadeOut(function(){
            $("#laraface").css('height', '22.2%');
            $("#laraface").css('width', '11.2%');
            $("#laraface").css('position', 'relative');
            $("#laraface").css('top', '25%');
        });
        $("#laraface").fadeIn();
        acesso = 0;
    }

    entity = "";
    properties = "";
	if(recognition !== null){
        recognition.stop();
        recognition = null;
    }
    idle_time = 0;
}

var IDLE_MAX = 60; // Segundos.
var idle_time = 0; // Segundos.

$(window).on('click mousemove keypress input', function(){
    idle_time = 0;
});

window.setInterval(checkTime, 1000);

function checkTime(){
    ++idle_time;
    if(idle_time >= IDLE_MAX){
        reset();
        idle_time = 0;
    }
}

