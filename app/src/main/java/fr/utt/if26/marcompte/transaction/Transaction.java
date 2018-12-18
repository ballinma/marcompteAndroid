package fr.utt.if26.marcompte.transaction;

public class Transaction {

    private String name;
    private String type;
    private double price;
    //private Date date;

    /**
     * Constructeur initialisateur d'une transaction
     * @param name le nom de la transaction
     * @param type le type à partir d'une liste définie
     * @param price le prix associé
     */
    public Transaction(String name, String type, double price) {
        this.setName(name);
        this.setType(type);
        this.setPrice(price);
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return "Transaction{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                '}';
    }
}
