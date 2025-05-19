package abstraction.eqXRomu.bourseCacao;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.filiere.Banque;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.filiere.IAssermente;
import abstraction.eqXRomu.general.Courbe;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariableReadOnly;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;
import presentation.secondaire.FenetreGraphique;

public class BourseCacao implements IActeur, IAssermente {
	public static final int DUREE_BLACKLIST=6; // Si un acteur n'honore pas ses engagements en bourse il est blackliste durant les DUREE_BLACKLIST prochaines etapes
	public static final double EPSILON = 0.01; // On tolere un ecart de 10Kg.
	private HashMap<IActeur, Integer> cryptos;
	private Integer crypto;
	private HashMap<Feve, Journal> journal;
	private List<IVendeurBourse> vendeurs;
	private List<IAcheteurBourse> acheteurs;
	private HashMap<Feve, Variable> cours;
	private HashMap<IAcheteurBourse, Integer> blackListA; //nombre de steps pendant lequel l'acheteur ne peut plus acheter (car a fait une demande qu'il n'a pas pu honorer)
	private HashMap<IVendeurBourse, Integer> blackListV; //nombre de steps pendant lequel le vendeur ne peut plus vendre (car a fait une vente qu'il n'a pas pu honorer)

	private HashMap<Feve, HashMap<IAcheteurBourse, Courbe>> souhaitsA;
	private HashMap<Feve, HashMap<IAcheteurBourse, Courbe>> obtenusA;
	private HashMap<Feve, HashMap<IVendeurBourse, Courbe>> souhaitsV;
	private HashMap<Feve, HashMap<IVendeurBourse, Courbe>> obtenusV;
	private HashMap<Feve, Courbe> totalSouhaitsA, totalSouhaitsV;
	private HashMap<Feve, FenetreGraphique> graphique;
	private Variable affichageGraphiques;	

	public BourseCacao() {
		this.affichageGraphiques = new Variable(getNom()+" Aff.Graph.","0=pas d'affichage",this,0.0,1.0,0.0);
		this.journal = new HashMap<Feve, Journal>();
		this.cours=new HashMap<Feve, Variable>();
		// 1472 euros = 1600 dollars prix min du cours du cacao par tonne (sur les 20 annees avant 2023)
		// 2417 euros = 2627 dollars prix moy du cours du cacao par tonne de l'annee passee
		// 3221 euros = 3500 dollars prix max du cours du cacao par tonne (sur les 20 annees avant 2023)

		this.cours.put(Feve.F_MQ, new VariableReadOnly(getNom()+" cours M", "<html>le cours actuel<br>de FEVE_MOYENNE</html>", this,1472.0, 3221.0, 2417.0)); 
		this.cours.put(Feve.F_BQ, new VariableReadOnly(getNom()+" cours B", "<html>le cours actuel<br>de FEVE_BASSE</html>", this,1272.0, 3021.0, 2217.0));
		// PAS d'equitable en bourse : l'echange de produits equitables s'effectue via
		// des contrats cadre.
		// Un producteur qui aurait un stock excessif de F_MQ_BE pourrait eventuellement
		// les mettre en vente en bourse mais en tant que F_MQ  (perte du caractere
		//this.cours.put(Feve.F_HQ_BE, new VariableReadOnly(getNom()+" cours HBE", "<html>le cours actuel<br>de FEVE_HAUTE_BIO_EQUITABLE</html>", this, 1772.0, 3521.0, 2717.0));  
		//this.cours.put(Feve.F_MQ_BE, new VariableReadOnly(getNom()+" cours MBE", "<html>le cours actuel<br>de FEVE_MOYENNE_BIO_EQUITABLE</html>", this,1572.0, 3321.0, 2517.0));  
		this.blackListA=new HashMap<IAcheteurBourse, Integer>();
		this.blackListV=new HashMap<IVendeurBourse, Integer>();
		for (Feve f : Feve.values()) {
			if (!f.isEquitable()) {
				this.journal.put(f, new Journal("Journal "+this.getNom()+" "+f, this));
			}
		}
	}

