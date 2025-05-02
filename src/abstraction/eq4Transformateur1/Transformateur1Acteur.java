package abstraction.eq4Transformateur1;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.acteurs.Romu;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.filiere.IMarqueChocolat;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariablePrivee;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit; 

public class Transformateur1Acteur implements IActeur, IMarqueChocolat {

	protected Journal journal;	
	protected Journal journalStock;
	protected Journal journalCC;
	protected Journal journalTransactions;
	protected Journal journalPeremption;
	protected int cryptogramme;

	protected List<Feve> lesFeves; 
	protected List<Chocolat> lesChocolats;
	protected List<ChocolatDeMarque> chocolatsLimDt; 



	//Stock de fèves
	protected Variable stock_F_BQ;
	protected Variable stock_F_BQ_E;
	protected Variable stock_F_MQ_E;
	protected Variable stock_F_HQ_BE;
	protected HashMap<Feve, Variable> stocksFevesVar;

	//Stock de chocolat NON MARQUE
	protected Variable stock_C_BQ;
	protected Variable stock_C_BQ_E;
	protected Variable stock_C_MQ_E;
	protected Variable stock_C_HQ_BE;
	protected HashMap<Chocolat, Variable> stocksChocoVar;

	//Stock de chocolat de marque
	protected Variable stock_C_BQ_Limdt;
	protected Variable stock_C_BQ_E_Limdt;
	protected Variable stock_C_MQ_E_Limdt;
	protected Variable stock_C_HQ_BE_Limdt;
	protected HashMap<ChocolatDeMarque, Variable> stocksMarqueVar;

	protected double[] péremption_C_BQ_Limdt = new double[12];
	protected double[] péremption_C_BQ_E_Limdt = new double[12];
	protected double[] péremption_C_MQ_E_Limdt = new double[12];
	protected double[] péremption_C_HQ_BE_Limdt = new double[12];


