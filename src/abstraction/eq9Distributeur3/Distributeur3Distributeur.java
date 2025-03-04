package abstraction.eq9Distributeur3;

import abstraction.eqXRomu.clients.ClientFinal;
import abstraction.eqXRomu.filiere.IDistributeurChocolatDeMarque;
import abstraction.eqXRomu.produits.ChocolatDeMarque;

public class Distributeur3Distributeur extends Distributeur3Acteur implements IDistributeurChocolatDeMarque {
    @Override
    public double prix(ChocolatDeMarque choco) {
        return 2000;
    }

    @Override
    public double quantiteEnVente(ChocolatDeMarque choco, int crypto) {
        if (this.cryptogramme==crypto && this.stockChocoMarque.keySet().contains(choco)) {
            //return this.stockChocoMarque.get(choco)/10;
            return 100;
        } else {
            return 0.0;
        }
    }

    @Override
    public double quantiteEnVenteTG(ChocolatDeMarque choco, int crypto) {
        return 0;
    }

    @Override
    public void vendre(ClientFinal client, ChocolatDeMarque choco, double quantite, double montant, int crypto) {

    }

    @Override
    public void notificationRayonVide(ChocolatDeMarque choco, int crypto) {

    }
}
