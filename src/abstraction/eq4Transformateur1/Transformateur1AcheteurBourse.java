package abstraction.eq4Transformateur1;

import abstraction.eqXRomu.bourseCacao.IAcheteurBourse;
import abstraction.eqXRomu.produits.Feve;

public class Transformateur1AcheteurBourse extends Transformateur1Stocks implements IAcheteurBourse{
	
	private Feve feve;
	private double T;

	public Transformateur1AcheteurBourse() {
		super();
		this.feve = Feve.F_MQ;
		this.T = 80.0;
	}

	@Override
	public double demande(Feve f, double cours) {
		if (this.feve.equals(f)) {
			this.journal.ajouter("Je demande " + T + " tonnes de " + f + " au cours de " + cours + " euros par tonne.");
			return T;
		} else {
			return 0.0;
		}
	}

	@Override
	public void notificationAchat(Feve f, double quantiteEnT, double coursEnEuroParT) {
		this.totalStocksFeves.setValeur(this, this.totalStocksFeves.getValeur() +quantiteEnT);
		this.journal.ajouter("J'ai achete " + quantiteEnT + " tonnes de " + f + " au cours de " + coursEnEuroParT + " euros par tonne.");
		
		this.stockFeves.put(f, stockFeves.get(f) + quantiteEnT);
		this.journal.ajouter("J'ai maintenant " + this.stockFeves.get(f) + " tonnes de " + f + " en stock.");
	}

	@Override
	public void notificationBlackList(int dureeEnStep) {
		this.journal.ajouter("Aie... je suis blackliste... j'aurais du verifier que j'avais assez d'argent avant de passer une trop grosse commande en bourse...");
	}
}
