package abstraction.eq9Distributeur3;

import abstraction.eqXRomu.clients.ClientFinal;
import abstraction.eqXRomu.filiere.IDistributeurChocolatDeMarque;
import abstraction.eqXRomu.produits.ChocolatDeMarque;

import java.util.HashMap;

public class Distributeur3Distributeur extends Distributeur3Acteur implements IDistributeurChocolatDeMarque {

    HashMap<ChocolatDeMarque, Double> stocks;
    HashMap<ChocolatDeMarque, Float> prix;


    public Distributeur3Distributeur() {
        super();
    }


    @Override
    public double prix(ChocolatDeMarque choco) {
        return prix.get(choco);
    }


    @Override
    public double quantiteEnVente(ChocolatDeMarque choco, int crypto) {
        if (this.cryptogramme==crypto && this.stocks.containsKey(choco)) {
            if(this.stocks.get(choco)>=100) {
                return 100;
            }else{
                return this.stocks.get(choco);
            }
        } else {
            return 0.0;
        }
    }

    @Override
    public double quantiteEnVenteTG(ChocolatDeMarque choco, int crypto) {
        return this.quantiteEnVente(choco,crypto)*ClientFinal.POURCENTAGE_MAX_EN_TG;
    }

    @Override
    public void vendre(ClientFinal client, ChocolatDeMarque choco, double quantite, double montant, int crypto) {
        if(crypto==this.cryptogramme){
            stocks.put(choco,Double.valueOf(this.stocks.get(choco)-quantite));
            journalActeur.ajouter("Vente de "+quantite+" tonnes de "+choco.toString()+" à "+client.getNom()+" pour "+montant+" euros");
        }
    }

    @Override
    public void notificationRayonVide(ChocolatDeMarque choco, int crypto) {
        this.journalActeur.ajouter("Le rayon de "+choco.toString()+" est vide ");
    }
}
