package abstraction.eq6Transformateur3;

import abstraction.eqXRomu.appelDOffre.AppelDOffre;
import abstraction.eqXRomu.appelDOffre.IVendeurAO;
import abstraction.eqXRomu.appelDOffre.OffreVente;
import abstraction.eqXRomu.produits.IProduit;
// @author Henri Roth
public class Transformateur3AO extends Transformateur3AcheteurBourse implements IVendeurAO {

    public Transformateur3AO(){
    }

    @Override
    public OffreVente proposerVente(AppelDOffre offre) {
        IProduit produit = offre.getProduit();
        if (stockChoco.contains(produit)){
            double Q = offre.getQuantiteT();
            if(stockChoco.getQuantityOf(produit) > Q){
                double prix = Q*3;
                OffreVente offrevente = new OffreVente(offre, this, produit, prix);
                return offrevente;
            }
        }
        return null;
    }

    @Override
    public void notifierVenteAO(OffreVente propositionRetenue) {
        journalAO.ajouter("Nouvelle vente " + propositionRetenue);
        IProduit P = propositionRetenue.getProduit();
        double Q = propositionRetenue.getQuantiteT();
        stockChoco.remove(P, Q);
    }

    @Override
    public void notifierPropositionNonRetenueAO(OffreVente propositionRefusee) {
        journalAO.ajouter("Proposition non retenue " + propositionRefusee);
    }
    
}
