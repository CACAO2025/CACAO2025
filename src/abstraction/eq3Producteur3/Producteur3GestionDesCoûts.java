package abstraction.eq3Producteur3;

import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.filiere.Filiere;

public class Producteur3GestionDesCoûts extends Producteur3Employés{
    
    double cumpBQ = 7.5;
    double cumpBQ_E = 7.5;
    double cumpMQ = 7.5;
    double cumpMQ_E = 7.5;
    double cumpHQ = 7.5;
    double cumpHQ_B = 7.5;

    public Producteur3GestionDesCoûts() {
        super();
    }
    
    // Paul
    
    public double coutsTotaux() {
        return calculCoutStock() + getMasseSalariale();
    }

    public double getCoutUnitaire(Feve feve) {
        if (getNouveauStockParticulier(feve) == 0) {
            return 7.5;
        }
        else {
            return 7.5 + (getMasseSalarialeParticulier(feve) / getNouveauStockParticulier(feve));
        }
        
    }

    public double getCump(Feve feve) {
        double nouveauStock = getNouveauStockParticulier(feve);
        double totalStock = calculTotalStockParticulier(feve);
        double ancienStock = totalStock - nouveauStock;
        if (totalStock == 0) {
            return 7.5;
        }

        if(feve.getGamme().equals(Gamme.BQ)){
            if(feve.isEquitable()){
                cumpBQ_E = (ancienStock * cumpBQ_E + nouveauStock * getCoutUnitaire(feve)) / totalStock;
                return cumpBQ_E;
            }else{
                cumpBQ = (ancienStock * cumpBQ + nouveauStock * getCoutUnitaire(feve)) / totalStock;
                return cumpBQ;
            }
        }
        else if(feve.getGamme().equals(Gamme.MQ)){
            if(feve.isEquitable()){
                cumpMQ_E = (ancienStock * cumpMQ_E + nouveauStock * getCoutUnitaire(feve)) / totalStock;
                return cumpMQ_E;
            }else{
                cumpMQ = (ancienStock * cumpMQ + nouveauStock * getCoutUnitaire(feve)) / totalStock;
                return cumpMQ;
            }
        }else{
            if (feve.isBio()){
                cumpHQ_B = (ancienStock * cumpHQ_B + nouveauStock * getCoutUnitaire(feve)) / totalStock;
                return cumpHQ_B;
            }else{
                cumpHQ = (ancienStock * cumpHQ + nouveauStock * getCoutUnitaire(feve)) / totalStock;
                return cumpHQ;
            }
        }
    }

    void actualiserJournalCump(){
        journalCump.ajouter("-----ETAPE " + Filiere.LA_FILIERE.getEtape() + "-----\n");
        for (Feve feve : Feve.values()){
            journalCump.ajouter("CUMP " + feve + " : " + getCump(feve) + "\n");
        }
    }
    }

