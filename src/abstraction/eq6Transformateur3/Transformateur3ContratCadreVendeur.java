package abstraction.eq6Transformateur3;

import java.util.LinkedList;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.produits.IProduit;
// @author Henri Roth & Eric Schiltz
public class Transformateur3ContratCadreVendeur extends Transformateur3Fabriquant implements IVendeurContratCadre {
    //des attributs
    protected LinkedList<ExemplaireContratCadre> ContratsVendeur;
    //des constructeurs
    public Transformateur3ContratCadreVendeur() {
        this.ContratsVendeur=new LinkedList<ExemplaireContratCadre>();
	}
    //des méthodes
    //à chaque next on va proposer des contrats cadres pour vendre du chocolat
    @Override
    public void next(){
        //on récupère le next de tous les pères
        super.next();
        //on crée un contrat cadre
        SuperviseurVentesContratCadre supCCadre = (SuperviseurVentesContratCadre) Filiere.LA_FILIERE.getActeur("Sup.CCadre");
        //on parcourt tous les types de chocolat
        for(IProduit choco : super.lesChocolats){
            //on met en vente dès que on a plus de 1000 unités de ce chocolat
            if(stockChoco.getQuantityOf(choco)>1000){
                //on parcourt les acteurs de la filière
                for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
                    //si l'acteur n'est pas nous et si l'acteur achète des contrats cadres et s'il achète
                    //du chocolat par contrat cadre
			        if (acteur!=this && acteur instanceof IAcheteurContratCadre && ((IAcheteurContratCadre)acteur).achete(choco)) {
                        //on propose un contrat cadre à l'acteur en question qui démarre à l'étape
                        //suivante de la filière, qui dure 10 step avec une quantité livrée de 100 unités
                        //par step et avec ou non tête de gondole ici en fonction du chocolat
                        // pour hypocritolat on le demande pas 
                        if (choco==hypo){
                            supCCadre.demandeVendeur((IAcheteurContratCadre)acteur, (IVendeurContratCadre)this,(IProduit) choco, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, 100.0), cryptogramme, false);
                        }
                        //pour tous les autres chocolats on le demande
                        else {
                            supCCadre.demandeVendeur((IAcheteurContratCadre)acteur, (IVendeurContratCadre)this,(IProduit) choco, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, 100.0), cryptogramme, true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean vend(IProduit produit) {
        if(super.lesChocolats.contains(produit)){
            if(stockChoco.getQuantityOf(produit)>100){
                return true;
            }
        }
        return false;
    }

    @Override
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        IProduit p = contrat.getProduit();
        if(super.lesChocolats.contains(p)){
            return contrat.getEcheancier();
        }
        return null;
    }

    @Override
    public double propositionPrix(ExemplaireContratCadre contrat) {
        return 10000;
    }

    @Override
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
        return contrat.getPrix();
    }

    @Override
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        journalCC.ajouter("Nouveau contrat cadre" +contrat);
        ContratsVendeur.add(contrat);
    }

    @Override
    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        double stockActuel = stockChoco.getQuantityOf(produit);
		double aLivre = Math.min(quantite, stockActuel);
		journalCC.ajouter("   Livraison de "+aLivre+" T de "+produit+" sur "+quantite+" exigees pour contrat "+contrat.getNumero());
		stockChoco.remove(produit, aLivre);
		return aLivre;
	}
    
}
