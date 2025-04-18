package abstraction.eq1Producteur1;

import java.util.HashMap;

import abstraction.eqXRomu.general.Journal;

public class Producteur1arbes extends plantation {
    private Journal journal;
    private plantation basse_qualite;
    private plantation moyenne_qualite;
    private plantation haute_qualite;


    public Producteur1arbes() {
        this.basse_qualite = new plantation();
        this.moyenne_qualite = new plantation();
        this.haute_qualite = new plantation();
    }


    public void planter_parcelle_basse_q(){
        this.journal.ajouter("Plantation de la parcelle de type " + this.basse_qualite);
    }

    public void planter_parcelle_moyenne_q(){
        this.journal.ajouter("Plantation de la parcelle de type " + this.moyenne_qualite);
    }
    
    public void planter_parcelle_haute_q(){
        this.journal.ajouter("Plantation de la parcelle de type " + this.haute_qualite);
    }




}
