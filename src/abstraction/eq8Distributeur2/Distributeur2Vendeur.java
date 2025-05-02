/**
 * @author tidzzz 
 */

package abstraction.eq8Distributeur2;

import java.util.HashMap;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.filiere.IDistributeurChocolatDeMarque;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;

import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.acteurs.Romu;
import abstraction.eqXRomu.clients.ClientFinal;

import abstraction.eqXRomu.general.Journal;





public class Distributeur2Vendeur extends Distributeur2Acteur implements IDistributeurChocolatDeMarque {
    
    
    protected double capaciteDeVente;
	
	protected HashMap<ChocolatDeMarque, Double> prix_min;
	
	protected  HashMap<ChocolatDeMarque, Double> ListPrix;
	protected String[] marques;
	protected Journal journalVente;

	protected HashMap<String,Double> Coefficient;
	protected LinkedList<String> equipe;
	
	protected HashMap<ChocolatDeMarque,Boolean> aVendu;


	public Distributeur2Vendeur() {
		super();
		this.capaciteDeVente=120000.0;  //capacite de vente par step
		this.ListPrix = new HashMap<ChocolatDeMarque, Double>();
		this.marques = new String[chocolats.size()];
		this.journalVente= new Journal ("journal des ventes", this);
		
		
		this.equipe = new LinkedList<String>();
		
		this.aVendu = new HashMap<ChocolatDeMarque,Boolean>();
	}

	public void initialiser () {
		super.initialiser();
		for (ChocolatDeMarque choco : chocolats) {
			this.setPrix(choco);
		}
		
		
		this.equipe.add("EQ4");
		this.equipe.add("EQ5");
		this.equipe.add("EQ6");
		
		
		for (ChocolatDeMarque choc : chocolats) {
			this.aVendu.putIfAbsent(choc, false);
		}
	}


public void setPrix(ChocolatDeMarque choco) {

	if (choco.getChocolat() == Chocolat.C_MQ_E) {
		ListPrix.put(choco, (double) 10000);
	}
	

	if (choco.getChocolat() == Chocolat.C_HQ_E) {
		ListPrix.put(choco, (double) 22000);
	}
	if (choco.getChocolat() == Chocolat.C_HQ_BE) {
		ListPrix.put(choco, (double) 30000);
	}

}


    public double prix(ChocolatDeMarque cm){
        if (ListPrix.containsKey(cm)) {
			return ListPrix.get(cm);
		} 
		else { 
			return 0;
		}
    }

    
    public double quantiteEnVente(ChocolatDeMarque choco, int crypto){
        if (crypto!=this.cryptogramme || !chocolats.contains(choco)) {
			journalVente.ajouter("Quelqu'un essaye de me pirater !");
			return 0.0;
		} 
		else {
			
			if (choco.toString().contains("C_MQ_E")) {
				double x = (capaciteDeVente*0.30/nombreMarquesParType.get(Chocolat.C_MQ_E));
				return Math.max(Math.min(x , this.getQuantiteEnStock(choco,crypto)),0.0);
			}
			
			if (choco.toString().contains("C_HQ_BE")) {
				double x = (capaciteDeVente*0.30/nombreMarquesParType.get(Chocolat.C_HQ_BE));
				return Math.max(Math.min(x , this.getQuantiteEnStock(choco,crypto)),0.0);
			}
			if (choco.toString().contains("C_HQ_E")) {
				double x = (capaciteDeVente*0.40/nombreMarquesParType.get(Chocolat.C_HQ_E));
				return Math.max(Math.min(x , this.getQuantiteEnStock(choco,crypto)),0.0);
			}
			
		}
		return 0.0;
	}

	//@author ArmandCHANANE
	public double quantiteEnVenteTotal(){
		double qte = 0;

		for (ChocolatDeMarque cm :this.chocolats){
			qte = qte + this.quantiteEnVente(cm, cryptogramme);
		}
		return qte;
	}


	//@author ArmandCHANANE
	public double quantiteEnVenteTG(ChocolatDeMarque choco, int crypto){
        if (crypto == this.cryptogramme) {
			double capaciteDeVenteTG = this.quantiteEnVenteTotal() * ClientFinal.POURCENTAGE_MAX_EN_TG;
			

			if(choco.getChocolat() == Chocolat.C_HQ_E){
				
				return ((0.3 * capaciteDeVenteTG)/nombreMarquesParType.get(Chocolat.C_HQ_E));
			}

			if(choco.getChocolat() == Chocolat.C_HQ_BE){
				return ((0.7 * capaciteDeVenteTG)/nombreMarquesParType.get(Chocolat.C_HQ_BE));
			}
			
			return 0.0;
		} 
		else {return 0.0;}
    }

	//@author ArmandCHANANE
	public double quantiteEnVenteTGTotal(){
		double qte = 0;

		for (ChocolatDeMarque cm :this.chocolats){
			qte = qte + this.quantiteEnVenteTG(cm, cryptogramme);
		}
		return qte;
	}



    public void vendre(ClientFinal client, ChocolatDeMarque choco, double quantite, double montant, int crypto) {
		int pos = (chocolats.indexOf(choco));
		if (pos>=0) {
			double nouveauStock = this.getQuantiteEnStock(choco,crypto) - quantite;
			if (nouveauStock >= 0) {
				stock_Choco.put(choco, nouveauStock);
				this.aVendu.replace(choco, true);
				journalVente.ajouter(Romu.COLOR_GREEN, Romu.COLOR_LLGRAY, client.getNom()+" a acheté "+String.format("%.2f", quantite)+"kg de "+choco+" pour "+String.format("%.2f", montant)+" d'euros ");

			} else {
				journalVente.ajouter("ERREUR : Tentative de vendre plus que le stock disponible pour "+choco);
			}
		}
	}


