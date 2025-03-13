package abstraction.eq3Producteur3;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariablePrivee;
import abstraction.eqXRomu.produits.Feve;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class Producteur3Stock extends Producteur3GestionTerrains {
    //protected VariablePrivee stockFeve;
    protected HashMap<Feve,VariablePrivee> stockFeve = new HashMap<Feve,VariablePrivee>();
    protected VariablePrivee totalStock;
    protected VariablePrivee coutStock;



    public Producteur3Stock(){
        super();
        totalStock = new VariablePrivee(this.getNom()+"Stock Total", this, 0.0, 100000000000000.0, 0);
        coutStock = new VariablePrivee(this.getNom() + "Cout Stockage", this, 0.0, 1000000000.0, 0);
        getIndicateurs().add(totalStock);
        getIndicateurs().add(coutStock);
    }
  
    //Zoé
    public void initStock(Feve feve, double stock){
        if (feve==null ||stock<=0) {
			throw new IllegalArgumentException("creation d'une instance de Producteur3Stock avec des arguments non valides");
		}		
		VariablePrivee tmp = new VariablePrivee(this.getNom()+"Stock"+feve, this, 0.0, 1000000000.0,stock);
        tmp.setValeur(this, 1000000.0, cryptogramme);
        journal.ajouter(Double.toString(tmp.getHistorique().getValeur()));
        stockFeve.put(feve,tmp);
        getIndicateurs().add(tmp);
    }

    public void calculTotalStock(){
        double total = 0;
        total+= stockFeve.get(Feve.F_BQ).getValeur(cryptogramme);
        total+= stockFeve.get(Feve.F_BQ_E).getValeur(cryptogramme);
        total+= stockFeve.get(Feve.F_MQ).getValeur(cryptogramme);
        total+= stockFeve.get(Feve.F_MQ_E).getValeur(cryptogramme);
        total+= stockFeve.get(Feve.F_HQ_E).getValeur(cryptogramme);
        total+= stockFeve.get(Feve.F_HQ_BE).getValeur(cryptogramme);
        totalStock.setValeur(this, total, cryptogramme);

    }
    
    
// Alice
    public void ajouterStock(Feve feve,double delta){
        stockFeve.get(feve).ajouter(this,delta);
        journal.ajouter("Ajout de " + delta + " au stock de " + feve + ". Nouveau stock : " + stockFeve.get(feve).getValeur());
        
    }
    public void retirerStock(Feve feve,double delta){
        stockFeve.get(feve).retirer(this,delta);
        journal.ajouter("Retrait de " + delta + " du stock de " + feve + ". Nouveau stock : " + stockFeve.get(feve).getValeur());

    }

    public void calculCoutStock() {
        /*double totalCout = 0;
        for (VariablePrivee p : stockFeve.values()) {
            double stockEnTonnes = stockFeve.get(p).getValeur(cryptogramme) / 1000; // Conversion kg → tonnes
            totalCout += stockEnTonnes * 7.5;// 7,5 €/tonne par step
        }
        coutStock.setValeur(this, totalCout, cryptogramme);
        journal.ajouter("Coût total du stockage : " + totalCout + " €");*/
    }

    public int getStepActuel() {
        return Filiere.LA_FILIERE.getEtape(); // Récupère le step actuel depuis la filière
    }


  /*public int getDureeConservation(Feve feve) {
        int dateAjout = stockFeve.get(feve).dateAjout; // Date d'ajout du lot le plus ancien
        return getStepActuel() - dateAjout; // Différence entre le step actuel et la date d'ajout
    }*/



    protected VariablePrivee getStock(Feve feve){
        return this.stockFeve.get(feve);
    }

    
}
