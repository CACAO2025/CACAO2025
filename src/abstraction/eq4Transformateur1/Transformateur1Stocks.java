package abstraction.eq4Transformateur1;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List; 

import abstraction.eqXRomu.acteurs.Romu;
import abstraction.eqXRomu.bourseCacao.BourseCacao;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IFabricantChocolatDeMarque;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.general.Variable;

public class Transformateur1Stocks extends Transformateur1Acteur implements IFabricantChocolatDeMarque {

	//Des variables qui ne seront au final que des constantes lors de la simulation
	private double coutStockage; 
	private double coutProd;
	protected double STOCK_MAX_TOTAL_FEVES = 1000000;

	//Listes regroupant les contrats cadres actifs
	protected List<ExemplaireContratCadre> mesContratEnTantQuAcheteur;
	protected List<ExemplaireContratCadre> mesContratEnTantQueVendeur;

	private List<ChocolatDeMarque> chocosProduits; // la liste de toutes les sortes de ChocolatDeMarque que l'acteur produit et peut vendre
	protected HashMap<Feve, HashMap<Chocolat, Double>> pourcentageTransfo; // pour les differentes feves, le chocolat qu'elles peuvent contribuer a produire avec le ratio qttChocoProduit/qttFevesUtilisée

	//Des tables de hachages pour connaître l'état des chocolats à une période précise
	protected HashMap<Feve, Double> qttEntrantesFeve;//Contient les quantités entrant dans le stock à la période actuelle
	protected HashMap<Feve, Double> prixTFeveStockee;//Contient les prix moyens des fèves en stock
	protected HashMap<Chocolat, Double> prixTChocoBase;//Contient les prix des chocolats produits en s'appuyant sur le prix du stock de fèves
	protected HashMap<Chocolat, Double> qttSortantesChoco;
	protected HashMap<Chocolat, Double> marges;


