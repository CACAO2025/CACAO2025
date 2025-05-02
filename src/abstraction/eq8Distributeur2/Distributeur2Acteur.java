package abstraction.eq8Distributeur2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariablePrivee;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;

import abstraction.eqXRomu.acteurs.Romu;


public class Distributeur2Acteur implements IActeur {
	
	
	// stocks
	protected Variable stockTotal;
	protected HashMap<ChocolatDeMarque, Double> stock_Choco;
	protected HashMap<Chocolat, Variable> stock_chocolat_qualite;

	
	protected Journal journal;
	protected int cryptogramme;
	
	protected IProduit produit;
	protected List<ChocolatDeMarque> chocolats;
	
	protected HashMap<Chocolat,Integer> nombreMarquesParType;
	
	

	public Distributeur2Acteur() {
		
		this.journal= new Journal(this.getNom()+" journal", this);
		this.chocolats = new LinkedList<ChocolatDeMarque>();
		
		this.stockTotal = new VariablePrivee("Eq8DStockChocoMarque","Quantite totale de chocolat de marque en stock",this,0);
		this.stock_chocolat_qualite= new HashMap<Chocolat, Variable>();
		for (Chocolat c : Chocolat.values()) {
			this.stock_chocolat_qualite.put(c,new Variable ("EQ8 stock de : "+c,this,0));}
	}
	
	
    
    public Variable getStockTotal(){
		return stockTotal;
	}

    public void initialiser() {
		
		this.stock_Choco=new HashMap<ChocolatDeMarque,Double>();
		this.nombreMarquesParType=new HashMap<Chocolat,Integer>();
		
		
		chocolats= Filiere.LA_FILIERE.getChocolatsProduits();
		
		for (ChocolatDeMarque cm : chocolats) {
		    Chocolat typeChoco = cm.getChocolat();
		    nombreMarquesParType.put(typeChoco, nombreMarquesParType.getOrDefault(typeChoco, 0) + 1);
	    }
		
		this.journal.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_LBLUE,"==================== STOCK INITIAL ====================");
		for (ChocolatDeMarque cm : chocolats) {
			double stock = 0;
			
			if (cm.getChocolat() == Chocolat.C_MQ_E) {
				stock=12000;
			}
			
			if (cm.getChocolat() == Chocolat.C_HQ_E) {
				stock=12000;
			}
			if (cm.getChocolat() == Chocolat.C_HQ_BE)  {
				stock=12000;
			}
			this.stock_Choco.put(cm, stock);
			this.journal.ajouter(cm+"->"+this.stock_Choco.get(cm));
			this.stockTotal.ajouter(this, stock, cryptogramme);
		}

		for (Chocolat choc : Chocolat.values()) {
			double totalStock = 0;
			for (ChocolatDeMarque cm : chocolats) {
				if (cm.getChocolat().equals(choc)) {
					totalStock += stock_Choco.get(cm);
				}
			}
			this.stock_chocolat_qualite.get(choc).setValeur(this, totalStock, cryptogramme);
		}
		
		this.journal.ajouter("");
		
	}



	public String getNom() {// NE PAS MODIFIER
		return "EQ8";
	}
	
	public String toString() {// NE PAS MODIFIER
		return this.getNom();
	}

	////////////////////////////////////////////////////////
	//         En lien avec l'interface graphique         //
	////////////////////////////////////////////////////////

	public void next() {
		
	}

	
	
	
	public Color getColor() {// NE PAS MODIFIER
		return new Color(209, 179, 221); 
	}

	public String getDescription() {
		return "Bla bla bla";
	}

	// Renvoie les indicateurs
	public List<Variable> getIndicateurs() {
		List<Variable> res = new ArrayList<Variable>();
		res.add(this.stockTotal);
		for (Chocolat choc : Chocolat.values()) {
			res.add(this.stock_chocolat_qualite.get(choc));
		}
		return res;
	}

	// Renvoie les parametres
	public List<Variable> getParametres() {
		List<Variable> res=new ArrayList<Variable>();
		
		
		return res;
	}

	// Renvoie les journaux
	public List<Journal> getJournaux() {
		List<Journal> res=new ArrayList<Journal>();
		
		res.add(journal);
		return res;
	}

	////////////////////////////////////////////////////////
	//               En lien avec la Banque               //
	////////////////////////////////////////////////////////

	// Appelee en debut de simulation pour vous communiquer 
	// votre cryptogramme personnel, indispensable pour les
	// transactions.
	public void setCryptogramme(Integer crypto) {
		this.cryptogramme = crypto;
	}

	// Appelee lorsqu'un acteur fait faillite (potentiellement vous)
	// afin de vous en informer.
	public void notificationFaillite(IActeur acteur) {
	}

	// Apres chaque operation sur votre compte bancaire, cette
	// operation est appelee pour vous en informer
	public void notificationOperationBancaire(double montant) {
	}
	
	// Renvoie le solde actuel de l'acteur
	protected double getSolde() {
		return Filiere.LA_FILIERE.getBanque().getSolde(Filiere.LA_FILIERE.getActeur(getNom()), this.cryptogramme);
	}

	////////////////////////////////////////////////////////
	//        Pour la creation de filieres de test        //
	////////////////////////////////////////////////////////

	// Renvoie la liste des filieres proposees par l'acteur
	public List<String> getNomsFilieresProposees() {
		ArrayList<String> filieres = new ArrayList<String>();
		return(filieres);
	}

	// Renvoie une instance d'une filiere d'apres son nom
	public Filiere getFiliere(String nom) {
		return Filiere.LA_FILIERE;
	}

	public double getQuantiteEnStock(IProduit p, int cryptogramme) {
		if (this.cryptogramme==cryptogramme) { // c'est donc bien un acteur assermente qui demande a consulter la quantite en stock
			if (stock_Choco.containsKey(p)) {
			return stock_Choco.get(p);
			}
		} 
		
		System.out.println("Cet acteur n'est pas assermenté");
		return 0; // Les acteurs non assermentes n'ont pas a connaitre notre stock
	}

	public double getQuantiteEnStockTotal() {
		double stock = 0;
		for (ChocolatDeMarque cm : chocolats) {
			stock += stock_Choco.get(cm);
		}
		return stock;
	}
	
}