	public Variable getCours(Feve f) {
		if (f==null || !this.cours.keySet().contains(f)) {
			throw new IllegalArgumentException("Appel de getCours(f) de BourseCacao avec un parametre invalide ("+f+")");
		} else {
			return this.cours.get(f);
		}
	}
	public String getNom() {
		return "BourseCacao";
	}

	public String getDescription() {
		return this.getNom();
	}

	public Color getColor() {
		return new Color(215, 215, 215);
	}

	public List<String> getNomsFilieresProposees() {
		return new LinkedList<String>();
	}

	public Filiere getFiliere(String nom) {
		return null;
	}

	public List<Variable> getIndicateurs() {
		List<Variable> res = new LinkedList<Variable>();
		for (Feve f : Feve.values()) {
			if (!f.isEquitable()) {
				res.add(this.cours.get(f));
			}
		}
		res.add(affichageGraphiques);
		return res;
	}

	public List<Variable> getParametres() {
		return new LinkedList<Variable>();
	}

	public List<Journal> getJournaux() {
		List<Journal> res = new LinkedList<Journal>();
		for (Feve f : Feve.values()) {
			if (!f.isEquitable()) {
				res.add(this.journal.get(f));
			}
		}
		return res;
	}

	public void setCryptogramme(Integer crypto) {
		this.crypto = crypto;
	}

	public void notificationFaillite(IActeur acteur) {
		if (acteur instanceof IVendeurBourse) {
			this.vendeurs.remove((IVendeurBourse)acteur);
		}
		if (acteur instanceof IAcheteurBourse) {
			this.acheteurs.remove((IAcheteurBourse)acteur);
		}
	}

	public void notificationOperationBancaire(double montant) {
	}

	public void setCryptos(HashMap<IActeur, Integer> cryptos) {
		if (this.cryptos==null) { // Les cryptogrammes ne sont indique qu'une fois par la banque : si la methode est appelee une seconde fois c'est que l'auteur de l'appel n'est pas la banque et qu'on cherche a "pirater" l'acteur
			this.cryptos= cryptos;
		}
	}


	public void initialiser() {
		int numMarque=3;
		this.vendeurs = new LinkedList<IVendeurBourse>();
		this.acheteurs = new LinkedList<IAcheteurBourse>();
		List<IActeur> acteurs = Filiere.LA_FILIERE.getActeurs();
		for (IActeur acteur : acteurs) {
			if (acteur instanceof IVendeurBourse) {
				this.vendeurs.add(((IVendeurBourse)acteur));
				this.blackListV.put((IVendeurBourse)acteur, 0);
			}
			if (acteur instanceof IAcheteurBourse) {
				this.acheteurs.add(((IAcheteurBourse)acteur));
				this.blackListA.put((IAcheteurBourse)acteur, 0);
			}
		}
		this.graphique=new HashMap<Feve, FenetreGraphique>();
		this.totalSouhaitsA=new HashMap<Feve,Courbe>();
		this.totalSouhaitsV=new HashMap<Feve,Courbe>();
		this.souhaitsA=new HashMap<Feve, HashMap<IAcheteurBourse, Courbe>>();
		this.obtenusA=new HashMap<Feve, HashMap<IAcheteurBourse, Courbe>>();
		this.souhaitsV=new HashMap<Feve, HashMap<IVendeurBourse, Courbe>>();
		this.obtenusV=new HashMap<Feve, HashMap<IVendeurBourse, Courbe>>();
		for (Feve f : Feve.values()) {
			if (!f.isEquitable()) {
				this.graphique.put(f, new FenetreGraphique("Offres et demandes de "+f, 500,400));
				Courbe ctsa = new Courbe("Total souhaits A ");
				ctsa.setCouleur(Color.BLACK);
				ctsa.setMarque(numMarque);
				numMarque++;
				this.totalSouhaitsA.put(f, ctsa);
				Courbe ctsv = new Courbe("Total souhaits V ");
				ctsv.setCouleur(Color.BLACK);
				ctsa.setMarque(numMarque);
				numMarque++;
				this.totalSouhaitsV.put(f, ctsv);
				this.graphique.get(f).ajouter(ctsa);
				this.graphique.get(f).ajouter(ctsv);
				this.souhaitsV.put(f, new HashMap<IVendeurBourse, Courbe>());
				this.obtenusV.put(f, new HashMap<IVendeurBourse, Courbe>());
				this.souhaitsA.put(f, new HashMap<IAcheteurBourse, Courbe>());
				this.obtenusA.put(f, new HashMap<IAcheteurBourse, Courbe>());
				for (IActeur acteur : acteurs) {
					if (acteur instanceof IVendeurBourse) {
						Courbe cs = new Courbe("souhaits v "+acteur.getNom());
						cs.setCouleur(acteur.getColor());
						cs.setMarque(numMarque);
						numMarque++;
						souhaitsV.get(f).put((IVendeurBourse)acteur, cs);
						graphique.get(f).ajouter(cs);
						Courbe co = new Courbe("obtenus v "+acteur.getNom());
						co.setCouleur(acteur.getColor());
						co.setMarque(numMarque);
						numMarque++;
						obtenusV.get(f).put((IVendeurBourse)acteur, co);
						graphique.get(f).ajouter(co);
					}
					if (acteur instanceof IAcheteurBourse) {
						Courbe cs = new Courbe("souhaits a "+acteur.getNom());
						cs.setCouleur(acteur.getColor());
						cs.setMarque(numMarque);
						numMarque++;
						souhaitsA.get(f).put((IAcheteurBourse)acteur, cs);
						graphique.get(f).ajouter(cs);
						Courbe co = new Courbe("obtenus a "+acteur.getNom());
						co.setCouleur(acteur.getColor());
						co.setMarque(numMarque);
						numMarque++;
						//co.setTransparence(50);
						obtenusA.get(f).put((IAcheteurBourse)acteur, co);
						graphique.get(f).ajouter(co);
					}
				}

			}
		}
	}

