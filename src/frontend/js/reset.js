// Reinicia a página ao seu estado inicial.
function reset(){
    $('#results').text("");
    $('#searchbar').val("");
    $('#searchbar').blur();
    $('#results').css('top', '60%');
    $('#searchdiv').css('top', '50%');
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
    }
}

