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
public class Producteur3Vente extends Producteur3GestionDesCoûts implements IVendeurBourse, IVendeurContratCadre{
    protected List<ExemplaireContratCadre> mesContratCadres = new ArrayList<ExemplaireContratCadre>();

   
        public Producteur3Vente() {
            super();

        }


    // VENTE EN BOURSE //

    
    @Override
    public double offre(Feve feve, double cours) {
       double stock = calculTotalStockParticulier(feve);
        if (stock > 0){
            BourseCacao bourse = (BourseCacao)(Filiere.LA_FILIERE.getActeur("BourseCacao"));
            double pourcentage = (bourse.getCours(feve).getValeur()-bourse.getCours(feve).getMin())/(bourse.getCours(feve).getMax()-bourse.getCours(feve).getMin());
            return stock*pourcentage;

        }else{
            return 0.0;
        }
       
    }

    @Override
    public double notificationVente(Feve f, double coursEnEuroParT, double quantiteEnT) {
        double livrable = Math.min(calculTotalStockParticulier(f), quantiteEnT);
        if(f.getGamme().equals(Gamme.BQ)){
            if(f.isEquitable()){
                retirerStockBQ_E(f,livrable);
                journalBourse.ajouter("Vente de " + livrable + " tonnes de " + f + " à la bourse pour " + coursEnEuroParT + " euros par tonne.");
            }else{
                retirerStockBQ(f, livrable);
                journalBourse.ajouter("Vente de " + livrable + " tonnes de " + f + " à la bourse pour " + coursEnEuroParT + " euros par tonne.");
            }
        }else if (f.getGamme().equals(Gamme.MQ)){
            if (f.isEquitable()){
                retirerStockMQ_E(f,livrable);
                journalBourse.ajouter("Vente de " + livrable + " tonnes de " + f + " à la bourse pour " + coursEnEuroParT + " euros par tonne.");
            }else{
                retirerStockMQ(f, livrable);
                journalBourse.ajouter("Vente de " + livrable + " tonnes de " + f + " à la bourse pour " + coursEnEuroParT + " euros par tonne.");
            }
        }else{
            if (f.isBio()){
                retirerStockHQ_B(f,livrable);
                journalBourse.ajouter("Vente de " + livrable + " tonnes de " + f + " à la bourse pour " + coursEnEuroParT + " euros par tonne.");
            }else{
                retirerStockHQ(f, livrable);
                journalBourse.ajouter("Vente de " + livrable + " tonnes de " + f + " à la bourse pour " + coursEnEuroParT + " euros par tonne.");
            }
        }
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
            double stockActuel = calculTotalStockParticulier(feve);
            double stockNouveau = getNouveauStockParticulier(feve);
            double stockPotentiel = stockActuel + stockNouveau*12;
            double aLivrer = 0;
            for (ExemplaireContratCadre c : this.mesContratCadres){
                if (c.getProduit().equals(feve)){
                    aLivrer += c.getQuantiteRestantALivrer();
                }
            }
            boolean A = feve.getGamme().equals(Gamme.MQ) && feve.isEquitable() && calculTotalStockParticulier(feve) >= 0 && stockPotentiel*0.7 >= aLivrer;
            boolean B = feve.getGamme().equals(Gamme.BQ) && !feve.isEquitable()&& calculTotalStockParticulier(feve) >= 0 && stockPotentiel*0.7 >= aLivrer;
            boolean C = feve.getGamme().equals(Gamme.HQ) && feve.isBio()&& calculTotalStockParticulier(feve) >= 0 && stockPotentiel*0.7 >= aLivrer;
            return A || B || C;
        }
        return false;
    }
    
    
    @Override
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        Echeancier echeancier = contrat.getEcheancier();
        double stockActuel = calculTotalStockParticulier((Feve)contrat.getProduit());
        double stockNouveau = getNouveauStockParticulier((Feve)contrat.getProduit());
        int stepDebut = contrat.getEcheancier().getStepDebut();
        int stepFin = contrat.getEcheancier().getStepFin();
        int nbStep = stepFin - stepDebut + 1;
        if (this.mesContratCadres.size() == 0){
            if ( stockActuel*0.5 >= contrat.getQuantiteTotale() && stockActuel*0.05 <= contrat.getQuantiteTotale() && contrat.getQuantiteTotale()/nbStep >= 100){
                return echeancier;
            }
            double contreQuantite = (contrat.getQuantiteTotale()+stockActuel*0.5)/2;
            if (contreQuantite < nbStep*101){
                return null;
            }
            else{
                return new Echeancier(Filiere.LA_FILIERE.getEtape()+1, nbStep, contreQuantite/nbStep);
            }
        }
        else {
            double stockPotentiel = stockActuel + stockNouveau*nbStep;
            double parStep = contrat.getQuantiteTotale()/nbStep;

            double aLivrer = contrat.getQuantiteTotale();
            for (ExemplaireContratCadre c : this.mesContratCadres){
                if (c.getProduit().equals(contrat.getProduit())){
                    aLivrer += c.getQuantiteRestantALivrer();
                }
            
            }
            if (aLivrer <= stockPotentiel*0.7 && parStep >= 100){
                return echeancier;
            }
            else{
                return null;
            }
        }
            /* 
            double surplus = ;
            double contreQuantite = (contrat.getQuantiteTotale() - surplus)*0.8;
            if (contreQuantite < nbStep*101){
                return null;
            }
            else{
                return new Echeancier(Filiere.LA_FILIERE.getEtape()+1, nbStep, contreQuantite/nbStep);
            } 
        }*/
        
    }


    @Override
    public double propositionPrix(ExemplaireContratCadre contrat) {
        Feve feve = (Feve)contrat.getProduit();
        double Cump = getCump(feve);
        Gamme gamme = feve.getGamme();
        if (gamme.equals(Gamme.HQ)){
            return Cump*1.2;
        }
        else{
            BourseCacao bourse = (BourseCacao)(Filiere.LA_FILIERE.getActeur("BourseCacao"));
            double cours = bourse.getCours(Feve.get(gamme,false,false)).getValeur();
            if (Cump*1.2 >= cours*1.5) {
                return Cump*1.2;
            }
            else{
                return cours*1.5;
            }
        }
        
    }


    @Override
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
        Double prixProp = contrat.getPrix();
        Feve feve = (Feve)contrat.getProduit();
        double Cump = getCump(feve);
        double ancienneOffre = contrat.getListePrix().get(contrat.getListePrix().size()-2);
        double nouvelleOffre = (ancienneOffre + prixProp)/2;
        if (Cump*1.1 >= nouvelleOffre) {
            return Cump*1.1;
        }
        else{
            return nouvelleOffre;}
    }


    @Override
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        this.mesContratCadres.add(contrat);
        journalContratCadre.ajouter("Nouveau contrat cadre : "+ "\n");
        journalContratCadre.ajouter("Produit : " + contrat.getProduit() + "\n");
        journalContratCadre.ajouter("Prix : " + contrat.getPrix() + "\n");
        journalContratCadre.ajouter("Quantité : " + contrat.getQuantiteTotale() + "\n");
        journalContratCadre.ajouter("Acheteur : " + contrat.getAcheteur() + "\n");
        journalContratCadre.ajouter("Nb de Contrats en cours : " + this.mesContratCadres.size() + "\n");
    }

    @Override
    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        Feve f = (Feve)produit;
        double stockActuel = calculTotalStockParticulier(f);
        double livrable = Math.max(0,Math.min(stockActuel, quantite));
        if(f.getGamme().equals(Gamme.BQ)){
            if(f.isEquitable()){
                retirerStockBQ_E(f,livrable);
            }else{
                retirerStockBQ(f, livrable);
            }
        }else if (f.getGamme().equals(Gamme.MQ)){
            if (f.isEquitable()){
                retirerStockMQ_E(f,livrable);
            }else{
                retirerStockMQ(f, livrable);
            }
        }else{
            if (f.isBio()){
                retirerStockHQ_B(f,livrable);
            }else{
                retirerStockHQ(f, livrable);
            }
        }
        return livrable;
    }
    

}
