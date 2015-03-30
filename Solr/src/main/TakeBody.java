package main;

public class TakeBody {

	public static void main(String[] args) {
		/*String body = "La parola ciao (IPA: [ˈʧaːo]) è una forma di saluto "
				+ "amichevole ed informale della lingua italiana, usata sia nell'incontrarsi, "
				+ "sia nell'accomiatarsi (in quest'ultimo caso, talvolta, si usa raddoppiato, "
				+ "ovvero 'ciao ciao'). 'Fare ciao' è l'espressione con cui ci si riferisce "
				+ "ad un gesto di saluto informale ottenuto agitando la mano.";
		
		String titolo = "Ciao - Wikipedia Opinioni e recensioni sui migliori prodotti su Ciao "
				+ "Entra in videochat è senza registrazione! - ciao aMigos";*/
		String titolo = "";
		String body = "ovvero 'ciao ciao'). 'Fare ciao' è l'espressione con cui ci si riferisce "
				+ "ad un gesto di saluto informale ottenuto agitando la mano.";
		
		String body_corto = "";  
		String titolo_corto = "";
		if(titolo!="") {
			String[] parole_titolo = titolo.split(" ", 11);
			for(int i=0;i<10 && i<parole_titolo.length;i++) {
				titolo_corto += parole_titolo[i] + " ";
			}
			if(parole_titolo.length>10)
				titolo_corto += "...";
			
			String[] parole_body = body.split(" ", 21);
			for(int i=0;i<20 && i<parole_body.length;i++) {
				body_corto += parole_body[i] + " ";
			}
			if(parole_body.length>20)
				body_corto += "...";
		}
		else {
			String[] parole_body = body.split(" ", 31);
			int i;
			for(i=0;i<10 && i<parole_body.length;i++) {
				titolo_corto += parole_body[i] + " ";
			}
			if(parole_body.length>10)
				titolo_corto += "...";
			
			while(i<30 && i<parole_body.length){
				body_corto += parole_body[i] + " ";
				i++;
			}
			if(parole_body.length>30)
				body_corto += "...";
		}
		System.out.println(titolo_corto);
		System.out.println(body_corto);
	}
}
