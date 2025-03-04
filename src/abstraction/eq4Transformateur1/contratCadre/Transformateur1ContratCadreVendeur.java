package abstraction.eq4Transformateur1.contratCadre;

import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.contratsCadres.*;


/*
 * @author MURY Julien
 * Cette calsse, héritant de Transformateur1ContratCadre, décrit le comportement de notre acteur lors de la creation d'un contrat cadre en tant que venndeur
 */

public class Transformateur1ContratCadreVendeur extends TransformateurContratCadre implements IVendeurContratCadre {

	protected List<ExemplaireContratCadre> mesContratEnTantQueVendeur;//Contient tous les contrats de vente
	protected double qttInitialementVoulue;	
	protected double prixInitialementVoulu;
	protected double epsilon;
	
	public Transformateur1ContratCadreVendeur(IProduit produit) {
		super(produit);
		this.mesContratEnTantQueVendeur=new LinkedList<ExemplaireContratCadre>();
		this.qttInitialementVoulue = stockChoco.get((Chocolat) produit) * 0.90;  //On cherche à vendre 90% de notre stock total actuel
		this.epsilon = 0.1;  //Pourcentage d'erreur entre la quantite voulue et celle du contrat actuel

	}


	public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {

		if (contrat.getProduit().equals(produit)) {
			double qtt_totale_voulue = qttInitialementVoulue;

			if (contrat.getEcheancier().getQuantiteTotale()< stockChoco.get((Chocolat) produit)) {  //On chercher à honorer le contrat sur les qtts

				//On vérifie que le contrat propose met en jeu suffisemment de quantite par rapport aux quantite voulues par notre acteur, a plus ou moins epsilon pres
				double delta = contrat.getEcheancier().getQuantiteTotale() - qtt_totale_voulue;

				if (Math.abs(delta) <= epsilon*qtt_totale_voulue ) {
					return contrat.getEcheancier(); // on ne cherche pas a negocier sur le previsionnel de livraison
				} 
				else {
					//Mise à jour de l'échéancier pour prendre en compte ces modifications
					Echeancier e = contrat.getEcheancier();
					//On procède par dichotomie en augmentant ou diminuant le volume de vente de nos produits
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
			} else {
				return null; // on est frileux : on ne s'engage dans un contrat cadre que si on a toute la quantite en stock (on pourrait accepter meme si nous n'avons pas tout car nous pouvons produire/acheter pour tenir les engagements) 
			}
		} else {
			return null;// on ne vend pas de ce produit
		}
	}


	//A MODIFIER
	// Cette proposition doit mettre en place une réduction pour des quantités élevées par rapport à 
	//un seuil définissant ces quantités élevées. Le prix proposé est un prix à la tonne. 
	//Le prix moyen d'une tonne de chocolat est de 5226eur 
	//On se base la dessus pour une première version, il faudra prendre en compte le produit dont il est question et 
	//adopter une stratégie selon celui-ci
	public double propositionPrix(ExemplaireContratCadre contrat) {
		if(contrat.getQuantiteTotale() >= 2000){
			this.prixInitialementVoulu = 0.75*5226;
			return 0.75*5226; 
		}
		this.prixInitialementVoulu = 5226*(1 - 0.25*contrat.getQuantiteTotale()/2000);
		return 5226*(1 - 0.25*contrat.getQuantiteTotale()/2000);// plus la quantite est elevee, plus le prix est interessant
	}


	//A MODIFIER
	//Comme précédemment, la stratégie à adopter dépend du produit dont il est question (et le prix négocié aussi)
	//Pour le moment on s'appuie sur le prix moyen de la tonne de chocolat
	public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
		//Si le prix est beaucoup trop faible, l'algorithme par dichotomie risque de ne pas fonctionner 
		// et de nous faire vendre à perte. On arrête donc les négociations.
		if (contrat.getPrix() < 0.65*5226){
			return -1;
		}
		else{
			//On procède par dichotomie sur le prix proposé et notre prix voulu.
			//Si le prix proposé est supérieur à notre prix, on accepte le contrat
			if(contrat.getPrix() > this.prixInitialementVoulu){
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

	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
		this.mesContratEnTantQueVendeur.add(contrat);
	}
	

	public void next() {
		List<ExemplaireContratCadre> contratsObsoletes=new LinkedList<ExemplaireContratCadre>();
		for (ExemplaireContratCadre contrat : this.mesContratEnTantQueVendeur) {
			if (contrat.getQuantiteRestantALivrer()==0.0 && contrat.getMontantRestantARegler()==0.0) {
				contratsObsoletes.add(contrat);
			}
		}
		this.mesContratEnTantQueVendeur.removeAll(contratsObsoletes);
	}

	public boolean vend(IProduit produi) {
		return super.produit==produi;
	}

	public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
		double livre = Math.min(stockChoco.get((Chocolat) produit), quantite);
		if (livre>0.0) {
			totalStocksChoco.retirer(this,  livre);
			double currStockChoco = stockChoco.get(produit);
			stockChoco.put((Chocolat) produit, currStockChoco-livre);
		}
		return livre;
	}

	public boolean peutVendre(IProduit produit) {
		return super.produit.equals(produit);
	}
	public String toString() {
		return this.getNom();
	}

}
