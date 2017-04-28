$(document).ready(function(){
    $("#calendarimg").mousedown(function(){
		$("div.overlayCalendar").fadeIn("fast", function(){
            $("div.overlay").css('display', 'block');
        });

    });
});

$(document).ready(function(){
    $("#profileimg").mousedown(function(){
		$("div.overlayProfile").fadeIn("fast", function(){
            $("div.overlayProfile").css('display', 'block');
        });

    });
});


$(document).ready(function(){
    $("div.overlayCalendar").mousedown(function(){
		$("div.overlayCalendar").fadeOut("fast", function(){
            $("div.overlayCalendar").css('display', 'none');
        });

    });
});

$(document).ready(function(){
    $("div.overlayProfile").mousedown(function(){
        $("div.overlayProfile").fadeOut("fast", function(){
            $("div.overlayProfile").css('display', 'none');
        });

    });
});


