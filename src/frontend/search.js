$(document).on('input', '#searchbar', function(){
    var str = $("#searchbar").val();
    $("#results").text(str);
});

