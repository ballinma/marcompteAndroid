package fr.utt.if26.marcompte.groupe;

import java.util.ArrayList;

public interface GroupPersistanceInterface {

    // ajoute un module dans la base (faire attention à sigle qui est clé primaire)
    public void addNewGroup(Groupe g);

    // ajoute l'ensemble des modules du Cursus via la méthode getModules()
    public void initData();

    // supprime un module dans la base (faire attention à sigle qui est clé primaire)
    public void delGroup(Groupe g);

    // mise à jour un module dans la base (faire attention à sigle qui est clé primaire)
    public void updateGroup(Groupe g);

    // recherche d'un module dans la base via la clé primaire (sigle)
    public Groupe getGroup(String key);

    // retourne le nombre de module
    public int countGroup();

    // retourne l'ensemble des modules de la base
    public ArrayList<Groupe> getAllGroups();

}
