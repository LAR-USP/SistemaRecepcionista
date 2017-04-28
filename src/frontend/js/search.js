// Detecta a inserção de caracteres na barra de pesquisa
// e envia a entrada para o servidor via AJAX.

var ultima_entidade = "";
var ultima_propriedade = "";
var acesso = 0;

var entity = "";
var properties = "";

$(document).on('input', '#searchbar', function(){
    var str = $("#searchbar").val();
    $("#searchdiv").css('top', '0%');
    $("#results").css('top', '10%');
    
    if(acesso == 0){
        $("#laraface").fadeOut(function(){
            $("#laraface").css('height', '50px');
            $("#laraface").css('width', '50px');
            $("#laraface").css('position', 'absolute');
            $("#laraface").css('top', '0.5%');
        });
        $("#laraface").fadeIn();
        acesso = 1;
    }

    $.ajax({
            url:'Search',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            data:{content:str, entity:entity, properties:properties},
            type:'get',
            cache:false,
            success:function(data){
                $('#results').html(data.answer);
                entity = data.entity;
                properties = JSON.stringify(data.properties);
            },
        error:function(){
            alert('error');
        }
    });

    $.ajax({
            url:'Search',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            data:{content:str, entity:entity, properties: possuiEmail},
            type:'get',
            cache:false,
            success:function(data){
                entity = data.entity;
                properties = JSON.stringify(data.properties);
                $('#email').html(data.properties);
            },
        error:function(){
            alert('error');
        }
    });

});

var recogntion = null;

// Inicia a entrada de voz.
function startDictation() {
    if (window.hasOwnProperty('webkitSpeechRecognition')) {
        recognition = new webkitSpeechRecognition();
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