	public Transformateur1Stocks() {
		super();

		this.chocosProduits = new LinkedList<ChocolatDeMarque>();
		this.pourcentageTransfo = new HashMap<Feve, HashMap<Chocolat, Double>>();
		

		this.qttEntrantesFeve = new HashMap<Feve, Double>();
		this.prixTFeveStockee = new HashMap<Feve, Double>();
		this.prixTChocoBase = new HashMap<Chocolat, Double>();
		this.qttSortantesChoco = new HashMap<Chocolat, Double>();

		this.marges = new HashMap<Chocolat, Double>();

	}





	
	public void initialiser() {
		super.initialiser();

		//Initialisation des prix de nos stocks de fève
		this.prixTFeveStockee.put(Feve.F_BQ, 2000.);
		this.prixTFeveStockee.put(Feve.F_BQ_E, 2000.);
		this.prixTFeveStockee.put(Feve.F_MQ_E, 2000.);
		this.prixTFeveStockee.put(Feve.F_HQ_BE, 2000.);

		this.coutStockage = Filiere.LA_FILIERE.getParametre("cout moyen stockage producteur").getValeur()*4;
		this.coutProd = 4000; //A MODIFIER il s'agit du cout de la production d'une tonne de chocolat, valeur arbitraire censée contenir salaires, ingrédients secondaires, et autres couts fixes

		//Initialisation des prix de base des chocolats que l'on veut produire
		this.prixTChocoBase.put(Chocolat.C_BQ, 2000.);
		this.prixTChocoBase.put(Chocolat.C_BQ_E, 2000.);
		this.prixTChocoBase.put(Chocolat.C_HQ_BE, 2000.);
		this.prixTChocoBase.put(Chocolat.C_MQ_E, 2000.);
		

		//Initialisation des marges que l'on va faire sur les différents produits
		this.marges.put(Chocolat.C_BQ, 1.5);
		this.marges.put(Chocolat.C_BQ_E, 1.16);
		this.marges.put(Chocolat.C_MQ_E, 1.16);
		this.marges.put(Chocolat.C_HQ_BE, 1.3);


		//Initialisation des pourcentage de conversion fèves vers chocolat
		this.pourcentageTransfo.put(Feve.F_HQ_BE, new HashMap<Chocolat, Double>());
		double conversion = 1.0 + (100.0 - Filiere.LA_FILIERE.getParametre("pourcentage min cacao HQ").getValeur())/100.0;
		this.pourcentageTransfo.get(Feve.F_HQ_BE).put(Chocolat.C_HQ_BE, conversion);// la masse de chocolat obtenue est plus importante que la masse de feve vue l'ajout d'autres ingredients

		this.pourcentageTransfo.put(Feve.F_MQ_E, new HashMap<Chocolat, Double>());
		conversion = 1.0 + (100.0 - Filiere.LA_FILIERE.getParametre("pourcentage min cacao MQ").getValeur())/100.0;
		this.pourcentageTransfo.get(Feve.F_MQ_E).put(Chocolat.C_MQ_E, conversion);

		this.pourcentageTransfo.put(Feve.F_BQ, new HashMap<Chocolat, Double>());
		conversion = 1.0 + (100.0 - Filiere.LA_FILIERE.getParametre("pourcentage min cacao BQ").getValeur())/100.0;
		this.pourcentageTransfo.get(Feve.F_BQ).put(Chocolat.C_BQ, conversion);

		this.pourcentageTransfo.put(Feve.F_BQ_E, new HashMap<Chocolat, Double>());
		this.pourcentageTransfo.get(Feve.F_BQ_E).put(Chocolat.C_BQ_E, conversion);


		this.journalStock.ajouter(Romu.COLOR_LLGRAY, Color.PINK, "Stock initial chocolat de marque : ");

		this.journalCC.ajouter(Color.orange, Color.BLACK, "Les achats seront en marron;");
		this.journalCC.ajouter(Color.orange, Color.BLACK, "Les ventes LimDt en mauve;");
		this.journalCC.ajouter(Color.orange, Color.BLACK, "Et les autres ventes en vert.");
		this.journalCC.ajouter("\n");

		this.journalTransactions.ajouter(Color.orange, Color.BLACK, "Les achats en bourse seront en magenta;");
		this.journalTransactions.ajouter(Color.orange, Color.BLACK, "Les ventes aux enchères en gris foncé;");
		this.journalTransactions.ajouter(Color.orange, Color.BLACK, "Et les ventes AO en rouge.");
		this.journalTransactions.ajouter("\n");

		//Initialisation des quantités de fève entrantes
		this.qttEntrantesFeve.put(Feve.F_BQ, 0.);
		this.qttEntrantesFeve.put(Feve.F_BQ_E, 0.);
		this.qttEntrantesFeve.put(Feve.F_HQ_BE, 0.);
		this.qttEntrantesFeve.put(Feve.F_MQ_E, 0.);
	}





	////////////////////////////////////////////////////////
	//      En lien avec la comptabilité et production    //
	////////////////////////////////////////////////////////




