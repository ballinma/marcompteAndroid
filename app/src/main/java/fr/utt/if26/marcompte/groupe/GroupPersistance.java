package fr.utt.if26.marcompte.groupe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.utt.if26.marcompte.transaction.Transaction;

public class GroupPersistance extends SQLiteOpenHelper implements GroupPersistanceInterface {

    public GroupPersistance(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "groups.db";
    private static final String TABLE_GROUP = "groupes";
    private static final String ATTRIBUT_NAME = "nom";
    private static final String ATTRIBUT_NBPARTICIPANTS = "participants";
    private static final String ATTRIBUT_TRANSACTIONS = "transactions";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String table_group_create =
                "CREATE TABLE " + TABLE_GROUP + "("
                        + ATTRIBUT_NAME + " TEXT primary key,"
                        + ATTRIBUT_NBPARTICIPANTS + " INT,"
                        + ATTRIBUT_TRANSACTIONS + " TEXT"
                        //+ ATTRIBUT_TOTAL + " DOUBLE"
                        + ")";
        sqLiteDatabase.execSQL(table_group_create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        onCreate(db);
    }

    @Override
    public void addNewGroup(Groupe g) {
        JSONObject json = new JSONObject();
        try
        {
            JSONArray jArray = json.getJSONArray("transactionList");
            for (Transaction transaction : g.getTransactions()) {
                JSONObject transactionsJSON = new JSONObject();
                transactionsJSON.put("name", transaction.getName());
                transactionsJSON.put("type", transaction.getType());
                transactionsJSON.put("price", transaction.getPrice());
                jArray.put(transactionsJSON);
            }
            json.put("transactionList", jArray);
            Log.d("transactions liste: ",""+jArray);
        } catch (JSONException jse) {
            Log.d("transaction erroor: ", ""+jse);
        }
        String transactionList = json.toString();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ATTRIBUT_NAME, g.getName());
        values.put(ATTRIBUT_NBPARTICIPANTS, g.getNbParticipants());
        values.put(ATTRIBUT_TRANSACTIONS, transactionList);

        System.out.println(values.toString());

        db.insert(TABLE_GROUP, null, values);
        db.close();
    }

    @Override
    public void initData() {
        addNewGroup(new Groupe("Bienvenue!", 1));
    }

    @Override
    public void delGroup(Groupe g) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUP,ATTRIBUT_NAME + " = " + "\'" + g.getName() + "\'", null);
        db.close();
    }

    @Override
    public void updateGroup(Groupe g) {
        JSONObject json = new JSONObject();
        try
        {
            JSONArray jArray = new JSONArray();
            for (Transaction transaction : g.getTransactions()) {
                JSONObject transactionsJSON = new JSONObject();
                transactionsJSON.put("name", transaction.getName());
                transactionsJSON.put("type", transaction.getType());
                transactionsJSON.put("price", transaction.getPrice());
                jArray.put(transactionsJSON);
            }
            json.put("transactionList", jArray);
        } catch (JSONException jse) {
            Log.d("transactionErrorUpdate", g.getName()+" "+jse);
        }
        String transactionList = json.toString();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ATTRIBUT_NAME, g.getName());
        values.put(ATTRIBUT_NBPARTICIPANTS,g.getNbParticipants());
        values.put(ATTRIBUT_TRANSACTIONS,transactionList);
        db.update(TABLE_GROUP, values, ATTRIBUT_NAME + "=" + "\'" + g.getName() + "\'", null);
        db.close();
    }

    @Override
    public Groupe getGroup(String key) {
        Groupe g = new Groupe("",0);
        String selectQuery = "SELECT  * FROM " + TABLE_GROUP + " WHERE "+ ATTRIBUT_NAME +" = " + "\'" + key + "\';";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ArrayList<Transaction> transactions = new ArrayList<Transaction>();
                try
                {
                    JSONObject json = new JSONObject(cursor.getString(2));
                    JSONArray cast = json.getJSONArray("transactionList");
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject transactionJSON = cast.getJSONObject(i);
                        Transaction t = new Transaction(transactionJSON.getString("name"),transactionJSON.getString("type"),
                                transactionJSON.getDouble("price"));
                        transactions.add(t);
                    }
                } catch (JSONException jse) {
                    Log.d("getgroup", g.getName()+"JSON ERROR "+jse);
                }
                g = new Groupe(cursor.getString(0),Integer.parseInt(cursor.getString(1)),transactions);
            } while (cursor.moveToNext());
        }
        db.close();
        return g;
    }

    @Override
    public int countGroup() {
        return 0;
    }

    @Override
    public ArrayList<Groupe> getAllGroups() {
        ArrayList<Groupe> mesGroupes = new ArrayList<Groupe>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GROUP;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ArrayList<Transaction> transactions = new ArrayList<Transaction>();
                try
                {
                    JSONObject json = new JSONObject(cursor.getString(2));
                    JSONArray cast = json.getJSONArray("transactionList");
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject transactionJSON = cast.getJSONObject(i);
                        transactions.add(new Transaction(transactionJSON.getString("name"),transactionJSON.getString("type"),
                                transactionJSON.getDouble("price")));
                    }
                } catch (JSONException jse) {
                    Log.d("getAllgroups", cursor.getString(0)+" "+jse+" ("+cursor.getString(2)+")");
                }
                // rajouter les paramètres au constructeur
                Groupe g = new Groupe(cursor.getString(0),Integer.parseInt(cursor.getString(1)),transactions);

                mesGroupes.add(g);
            } while (cursor.moveToNext());
        }
        db.close();

        return mesGroupes;
    }

    @Override
    public String toString() {
        ArrayList<Groupe> test = this.getAllGroups();
        ArrayList<String> pouf = new ArrayList<String>();
        for (int i = 0; i < test.size(); i++)
        {
            pouf.add(test.get(i).getName());
        }
        return "contenu de la bdd: "+ pouf;
    }

}
