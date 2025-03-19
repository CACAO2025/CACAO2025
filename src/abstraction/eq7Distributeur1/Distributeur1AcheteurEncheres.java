package abstraction.eq7Distributeur1;
///Maxime GUY///
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import abstraction.eqXRomu.encheres.Enchere;
import abstraction.eqXRomu.encheres.IAcheteurAuxEncheres;
import abstraction.eqXRomu.encheres.MiseAuxEncheres;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;

public class Distributeur1AcheteurEncheres extends Distributeur1AcheteurContratCadre implements IAcheteurAuxEncheres  {

	//protected Integer cryptogramme;
	//private List<Double> priceProduct;
	//private List<Double> requiredQuantities;
	private List<Integer> successedSell;
	//private String name;
	//private Color color;
	//private HashMap<ChocolatDeMarque,Variable> stock;

	public Distributeur1AcheteurEncheres() {
		super();

		List<Integer> successedSell = null;
		//this.priceProduct = priceProduct;
		//this.name = name ;
		//this.color = color;
		//this.requiredQuantities = requiredQuantities;
		//this.stock = stock;
		this.successedSell = successedSell;
	}

	    public int getInt(Chocolat product){
        int idProduct = 0;
        switch(product.getGamme()){
            case BQ : idProduct=0;
            case MQ : idProduct=2;
            case HQ : idProduct=4;
        }
        if (product.isBio()){
            idProduct++;
        }
        if (product.isEquitable()){
            idProduct++;
        }
        return(idProduct);
    }

	public double proposerPrix(MiseAuxEncheres encheres){
		IProduit product = encheres.getProduit();
		if (product instanceof ChocolatDeMarque) {
        	ChocolatDeMarque chocolat = (ChocolatDeMarque) product;
			double volume = encheres.getQuantiteT();
			int idProduct = getInt(chocolat.getChocolat());
			double price = this.priceProduct.get(idProduct);
			double wantedquantity = this.requiredQuantities.get(idProduct);
			int numberSuccessedSell = this.successedSell.get(idProduct);
			if (wantedquantity<volume){
				return(price*(0.9+0.1*(1-Math.exp(-1*numberSuccessedSell/5))*(1-Math.exp((wantedquantity-volume)/1000))));
			}
		return(price*(1.1+0.02*numberSuccessedSell));
		}
		return(0);
	}
	public void initialiser(){

	}

	public void notifierAchatAuxEncheres(Enchere enchereRetenue){

	}
	public void notifierEnchereNonRetenue(Enchere enchereNonRetenue){
		
	};

	public String getNom(){
		return(this.name);
	}

	public Color getColor(){
		return(this.color);
	}

	public String getDescription(){
		return("Acheteur aux encheres de l'equipe 7");
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
	
}