	/**
	 * @author ABBASSI Rayene
	 * Cette méthode transforme une partie du stock de fève en chacun des chocolats que l'on a décidé de produire.
	 * Elle détermine et place dans la HashMap prixTChocoBase le prix sans marge des chocolats produits en se basant sur le prix du stock de fèves
	 * @param None
	 * @return None
	 */
	protected void transformation(){

		for (Feve f : lesFeves) {
			for (Chocolat c : lesChocolats) {

				double transfo;
				if (this.stocksFevesVar.get(f) != null && this.pourcentageTransfo.get(f).get(c) != null){
					//On transforme toutes nos fèves
					transfo = this.stocksFevesVar.get(f).getValeur();

					//On s'assure que l'on produit quelque chose pour faire nos opérations
					if (transfo<=this.getQuantiteEnStock(f, this.cryptogramme) && transfo >0) {



						double pourcentageMarque = 1.0;  //Modifiable
						// La Pourcentage ainsi definie sera stockee sous forme de marquee, la quantité restante sera alors stockee comme non marquee

	
						
						//A MODIFIER
						int pourcentageCacao =  (int) (Filiere.LA_FILIERE.getParametre("pourcentage min cacao "+c.getGamme()).getValeur());
						ChocolatDeMarque cm= new ChocolatDeMarque(c, "LimDt", pourcentageCacao);
						
						//calcul du stock de chocolat après les transformations
						double nouveauStock = transfo*this.pourcentageTransfo.get(f).get(c);

						//Détermination du prix de base des chocolats à la tonne en pondérant avec les coûts de la période précédente
						if(prixTChocoBase.containsKey(c) && nouveauStock > 0){
							double ancienPrix = prixTChocoBase.get(c);
							double nouveauPrix = ancienPrix * ((getQuantiteEnStock(c, this.cryptogramme) + getQuantiteEnStock(cm, this.cryptogramme))/ (nouveauStock+getQuantiteEnStock(c, this.cryptogramme) + getQuantiteEnStock(cm, this.cryptogramme))) + (prixTFeveStockee.get(f) + coutProd + coutStockage) * (pourcentageTransfo.get(f).get(c) * transfo / (nouveauStock+ getQuantiteEnStock(c, this.cryptogramme) + getQuantiteEnStock(cm, this.cryptogramme)));

							prixTChocoBase.put(c, nouveauPrix);
						}

						//Ajout des chocolats produits au stock
						this.retirerDuStock(f, transfo, this.cryptogramme);

						//this.ajouterAuStock(c, nouveauStock * (1.0-pourcentageMarque), this.cryptogramme);
						this.ajouterAuStock(cm, nouveauStock * pourcentageMarque, this.cryptogramme);

						
						//Notification dans le journal
						this.journal.ajouter(Romu.COLOR_LLGRAY, Color.PINK, "Transfo de "+(transfo<10?" "+transfo:transfo)+" T de "+f+" en "+Journal.doubleSur(transfo*this.pourcentageTransfo.get(f).get(c),3,2)+" T de "+c);
						this.journal.ajouter(Romu.COLOR_LLGRAY, Color.BLACK," stock("+f+")->"+this.getQuantiteEnStock(f, this.cryptogramme));
						this.journal.ajouter(Romu.COLOR_LLGRAY, Color.BLACK," stock("+c+")->"+this.getQuantiteEnStock(c, this.cryptogramme));
						this.journal.ajouter(Romu.COLOR_LLGRAY, Color.BLACK," stock("+cm+")->"+this.getQuantiteEnStock(cm, this.cryptogramme));
						this.journal.ajouter("\n");
					}
				}
			}
		}
	}










	/**
	 * @author MURY Julien
	 * Une méthode qui permet de déterminer la quantitié de fèves entrant dans le stock à la période actuelle selon les contrats négociés et achats en bourse
	 * Les résultats sont stockés dans la HashMap qttEntrantes
	 * @param None
	 * @return None
	 */
	public void determinerQttEntrantFeves(){

		//Quantité entrante de fèves f par contrat cadre
		for (Feve f : pourcentageTransfo.keySet()){
			for (ExemplaireContratCadre cc : mesContratEnTantQuAcheteur){
				if (cc.getProduit() == f){
					if (this.qttEntrantesFeve.containsKey(f)){
						this.qttEntrantesFeve.put(f, cc.getPrix()+qttEntrantesFeve.get(f));
					}
					else {
						this.qttEntrantesFeve.put(f, cc.getPrix());
					}
				}
			}
		}

		//Quantité entrante de fèves par achat en bourse
		qttEntrantesFeve.put(Feve.F_BQ, 80.);



	}




/**
 * @author MURY Julien
 */
	public void determinerQttSortantChoco(){
		//Qtt sortante par contrat cadre
		for (Chocolat c : lesChocolats){
			for (ExemplaireContratCadre cc : mesContratEnTantQueVendeur){
				if (cc.getProduit().equals(c) || ((ChocolatDeMarque)cc.getProduit()).getChocolat().equals(c)){
					if (this.qttSortantesChoco.containsKey(c)){
						this.qttSortantesChoco.put(c, cc.getPrix()+qttSortantesChoco.get(c));
					}
					else {
						this.qttSortantesChoco.put(c, cc.getPrix());
					}
				}
			}
		}

		//Quantité sortante par enchère
		

		//Quantité sortante par appel d'offre
	}





