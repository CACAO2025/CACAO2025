package abstraction.eq9Distributeur3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariablePrivee;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;

// Auteur : Héloïse
public class Distributeur3Acteur implements IActeur {
	
	protected int cryptogramme;
	protected Journal journalActeur;
	protected Journal journalDeVente;
	protected Journal journalContrats;
	protected Journal journalStocks;
	protected Journal journalCharges;
	protected Journal valeurMoyennes;
	protected Journal journalPrintContrat;


	protected HashMap<ChocolatDeMarque, Double> stockChocoMarque;
	//protected List<ChocolatDeMarque> chocolatsVillors;


	public Distributeur3Acteur() {

		this.journalActeur = new Journal("journal équipe 9 gestion des stocks",this);
		this.journalDeVente = new Journal("journal de vente équipe 9",this);
		this.journalContrats = new Journal("Journal de contrats cadre",this);
		this.journalStocks = new Journal("Journal stocks",this);
		this.journalCharges = new Journal("Journal des charges",this);
		this.valeurMoyennes = new Journal("Valeur moyenne",this);
		this.journalPrintContrat = new Journal("Journal print contrats",this);
	}
	
	public void initialiser() {

	}

	public String getNom() {// NE PAS MODIFIER
		return "EQ9";
	}
	
	public String toString() {// NE PAS MODIFIER
		return this.getNom();
	}

	////////////////////////////////////////////////////////
	//         En lien avec l'interface graphique         //
	////////////////////////////////////////////////////////

	public void next() {
		journalActeur.ajouter("étape : "+Filiere.LA_FILIERE.getEtape());
	}

	public Color getColor() {// NE PAS MODIFIER
		return new Color(245, 155, 185); 
	}

	public String getDescription() {
		return "Bla bla bla";
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
		res.add(journalActeur);
		res.add(journalDeVente);
		res.add(journalContrats);
		res.add(journalStocks);
		res.add(journalCharges);
		res.add(valeurMoyennes);
		res.add(journalPrintContrat);
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
		//System.out.println("set crypto : "+this.cryptogramme);
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
			return 0; // A modifier
		} else {
			return 0; // Les acteurs non assermentes n'ont pas a connaitre notre stock
		}
	}
}
