package abstraction.eq7Distributeur1;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* @Ethan */
public class Distributeur1Miseenstock extends Distributeur1 {
    private int etape;
    protected Integer cryptogramme;
    protected Map<ChocolatDeMarque,Variable> stockVenteChocolat;
    protected List<ChocolatDeMarque> chocolats;

    public Distributeur1Miseenstock(){
        super();
        this.etape = Filiere.LA_FILIERE.getEtape();

        this.chocolats = new ArrayList<ChocolatDeMarque>();
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_HQ_BE, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_HQ_E, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_MQ_E, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_MQ, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_BQ_E, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_BQ, "Hexafridge", 50));

        //Toute cette initialisation sera à refaire plus tard
        stockVenteChocolat.put(chocolats.get(0), new Variable("7stockVenteHQBE", this)); // CHOCOLAT HAUTE QUALITE BIO EQUITABLE
        stockVenteChocolat.put(chocolats.get(1), new Variable("7stockVenteHQE", this));  // CHOCOLAT HAUTE QUALITE EQUITABLE
        stockVenteChocolat.put(chocolats.get(2), new Variable("7stockVenteMQE", this));  // CHOCOLAT MOYENNE QUALITE EQUITABLE
        stockVenteChocolat.put(chocolats.get(3), new Variable("7stockVenteMQ", this));   // CHOCOLAT MOYENNE QUALITE (NI BIO NI EQUITABLE)
        stockVenteChocolat.put(chocolats.get(4), new Variable("7stockVenteBQE", this));  // CHOCOLAT BASSE QUALITE EQUITABLE
        stockVenteChocolat.put(chocolats.get(5), new Variable("7stockVenteBQ", this));   // CHOCOLAT BASSE QUALITE (NI BIO NI EQUITABLE)

    }

    public Boolean stockdisponible(ChocolatDeMarque produit){
        return(this.stocksChocolats.get(produit).getValeur() > 100000);
    }

    public double QuantiteMiseEnVente(ChocolatDeMarque choco, int crypto) {
		if (this.cryptogramme==crypto && stockdisponible(choco)==true) {
            this.stockVenteChocolat.get(choco).setValeur(this, this.stockVenteChocolat.get(choco).getValeur()+ 100000);
			return this.stockVenteChocolat.get(choco).getValeur()/10;
		} else {
			return 0.0;
		}
	}

    public double restock (ChocolatDeMarque choco, int crypto){
        if (etape != Filiere.LA_FILIERE.getEtape()) {
            etape = Filiere.LA_FILIERE.getEtape();
            return QuantiteMiseEnVente(choco, crypto);
        } else {
            return 0.0;
        }
    }
}
