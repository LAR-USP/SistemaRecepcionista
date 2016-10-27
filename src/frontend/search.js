$(document).on('input', '#searchbar', function(){
    var str = $("#searchbar").val();
    $("#searchdiv").css('top', '0%');
    $("#results").css('top', '10%');
    $.ajax({
            url:'Search',
            data:{content:str},
            type:'get',
            cache:false,
            success:function(data){
                $('#results').text(data); 
            },
        error:function(){
            alert('error');
        }
    });
});

