package abstraction.eq4Transformateur1.contratCadre;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.acteurs.Romu;
import abstraction.eqXRomu.bourseCacao.BourseCacao;
import abstraction.eqXRomu.contratsCadres.*;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve; 
import abstraction.eqXRomu.produits.Chocolat;

import java.util.List;
import java.awt.Color;
import java.util.LinkedList;

/**
 * @author MURY Julien
 */
public class Transformateur1ContratCadreVendeurAcheteur extends Transformateur1ContratCadreVendeur implements IAcheteurContratCadre {
    
    
    protected double qttInitialementVoulue;	
	protected double prixInitialementVoulu;
	protected double epsilon;


	public Transformateur1ContratCadreVendeurAcheteur() {
		super();
		this.mesContratEnTantQuAcheteur=new LinkedList<ExemplaireContratCadre>();
        this.epsilon  = 0.1;

        this.qttInitialementVoulue = 0.5*STOCK_MAX_TOTAL_FEVES;//On cherche à acheter de quoi remplir ou vendre notre stock à hauteur de 50%

        this.prixInitialementVoulu = 0.75*9500; //Une valeur arbitraire s'appuyant sur le prix moyen du chocolat en 2024
	}

	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {

        //Si la qtt proposée est cohérente avec la quantité que nous voulions initialement, on accepte l'echeancier
		if (contrat.getEcheancier().getQuantiteTotale()>SuperviseurVentesContratCadre.QUANTITE_MIN_ECHEANCIER){
			if (Math.abs((this.qttInitialementVoulue - contrat.getEcheancier().getQuantiteTotale())/this.qttInitialementVoulue) <= epsilon){

				return contrat.getEcheancier();
			}
			//Sinon on négocie par dichotomie particulière
			else{

				double qttContrat = contrat.getEcheancier().getQuantiteTotale();
				double qttVoulue = 0.25*qttContrat + 0.75 * this.qttInitialementVoulue;

				//Si on calcule une quantité voulue inférieure à celle du contrat, on accepte la dernière offre
				if (qttVoulue<SuperviseurVentesContratCadre.QUANTITE_MIN_ECHEANCIER) return contrat.getEcheancier();

				//Mise à jour de l'échéancier pour prendre en compte ces modifications
				Echeancier e = contrat.getEcheancier();

				//Redistribution uniforme de la hausse ou de la baisse des qtt vendues 
				for(int i = e.getStepDebut() ; i< e.getStepFin() ; i++){
					double qtti = e.getQuantite(i)*(qttVoulue/qttContrat);

					//Si la quantité par step ne respecte pas les exigeances des règles du contrat, on met la part minimale
					//Mais si cette condition est vérifiée, il risque d'y avoir une augmentation de la qtt totale et donc que le problème se reporte sur d'autres step
					if (qtti<qttVoulue/(10*e.getNbEcheances())){

						e.set(i, qttVoulue/(10*e.getNbEcheances()));
					}
				}
				if (e.getQuantiteTotale()<SuperviseurVentesContratCadre.QUANTITE_MIN_ECHEANCIER) return contrat.getEcheancier();
				else return e;

			}
		}
		else {
			return null;
		}
	}
	
