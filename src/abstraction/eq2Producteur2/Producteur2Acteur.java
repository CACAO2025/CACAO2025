package abstraction.eq2Producteur2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariableReadOnly;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;

public class Producteur2Acteur implements IActeur {
	
	protected HashMap<Feve,Double> prodParStep;
	protected HashMap<Feve,Variable> stock;
	protected int cryptogramme;
	protected Variable stockTotal;
	private static final double PART=0.1;  // La part de marche initiale
	private double coutStockage;
	private int numero = 0;
	protected Journal num = new Journal("Journal Eq2", this);

	public Producteur2Acteur() {

		this.stock = new HashMap<Feve, Variable>();
		this.prodParStep = new HashMap<Feve, Double>();
		this.prodParStep.put(Feve.F_HQ_BE, PART*0.0);
		this.prodParStep.put(Feve.F_HQ_E, PART*0.0);
		this.prodParStep.put(Feve.F_MQ_E, PART*0.0);
		this.prodParStep.put(Feve.F_MQ, PART*2000.0);
		this.prodParStep.put(Feve.F_BQ_E, PART*0.0);
		this.prodParStep.put(Feve.F_BQ, PART*0.0);

		double totalInitialStock = 0.0;
		for (Feve f : Feve.values()) {
            double initialStock = prodParStep.get(f) * 6;
            this.stock.put(f, new VariableReadOnly(this+"Stock"+f.toString().substring(2), "<html>Stock de feves "+f+"</html>",this, 0.0, prodParStep.get(f)*24, initialStock));
            totalInitialStock += initialStock;
        }
        
        this.stockTotal = new Variable("Stock total", "Quantité totale de fèves en stock", this, totalInitialStock);
    }
	
	public void initialiser() {
		this.coutStockage = Filiere.LA_FILIERE.getParametre("cout moyen stockage producteur").getValeur();
	}

	public String getNom() {// NE PAS MODIFIER
		return "EQ2";
	}
	
	public String toString() {// NE PAS MODIFIER
		return this.getNom();
	}
	
	////////////////////////////////////////////////////////
	//         En lien avec l'interface graphique         //
	////////////////////////////////////////////////////////

	public void next() {
		num.ajouter("Numero : " + numero);
		numero++;
		SetStock();
	
	}


	public void SetStock(){

		double totalStock=0.0;


		for (Feve f : Feve.values()) {
			this.stock.get(f).ajouter(this, this.prodParStep.get(f), cryptogramme);
			totalStock+=this.stock.get(f).getValeur();
			this.stockTotal.setValeur(this, totalStock);
		}
		Filiere.LA_FILIERE.getBanque().payerCout(this, cryptogramme, "Stockage", totalStock*this.coutStockage);
		
	}

	public Color getColor() {// NE PAS MODIFIER
		return new Color(244, 198, 156); 
	}

	public String getDescription() {
		return "Bla bla bla";
	}

	// Renvoie les indicateurs
	public List<Variable> getIndicateurs() {
		List<Variable> res = new ArrayList<Variable>();
		res.addAll(this.stock.values());
		res.add(this.stockTotal);
		return res;
	}

	// Renvoie les parametres
	public List<Variable> getParametres() {
		List<Variable> res=new ArrayList<Variable>();
		return res;
	}

	// Renvoie les journaux
	//public List<Journal> getJournaux() {
	//	List<Journal> res=new ArrayList<Journal>();
	//	res.add(num);;
	//	return res;
	//}

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
		if (this.cryptogramme==cryptogramme && this.stock.keySet().contains(p)) { 
			return this.stock.get(p).getValeur((Integer)cryptogramme);
		} else {
			return 0; // Les acteurs non assermentes n'ont pas a connaitre notre stock
		}
	}

	@Override
	public List<Journal> getJournaux() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getJournaux'");
	}
}