	/**
	 * Cette classe est la plus haute dans l'arbre d'héritage du transformateur 1
	 * On y définit les différentes variables et types construits qui nous seront nécessaires pour toutes les autres classe héritières
	 * Il ne fait qu'initialiser ces mêmes variables et commence la mise en page des journaux
	 */
	public Transformateur1Acteur() {

		//Initialisation des journaux
		this.journal = new Journal("Journal " + this.getNom(), this);
		this.journalStock = new Journal("Journal Stock " + this.getNom(), this);
		this.journalCC = new Journal("Journal CC " + this.getNom(), this);
		this.journalTransactions = new Journal("Journal Transactions " + this.getNom(), this);
		this.journalPeremption = new Journal("Journal Péremption " + this.getNom(), this);




		//On fixe les types de fèves dont on aura besoin
		this.lesFeves = new LinkedList<Feve>();
		this.lesFeves.add(Feve.F_HQ_BE);
		this.lesFeves.add(Feve.F_MQ_E);
		this.lesFeves.add(Feve.F_BQ_E);
		this.lesFeves.add(Feve.F_BQ);


		//On fixe les type de chocolat que l'on va produire
		this.lesChocolats = new LinkedList<Chocolat>();
		this.lesChocolats.add(Chocolat.C_BQ);
		this.lesChocolats.add(Chocolat.C_BQ_E);
		this.lesChocolats.add(Chocolat.C_MQ_E);
		this.lesChocolats.add(Chocolat.C_HQ_BE);




		//Constructions des variables de stocks 
		/**
		 * @author MURY Julien
		 * @author ABBASSI Rayene
		 */
		this.stock_F_BQ =new Variable("F_BQ","<html>Quantite totale de F_BQ en stock</html>", this, 0., 1000000., 5000.);
		this.stock_F_BQ_E = new Variable("F_BQ_E","<html>Quantite totale de F_BQ_E en stock</html>", this, 0., 1000000., 5000.);
		this.stock_F_MQ_E = new Variable("F_MQ_E", "<html>Quantite totale de F_MQ_E en stock</html>", this, 0., 1000000., 5000.);
		this.stock_F_HQ_BE = new Variable("F_HQ_BE", "<html>Quantite totale de F_HQ_BE en stock</html>", this, 0., 1000000., 5000.);

		this.stock_C_BQ=new Variable("EQ4T Stock C_BQ", "<html>Quantite totale de C_BQ en stock</html>",this, 0.0, 1000000.0, 0.0);
		this.stock_C_BQ_E=new Variable("EQ4T Stock C_BQ_E", "<html>Quantite totale de C_BQ_E en stock</html>",this, 0.0, 1000000.0, 0.0);
		this.stock_C_MQ_E=new Variable("EQ4T Stock C_MQ_E", "<html>Quantite totale de C_MQ_E en stock</html>",this, 0.0, 1000000.0, 0.0);
		this.stock_C_HQ_BE=new Variable("EQ4T Stock C_HQ_BE", "<html>Quantite totale de C_HQ_BE en stock</html>",this, 0.0, 1000000.0, 0.0);
		
		this.stock_C_BQ_Limdt=new Variable("EQ4T Stock C_BQ_Limdt", "<html>Quantite totale de C_BQ_Limdt en stock</html>",this, 0.0, 1000000.0, 40000.0);
		this.stock_C_BQ_E_Limdt=new Variable("EQ4T Stock C_BQ_E_Limdt", "<html>Quantite totale de C_BQ_E_Limdt en stock</html>",this, 0.0, 1000000.0, 40000.0);
		this.stock_C_MQ_E_Limdt=new Variable("EQ4T Stock C_MQ_E_Limdt", "<html>Quantite totale de C_MQ_E_Limdt en stock</html>",this, 0.0, 1000000.0, 40000.0);
		this.stock_C_HQ_BE_Limdt=new Variable("EQ4T Stock C_HQ_BE_Limdt", "<html>Quantite totale de C_HQ_BE_Limdt en stock</html>",this, 0.0, 1000000.0, 40000.0);
	

		this.stocksFevesVar = new HashMap<Feve, Variable>();
		this.stocksFevesVar.put(Feve.F_BQ, stock_F_BQ);
		this.stocksFevesVar.put(Feve.F_BQ_E, stock_F_BQ_E);
		this.stocksFevesVar.put(Feve.F_MQ_E, stock_F_MQ_E);
		this.stocksFevesVar.put(Feve.F_HQ_BE, stock_F_HQ_BE);

		this.stocksChocoVar= new HashMap<Chocolat, Variable>();
		this.stocksChocoVar.put(Chocolat.C_BQ, stock_C_BQ);
		this.stocksChocoVar.put(Chocolat.C_BQ_E, stock_C_BQ_E);
		this.stocksChocoVar.put(Chocolat.C_MQ_E, stock_C_MQ_E);
		this.stocksChocoVar.put(Chocolat.C_HQ_BE, stock_C_HQ_BE);


		this.stocksMarqueVar = new HashMap<ChocolatDeMarque, Variable>();

        this.péremption_C_BQ_Limdt = new double[12];
		this.péremption_C_BQ_E_Limdt = new double[12];
		this.péremption_C_MQ_E_Limdt = new double[12];
		this.péremption_C_HQ_BE_Limdt = new double[12];
	
	}

	public void initialiser() {
		
		//On fixe les chocolats de marque que l'on va produire
		//On ne peut pas le faire dans le constructeur de la classe car pour créer un chocolat de marque on a besoin que la filière soit initialisée.
		this.chocolatsLimDt=new LinkedList<ChocolatDeMarque>();
		for (Chocolat c : lesChocolats) {
			int pourcentageCacao =  (int) (Filiere.LA_FILIERE.getParametre("pourcentage min cacao "+c.getGamme()).getValeur());
			ChocolatDeMarque cm= new ChocolatDeMarque(c, "LimDt", pourcentageCacao);
			this.chocolatsLimDt.add(cm);

		}


		//Initialisation des stocks de chocolat de marque
		for (ChocolatDeMarque cm : chocolatsLimDt){
			switch (cm.getChocolat()){
				case C_BQ : 
					stocksMarqueVar.put(cm, stock_C_BQ_Limdt);

					this.péremption_C_BQ_Limdt[0] = stock_C_BQ_Limdt.getValeur();

					for (int i=1; i<12; i++){
						this.péremption_C_BQ_Limdt [i] = 0.0;
					}
					break;
				case C_BQ_E : 
					stocksMarqueVar.put(cm, stock_C_BQ_E_Limdt);

					this.péremption_C_BQ_E_Limdt[0] = stock_C_BQ_E_Limdt.getValeur();

					for (int i=1; i<12; i++){
						this.péremption_C_BQ_E_Limdt [i] = 0.0;
					}
					break;
				case C_MQ_E : 
					stocksMarqueVar.put(cm, stock_C_MQ_E_Limdt);

					this.péremption_C_MQ_E_Limdt[0] = stock_C_MQ_E_Limdt.getValeur();
					
					for (int i=1; i<12; i++){
						this.péremption_C_MQ_E_Limdt [i] = 0.0;
					}
					break;
				case C_HQ_BE : 
					stocksMarqueVar.put(cm, stock_C_HQ_BE_Limdt);

					this.péremption_C_HQ_BE_Limdt[0] = stock_C_HQ_BE_Limdt.getValeur();

					for (int i=1; i<12; i++){
						this.péremption_C_HQ_BE_Limdt [i] = 0.0;
					}
					break;

				default : 

					journalStock.ajouter(Color.pink, Color.BLACK,"Le chocolat " + cm + " ne devrait pas être présent dans notre gammme");

					break;
			}
			this.journalStock.ajouter(Romu.COLOR_LLGRAY, Color.BLACK," stock("+cm+")->"+this.stocksMarqueVar.get(cm).getValeur());
			this.journalStock.ajouter("\n");
		}

	}


