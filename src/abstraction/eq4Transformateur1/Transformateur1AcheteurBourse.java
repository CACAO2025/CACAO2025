package abstraction.eq4Transformateur1;

import java.awt.Color;

import abstraction.eq4Transformateur1.contratCadre.Transformateur1ContratCadreVendeurAcheteur;
import abstraction.eqXRomu.acteurs.Romu;
import abstraction.eqXRomu.bourseCacao.IAcheteurBourse;
import abstraction.eqXRomu.produits.Feve; 

/**
 * @author YAOU Reda
 */

public class Transformateur1AcheteurBourse extends Transformateur1ContratCadreVendeurAcheteur implements IAcheteurBourse{
	
	private Feve feve;
 
	public Transformateur1AcheteurBourse() {
		super();
		this.feve = Feve.F_MQ;
	}


	@Override
	public double demande(Feve f, double cours) {
		if (this.feve.equals(f)) {
			this.journalTransactions.ajouter(Color.white, Color.magenta,"B: Je demande " + qttFevesAcheteesBourse.getValeur() + " tonnes de " + f + " au cours de " + cours + " euros par tonne.");
			return 1000;
		} else {
			return 0.0;
		}
	}

	@Override
	public void notificationAchat(Feve f, double quantiteEnT, double coursEnEuroParT) {

		this.journalTransactions.ajouter(Romu.COLOR_LLGRAY, Color.magenta, "--> B: J'ai achete " + quantiteEnT + " tonnes de " + f + " au cours de " + coursEnEuroParT + " euros par tonne.");
		this.qttFevesAcheteesBourse.setValeur(this, quantiteEnT);
		this.journalTransactions.ajouter("\n");
		
		//Ajoute des fèves achetées dans notre stock
		this.ajouterAuStock(f, quantiteEnT, this.cryptogramme);
	}

	@Override
	public void notificationBlackList(int dureeEnStep) {
		this.journalTransactions.ajouter(Color.pink, Color.magenta, "--> B: Aie... je suis blackliste... j'aurais du verifier que j'avais assez d'argent avant de passer une trop grosse commande en bourse...");
		this.journalTransactions.ajouter("\n");
	}
}


