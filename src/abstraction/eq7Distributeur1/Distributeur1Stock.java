package abstraction.eq7Distributeur1;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.produits.IProduit;

public class Distributeur1Stock extends Distributeur1Acteur{
    protected Map<ChocolatDeMarque, Variable> stocksChocolats;
    protected List<ChocolatDeMarque> chocolats;

    public Distributeur1Stock()
    {
        this.stocksChocolats = new HashMap<>();

        this.chocolats = new ArrayList<ChocolatDeMarque>();
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_HQ_BE, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_HQ_E, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_MQ_E, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_MQ, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_BQ_E, "Hexafridge", 50));
		this.chocolats.add(new ChocolatDeMarque(Chocolat.C_BQ, "Hexafridge", 50));

        for (int i=0; i<this.chocolats.size(); i++) {
			this.stocksChocolats.put(chocolats.get(i), new Variable("Stock"+chocolats.get(i).getNom(), this, 1000.0));
		}
    }

    public Variable getStock(ChocolatDeMarque c) { // par Alexiho
		return this.stocksChocolats.get(c);
	}

	public Map<ChocolatDeMarque, Variable> getStocksChocolats() { // par Alexiho
		return this.stocksChocolats;
	}

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
}
