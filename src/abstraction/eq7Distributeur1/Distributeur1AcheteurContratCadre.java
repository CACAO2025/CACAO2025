package abstraction.eq7Distributeur1;
///Maxime GUY///

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;

public class Distributeur1AcheteurContratCadre extends Distributeur1Stock implements IAcheteurContratCadre{

	protected Integer cryptogramme;
	protected List<Double> priceProduct;
	protected List<Double> requiredQuantities;
	protected String name;
	protected Color color;
	protected List<Double> predictionsVentesPourcentage;
	//private HashMap<ChocolatDeMarque,Variable> stock;

	public Distributeur1AcheteurContratCadre() {
		super();

		List<Double> predictionsVentesPourcentage = null;
		List<Double> priceProduct = null;
		List<Double> requiredQuantities = null;
		int cryptogramme = -1;
		int step = -1;
		int product = -1;

		String name = "EQ7";
		Color color = new Color(162, 207, 238);

		this.predictionsVentesPourcentage = predictionsVentesPourcentage;
		this.priceProduct = priceProduct;
		this.requiredQuantities = requiredQuantities;
		this.cryptogramme = cryptogramme;
		this.name = name;
		this.color = color;
		//this.stock = stock;
	}

	    public int getInt(ChocolatDeMarque product){
        int idProduct = 0;
        switch(product.getGamme()){
            case BQ : idProduct=0;break;
            case MQ : idProduct=2;break;
            case HQ : idProduct=4;break;
        }
        if (product.isBio()){
            idProduct++;
        }
        if (product.isEquitable()){
            idProduct++;
        }
        return(idProduct);
    }
	
	public boolean achete(IProduit produit){
		if (produit instanceof ChocolatDeMarque){
			ChocolatDeMarque chocolat = (ChocolatDeMarque) produit;
			return(requiredQuantities.get(getInt(chocolat))>0); 
		}
		return(false);
	}

	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat){
		List<Echeancier> listeEcheancier = contrat.getEcheanciers();
		int tour = 0;
		ChocolatDeMarque chocolat = (ChocolatDeMarque) contrat.getProduit();
		Echeancier echeancierActuel = null;
		if (listeEcheancier.isEmpty()){
			tour = 0;
			echeancierActuel = new Echeancier(0, 12, requiredQuantities.get(getInt(chocolat)));
		}
		else {
			tour = listeEcheancier.size();
			echeancierActuel = listeEcheancier.get(listeEcheancier.size()-1);
		}
		for (int step = echeancierActuel.getStepDebut(); step<=echeancierActuel.getStepFin() ; step++){
			double quantiteDemandee = echeancierActuel.getQuantite(step);
			double quantiteVoulue = requiredQuantities.get(getInt(chocolat))/predictionsVentesPourcentage.get(0)*predictionsVentesPourcentage.get(step);
			if (quantiteDemandee > quantiteVoulue*(1+0.01*tour)){
				echeancierActuel.set(step, quantiteVoulue*(1+0.01*tour));
			}
			if (quantiteDemandee < quantiteVoulue*(1-0.01*tour)){
				echeancierActuel.set(step, quantiteVoulue*(1-0.01*tour));
			}
		}
		return(echeancierActuel);
	}

	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat){
		List<Double> listePrix = contrat.getListePrix();
		ChocolatDeMarque chocolat = (ChocolatDeMarque) contrat.getProduit();
		int tour = 0;
		Double dernierPrix = 0.0;
		if (listePrix.isEmpty()){
			tour = 0;
			dernierPrix = 10 * priceProduct.get(getInt(chocolat));
		}
		else {
			tour = listePrix.size();
			dernierPrix = listePrix.get(listePrix.size()-1);
		}
		double prixPropose = priceProduct.get(getInt(chocolat))*(0.87+0.04*tour);
		if (tour<6 && dernierPrix>prixPropose){
			return(prixPropose);
		}
		if (dernierPrix<=prixPropose){
			return(listePrix.get(listePrix.size()-1));
		}
		return(priceProduct.get(getInt(chocolat)));
	}

	public void initialiser(){

	}

	public String getNom(){
		return(this.name);
	}

	public Color getColor(){
		return(this.color);
	}

	public String getDescription(){
		return("Acheteur contrat cadre de l'equipe 7");
	}

	public void next(){

	}

	public List<Variable> getIndicateurs(){
		List<Variable> indicateurs = new ArrayList<Variable>();
		return(indicateurs);
	}

	public List<Variable> getParametres(){
		List<Variable> parametres = new ArrayList<Variable>();
		return(parametres);
	}

	public List<Journal> getJournaux(){
		List<Journal> journaux = new ArrayList<Journal>();
		return(journaux);
	}

	public void setCryptogramme(Integer crypto){
		this.cryptogramme = crypto;
	}

	public void notificationFaillite(IActeur acteur){

	}

	public void notificationOperationBancaire(double montant){

	}

	public List<String> getNomsFilieresProposees(){
		List<String> noms = new ArrayList<String>();
		return(noms);
	}

	public Filiere getFiliere(String nom){
		Filiere test = new Filiere(0);
		return(test);
	}

	public double getQuantiteEnStock(IProduit p, int cryptogramme ){
		if (this.cryptogramme == cryptogramme){
			if (p instanceof ChocolatDeMarque){
				ChocolatDeMarque chocolat = (ChocolatDeMarque) p;
				if (this.stocksChocolats != null && this.stocksChocolats.containsKey(chocolat)){
					return(this.stocksChocolats.get((chocolat)).getValeur());
				}
			}
			return(0);
		}
		return(0);
	}

	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat){
		
	}
	
	public void receptionner(IProduit p, double quantiteEnTonnes, ExemplaireContratCadre contrat){

	}
}

