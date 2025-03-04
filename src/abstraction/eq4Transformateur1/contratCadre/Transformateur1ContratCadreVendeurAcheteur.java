package abstraction.eq4Transformateur1.contratCadre;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.contratsCadres.*;
import abstraction.eqXRomu.produits.Feve;

import java.util.List;
import java.util.LinkedList;

public class Transformateur1ContratCadreVendeurAcheteur extends Transformateur1ContratCadreVendeur implements IAcheteurContratCadre {
    
    protected List<ExemplaireContratCadre> mesContratEnTantQuAcheteur;
    protected double qttInitialementVoulue;	
	protected double prixInitialementVoulu;
	protected double epsilon;

	public Transformateur1ContratCadreVendeurAcheteur(IProduit produit) {
		super(produit);
		this.mesContratEnTantQuAcheteur=new LinkedList<ExemplaireContratCadre>();
        this.epsilon  = 0.1;
        this.qttInitialementVoulue = 0.5*stock.getMax();//On cherche à acheter de quoi remplir notre stock à hauteur de 50%
        this.prixInitialementVoulu = 0.75*9500; //Une valeur arbitraire s'appuyant sur le prix moyen des fèves de cacao en 2024
	}

	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
        //Si la qtt proposée est cohérente avec la quantité que nous voulions initialement, on accepte l'echeancier
		if (Math.abs((this.qttInitialementVoulue - contrat.getEcheancier().getQuantiteTotale())/this.qttInitialementVoulue) <= epsilon){
            return contrat.getEcheancier();
        }
        //Sinon on négocie par dichotomie
        else{
            //Mise à jour de l'échéancier pour prendre en compte ces modifications
            Echeancier e = contrat.getEcheancier();
			//On procède par dichotomie en augmentant ou diminuant le volume de vente de nos produits
            double delta = contrat.getEcheancier().getQuantiteTotale()- qttInitialementVoulue;
            double signe = delta/Math.abs(delta);
			this.qttInitialementVoulue = this.qttInitialementVoulue * (1 + signe*0.125);//si detla<0, proposition trop faible donc on diminue notre demande
															                            //si delta>0, proposition trop forte donc on augmante notre demande

			//Redistribution uniforme de la hausse ou de la baisse des qtt vendues 
			for(int i = e.getStepDebut() ; i< e.getStepFin() ; i++){
				double qtti = e.getQuantite(i);
				e.set(i, qtti*(1+signe*0.125));
			}
			return e;
        }
	}
	

	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
		//Si le prix est aberrant, on refuse d'office la négociation
        if (contrat.getPrix() > 20000){
			return -1;
		}
		else{
			//On procède par dichotomie sur le prix proposé et notre prix voulu.
			//Si le prix proposé est inférieur à notre prix, on accepte le contrat
			if(contrat.getPrix() < this.prixInitialementVoulu){
				return contrat.getPrix(); 
			}

			//Sinon on vérifie si le prix est cohérent avec le notre d'un seuil epsilon
			if (Math.abs((contrat.getPrix()-prixInitialementVoulu)/prixInitialementVoulu) <= this.epsilon ){
				return contrat.getPrix();
			}
			//Sinon on contre-porpose un prix intermédiaire par rapport au prix proposé
			else{
				this.prixInitialementVoulu = this.prixInitialementVoulu + (contrat.getPrix()-prixInitialementVoulu)*0.2;
				return this.prixInitialementVoulu;
			}
		}
	}


	public void next() {
		// On enleve les contrats obsolete (nous pourrions vouloir les conserver pour "archive"...)
		List<ExemplaireContratCadre> contratsObsoletes=new LinkedList<ExemplaireContratCadre>();
		for (ExemplaireContratCadre contrat : this.mesContratEnTantQuAcheteur) {
			if (contrat.getQuantiteRestantALivrer()==0.0 && contrat.getMontantRestantARegler()==0.0) {
				contratsObsoletes.add(contrat);
			}
		}
		this.mesContratEnTantQuAcheteur.removeAll(contratsObsoletes);
		
		// Proposition d'un nouveau contrat a tous les vendeurs possibles
		/*
		for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
			if (acteur!=this && acteur instanceof IVendeurContratCadre && ((IVendeurContratCadre)acteur).vend(produit)) {
				Filiere.LA_FILIERE.getSuperviseurContratCadre().demande((IAcheteurContratCadre)this, ((IVendeurContratCadre)acteur), produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, 5.0), cryptogramme);
			}
		}*/
		// OU proposition d'un contrat a un des vendeurs choisi aleatoirement
		journal.ajouter("Recherche d'un vendeur aupres de qui acheter");
		List<IVendeurContratCadre> vendeurs = supCCadre.getVendeurs(produit);
		if (vendeurs.contains(this)) {
			vendeurs.remove(this);
		}
		IVendeurContratCadre vendeur = null;
		if (vendeurs.size()==1) {
			vendeur=vendeurs.get(0);
		} else if (vendeurs.size()>1) {
			vendeur = vendeurs.get((int)( Filiere.random.nextDouble()*vendeurs.size()));
		}
		if (vendeur!=null) {
			journal.ajouter("Demande au superviseur de debuter les negociations pour un contrat cadre de "+produit+" avec le vendeur "+vendeur);
			ExemplaireContratCadre cc = supCCadre.demandeAcheteur((IAcheteurContratCadre)this, vendeur, produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, (SuperviseurVentesContratCadre.QUANTITE_MIN_ECHEANCIER+10.0)/10), cryptogramme,false);
			journal.ajouter("-->aboutit au contrat "+cc);
		}
		// Proposition d'un contrat a un des achteur choisi aleatoirement
		journal.ajouter("Recherche d'un acheteur aupres de qui vendre");
		List<IAcheteurContratCadre> acheteurs = supCCadre.getAcheteurs(produit);
		if (acheteurs.contains(this)) {
			acheteurs.remove(this);
		}
		IAcheteurContratCadre acheteur = null;
		if (acheteurs.size()==1) {
			acheteur=acheteurs.get(0);
		} else if (acheteurs.size()>1) {
			acheteur = acheteurs.get((int)( Filiere.random.nextDouble()*acheteurs.size()));
		}
		if (acheteur!=null) {
			journal.ajouter("Demande au superviseur de debuter les negociations pour un contrat cadre de "+produit+" avec l'acheteur "+acheteur);
			ExemplaireContratCadre cc = supCCadre.demandeVendeur(acheteur, (IVendeurContratCadre)this, produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, (SuperviseurVentesContratCadre.QUANTITE_MIN_ECHEANCIER+10.0)/10), cryptogramme,false);
			journal.ajouter("-->aboutit au contrat "+cc);
		}
	}

	public void receptionner(IProduit produit, double quantiteEnTonnes, ExemplaireContratCadre contrat) {
		totalStocksFeves.ajouter(this, quantiteEnTonnes); 
        double currStockFeves = stockFeves.get((Feve) produit);
        stockFeves.put((Feve) produit, currStockFeves+quantiteEnTonnes);
        journal.ajouter("Reception de " + quantiteEnTonnes +"feves " + ((Feve)produit).getGamme());
	}

	public boolean achete(IProduit produit) {
		return true;
	}
	public String toString() {
		return this.getNom();
	}

	public int fixerPourcentageRSE(IAcheteurContratCadre acheteur, IVendeurContratCadre vendeur, IProduit produit,
			Echeancier echeancier, long cryptogramme, boolean tg) {
		return 5;
	}


}
