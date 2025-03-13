package abstraction.eq3Producteur3;

import abstraction.eqXRomu.bourseCacao.BourseCacao;
import abstraction.eqXRomu.bourseCacao.IVendeurBourse;
import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.IProduit;

import java.util.List;
import java.util.ArrayList;

// Paul
public class Producteur3Vente extends Producteur3Stock implements IVendeurBourse, IVendeurContratCadre{
    protected List<ExemplaireContratCadre> mesContratCadres = new ArrayList<ExemplaireContratCadre>();

   
        public Producteur3Vente() {
            super();

        }


    // VENTE EN BOURSE //

    
    @Override
    public double offre(Feve feve, double cours) {
       
        if (this.stockFeve.get(feve).getValeur(cryptogramme)>0){
            BourseCacao bourse = (BourseCacao)(Filiere.LA_FILIERE.getActeur("BourseCacao"));
            double pourcentage = (bourse.getCours(feve).getValeur()-bourse.getCours(feve).getMin())/(bourse.getCours(feve).getMax()-bourse.getCours(feve).getMin());
            return this.stockFeve.get(feve).getValeur(cryptogramme)*pourcentage;

        }else{
            return 0.0;
        }
       
    }

    @Override
    public double notificationVente(Feve f, double coursEnEuroParT, double quantiteEnT) {
        double livrable = Math.min(this.stockFeve.get(f).getValeur(cryptogramme), quantiteEnT);
		this.stockFeve.get(f).setValeur(this, stockFeve.get(f).getValeur(cryptogramme)-livrable,cryptogramme);
        calculTotalStock();
		return livrable;
    }

    @Override
    public void notificationBlackList(int dureeEnStep) {
        
    }

    // CONTRAT CADRE //

   

    @Override
    public boolean vend(IProduit produit) {
        if (produit instanceof Feve) {
            Feve feve = (Feve)produit;
            return feve.getGamme().equals(Gamme.MQ);
        }
        return false;
    }
    
    @Override
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        Echeancier echeancier = contrat.getEcheancier();
        double qteTotale = contrat.getQuantiteTotale();
        double qteInit = contrat.getEcheanciers().get(0).getQuantiteTotale();
        if (qteTotale >= qteInit*1.1) {
            return echeancier;
        }
        int stepDebut = contrat.getEcheancier().getStepDebut();
        int stepFin = contrat.getEcheancier().getStepFin();
        int nbStep = stepFin - stepDebut;
        return new Echeancier(Filiere.LA_FILIERE.getEtape()+1, nbStep,(int)(qteInit*1.2/nbStep));
    }


    @Override
    public double propositionPrix(ExemplaireContratCadre contrat) {
        Feve feve = (Feve)contrat.getProduit();
        Gamme gamme = feve.getGamme();
        BourseCacao bourse = (BourseCacao)(Filiere.LA_FILIERE.getActeur("BourseCacao"));
        double cours = bourse.getCours(Feve.get(gamme, false, false)).getValeur();
        return cours*1.5;
    }


    @Override
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
        Feve feve = (Feve)contrat.getProduit();
        Gamme gamme = feve.getGamme();
        Double prixProp = contrat.getPrix();
        BourseCacao bourse = (BourseCacao)(Filiere.LA_FILIERE.getActeur("BourseCacao"));
        double cours = bourse.getCours(Feve.get(gamme, false, false)).getValeur();
        if (prixProp >= cours*1.1) {
            return prixProp;
        }
        return cours*1.1;
    }


    @Override
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        this.mesContratCadres.add(contrat);
    }


    @Override
    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        double stockActuel = this.stockFeve.get((Feve)produit).getValeur(cryptogramme);
        double qteALivrer = Math.min(stockActuel, quantite);
        this.stockFeve.get((Feve)produit).setValeur(this, stockActuel-qteALivrer, cryptogramme);
        return qteALivrer;
    }
    
}