	public String getNom() {// NE PAS MODIFIER
		return "EQ4T";
	}
	
	public String toString() {// NE PAS MODIFIER
		return this.getNom();
	}

	////////////////////////////////////////////////////////
	//         En lien avec l'interface graphique         //
	////////////////////////////////////////////////////////

	public void next() {
		
		/*
		this.journalStock.ajouter("Stock de fèves : " + this.totalStocksFeves.getValeur(this.cryptogramme));
		this.journalStock.ajouter("Stock de chocolat : " + this.totalStocksChoco.getValeur(this.cryptogramme));
		this.journalStock.ajouter("Stock de chocolat de marque : " + this.totalStocksChocoMarque.getValeur(this.cryptogramme));
		this.journalStock.ajouter("\n");
		*/

		this.journal.ajouter("Solde : " + this.getSolde());
		this.journal.ajouter("\n");

	}

	public Color getColor() {// NE PAS MODIFIER
		return new Color(229, 243, 157); 
	}

	public String getDescription() {
		return "Transformateur spécialisé dans le chocolat équitable à petits prix";
	}

	// Renvoie les indicateurs
	public List<Variable> getIndicateurs() {
		List<Variable> res = new ArrayList<Variable>();
		
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
		res.add(this.journal);
		res.add(this.journalStock);
		res.add(this.journalTransactions);
		res.add(this.journalCC);
		res.add(this.journalPeremption);
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
		journal.ajouter("Opération sur compte bancaire : " +  montant);
		this.journal.ajouter("\n");
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

	@Override
	public List<String> getMarquesChocolat() {
		LinkedList<String> marques = new LinkedList<String>();
		marques.add("LimDt");
		return marques;
	}



	/**
	 * @author MURY Julien
	 * Cette méthode permet de connaitre notre stock d'un produit bien précis, très utile si on ne veut qu'accéder au stock sans nécessairement faire de modifications.
	 * Si des modifications sont prévues, on utilisera directement les variables.
	 */
	@Override
	public double getQuantiteEnStock(IProduit p, int cryptogramme) {
		if (this.cryptogramme==cryptogramme) { // c'est donc bien un acteur assermente qui demande a consulter la quantite en stock
			if (p instanceof Feve) {
				if (this.lesFeves.contains(p)) {
					return this.stocksFevesVar.get(p).getValeur();
				} else {
					return 0.0;
				}
			} else if (p instanceof Chocolat) {
				if (this.lesChocolats.contains(p)) {
					return this.stocksChocoVar.get(p).getValeur();
				} else {
					return 0.0;
				}
			} else {
				if (this.chocolatsLimDt.contains(p)) {
					return this.stocksMarqueVar.get(p).getValeur();
				} else {
					return 0.0;
				}
			}
		} else {
			return 0; // Les acteurs non assermentes n'ont pas a connaitre notre stock
		}
	}



/**
 * @author MURY Julien
 * Cette méthode permet l'ajout en stock d'une certaine quantité d'un certain produit
 * @param produit : le produit à ajouter
 * @param quantite : la quantité de produit à ajouter 
 */
	public void ajouterAuStock(IProduit produit, double quantite, int cryptogramme){
		//On vérifie que l'acteur voulant accéder aux stocks est bien le bon 
		if (this.cryptogramme == cryptogramme){

			String type = produit.getType();

			if (type.equals("Feve")){
				switch ((Feve)produit){
					case F_BQ : 
						this.stocksFevesVar.get(Feve.F_BQ).ajouter(this, quantite, cryptogramme);
						break;
					case F_BQ_E : 
						this.stocksFevesVar.get(Feve.F_BQ_E).ajouter(this, quantite, cryptogramme);
						break;
					case F_MQ_E : 
						this.stocksFevesVar.get(Feve.F_MQ_E).ajouter(this, quantite, cryptogramme);
						break;
					case F_HQ_BE : 
						this.stocksFevesVar.get(Feve.F_HQ_BE).ajouter(this, quantite, cryptogramme);
						break;
					default : 
						journalStock.ajouter(Color.pink, Color.BLACK, "EQ4T : Ce type de fève n'est pas censée entrer dans nos stocks: " + produit);
				}
				
			}
			else if (type.equals("Chocolat")){
				switch ((Chocolat)produit){
					case C_BQ : 
						this.stocksChocoVar.get(Chocolat.C_BQ).ajouter(this, quantite, cryptogramme);
						break;
					case C_BQ_E : 
						this.stocksChocoVar.get(Chocolat.C_BQ_E).ajouter(this, quantite, cryptogramme);
						break;
					case C_MQ_E : 
						this.stocksChocoVar.get(Chocolat.C_MQ_E).ajouter(this, quantite, cryptogramme);
						break;
					case C_HQ_BE : 
						this.stocksChocoVar.get(Chocolat.C_HQ_BE).ajouter(this, quantite, cryptogramme);
						break;
					default : 
						journalStock.ajouter(Color.pink, Color.BLACK,"EQ4T : Ce type de chocolat n'est pas censée entrer dans nos stocks: " + produit);
				}

			}
			else if (type.equals("ChocolatDeMarque")){
				switch (((ChocolatDeMarque)produit).getChocolat()){
					case C_BQ : 
						this.stocksMarqueVar.get(produit).ajouter(this, quantite, cryptogramme);
						this.péremption_C_BQ_Limdt[0] += quantite;
						break;
					case C_BQ_E : 
						this.stocksMarqueVar.get(produit).ajouter(this, quantite, cryptogramme);
						this.péremption_C_BQ_E_Limdt[0] += quantite;
						break;
					case C_MQ_E : 
						this.stocksMarqueVar.get(produit).ajouter(this, quantite, cryptogramme);
						this.péremption_C_MQ_E_Limdt[0] += quantite;
						break;
					case C_HQ_BE : 
						this.stocksMarqueVar.get(produit).ajouter(this, quantite, cryptogramme);
						this.péremption_C_HQ_BE_Limdt[0] += quantite;
						break;
					default : 
						journalStock.ajouter(Color.pink, Color.BLACK,"EQ4T : Ce type de chocolat n'est pas censée entrer dans nos stocks: " + produit);
				}
			}
			else {
				journalStock.ajouter(Color.pink, Color.BLACK,"Ce produit (" + produit + ") n'a pas un type connu ("+ type + ")");
			}
		}
	}






	

	/**
 * @author MURY Julien
 * Cette méthode permet le retrait du stock d'une certaine quantité d'un certain produit
 * @param produit : le produit à ajouter
 * @param quantite : la quantité de produit à ajouter 
 */
public void retirerDuStock(IProduit produit, double quantite, int cryptogramme){
	//On vérifie que l'acteur voulant accéder aux stocks est bien le bon 
	if (this.cryptogramme == cryptogramme){
		String type = produit.getType();
		if (type.equals("Feve")){
			switch ((Feve)produit){
				case F_BQ : 
					this.stocksFevesVar.get(Feve.F_BQ).retirer(this, quantite, cryptogramme);
					break;
				case F_BQ_E : 
					this.stocksFevesVar.get(Feve.F_BQ_E).retirer(this, quantite, cryptogramme);
					break;
				case F_MQ_E : 
					this.stocksFevesVar.get(Feve.F_MQ_E).retirer(this, quantite, cryptogramme);
					break;
				case F_HQ_BE : 
					this.stocksFevesVar.get(Feve.F_HQ_BE).retirer(this, quantite, cryptogramme);
					break;
				default : 
					journalStock.ajouter(Color.pink, Color.BLACK,"EQ4T : Ce type de fève n'est pas censée entrer dans nos stocks: " + produit);
			}
			
		}
		else if (type.equals("Chocolat")){
			switch ((Chocolat)produit){
				case C_BQ : 
					this.stocksChocoVar.get(Chocolat.C_BQ).retirer(this, quantite, cryptogramme);
					break;
				case C_BQ_E : 
					this.stocksChocoVar.get(Chocolat.C_BQ_E).retirer(this, quantite, cryptogramme);
					break;
				case C_MQ_E : 
					this.stocksChocoVar.get(Chocolat.C_MQ_E).retirer(this, quantite, cryptogramme);
					break;
				case C_HQ_BE : 
					this.stocksChocoVar.get(Chocolat.C_HQ_BE).retirer(this, quantite, cryptogramme);
					break;
				default : 
					journalStock.ajouter(Color.pink, Color.BLACK,"EQ4T : Ce type de chocolat n'est pas censée entrer dans nos stocks: " + produit);
			}

		}
		else if (type.equals("ChocolatDeMarque")){
			switch (((ChocolatDeMarque)produit).getChocolat()){
				case C_BQ : 
					this.stocksMarqueVar.get(produit).retirer(this, quantite, cryptogramme);
					for (int i=11; i>=0; i--){
						if (this.péremption_C_BQ_Limdt[i] > 0 && this.péremption_C_BQ_Limdt[i] - quantite >= 0){
							this.péremption_C_BQ_Limdt[i] -= quantite;
							break;
						}
						else if (this.péremption_C_BQ_Limdt[i] > 0 && this.péremption_C_BQ_Limdt[i] - quantite < 0){
							quantite -= this.péremption_C_BQ_Limdt[i];
							this.péremption_C_BQ_Limdt[i] = 0;
						}
					}
					break;
				case C_BQ_E : 
					this.stocksMarqueVar.get(produit).retirer(this, quantite, cryptogramme);
					for (int i=11; i>=0; i--){
						if (this.péremption_C_BQ_E_Limdt[i] > 0 && this.péremption_C_BQ_E_Limdt[i] - quantite >= 0){
							this.péremption_C_BQ_E_Limdt[i] -= quantite;
							break;
						}
						else if (this.péremption_C_BQ_E_Limdt[i] > 0 && this.péremption_C_BQ_E_Limdt[i] - quantite < 0){
							quantite -= this.péremption_C_BQ_E_Limdt[i];
							this.péremption_C_BQ_E_Limdt[i] = 0;
						}
					}
					break;
				case C_MQ_E : 
					this.stocksMarqueVar.get(produit).retirer(this, quantite, cryptogramme);
					for (int i=11; i>=0; i--){
						if (this.péremption_C_MQ_E_Limdt[i] > 0 && this.péremption_C_MQ_E_Limdt[i] - quantite >= 0){
							this.péremption_C_MQ_E_Limdt[i] -= quantite;
							break;
						}
						else if (this.péremption_C_MQ_E_Limdt[i] > 0 && this.péremption_C_MQ_E_Limdt[i] - quantite < 0){
							quantite -= this.péremption_C_MQ_E_Limdt[i];
							this.péremption_C_MQ_E_Limdt[i] = 0;
						}
					}
					break;
				case C_HQ_BE : 
					this.stocksMarqueVar.get(produit).retirer(this, quantite, cryptogramme);
					for (int i=11; i>=0; i--){
						if (this.péremption_C_HQ_BE_Limdt[i] > 0 && this.péremption_C_HQ_BE_Limdt[i] - quantite >= 0){
							this.péremption_C_HQ_BE_Limdt[i] -= quantite;
							break;
						}
						else if (this.péremption_C_HQ_BE_Limdt[i] > 0 && this.péremption_C_HQ_BE_Limdt[i] - quantite < 0){
							quantite -= this.péremption_C_HQ_BE_Limdt[i];
							this.péremption_C_HQ_BE_Limdt[i] = 0;
						}
					}
					break;
				default : 
					journalStock.ajouter(Color.pink, Color.BLACK,"EQ4T : Ce type de chocolat n'est pas censée entrer dans nos stocks: " + produit);
			}
		}
		else {
			journalStock.ajouter(Color.pink, Color.BLACK,"Ce produit (" + produit + ") n'a pas un type connu ("+ type + ")");
		}
	}
}

}