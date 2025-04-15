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
	protected List<Integer> successedSell;
	//private String name;
	//private Color color;
	//private HashMap<ChocolatDeMarque,Variable> stock;

	public Distributeur1AcheteurEncheres() {
		super();

		List<Integer> successedSell = new ArrayList<>();
		/*
		for (int i=0; i<6; i++){
			successedSell.add(i,0);
		}
			*/
		//this.priceProduct = priceProduct;
		//this.name = name ;
		//this.color = color;
		//this.requiredQuantities = requiredQuantities;
		//this.stock = stock;
		this.successedSell = successedSell;
	} 

	@Override
	public double proposerPrix(MiseAuxEncheres encheres){
		IProduit product = encheres.getProduit();
		if (product instanceof ChocolatDeMarque) {
        	ChocolatDeMarque chocolat = (ChocolatDeMarque) product;
			double volume = encheres.getQuantiteT();
			int idProduct = cdmToInt(chocolat);
			double price = this.priceProduct.get(idProduct)*volume;
			double wantedquantity = this.requiredQuantities.get(idProduct);
			int numberSuccessedSell = this.successedSell.get(idProduct);
			if (wantedquantity<volume){
				return(Math.min(price*(0.9+0.1*(1-Math.exp(-1*numberSuccessedSell/5))*(1-Math.exp((wantedquantity-volume)/1000))), price*1.5));
			}
		return(Math.min(price*(1.1+0.02*numberSuccessedSell),price*1.5));
		}
		return(0);
	}

	@Override
	public void initialiser(){

	}
	@Override
	public void notifierAchatAuxEncheres(Enchere enchereRetenue){
		IProduit product = enchereRetenue.getProduit();
		if (product instanceof ChocolatDeMarque){
			ChocolatDeMarque chocolat = (ChocolatDeMarque) product;
		this.successedSell.set(cdmToInt(chocolat),this.successedSell.get(cdmToInt(chocolat))+1);
		this.getStock(chocolat).ajouter(this, enchereRetenue.getQuantiteT());
		}
	}

	@Override
	public void notifierEnchereNonRetenue(Enchere enchereNonRetenue){
		IProduit product = enchereNonRetenue.getProduit();
		if (product instanceof ChocolatDeMarque){
			ChocolatDeMarque chocolat = (ChocolatDeMarque) product;
		this.successedSell.set(cdmToInt(chocolat),this.successedSell.get(cdmToInt(chocolat))-1);
		}
	}

	@Override
	public String getNom(){
		return(this.name);
	}

	@Override
	public Color getColor(){
		return(this.color);
	}

	@Override
	public String getDescription(){
		return("Acheteur aux encheres de l'equipe 7");
	}

	@Override
	public void next(){}

	@Override
	public List<Variable> getIndicateurs(){
		List<Variable> indicateurs = new ArrayList<Variable>();
		return(indicateurs);
	}

	@Override
	public List<Variable> getParametres(){
		List<Variable> parametres = new ArrayList<Variable>();
		return(parametres);
	}
	@Override
	public List<Journal> getJournaux(){
		List<Journal> journaux = new ArrayList<Journal>();
		return(journaux);
	}
	@Override
	public void notificationFaillite(IActeur acteur){

	}
	@Override
	public void notificationOperationBancaire(double montant){

	}

	@Override
	public List<String> getNomsFilieresProposees(){
		List<String> noms = new ArrayList<String>();
		return(noms);
	}

	@Override
	public Filiere getFiliere(String nom){
		Filiere test = new Filiere(0);
		return(test);
	}

	@Override
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