    public void notificationRayonVide(ChocolatDeMarque choco, int crypto){
        journalVente.ajouter("J'aurais du mettre davantage de " + choco.getNom() + " en vente");
    }


	public List<Journal> getJournaux() {
		
		List<Journal> jour = super.getJournaux();
		jour.add(this.journalVente);
		return jour;
	}



	public void next() {
		super.next();
		
		journalVente.ajouter("");
		journalVente.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_PURPLE,"==================== STEP "+Filiere.LA_FILIERE.getEtape()+" ====================");
		journalVente.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_PURPLE,"QuantitéEnVenteTotal à l'Etape "+Filiere.LA_FILIERE.getEtape()+" : " +this.quantiteEnVenteTotal());
		journalVente.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_PURPLE,"QuantitéEnVenteTGTotal à l'Etape "+Filiere.LA_FILIERE.getEtape()+" : "+this.quantiteEnVenteTGTotal());
		journalVente.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_PURPLE,"=================================");
		journalVente.ajouter("");
		
		for (ChocolatDeMarque choco : chocolats) {
			journalVente.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_PURPLE,"prix de vente pour le chocolats "+choco+" est de : "+String.format("%.2f", this.prix(choco)));
		}
		
		for (int i=0;i<this.ListPrix.size(); i++) {
			this.setPrix(chocolats.get(i));
		}

		ajusterPrix();
		
		if (capaciteDeVente > stockTotal.getValeur()) {
			capaciteDeVente = stockTotal.getValeur();
		}	
		else {
			capaciteDeVente = 120000;
		}

		
		
	}

	
	public void ajusterPrix() {
		for (ChocolatDeMarque cm : chocolats) {
			if (cm.getChocolat() == Chocolat.C_HQ_E || cm.getChocolat() == Chocolat.C_HQ_BE || cm.getChocolat() == Chocolat.C_MQ_E){
				double stockActuel = this.getQuantiteEnStock(cm, cryptogramme);
				double prixOriginal = ListPrix.get(cm);
				double prixActuel = prixOriginal;
				boolean prixModifie = false;
				String raisonModification = "";
				
				// Ajustement en fonction du stock
				if (stockActuel < 3000) {
					// Si stock faible, augmenter les prix
					prixActuel *= 1.05; // +5%
					prixModifie = true;
					raisonModification = "stock faible";
				} else if (stockActuel > 10000) {
					// Si stock élevé, baisser les prix
					prixActuel *= 0.98; // -2%
					prixModifie = true;
					raisonModification = "stock élevé";
				}
				
				// Ajustement en fonction du type de chocolat
				double prixMinimum;
				double prixMaximum;
				
				if (cm.getChocolat() == Chocolat.C_MQ_E) {
					prixMinimum = prix_minimum(cm, 9500);
					prixMaximum = 13000;
				} else if (cm.getChocolat() == Chocolat.C_HQ_E) {
					prixMinimum = prix_minimum(cm, 20000);
					prixMaximum = 25000;
				} else if (cm.getChocolat() == Chocolat.C_HQ_BE) {
					prixMinimum = prix_minimum(cm, 28000);
					prixMaximum = 35000;
				} else {
					prixMinimum = 8000;
					prixMaximum = 11000;
				}
				
				// Vérifier que le prix est dans les limites
				if (prixActuel < prixMinimum) {
					prixActuel = prixMinimum;
					prixModifie = true;
					raisonModification = "prix minimum atteint";
				} else if (prixActuel > prixMaximum) {
					prixActuel = prixMaximum;
					prixModifie = true;
					raisonModification = "prix maximum atteint";
				}
				
				// Mettre à jour le prix
				ListPrix.put(cm, prixActuel);
				
				// Journalisation
				if (prixModifie) {
					String evolution = prixActuel > prixOriginal ? "augmenté" : "baissé";
					double pourcentage = Math.abs((prixActuel - prixOriginal) / prixOriginal * 100);
					String message = "Prix de " + cm + " " + evolution + " de " + String.format("%.2f", pourcentage) + "% (" 
						+ String.format("%.2f", prixOriginal) + " → " + String.format("%.2f", prixActuel) 
						+ " euros) - Raison: " + raisonModification;
					
					if (evolution.equals("augmenté")) {
						journalVente.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_GREEN, message);
					} else {
						journalVente.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN, message);
					}
				} else {
					journalVente.ajouter("Prix de " + cm + " inchangé à " + String.format("%.2f", prixActuel) + " euros");
				}
			}
		}
	}

	private double prix_minimum(ChocolatDeMarque choco, double min) {
		double minimum = min;
		LinkedList<IDistributeurChocolatDeMarque> distributeurs = new LinkedList<>();
		for (IActeur distributeur : Filiere.LA_FILIERE.getActeurs()) {
			
			if (distributeur instanceof IDistributeurChocolatDeMarque && distributeur != this) {
				distributeurs.add((IDistributeurChocolatDeMarque) distributeur);
			}
			IDistributeurChocolatDeMarque distributeurChoco = (IDistributeurChocolatDeMarque) distributeur;
			double prix = distributeurChoco.prix(choco);
			if (prix < minimum && prix > 0) {
				//System.out.println("prix de "+choco+" chez "+distributeurChoco.getNom()+" est de "+prix);
				minimum = prix;
			}
		}
		return minimum;
	}


}
