package abstraction.eq3Producteur3;

import abstraction.eqXRomu.filiere.Filiere;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

//Zoé
public class Producteur3GestionTerrains extends Producteur3Acteur{

    protected LinkedList<Parcelle> terrainBQ = new LinkedList();
    protected LinkedList<Parcelle> terrainMQ = new LinkedList();
    protected LinkedList<Parcelle> terrainHQ = new LinkedList();

    protected HashMap<Integer,LinkedList<Parcelle>> vie = new HashMap<Integer,LinkedList<Parcelle>>();
    protected  HashMap<Integer,LinkedList<Parcelle>> recolte = new HashMap<Integer,LinkedList<Parcelle>>();   

    protected LinkedList<Integer> deficteTerrain = new LinkedList();
    protected LinkedList<Integer> beneficeTerrain = new LinkedList();

    protected int [] effectifs = new int [3];
    protected int feveProduites = 0;


    void achatTerrain(Qualite q, boolean bio){
        if(q instanceof QualiteBQ){
            terrainBQ.add(new Parcelle(q,0));//modifier date de début
            deficteTerrain.add(q.achat);
            if(((QualiteBQ) q).equitable){
                effectifs[2]+=800;
            }else{
                effectifs[0]+=1200;
                effectifs[1]+=200;
            }
        }else if( q instanceof QualiteMQ){
            terrainMQ.add(new Parcelle(q,0));//modifier date de début
            deficteTerrain.add(q.achat);
            if(((QualiteMQ) q).equitable){
                effectifs[2]+=600;
            }else{
                effectifs[1]+=600;
            }
        }else if( q instanceof QualiteHQ){
            terrainHQ.add(new Parcelle(q,0));//modifier date de début
            deficteTerrain.add(q.achat);
            effectifs[2]+=400;
        }else{
            journal.ajouter("Erreur qualité achat terrain.");
        }
    }

    void venteTerrain(Qualite q){
        if(q instanceof QualiteBQ){
            terrainBQ.removeFirst();
            beneficeTerrain.add(q.vente);
            if(((QualiteBQ) q).equitable){
                effectifs[2]-=800;
            }else{
                effectifs[0]-=1200;
                effectifs[1]-=200;
            }
        }else if( q instanceof QualiteMQ){
            terrainMQ.removeFirst();
            beneficeTerrain.add(q.vente);
            if(((QualiteMQ) q).equitable){
                effectifs[2]-=600;
            }else{
                effectifs[1]-=600;
            }
        }else if( q instanceof QualiteHQ){
            terrainMQ.removeFirst();
            beneficeTerrain.add(q.vente);
            effectifs[2]-=400;
        }else{
            journal.ajouter("Erreur qualité vente terrain.");
        }
    }

   
    void initTerrain(){
        for (int i = 0; i < 40; i++ ){
            vie.put(i,new LinkedList<>());
        }
        for (int i = 0; i< 12; i++){
            recolte.put(i, new LinkedList<>());
        }
        for (int i = 0; i < 3720; i++) {
            int mois = Filiere.random.nextInt(11);
            int annee = Filiere.random.nextInt(35)+5;

            effectifs[2]+=400;

            Parcelle p = new Parcelle(new QualiteHQ(true), annee);
            vie.get(annee).add(p);
            recolte.get(mois).add(p);
        }
        for (int i = 0; i < 6510; i++){
            int mois = Filiere.random.nextInt(11);
            int annee = Filiere.random.nextInt(36)+4;

            effectifs[2]+=600;

            Parcelle p = new Parcelle(new QualiteMQ(true), annee);
            vie.get(annee).add(p);
            recolte.get(mois).add(p);
        }
        for (int i = 0; i < 6510; i++){
            int mois = Filiere.random.nextInt(11);
            int annee = Filiere.random.nextInt(37)+3;

            effectifs[0]+=1200;
            effectifs[1]+=200;

            Parcelle p = new Parcelle(new QualiteBQ(false), annee);
            vie.get(annee).add(p);
            recolte.get(mois).add(p);

        }
    }

    void recolte(){
        LinkedList<Parcelle> prete = new LinkedList<>();
        prete = recolte.get(Filiere.LA_FILIERE.getNumeroMois());
        for (Parcelle p : prete) {
            if(p.qualite instanceof QualiteBQ){
                int nbFeve = Filiere.random.nextInt(5)+23;
                feveProduites+=((16*nbFeve)*0.753)*p.qualite.densité;
            }else if( p.qualite instanceof QualiteMQ){
                int nbFeve = Filiere.random.nextInt(4)+25;
                feveProduites+=((15*nbFeve)*0.750)*p.qualite.densité;
            }else if(p.qualite instanceof QualiteHQ){
                int nbFeve = Filiere.random.nextInt(3)+29;
                feveProduites+=((10*nbFeve)*0.765)*p.qualite.densité;
            }   
        }
    }


    void replanter(){
        LinkedList<Parcelle> tmp = vie.get(40);
        for(Parcelle t : tmp){
            deficteTerrain.add(t.qualite.replanter);
            tmp.remove(t);
            vie.get(0).add(t);
        } 
    }



    void cleanDeficite(){
        deficteTerrain.clear();
    }
    void cleanBenefice(){
        beneficeTerrain.clear();
    }
    void cleanCout(){
        cleanBenefice();
        cleanDeficite();
    }

    







    
}
