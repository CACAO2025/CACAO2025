package abstraction.eq7Distributeur1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.awt.Color;

import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IDistributeurChocolatDeMarque;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.appelDOffre.IAcheteurAO;
import abstraction.eqXRomu.clients.ClientFinal;
import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.general.Variable;

public class Distributeur1 extends Distributeur1AcheteurAppelOffre implements IDistributeurChocolatDeMarque {
	
	// défi 1 et 2 par Alexiho
	protected Journal journal; // Déclaration du journal
	protected Journal journalE;  // Déclaration du journal
	protected Journal journalCC; // Déclaration du journal
	protected Journal journalAO; // Déclaration du journal
	// protected Map<ChocolatDeMarque, Variable> stocksChocolats; // Table de hachage pour stocker les quantités de chocolat
	//protected List<ChocolatDeMarque> chocolats;
	protected List<Double> prix;
	protected List<Double> capaciteDeVente;
	protected IAcheteurAO identity;
	//protected List<Integer> successedSell = new ArrayList<Integer>();
	//protected List<Double> priceProduct = new ArrayList<Double>();
	//protected List<Double> requiredQuantities  = new ArrayList<Double>();
	protected int step = 0;
	protected String name = "HexaFridge";
	protected Color color = new Color(162, 207, 238);
	


    public Distributeur1() {
		super();
        
        this.journal = new Journal("Journal stock de EQ7", this); // Initialisation du journal
		this.journalE = new Journal("Journal d'enchères de EQ7", this); // Initialisation du journal
		this.journalCC = new Journal("Journal contrat cadre de EQ7", this);
		this.journalAO = new Journal("Journal appel d'offre de EQ7", this);

		
		predictionsVentesPourcentage = Arrays.asList(3.6 , 3.6 , 5.0 , 3.6 , 3.6 , 3.6 , 3.6 , 7.0 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 3.6 , 13.0);

		this.prix = new ArrayList<>();
		this.capaciteDeVente = new ArrayList<>();
    }

	@Override
	public void initialiser() // par Alexiho
	{
		this.chocolats= Filiere.LA_FILIERE.getChocolatsProduits();

		for (int i=0; i<this.chocolats.size(); i++) {
			this.stocksChocolats.put(chocolats.get(i), new Variable("Stock"+chocolats.get(i).getNom(), this, 1000.0));
			successedSell.add(0);
			priceProduct.add(1000.0);
			requiredQuantities.add(0.0);
			this.prix.add(10.0);
			this.capaciteDeVente.add(0.0);}

		for (int i = 0; i < this.chocolats.size(); i++) {
			this.stocksChocolats.put(chocolats.get(i), new Variable(this.getNom()+"Stock"+chocolats.get(i).getNom(), this, 1000.0));
			this.capaciteDeVente.set(i, stocksChocolats.get(chocolats.get(i)).getValeur()/1.5);
		}
	}

	@Override
	public double prix(ChocolatDeMarque choco) { // par Alexiho
		//ChocolatDeMarque chocoM = new ChocolatDeMarque(choco.getChocolat(), "Villors", 90);
		int pos= (chocolats.indexOf(choco));
		if (pos<0) {
			return 8880.0;
		} else {
			//return prix.get(pos);
			double price = 5*this.priceProduct.get(pos) ;
			return price;
		}
	}

	@Override
	public void next() // par Alexiho
	{
		//List<Double> requiredQuantities = new ArrayList<>();
		//Distributeur1Stock acteurStock = new Distributeur1Stock();
		int step = Filiere.LA_FILIERE.getEtape(); // Récupération du numéro de l'étape
		for (int i=0; i<chocolats.size(); i++){
			requiredQuantities.set(i, this.VolumetoBuy(chocolats.get(i),this.cryptogramme)*0.95);
		}
		
		if (step%8==0){
			//IAcheteurContratCadre acheteurContratCadre = new Distributeur1AcheteurContratCadre();
			this.next_cc();
			for (int i = 0 ; i<chocolats.size() ; i++){
				requiredQuantities.set(i, Math.max(requiredQuantities.get(i)/19,5));
			}}
		
		//IAcheteurAO acheteurAppelOffre = new Distributeur1AcheteurAppelOffre();
		this.next_ao();
		

		//ChocolatDeMarque produit = new ChocolatDeMarque(Chocolat.C_MQ, "Villors", 50); // Produit choisi
        //double quantiteAjoutee = 100.0; // 100 tonnes

		//capaciteDeVente.set(3, 100.0);

        //journal.ajouter("Étape " + etape + " : Ajout de " + quantiteAjoutee + " t de " + produit + " en rayon.");
		
		//par Ethan
		String str_journal_stock = "";
		String str_journal_E = "";
		String str_journal_CC = "";
		String str_journal_AO = "";

		for (int i = 0 ; i<chocolats.size() ; i++){
			str_journal_stock += this.stocksChocolats.get(chocolats.get(i)).getNom() + " = " + this.stocksChocolats.get(chocolats.get(i)).getValeur() + " ; ";
			str_journal_E += this.successedSell.get(i) + " ; ";
			str_journal_CC += "b";
			str_journal_AO += " ; ";
		}

		journal.ajouter(str_journal_stock);
		journalE.ajouter(str_journal_E);
		journalCC.ajouter(str_journal_CC);
		journalAO.ajouter(str_journal_AO);

		// définition des capacités de ventes

		for (int i=0; i<this.chocolats.size(); i++) {
			this.capaciteDeVente.set(i, stocksChocolats.get(chocolats.get(i)).getValeur()/2);
		}

	}

