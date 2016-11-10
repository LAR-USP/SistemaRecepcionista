window.on('load', function(){
    var str = $("#searchbar").val();
    $("#searchdiv").css('top', '0%');
    $("#results").css('top', '10%');
    $.ajax({
            url:'Detect',
            data:{},
            type:'post',
            cache:false,
            success:function(data){
                $('#results').text(data); 
            },
        error:function(){
            alert('error');
        }
    });
});