	/**
	 * @author MURY Julien
	 * Une méthode qui permet de déterminer les cout en fèves entrantes dans le stock à la période actuelle selon les contrats négociés et achats en bourse
	 * Les résultats sont stockés dans la HashMap qttEntrantes
	 * On amalgame le prix moyen total du stock avec le prix à la tonne des fèves qui entrent dans notre stock
	 * @param None 
	 * @return None
	 */
	public void determinerPrixTFevesStockees(){
		determinerQttEntrantFeves();
		
		for (Feve f : lesFeves){

			//On calcule le prix pour les fèves issues des contrats cadres
			double prix = 0.;
			for (ExemplaireContratCadre cc : mesContratEnTantQuAcheteur){
				if (cc.getProduit() == f){

					//On calcule le prix moyen de ce qui entre en pondérant par la part que représente ce contrat dans la qtt totale entrante (si tant est que le nombre de fèves qui entrent est non nul)
					if (qttEntrantesFeve.get(f) != 0.) prix += cc.getPrix() * (cc.getQuantiteALivrerAuStep()/qttEntrantesFeve.get(f));

				}
			}
			//On calcule le prix pour les ajouts par bourse
			if (f == Feve.F_BQ){
				BourseCacao bourse = (BourseCacao) Filiere.LA_FILIERE.getActeur("BourseCacao");	
				if (qttEntrantesFeve.get(f) != 0.) prix += bourse.getCours(f).getValeur() * (80. / qttEntrantesFeve.get(f));
			}
			if ((getQuantiteEnStock(f, this.cryptogramme)+ qttEntrantesFeve.get(f)) != 0.){
				double ancienPrixPondere =lesFeves.contains(f)? prixTFeveStockee.get(f)*(getQuantiteEnStock(f, this.cryptogramme)/(getQuantiteEnStock(f, this.cryptogramme)+ qttEntrantesFeve.get(f))) : 0.;
				prixTFeveStockee.put(f, ancienPrixPondere + prix*(qttEntrantesFeve.get(f)/(qttEntrantesFeve.get(f)+getQuantiteEnStock(f, this.cryptogramme))));
			}
			
		}
	}











	////////////////////////////////////////////////////////
	//         En lien avec l'interface graphique         //
	////////////////////////////////////////////////////////