	public List<String> getMarquesChocolat() { // par Alexiho
		List<String> marques = new ArrayList<String>();
		marques.add("Hexafridge");
		return marques;
	}

	@Override
	public double quantiteEnVente(ChocolatDeMarque choco, int crypto) { // par Alexiho
		if (crypto!=this.cryptogramme) {
			journal.ajouter("Quelqu'un essaye de me pirater !");
			return 0.0;
		} else {
			//ChocolatDeMarque chocoM = new ChocolatDeMarque(choco.getChocolat(), "Villors", 90);
			int pos= (chocolats.indexOf(choco));
			//journal.ajouter("pos : " + pos + "chocoval : " + this.getStock(chocoM).getValeur() + "capa : " + capaciteDeVente.get(pos));
			if (pos<0) {
				return 0.0;
			} else {
				return Math.min(capaciteDeVente.get(pos), this.getStock(choco).getValeur());
			}
		}
	}

	@Override
	// On met 10% de ce tout ce qu'on met en vente (on pourrait mettre l'accente sur
	// un produit a promouvoir mais il s'agit ici d'un exemple simpliste
	public double quantiteEnVenteTG(ChocolatDeMarque choco, int crypto) { // par Alexiho
		if (crypto!=this.cryptogramme) {
			journal.ajouter("Quelqu'un essaye de me pirater !");
			return 0.0;
		} else {
			//ChocolatDeMarque chocoM = new ChocolatDeMarque(choco.getChocolat(), "Villors", 90);
			int pos= (chocolats.indexOf(choco));
			if (pos<0) {
				return 0.0;
			} else {
				return Math.min(capaciteDeVente.get(pos), this.getStock(choco).getValeur())/10.0;
			}
		}
	}

	@Override
	public void vendre(ClientFinal client, ChocolatDeMarque choco, double quantite, double montant, int crypto) { // par Alexiho
		int pos= (chocolats.indexOf(choco));
		if (pos>=0) {
			this.getStock(choco).retirer(this, quantite);
		}
	}
	@Override
	public Variable getStock(ChocolatDeMarque c) { // par Alexiho
		return this.stocksChocolats.get(c);
	}

	@Override
	public Map<ChocolatDeMarque, Variable> getStocksChocolats() { // par Alexiho
		return this.stocksChocolats;
	}
	@Override
	public double getQuantiteEnStock(IProduit p, int cryptogramme) { // par Alexiho
		if (this.cryptogramme==cryptogramme) {
			for (ChocolatDeMarque c : this.stocksChocolats.keySet()) {
				if (c.equals(p)) {
					return this.stocksChocolats.get(c).getValeur();
				}
			}
			return 0;
		} else {
			return 0;
		}
	}

	public void notificationRayonVide(ChocolatDeMarque choco) {
		journal.ajouter(" Aie... j'aurais du mettre davantage de "+choco.getNom()+" en vente");
	}
	@Override
	public void notificationRayonVide(ChocolatDeMarque choco, int crypto)
	{
		journal.ajouter(" Aie... j'aurais du mettre davantage de "+choco.getNom()+" en vente");
	}
	@Override
	// Renvoie les journaux
	public List<Journal> getJournaux() { // par Alexiho
		List<Journal> res=new ArrayList<Journal>();
		res.add(journal);
		res.add(journalE);
		res.add(journalCC);
		res.add(journalAO);
		return res;
	}
	
}