	public void next() {
		int etape=Filiere.LA_FILIERE.getEtape();
		Banque banque = Filiere.LA_FILIERE.getBanque();
		for (Feve f : Feve.values()) {
			if (!f.isEquitable()) {
				HashMap<IAcheteurBourse, Double> demandes=new HashMap<IAcheteurBourse, Double>();
				double totalDemandes=0;
				double cours = this.cours.get(f).getValeur();
				for (IAcheteurBourse acheteur : this.acheteurs) {
					if (blackListA.get(acheteur)==0) {
						double demande = acheteur.demande(f, cours);
						souhaitsA.get(f).get(acheteur).ajouter(etape, demande);
						journal.get(f).ajouter(Journal.texteColore((IActeur)acheteur, ((IActeur)acheteur).getNom()+" souhaite acheter "+Journal.doubleSur(demande, 2)+" T de "+f));
						if (demande>0) {
							if (banque.verifierCapacitePaiement((IActeur)acheteur, cryptos.get((IActeur)acheteur), cours*demande)) {
								demandes.put(acheteur, demande);
								totalDemandes+=demande;
							} else {
								acheteur.notificationBlackList(DUREE_BLACKLIST);
								blackListA.put(acheteur,DUREE_BLACKLIST);
							}
						}
					} else {
						blackListA.put(acheteur,blackListA.get(acheteur)-1);
					}
				}
				HashMap<IVendeurBourse, Double> offres=new HashMap<IVendeurBourse, Double>();
				double totalOffres=0;
				for (IVendeurBourse vendeur : this.vendeurs) {
					if (blackListV.get(vendeur)==0) {
						double offre = vendeur.offre(f, cours);
						souhaitsV.get(f).get(vendeur).ajouter(etape, offre);
						journal.get(f).ajouter(Journal.texteColore((IActeur)vendeur, ((IActeur)vendeur).getNom()+" souhaite vendre  "+Journal.doubleSur(offre, 2)+" T de "+f));
						if (offre>0) {
							offres.put(vendeur, offre);
							totalOffres+=offre;
						}
					}else {
						blackListV.put(vendeur,blackListV.get(vendeur)-1);
					}
				}

				this.totalSouhaitsV.get(f).ajouter(etape, totalOffres);
				this.totalSouhaitsA.get(f).ajouter(etape, totalDemandes);
				if (totalOffres>=totalDemandes && totalDemandes>0.0) { // Les acheteurs vont obtenir la quantite souhaitee et vendeurs vendre au prorata de l'offre qu'ils ont faite
					journal.get(f).ajouter("l'offre ("+Journal.doubleSur(totalOffres, 2)+") est superieure a la demande ("+Journal.doubleSur(totalDemandes, 2)+")");
                    double totalLivre=0;
					for (IVendeurBourse v : offres.keySet()){
						// La quantite vendue est au prorata de la quantite mis en vente
						double quantite = Math.min(offres.get(v),(totalDemandes*offres.get(v))/totalOffres); 
						double livre = v.notificationVente(f, quantite,cours);
						Filiere.LA_FILIERE.ajouterEchange(this, this.cryptos.get(this), (IActeur)v, f, -quantite, "BOURSE");

						totalLivre+=livre;
						obtenusV.get(f).get(v).ajouter(etape, quantite);
						if (livre+EPSILON>=quantite) {
							banque.virer(this, crypto, (IActeur)v,cours*quantite);
							journal.get(f).ajouter(Journal.texteColore((IActeur)v, ((IActeur)v).getNom()+" vend "+Journal.doubleSur(quantite, 2)+" et est paye "+Journal.doubleSur(cours*quantite, 2)));
						} else {
							v.notificationBlackList(DUREE_BLACKLIST);
							blackListV.put(v,DUREE_BLACKLIST);
						}
					}
					// totalLivre peut etre inferieure a totalDemandes car certains vendeurs ont pu ne pas honorer leurs engagements
					for (IAcheteurBourse a : demandes.keySet()){
						// Si la quantite qui a pu etre livree reste superieure a la demande globale, la demande est honoree, sinon c'est au prorata
						double quantite = totalLivre>=totalDemandes ? demandes.get(a) : (totalLivre*demandes.get(a))/totalDemandes;
						if (quantite>=EPSILON) {
							obtenusA.get(f).get(a).ajouter(etape, quantite);
							boolean virementOk = banque.virer((IActeur)a, cryptos.get((IActeur)a), this,cours*quantite);
							if (virementOk) {
								a.notificationAchat(f, quantite, cours);
								Filiere.LA_FILIERE.ajouterEchange(this, this.cryptos.get(this), (IActeur)a, f, quantite, "BOURSE");
								journal.get(f).ajouter(Journal.texteColore((IActeur)a, ((IActeur)a).getNom()+" obtient "+Journal.doubleSur(quantite,2)+" et paye "+Journal.doubleSur(cours*quantite, 2)));
							} else { // Normalement la transaction peut avoir lieu vu qu'on a verifie au prealable la capacite de l'acheteur a payer
								a.notificationBlackList(DUREE_BLACKLIST);
								blackListA.put(a,DUREE_BLACKLIST);
							}
						} else {
							obtenusA.get(f).get(a).ajouter(etape, 0);
						}
					}
				} else if (totalOffres<=totalDemandes && totalOffres>0.0){ // offre<demande : Les vendeurs vont vendre tout ce qu'ils ont mis en vente et les acheteurs auront des feves au prorata de leur proposition d'achat
					journal.get(f).ajouter("la demande ("+Journal.doubleSur(totalDemandes, 2)+") est superieure a l'offre ("+Journal.doubleSur(totalOffres, 2)+")");
					double totalLivre=0;
					for (IVendeurBourse v : offres.keySet()){
						// Chaque vendeur vend tout ce qu'il a annonce vouloir vendre
						double quantite = offres.get(v); 
						journal.get(f).ajouter("vendeur "+v+" vend tout ="+quantite);
						obtenusV.get(f).get(v).ajouter(etape, quantite);
						double livre = v.notificationVente(f, quantite,cours);
						Filiere.LA_FILIERE.ajouterEchange(this, this.cryptos.get(this), (IActeur)v, f, -quantite, "BOURSE");

						if (livre+EPSILON>=quantite) {
							banque.virer(this, crypto, (IActeur)v,cours*quantite);
							totalLivre+=livre;
							journal.get(f).ajouter(Journal.texteColore((IActeur)v, ((IActeur)v).getNom()+" vend "+Journal.doubleSur(quantite, 2)+"T, livre "+livre+"T et est paye "+Journal.doubleSur(cours*quantite, 2)));
						} else {
							journal.get(f).ajouter(Journal.texteColore((IActeur)v, "vendeur "+v+" blackliste : demande a vendre "+quantite+" mais livre "+livre));
							v.notificationBlackList(DUREE_BLACKLIST);
							blackListV.put(v,DUREE_BLACKLIST);
						}
					}
					for (IAcheteurBourse a : demandes.keySet()){
						// La quantite achetee est au prorata de la quantite demandee
						double quantite = (totalLivre*demandes.get(a))/totalDemandes; 
						if (quantite>=EPSILON)  {
							obtenusA.get(f).get(a).ajouter(etape, quantite);
							if (cours*quantite>0) {
								boolean virementOk = banque.virer((IActeur)a, cryptos.get((IActeur)a), this,cours*quantite);
								if (virementOk) { // normalement c'est le cas vu qu'on a verifie auparavant
									a.notificationAchat(f, quantite, cours);
									Filiere.LA_FILIERE.ajouterEchange(this, this.cryptos.get(this), (IActeur)a, f, quantite, "BOURSE");

									journal.get(f).ajouter(Journal.texteColore((IActeur)a, ((IActeur)a).getNom()+" obtient "+Journal.doubleSur(quantite,2)+" et paye "+Journal.doubleSur(cours*quantite, 2)));
								} else {
									journal.get(f).ajouter(Journal.texteColore((IActeur)a, "acheteur "+a+" blackliste : ne parvient pas a payer ce qu'il a demande en bourse"));
									a.notificationBlackList(DUREE_BLACKLIST);
									blackListA.put(a,DUREE_BLACKLIST);
								}
							}
						} else {
							obtenusA.get(f).get(a).ajouter(etape, 0);
						}
					}
				}
				// Mise a jour du cours.
				if ( totalDemandes==0.0 && totalOffres==0) {
					// il ne se passe rien
				} else if (totalDemandes==0.0 && totalOffres>0.0) {
					double diminution = Filiere.random.nextDouble()*2.0;  // ca va diminuer entre 0 et 2%
					double min = this.cours.get(f).getMin();
					if (cours * (1.0-(diminution/100.0))<min) {
						diminution=Math.min(diminution, Filiere.random.nextDouble()*0.1); // Si ca fait aller en dessous du Min connu alors on diminue au plus de 1 pour mille
					}
					cours = cours * (1.0- (diminution/100.0));
					this.cours.get(f).setValeur(this, cours, crypto);
				} else if (totalDemandes>0.0 && totalOffres==0.0) {
					double augmentation = Filiere.random.nextDouble()*2.0;  // ca va augmenter entre 0 et 2%
					double max = this.cours.get(f).getMax();
					if (cours * (1.0+ (augmentation/100.0))>max) {
						augmentation=Math.min(augmentation, Filiere.random.nextDouble()*0.1); // Si ca fait aller au dela du Max connu alors on augmente au plus de 1 pour 1000
					}
					cours = cours * (1.0+ (augmentation/100.0));
					this.cours.get(f).setValeur(this, cours, crypto);
				} else if (totalDemandes>totalOffres) {// Le cours va monter
					double augmentation = Math.max(Filiere.random.nextDouble()*1.5, Math.min(12.5,  (totalDemandes-totalOffres)/totalOffres)); // plus l'ecart entre demande et offre est eleve, plus l'agumentation sera forte, mais pas plus de 12.5% d'augmentation, pas moins qu'un nombre au hasard tire entre 0 et 1.5.
					double max = this.cours.get(f).getMax();
					if (cours * (1.0+ (augmentation/100.0))>max) {
						augmentation=Math.min(augmentation, Filiere.random.nextDouble()*0.1); // Si ca fait aller au dela du Max connu alors on augmente au plus de 1 pour 1000
					}
					cours = cours * (1.0+ (augmentation/100.0));
					this.cours.get(f).setValeur(this, cours, crypto);
				} else { // le cours va baisser
					double diminution = Math.max(Filiere.random.nextDouble()*1.5, Math.min(12.5,  (totalOffres-totalDemandes)/totalDemandes)); // plus l'ecart entre demande et offre est eleve, plus la diminution sera forte, mais pas plus de 12.5% de diminution, pas moins qu'un nombre au hasard tire entre 0 et 1.5.
					double min = this.cours.get(f).getMin();
					if (cours * (1.0-(diminution/100.0))<min) {
						diminution=Math.min(diminution, Filiere.random.nextDouble()*0.1); // Si ca fait aller en dessous du Min connu alors on diminue au plus de 1 pour mille
					}
					cours = cours * (1.0- (diminution/100.0));
					this.cours.get(f).setValeur(this, cours, crypto);
				}
				journal.get(f).ajouter("--> Le cours de la feve "+f+" passe a :"+Journal.doubleSur(cours, 4));
			}
		}
		if (this.affichageGraphiques.getValeur()!=0.0) {
			for (Feve f : Feve.values()) {
				if (!f.isEquitable()) {
					this.graphique.get(f).setVisible(true);

					try {
						PrintWriter aEcrire= new PrintWriter(new BufferedWriter(new FileWriter("docs"+File.separator+"B_"+f+".csv")));
						Collection<IAcheteurBourse> ab=obtenusA.get(f).keySet();
						Collection<IVendeurBourse> vb=obtenusV.get(f).keySet();
//						System.out.println("vendeurs de  "+f+" : "+vb.size());
//						System.out.println("acheteurs de "+f+" : "+ab.size());
						String s="Etape;";
						IActeur unAc=null;
						IActeur unVe=null;
						for (IAcheteurBourse a : ab) {
							s=s+"Souhait "+((IActeur)a).getNom()+";";
							s=s+"Obtenus "+((IActeur)a).getNom()+";";
							unAc=((IActeur)a);
						}
						for (IVendeurBourse a : vb) {
							s=s+"Souhait "+((IActeur)a).getNom()+";";
							s=s+"Obtenus "+((IActeur)a).getNom()+";";
							unVe=((IActeur)a);
						}
						aEcrire.println( s );
						int nbPoints = Math.max(souhaitsA.get(f).get((IAcheteurBourse)unAc).getNbPoints(),souhaitsV.get(f).get((IVendeurBourse)unVe).getNbPoints());
//						System.out.println(s+"\nnb points="+nbPoints);
						for (int i=0; i<nbPoints; i++) {
							s=i+";";
							for (IAcheteurBourse a : ab) {
								s=s+souhaitsA.get(f).get(a).getY(i)+";";
								s=s+(souhaitsA.get(f).get(a).getY(i)>0?obtenusA.get(f).get(a).getY(i):0)+";";
							}
							for (IVendeurBourse a : vb) {
								s=s+souhaitsV.get(f).get(a).getY(i)+";";
								s=s+(souhaitsV.get(f).get(a).getY(i)>0?obtenusV.get(f).get(a).getY(i):0)+";";
								// if (souhaitsV.get(f).get(a).getY(i)<obtenusV.get(f).get(a).getY(i)) {
								// 	System.out.println(">>> "+a+" veut "+souhaitsV.get(f).get(a).getY(i)+" et obtient "+obtenusV.get(f).get(a).getY(i));
								// }
							}
							aEcrire.println( s );
//							System.out.println(s);
						}
						aEcrire.close();
					}
					catch (IOException e) {
						System.out.println("Une operation sur les fichiers a leve l'exception "+e) ;
					}
				}
			}
		}
	}
	public double getQuantiteEnStock(IProduit p, int cryptogramme) {
		return 0; // La acteur non assermente n'ont pas a connaitre notre stock
	}
}
