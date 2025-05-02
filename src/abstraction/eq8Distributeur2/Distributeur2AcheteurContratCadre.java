package abstraction.eq8Distributeur2;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;


import java.util.LinkedList;
import java.util.List;



import abstraction.eqXRomu.general.Journal;

import abstraction.eqXRomu.produits.Chocolat;

import abstraction.eqXRomu.acteurs.Romu;

public class Distributeur2AcheteurContratCadre extends Distributeur2Vendeur implements IAcheteurContratCadre{

	
	protected List<ExemplaireContratCadre> contrat_en_cours;
	protected List<ExemplaireContratCadre> contrat_term;
	protected Journal journalCC;
	
	// liste des produits que l'on souhaite acheter
    protected List<ChocolatDeMarque> produit_voulue;

	public Distributeur2AcheteurContratCadre() {
		super();
		this.contrat_en_cours = new LinkedList<ExemplaireContratCadre>();
		this.contrat_term = new LinkedList<ExemplaireContratCadre>();
		this.journalCC = new Journal (this.getNom() + "journal CC", this);

		this.produit_voulue = new LinkedList<ChocolatDeMarque>();
        
	}
    
    //@author ArmandCHANANE
    public void initialiser() {
		super.initialiser();
        
        for (ChocolatDeMarque cm : chocolats){
            if (cm.getChocolat() == Chocolat.C_HQ_E || cm.getChocolat() == Chocolat.C_HQ_BE || cm.getChocolat() == Chocolat.C_MQ_E){
                produit_voulue.add(cm);
            }
        }
        
    }
    
    //@author tidzzz
    public boolean achete(IProduit produit) {
        if (produit instanceof ChocolatDeMarque 
        && produit_voulue.contains(produit)   
        && stock_Choco.get(produit) <= 100000){
            journalCC.ajouter("Demande d'achat de " + produit.toString() + " acceptée.");
            return true;
        }
        return false;
    }

    // Méthode pour la contre-proposition de l'échéancier
public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
    Echeancier echeancier = contrat.getEcheancier();
    IProduit produit = contrat.getProduit();
    
    // Vérifier si nous avons besoin urgent du produit
    if (produit instanceof ChocolatDeMarque) {
        ChocolatDeMarque choco = (ChocolatDeMarque) produit;
        double stockActuel = stock_Choco.get(choco);
        
        // Si le stock est faible, on accepte l'échéancier tel quel
        if (stockActuel < 5000) {
            journalCC.ajouter("Stock faible pour " + choco + ", j'accepte l'échéancier proposé.");
            return echeancier;
        }
        
        // Sinon, on essaie d'étaler les livraisons pour mieux gérer notre stock
        if (echeancier.getNbEcheances() > 1) {
            Echeancier contre = new Echeancier(echeancier.getStepDebut(), echeancier.getNbEcheances() + 2, echeancier.getQuantiteTotale() / (echeancier.getNbEcheances() + 2));
            journalCC.ajouter("Contre-proposition d'échéancier: étalement sur " + contre.getNbEcheances() + " étapes.");
            return contre;
        }
    }
    
    journalCC.ajouter("J'accepte l'échéancier proposé pour " + produit);
    return echeancier;
}

// Méthode pour la contre-proposition du prix
public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
    double prixPropose = contrat.getPrix();
    IProduit produit = contrat.getProduit();
    
    if (produit instanceof ChocolatDeMarque) {
        ChocolatDeMarque choco = (ChocolatDeMarque) produit;
        double stockActuel = stock_Choco.get(choco);
        
        // Prix maximum que nous sommes prêts à payer selon le type de chocolat
        double prixMaximum;
        if (choco.getChocolat() == Chocolat.C_HQ_BE) {
            prixMaximum = 27000;
        } else if (choco.getChocolat() == Chocolat.C_HQ_E) {
            prixMaximum = 20000;
        } else if (choco.getChocolat() == Chocolat.C_MQ_E) {
            prixMaximum = 9000;
        } else {
            prixMaximum = 8000;
        }
        
        // Si notre stock est très bas, on est prêt à payer plus
        if (stockActuel < 2000) {
            prixMaximum *= 1.2; // +20%
        } else if (stockActuel > 50000) {
            prixMaximum *= 0.8; // -20%
        }
        
        // Si le prix proposé est trop élevé, on fait une contre-proposition
        if (prixPropose > prixMaximum) {
            double contreOffre = prixMaximum * 0.9; // On propose 90% de notre maximum
            journalCC.ajouter("Prix proposé (" + prixPropose + ") trop élevé pour " + choco + ", je propose " + contreOffre);
            return contreOffre;
        }
        
        // Si le prix est acceptable, on accepte mais on essaie quand même de négocier un peu
        if (prixPropose < prixMaximum * 0.8) {
            journalCC.ajouter("Prix proposé (" + prixPropose + ") acceptable pour " + choco + ", j'accepte");
            return prixPropose;
        } else {
            double contreOffre = prixPropose * 0.95; // On essaie de baisser de 5%
            journalCC.ajouter("Prix proposé (" + prixPropose + ") pour " + choco + ", je propose " + contreOffre);
            return contreOffre;
        }
    }
    
    return prixPropose;
}

    //@author ArmandCHANANE
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        journalCC.ajouter("Nouveau contrat cadre conclu : " + contrat);
    }

    //@author tidzzz
    public void receptionner(IProduit p, double quantiteEnTonnes, ExemplaireContratCadre contrat) {
        stock_Choco.put((ChocolatDeMarque) p,this.stock_Choco.get(p)+quantiteEnTonnes);
        journal.ajouter(Romu.COLOR_PURPLE, Romu.COLOR_GREEN,"Réception de " + quantiteEnTonnes + " tonnes de " + p.toString() + " du contrat " + contrat.toString());
        journal.ajouter("");
        journalCC.ajouter(Romu.COLOR_PURPLE, Romu.COLOR_GREEN,"Réception de " + quantiteEnTonnes + " tonnes de " + p.toString() + " pour le contrat " + contrat.toString());
        
        // Vérifier si le contrat est terminé
			if (contrat.getQuantiteRestantALivrer() == 0.0) {
				journalCC.ajouter("Contrat " + contrat.getNumero() + " terminé !");
				this.contrat_en_cours.remove(contrat);
				this.contrat_term.add(contrat);
			}
    
    }


    //@author tidzzz
    public List<Journal> getJournaux() {
		
		List<Journal> jour = super.getJournaux();
		jour.add(this.journalCC);
		return jour;
	}
}
