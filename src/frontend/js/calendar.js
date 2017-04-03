$(document).ready(function(){
    $("#calendarimg").mousedown(function(){
		$("#overlay").fadeIn("fast", function(){
            $("#overlay").css('display', 'block');
        });

    });
});

$(document).ready(function(){
    $("#overlay").mousedown(function(){
		$("#overlay").fadeOut("fast", function(){
            $("#overlay").css('display', 'none');
        });

    });
});
