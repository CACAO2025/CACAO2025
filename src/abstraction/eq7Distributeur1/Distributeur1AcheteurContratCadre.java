package abstraction.eq7Distributeur1;
///Maxime GUY///

import java.util.List;

import abstraction.eqXRomu.appelDOffre.IAcheteurAO;
import abstraction.eqXRomu.appelDOffre.IVendeurAO;
import abstraction.eqXRomu.contratsCadres.ContratCadre;
import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;

public class Distributeur1AcheteurContratCadre extends Distributeur1Acteur  {

	protected Integer cryptogramme;
	private Integer product;
	private List<Double> priceProduct;
	private IAcheteurAO identity;
	private List<Double> requiredQuantities;
	private int step;

	public Distributeur1AcheteurContratCadre(List<Double> priceProduct, List<Double> requiredQuantities, int cryptogramme, int step, IAcheteurAO identity, int product) {
		super();
		this.product = product;
		this.identity = identity;
		this.priceProduct = priceProduct;
		this.requiredQuantities = requiredQuantities;
		this.cryptogramme = cryptogramme;
		this.step = step;
	}
	
	public Echeancier creationEcheancier(){
		Echeancier echeancier = new Echeancier(this.step, this.requiredQuantities);
		return(echeancier);
	}

	public Void contratcadre(IVendeurAO vendeur){
		Echeancier echeancier = creationEcheancier();
		ExemplaireContratCadre contrat = new ExemplaireContratCadre(this.identity, vendeur, this.product, echeancier, this.cryptogramme, false);


	}
}

