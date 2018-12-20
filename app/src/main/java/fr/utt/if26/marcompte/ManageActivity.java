package fr.utt.if26.marcompte;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.utt.if26.marcompte.groupe.GroupPersistance;
import fr.utt.if26.marcompte.groupe.Groupe;
import fr.utt.if26.marcompte.transaction.AdapterTransaction;
import fr.utt.if26.marcompte.transaction.Transaction;

public class ManageActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Transaction> transactions;
    GroupPersistance persistance;
    Groupe actualGroupe;
    AdapterTransaction adapter;

    TextView tv_groupName;
    TextView tv_total;
    ListView lv_transactions;
    Button bt_add;


    String transactionName;
    String transactionType;
    Double transactionPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        persistance = new GroupPersistance(this, "groups.db", null, 1);

        //tv_groupName = (TextView) findViewById(R.id.tv_mga_groupName);
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        actualGroupe = persistance.getGroup(groupName);

        //tv_groupName.setText(actualGroupe.getName() + ": " + actualGroupe.getNbParticipants() + " particpants");

        setTitle(actualGroupe.getName() + ": " + actualGroupe.getNbParticipants() + " participants");

        transactions = actualGroupe.getTransactions();

        // Fonctionnera quand on créera un groupe dans la bdd
        tv_total = (TextView) findViewById(R.id.tv_mga_total);
        // Récupérer l'objet groupe via son attribut nom
        tv_total.setText("Total: " + String.format("%.2f",actualGroupe.getTotalTransaction()));

        // ajouter les transactions à la liste view
        lv_transactions = (ListView) findViewById(R.id.lv_mga_infoContent);
        adapter = new AdapterTransaction(this, R.layout.transaction, transactions);
        lv_transactions.setAdapter(adapter);

        lv_transactions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                modifyTransaction(position);
                return false;
            }
        });

        // ajouter les actions sur le bouton pour créer des transactions
        bt_add = (Button) findViewById(R.id.bt_mga_add);
        bt_add.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_mga_add:
                addTransaction();
                break;
            default:
                System.out.println("nothing");
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.manage_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_mga_modifGroup:
                modifyGroup();
                return true;
            case R.id.mi_mga_deletGroup:
                deleteGroup();
                return true;
            default:
                return false;
        }
    }

    /**
     * Affiche une popup pour modifier le nombre de participants au groupe
     */
    private void modifyGroup() {
        Toast.makeText(this, "​Je veux modifier mon groupe", Toast.LENGTH_LONG).show();

        // récupérer nom et nb de participant du groupe
        String groupName = actualGroupe.getName();
        int groubNbParticipants = actualGroupe.getNbParticipants();
        // proposer ces informations en pop up
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(actualGroupe.getName());
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText et_groupNbParticipant = new EditText(this);
        et_groupNbParticipant.setHint("Nombre de Participants");
        et_groupNbParticipant.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_groupNbParticipant.setText(String .valueOf(groubNbParticipants));
        layout.addView(et_groupNbParticipant);
        builder.setView(layout);

        builder.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int NbParticipants = Integer.parseInt(et_groupNbParticipant.getText().toString());
                if((NbParticipants < 1) || (NbParticipants > 50)){
                    Toast.makeText(ManageActivity.this, "Le nombre de participants doit être compris entre 1 et 50.", Toast.LENGTH_SHORT).show();
                }else {
                    actualGroupe.setNbParticipants(NbParticipants);
                    updateNbParticipants();
                }
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        // enregistrer ces modificatins

    }

    /**
     * Supprime un groupe définitivement
     */
    private void deleteGroup() {
        Toast.makeText(this, "​Je veux supprimer mon groupe ", Toast.LENGTH_LONG).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Voulez-vous vraiment supprimer ce groupe ?");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        builder.setView(layout);

        builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                persistance.delGroup(actualGroupe);
                finish();
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Affiche une popup permettant l'ajout d'une transaction
     */
    public void addTransaction(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter une transaction");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText et_transacName = new EditText(this);
        et_transacName.setHint("Nom");
        et_transacName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(et_transacName);
        final Spinner sp_transacType = new Spinner(this);
        String[] types = new String[]{"Nourriture", "Logement", "Transport", "Cadeaux", "Autres"};
        ArrayAdapter<String> a = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,types);
        sp_transacType.setAdapter(a);
        layout.addView(sp_transacType);
        final EditText et_transacPrice = new EditText(this);
        et_transacPrice.setHint("Prix");
        et_transacPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(et_transacPrice);
        builder.setView(layout);

        builder.setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // faire les vérifications
                boolean alreadyExist = false;
                transactionName = et_transacName.getText().toString();
                // faire des vérifications si le nom n'existe pas déjà et non vide
                for(Transaction t: transactions){
                    if(transactionName.toLowerCase().equals(t.getName().toLowerCase())){
                        Toast.makeText(ManageActivity.this, "Ce nom de groupe existe déjà.", Toast.LENGTH_SHORT).show();
                        alreadyExist = true;
                        Log.d("transactionss: ", "" + t.getName());
                    }
                }
                if((transactionName.equals("") || (et_transacPrice.getText().toString().equals("")))){
                    Toast.makeText(ManageActivity.this, "Veuillez remplir le formulaire.", Toast.LENGTH_SHORT).show();
                }else{
                    transactionPrice = Double.parseDouble(et_transacPrice.getText().toString());
                    if(!alreadyExist){
                        // new intent et ajout à la bdd
                        transactionType = sp_transacType.getSelectedItem().toString();
                        Transaction newT = new Transaction(transactionName, transactionType, transactionPrice);
                        actualGroupe.addNewTransaction(newT);
                        updateTransactions();
                    }
                }
                // ajouter les informations à la bdd
                // (redémarrer l'activité)
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Affiche une popup après un long clic sur un item transaction afin de la modifier
     * @param position
     */
    public void modifyTransaction(int position){
        final Transaction ttmp = (Transaction) lv_transactions.getItemAtPosition(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageActivity.this);
        builder.setTitle("Modifier la transaction " + ttmp.getName());
        LinearLayout layout = new LinearLayout(ManageActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final Spinner sp_transacType = new Spinner(ManageActivity.this);
        String[] types = new String[]{"Nourriture", "Logement", "Transport", "Cadeaux", "Autres"};
        ArrayAdapter<String> a = new ArrayAdapter<String>(ManageActivity.this,R.layout.support_simple_spinner_dropdown_item,types);
        sp_transacType.setAdapter(a);
        String type = ttmp.getType();
        int typePosition = a.getPosition(type);
        sp_transacType.setSelection(typePosition);
        layout.addView(sp_transacType);
        final EditText et_transacPrice = new EditText(ManageActivity.this);
        et_transacPrice.setHint("Prix");
        et_transacPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et_transacPrice.setText(String.valueOf(ttmp.getPrice()));
        layout.addView(et_transacPrice);
        builder.setView(layout);

        builder.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // faire les vérifications
                boolean alreadyExist = false;
                if(et_transacPrice.getText().toString().equals("")){
                    Toast.makeText(ManageActivity.this, "Veuillez remplir le formulaire.", Toast.LENGTH_SHORT).show();
                }else{
                    transactionPrice = Double.parseDouble(et_transacPrice.getText().toString());
                    if(!alreadyExist){
                        // new intent et ajout à la bdd
                        transactionType = sp_transacType.getSelectedItem().toString();
                        ttmp.setType(transactionType); ttmp.setPrice(transactionPrice);
                        actualGroupe.updateTrasanction(ttmp);
                        updateTransactions();
                    }
                }
                // ajouter les informations à la bdd
                // (redémarrer l'activité)
            }
        });

        builder.setNeutralButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //persistance.delGroup(actualGroupe);
                actualGroupe.deleteTransaction(ttmp);
                updateTransactions();
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //builder.show();

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button negativeButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                Button neutralButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEUTRAL);
                Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                //changer les colors
                neutralButton.setTextColor(Color.RED);
                positiveButton.setTextColor(Color.BLUE);
                negativeButton.setTextColor(Color.GRAY);
            }
        });

        dialog.show();

    }

    /**
     * Met à jour le groupe et réaffiche les nouvelles informations
     */
    public void updateNbParticipants(){
        persistance.updateGroup(actualGroupe);
        //tv_groupName.setText(actualGroupe.getName() + ": " + actualGroupe.getNbParticipants() + " particpants");
        setTitle(actualGroupe.getName() + ": " + actualGroupe.getNbParticipants() + " participants");
        tv_total.setText("Total: " + String.format("%.2f",actualGroupe.getTotalTransaction()));
    }


    /**
     * Met à jour une transaction et change l'affichage
     */
    public void updateTransactions(){
        persistance.updateGroup(actualGroupe);
        adapter.clear();
        actualGroupe = persistance.getGroup(actualGroupe.getName());
        transactions = actualGroupe.getTransactions();
        adapter = new AdapterTransaction(ManageActivity.this, R.layout.transaction, transactions);
        lv_transactions.setAdapter(adapter);
        tv_total.setText("Total: " + String.format("%.2f",actualGroupe.getTotalTransaction()));
    }

}
