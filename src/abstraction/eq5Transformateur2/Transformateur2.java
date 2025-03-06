package abstraction.eq5Transformateur2;

import abstraction.eqXRomu.bourseCacao.IAcheteurBourse;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Feve;

public class Transformateur2 extends Transformateur2Acteur implements IAcheteurBourse {
    
	protected Variable stocktotal;

    public Transformateur2() {
        super();
		

		

    }

   
    public double demande(Feve f, double cours) {
        if (f == Feve.F_MQ) { 
            return 80.0; // Achat de 80 tonnes de F_MQ à chaque next
        }
        return 0.0;
    }

    
    public void notificationAchat(Feve f, double quantiteEnT, double coursEnEuroParT) {
		super.stocktotal.setValeur(this, super.stocktotal.getValeur()+quantiteEnT);
		System.out.println("on a un stock total de " + this.stocktotal.getValeur() + " tonnes de " + f);

	}


   
    public void notificationBlackList(int dureeEnStep) {
        System.out.println("Attention ! Nous sommes exclus de la bourse pour " + dureeEnStep + " étapes.");
    }
}