	public void next() {
		super.next();

		this.journal.ajouter(Color.yellow, Romu.COLOR_LBLUE, "N° Etape " + Filiere.LA_FILIERE.getEtape());

		this.journalStock.ajouter("\n");
		this.journalStock.ajouter(Color.yellow, Romu.COLOR_LBLUE, "N° Etape " + Filiere.LA_FILIERE.getEtape());

		this.journalCC.ajouter(Color.yellow, Romu.COLOR_LBLUE, "N° Etape " + Filiere.LA_FILIERE.getEtape());

		this.journalTransactions.ajouter(Color.yellow, Romu.COLOR_LBLUE, "N° Etape " + Filiere.LA_FILIERE.getEtape());

		this.journalPeremption.ajouter("\n");
		this.journalPeremption.ajouter(Color.yellow, Romu.COLOR_LBLUE, "N° Etape " + Filiere.LA_FILIERE.getEtape());

		//OBSOLETE ou A MODIFIER
		/*
		this.journalStock.ajouter("Stock de fèves : " + this.totalStocksFeves.getValeur(this.cryptogramme));
		this.journalStock.ajouter("Stock de chocolat : " + this.totalStocksChoco.getValeur(this.cryptogramme));
		this.journalStock.ajouter("\n");
		*/

		//Affichage des stocks de chaque produit dans le journalStock à la période présente 
		this.journal.ajouter("=== STOCKS === ");
		for (Feve f : this.lesFeves) {
			this.journalStock.ajouter(Romu.COLOR_LLGRAY, Color.BLACK, "Stock de "+Journal.texteSurUneLargeurDe(f+"", 15)+" = "+this.getQuantiteEnStock(f, this.cryptogramme));
		}
		this.journalStock.ajouter("\n");

		for (Chocolat c : this.lesChocolats) {
			this.journalStock.ajouter(Romu.COLOR_LLGRAY, Color.BLACK, "Stock de "+Journal.texteSurUneLargeurDe(c+"", 15)+" = "+this.getQuantiteEnStock(c, this.cryptogramme));
		}
		this.journalStock.ajouter("\n");

		for (ChocolatDeMarque cm : this.chocolatsLimDt) {
			this.journalStock.ajouter(Romu.COLOR_LLGRAY, Color.BLACK, "Stock de "+Journal.texteSurUneLargeurDe(cm+"", 15)+" = "+this.getQuantiteEnStock(cm, this.cryptogramme));
		}
		this.journalStock.ajouter("\n");

		this.journalPeremption.ajouter(Romu.COLOR_LLGRAY, Color.BLACK, "Péremption C_BQ_Limdt : ");
		for (int i=0; i<12; i++) {
			this.journalPeremption.ajouter(Romu.COLOR_LLGRAY, Color.BLACK, i+" : "+this.péremption_C_BQ_Limdt[i]);
		}
		this.journalPeremption.ajouter("\n");

		this.journalPeremption.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_GREEN, "Péremption C_BQ_E_Limdt : ");
        for (int i=0; i<12; i++) {
			this.journalPeremption.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_GREEN, i+" : "+this.péremption_C_BQ_E_Limdt[i]);
		}
		this.journalPeremption.ajouter("\n");

		this.journalPeremption.ajouter(Romu.COLOR_LLGRAY, Color.BLUE, "Péremption C_MQ_E_Limdt : ");
		for (int i=0; i<12; i++) {
			this.journalPeremption.ajouter(Romu.COLOR_LLGRAY, Color.BLUE, i+" : "+this.péremption_C_MQ_E_Limdt[i]);
		}
		this.journalPeremption.ajouter("\n");

		this.journalPeremption.ajouter(Romu.COLOR_LLGRAY, Color.RED, "Péremption C_HQ_BE_Limdt : ");
		for (int i=0; i<12; i++) {
			this.journalPeremption.ajouter(Romu.COLOR_LLGRAY, Color.RED, i+" : "+this.péremption_C_HQ_BE_Limdt[i]);
		}
		this.journalPeremption.ajouter("\n");

		this.determinerPrixTFevesStockees();
		this.transformation();

		// Respect de la règle de péremption après 6 mois soit 12 nexts
		for (ChocolatDeMarque cm : chocolatsLimDt){
			switch (cm.getChocolat()){
				case C_BQ : 
				    if (péremption_C_BQ_Limdt[11] > 0) {
                        stocksMarqueVar.get(cm).retirer(this, péremption_C_BQ_Limdt[11], this.cryptogramme);
						this.journalPeremption.ajouter(Color.pink, Color.BLACK, "Péremption: On retire "+this.péremption_C_BQ_Limdt[11]+ " tonnes de "+cm+" de notre stock");
					}

					for (int i=11; i>=1; i--) {
						péremption_C_BQ_Limdt[i] = péremption_C_BQ_Limdt[i-1];
					}
					this.péremption_C_BQ_Limdt[0] = 0;
					break;
				case C_BQ_E : 
				    if (péremption_C_BQ_E_Limdt[11] > 0) {
						stocksMarqueVar.get(cm).retirer(this, péremption_C_BQ_E_Limdt[11], this.cryptogramme);
					    this.journalPeremption.ajouter(Color.pink, Romu.COLOR_GREEN, "Péremption: On retire "+this.péremption_C_BQ_E_Limdt[11]+ " tonnes de "+cm+" de notre stock");
					}

					for (int i=11; i>=1; i--) {
						péremption_C_BQ_E_Limdt[i] = péremption_C_BQ_E_Limdt[i-1];
					}
					this.péremption_C_BQ_E_Limdt[0] = 0;
					break;
				case C_MQ_E : 
				    if (péremption_C_MQ_E_Limdt[11] > 0) {
						stocksMarqueVar.get(cm).retirer(this, péremption_C_MQ_E_Limdt[11], this.cryptogramme);
					    this.journalPeremption.ajouter(Color.pink, Color.BLUE, "Péremption: On retire "+this.péremption_C_MQ_E_Limdt[11]+ " tonnes de "+cm+" de notre stock");
					}

					for (int i=11; i>=1; i--) {
						péremption_C_MQ_E_Limdt[i] = péremption_C_MQ_E_Limdt[i-1];
					}
					this.péremption_C_MQ_E_Limdt[0] = 0;
					break;
				case C_HQ_BE :
				    if (péremption_C_HQ_BE_Limdt[11] > 0) {
						stocksMarqueVar.get(cm).retirer(this, péremption_C_HQ_BE_Limdt[11], this.cryptogramme);
					    this.journalPeremption.ajouter(Color.pink, Color.RED, "Péremption: On retire "+this.péremption_C_HQ_BE_Limdt[11]+ " tonnes de "+cm+" de notre stock");
					} 

					for (int i=11; i>=1; i--) {
						péremption_C_HQ_BE_Limdt[i] = péremption_C_HQ_BE_Limdt[i-1];
					}
					this.péremption_C_HQ_BE_Limdt[0] = 0;
					break;

				default : 
					this.journalStock.ajouter(Color.pink, Color.BLACK, "Le chocolat " + cm + " ne devrait pas être présent dans notre gammme");
					break;
			}
		}
		//Calcul des stocks globaux pour payer le cout du stockage
		double totalStocks = 0;
		for (Feve f : this.lesFeves){
			totalStocks += this.getQuantiteEnStock(f, this.cryptogramme);
		}
		for (Chocolat c : lesChocolats){
			totalStocks += this.getQuantiteEnStock(c, this.cryptogramme);
		}
		for (ChocolatDeMarque cm : chocolatsLimDt){
			totalStocks += this.getQuantiteEnStock(cm, this.cryptogramme);
		}


		Filiere.LA_FILIERE.getBanque().payerCout(this, cryptogramme, "Stockage", (totalStocks*this.coutStockage));

		//System.out.println("Voici nos prix : " + prixTChocoBase);
	}







	public List<Variable> getIndicateurs(){
		List<Variable> res = super.getIndicateurs();

		res.add(this.stock_F_BQ);
		res.add(this.stock_F_BQ_E);
		res.add(this.stock_F_MQ_E);
		res.add(this.stock_F_HQ_BE);
		res.add(this.stock_C_BQ);
		res.add(this.stock_C_BQ_E);
		res.add(this.stock_C_MQ_E);
		res.add(this.stock_C_HQ_BE);
		res.add(this.stock_C_BQ_Limdt);
		res.add(this.stock_C_BQ_E_Limdt);
		res.add(this.stock_C_MQ_E_Limdt);
		res.add(this.stock_C_HQ_BE_Limdt);


		return res;
	}



	////////////////////////////////////////////////////////
	//        Pour la creation de filieres de test        //
	////////////////////////////////////////////////////////


	public List<String> getMarquesChocolat() {
		LinkedList<String> marques = new LinkedList<String>();
		marques.add("LimDt");
		return marques;
	}

	public List<ChocolatDeMarque> getChocolatsProduits() {
		if (this.chocosProduits.size()==0) {
			for (Chocolat c : Chocolat.values()) {
				int pourcentageCacao =  (int) (Filiere.LA_FILIERE.getParametre("pourcentage min cacao "+c.getGamme()).getValeur());
				this.chocosProduits.add(new ChocolatDeMarque(c, "LimDt", pourcentageCacao));
			}
		}
		return this.chocosProduits;
	}

}