	//A MODIFIER 
	/*Il faudrait s'appuyer sur le cours de la bourse pour négocier les prix avec les producteurs */
	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
		//Si le prix est aberrant, on refuse d'office la négociation
        if (contrat.getPrix() >10000){
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

	public void initialiser(){
		super.initialiser();
	}









	public void next() {
		super.next();

		// On enleve les contrats obsolete (nous pourrions vouloir les conserver pour "archive"...)
		List<ExemplaireContratCadre> contratsObsoletes=new LinkedList<ExemplaireContratCadre>();
		for (ExemplaireContratCadre contrat : this.mesContratEnTantQuAcheteur) {
			if (contrat.getQuantiteRestantALivrer()==0.0 && contrat.getMontantRestantARegler()==0.0) {
				contratsObsoletes.add(contrat);
			}
		}
		this.mesContratEnTantQuAcheteur.removeAll(contratsObsoletes);

		

		// OU proposition d'un contrat a un des vendeurs choisi aleatoirement
		for(IProduit produit : this.lesFeves){
			if(qttEntrantesFeve.get((Feve)produit)< 0.1*STOCK_MAX_TOTAL_FEVES){
				journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN, "Recherche d'un vendeur aupres de qui acheter " + produit);

				List<IVendeurContratCadre> vendeurs = supCCadre.getVendeurs(produit);
				if (vendeurs.contains(this)) {
					vendeurs.remove(this);
				}

                if (vendeurs.size()==0) {
					journalCC.ajouter(Color.pink, Romu.COLOR_BROWN, "-->Pas de vendeur potentiel");
					journalCC.ajouter("\n");
				} else {
				    journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN, "Voici les vendeurs potentiels : " + vendeurs);
				    journalCC.ajouter("\n");
				}

				IVendeurContratCadre vendeur = null;
				if (vendeurs.size()==1) {
					vendeur=vendeurs.get(0);
				} else if (vendeurs.size()>1) {
					//A MODIFIER
					//Recherche aléatoire d'un vendeur
					vendeur = vendeurs.get((int)( Filiere.random.nextDouble()*vendeurs.size()));
				}
				if (vendeur!=null) {
					journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN, "Demande au superviseur de debuter les negociations pour un contrat cadre de "+produit+" avec le vendeur "+vendeur);
					ExemplaireContratCadre cc = supCCadre.demandeAcheteur((IAcheteurContratCadre)this, vendeur, produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 30, STOCK_MAX_TOTAL_FEVES/30), cryptogramme,false);
					if (cc!=null) {
					    journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN, "-->aboutit au contrat "+cc);
					    journalCC.ajouter("\n");
						this.mesContratEnTantQuAcheteur.add(cc);
					}
					else {
					    journalCC.ajouter(Color.pink, Romu.COLOR_BROWN, "-->Le contrat n'a pas pu aboutir");
					    journalCC.ajouter("\n");
					}
				}
			}
		}
		// Recherche d'acheteurs de chocolat de marque
		for(ChocolatDeMarque produit : chocolatsLimDt){
			journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_PURPLE, "Recherche d'un acheteur aupres de qui vendre LimDt " + produit);

			List<IAcheteurContratCadre> acheteurs = supCCadre.getAcheteurs(produit);
			if (acheteurs.contains(this)) {
			    acheteurs.remove(this);
			}

			if (acheteurs.size()==0) {
				journalCC.ajouter(Color.pink, Romu.COLOR_PURPLE, "-->Pas d'acheteur potentiel");
				journalCC.ajouter("\n");
			} else {
			    journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_PURPLE, "Voici les acheteurs potentiels pour LimDt : " + acheteurs);
			    journalCC.ajouter("\n");
			}

			for(IAcheteurContratCadre acheteur : acheteurs){
				if (acheteur!=null) {
					journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_PURPLE, "Demande au superviseur de debuter les negociations pour un contrat cadre de "+produit+" avec l'acheteur "+acheteur);
					ExemplaireContratCadre cc = supCCadre.demandeVendeur(acheteur, (IVendeurContratCadre)this, produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 25, (0.3*this.getQuantiteEnStock(produit, this.cryptogramme)+10)/25), cryptogramme,false);
					if (cc!=null) {
					    journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_PURPLE, "-->aboutit au contrat "+cc);
					    journalCC.ajouter("\n");
						this.mesContratEnTantQueVendeur.add(cc);
					}
					else {
					    journalCC.ajouter(Color.pink, Romu.COLOR_PURPLE, "-->Le contrat n'a pas pu aboutir");
					    journalCC.ajouter("\n");
					}
				}
			}
		}

		//Recherche d'acheteurs de chocolat non marqué
		for(Chocolat produit : lesChocolats){
			journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_GREEN, "Recherche d'un acheteur aupres de qui vendre " + produit);

			List<IAcheteurContratCadre> acheteurs = supCCadre.getAcheteurs(produit);
			if (acheteurs.contains(this)) {
				acheteurs.remove(this);
			}

			if (acheteurs.size()==0) {
				journalCC.ajouter(Color.pink, Romu.COLOR_GREEN, "-->Pas d'acheteur potentiel");
				journalCC.ajouter("\n");
			} else {
			       journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_GREEN, "Voici les acheteurs potentiels : " + acheteurs);
			       journalCC.ajouter("\n");
			}


			for(IAcheteurContratCadre acheteur : acheteurs){
				if (acheteur!=null) {
					journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_GREEN, "Demande au superviseur de debuter les negociations pour un contrat cadre de "+produit+" avec l'acheteur "+acheteur);
					ExemplaireContratCadre cc = supCCadre.demandeVendeur(acheteur, (IVendeurContratCadre)this, produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, (SuperviseurVentesContratCadre.QUANTITE_MIN_ECHEANCIER+10.0)/10), cryptogramme,false);
					if (cc!=null) {
					    journalCC.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_GREEN, "-->aboutit au contrat "+cc);
					    journalCC.ajouter("\n");
					}
					else {
					    journalCC.ajouter(Color.pink, Romu.COLOR_GREEN, "-->Le contrat n'a pas pu aboutir");
					    journalCC.ajouter("\n");
					}
				}
			}
		}



		//Affichage dans les journaux des contrats cadres actifs
		journalCC.ajouter("Voici nos contrats actifs en tant qu'acheteur : ");
		journalCC.ajouter("");
		for (ExemplaireContratCadre cc : mesContratEnTantQuAcheteur){
			journalCC.ajouter("Acheteur : " + cc.getAcheteur() );
			journalCC.ajouter("Vendeur : " + cc.getVendeur() );
			journalCC.ajouter("Produit : " + cc.getProduit() );
			journalCC.ajouter("Prix : " + cc.getPrix() + "\n");
			journalCC.ajouter("Echeancier : " + cc.getEcheancier() );
			journalCC.ajouter("");
		}

		//Affichage des contrats vendeurs 
		journalCC.ajouter("Voici nos contrats actifs en tant que vendeur : ");
		journalCC.ajouter("");
		for (ExemplaireContratCadre cc : this.mesContratEnTantQueVendeur){
			journalCC.ajouter("Acheteur : " + cc.getAcheteur() );
			journalCC.ajouter("Vendeur : " + cc.getVendeur() );
			journalCC.ajouter("Produit : " + cc.getProduit() );
			journalCC.ajouter("Prix : " + cc.getPrix() );
			journalCC.ajouter("Echeancier : " + cc.getEcheancier() );
			journalCC.ajouter("");

		}
	}






	public void receptionner(IProduit produit, double quantiteEnTonnes, ExemplaireContratCadre contrat) {
		ajouterAuStock(produit, quantiteEnTonnes, this.cryptogramme);
		
        journalStock.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN, "Achat CC :");
		journalStock.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN, "Reception de " + quantiteEnTonnes +"feves " + ((Feve)produit).getGamme() + "(CC avec" + contrat.getVendeur() + ")");
		journalStock.ajouter("\n");
	}







	public boolean achete(IProduit produit) {
		//On n'achète que les fèves nous permettant de produire les chocolats que l'on veut produire
		return lesFeves.contains(produit);
	}




	public String toString() {
		return this.getNom();
	}




	public int fixerPourcentageRSE(IAcheteurContratCadre acheteur, IVendeurContratCadre vendeur, IProduit produit,
			Echeancier echeancier, long cryptogramme, boolean tg) {
		return 5;
	}


}
