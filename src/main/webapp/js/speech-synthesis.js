// Diz uma mensagem.
function mySpeak( text, callback )
{
	var u = new SpeechSynthesisUtterance();
	u.text = text;
	u.lang = 'pt-BR';

	u.onend = function () {
		if ( callback ) {
			callback();
		}
	};

	u.onerror = function ( e ) {
		if ( callback ) {
			callback( e );
		}
	};
 
	speechSynthesis.speak( u );
}
