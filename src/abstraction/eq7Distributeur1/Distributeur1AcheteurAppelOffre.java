package abstraction.eq7Distributeur1;
///Maxime GUY///


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


import abstraction.eqXRomu.appelDOffre.SuperviseurVentesAO;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.appelDOffre.IAcheteurAO;
import abstraction.eqXRomu.appelDOffre.OffreVente;
import abstraction.eqXRomu.encheres.Enchere;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.IProduit;



public class Distributeur1AcheteurAppelOffre extends Distributeur1AcheteurEncheres implements IAcheteurAO  {

	//protected Integer cryptogramme;
	//private List<Double> requiredQuantities;
	//private Color color;
	//private String name;
	//private List<Double> stock;
	//private List<Double> priceProduct;

	public Distributeur1AcheteurAppelOffre() {
		super();

		//this.requiredQuantities = requiredQuantities;
		//this.identity = identity;
		//this.color = color;
		//this.name = name;
		//this.stock = stock;
		//this.priceProduct = priceProduct;
		//this.identity = identity;

	}

	@Override
	public OffreVente choisirOV(List<OffreVente> propositions){
		int indice = -1;
		double volume = propositions.get(0).getQuantiteT();
		IProduit product = propositions.get(0).getProduit();
		if (product instanceof ChocolatDeMarque) {
        	ChocolatDeMarque chocolat = (ChocolatDeMarque) product;
			int idProduct = cdmToInt(chocolat);
			double price = 1.5*this.priceProduct.get(idProduct) ;
			for (int i=0; i<propositions.size(); i++){
				double priceProposed = propositions.get(i).getPrixT();
				if (priceProposed<price){
					indice = i;
					price = priceProposed;
					}
				}
				this.priceProduct.set(idProduct,price);
			}
		
		if (indice == -1){
			return(null);
		}
		this.getStock((ChocolatDeMarque) propositions.get(indice).getProduit()).ajouter(this, propositions.get(indice).getQuantiteT());
		return(propositions.get(indice));
		}
	

	@Override
	public void initialiser(){

	}

	@Override
	public String getDescription(){
		return("Appelleur d'offre de l'equipe 7");
	}

	public void next_ao(){
		SuperviseurVentesAO superviseur = (SuperviseurVentesAO)(Filiere.LA_FILIERE.getActeur("Sup.AO"));

		for (int i=0; i<chocolats.size(); i++){
			if (requiredQuantities.get(i)>5){
			superviseur.acheterParAO(this,this.cryptogramme, chocolats.get(i) , this.requiredQuantities.get(i));
			}
		}
	}
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
	public List<String> getNomsFilieresProposees(){
		List<String> noms = new ArrayList<String>();
		return(noms);
	}

	
}