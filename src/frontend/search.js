// Detecta a inserção de caracteres na barra de pesquisa
// e envia a entrada para o servidor via AJAX.
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

// Inicia a entrada de voz.
function startDictation() {
    if (window.hasOwnProperty('webkitSpeechRecognition')) {
        var recognition = new webkitSpeechRecognition();
        recognition.continuous = true;
        recognition.interimResults = false;
        recognition.lang = "pt-BR";
        recognition.start();
        var i = 0;
        recognition.onresult = function(e) {
            $('#searchbar').val(e.results[i][0].transcript);
            $('#searchbar').trigger("input");
            ++i;
        };
        recognition.onerror = function(e) {
            recognition.stop();
        };
    }
}
