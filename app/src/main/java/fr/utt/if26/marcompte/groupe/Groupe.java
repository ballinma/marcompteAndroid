package fr.utt.if26.marcompte.groupe;

import java.io.Serializable;
import java.util.ArrayList;

import fr.utt.if26.marcompte.transaction.Transaction;

public class Groupe implements Serializable {

    private String name;
    private int nbParticipants;
    private ArrayList<Transaction> transactions;
    //private ArrayList<Participant> participants;

    /**
     * Constructeur initialisateur de groupe
     * @param name nom du groupe (doit être unique)
     * @param nbParticipants nombre de participants au groupe
     */
    public Groupe(String name, int nbParticipants){
        transactions = new ArrayList<Transaction>();
        this.setName(name);
        this.setNbParticipants(nbParticipants);
        this.setTransactions(transactions);
    }

    /**
     * Constructeur pour récréer les groupe à partir de la base de données
     * @param name nom du groupe (doit être unique)
     * @param nbParticipants nombre de participants au groupe
     * @param transactions liste des transactions associées
     */
    public Groupe(String name, int nbParticipants, ArrayList<Transaction> transactions){
        this.setName(name);
        this.setNbParticipants(nbParticipants);
        this.setTransactions(transactions);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public ArrayList<Transaction> getTransactions() { return transactions; }

    public void setTransactions(ArrayList<Transaction> transactions) { this.transactions = transactions; }

    public int getNbParticipants() { return nbParticipants; }

    public void setNbParticipants(int nbParticipants) { this.nbParticipants = nbParticipants; }

    /**
     * Ajoute une transaction à la liste associée
     * @param t Transaction à ajouter
     */
    public void addNewTransaction(Transaction t){
        this.transactions.add(t);
    }

    /**
     * Supprime une transaction de la liste
     * @param t Transaction à supprimer
     */
    public void deleteTransaction(Transaction t){
        this.transactions.remove(t);
    }

    /**
     * Met à jour une transaction de la liste
     * @param t Transaction à mettre à jour
     */
    public void updateTrasanction(Transaction t){
        deleteTransaction(t);
        addNewTransaction(t);
    }

    /**
     * Permet d'obtenir le total des transactions de la liste
     * @return Montant que chaque personne du groupe doit payer
     */
    public double getTotalTransaction(){
        double total = 0;
        if(!(transactions.isEmpty())){
            for(Transaction t: transactions){
                total += t.getPrice();
            }
        }
        double totalShared = total / this.nbParticipants;
        return totalShared;
    }

    /**
     * Retourne la liste des transactions d'un certain type
     * @param type
     * @return
     */
    public ArrayList<Transaction> getTransactionOfType(String type){
        ArrayList<Transaction> transactionsType = new ArrayList<Transaction>();
        for(Transaction t: transactions){
            if(t.getType().equals(type)){
                transactionsType.add(t);
            }
        }
        return transactionsType;
    }

    /**
     * Retourne le total des transactions d'un certain type
     * @param type
     * @return
     */
    public float getTransactionsTotalOfType(String type){
        double typeTotal = 0;
        for(Transaction t: transactions){
            if(t.getType().equals(type)){
                typeTotal += t.getPrice();
            }
        }
        String test = String.format("%.2f",typeTotal);
        System.out.println(typeTotal + " " + test);
        test =  test.replace(",",".");
        float test1 = Float.valueOf(test);
        return test1;
    }
}
