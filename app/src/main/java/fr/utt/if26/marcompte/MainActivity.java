package fr.utt.if26.marcompte;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import fr.utt.if26.marcompte.groupe.AdapterGroup;
import fr.utt.if26.marcompte.groupe.GroupPersistance;
import fr.utt.if26.marcompte.groupe.Groupe;

/**
 * MainActivity: Page d'accueil avec l'affichage des groupes et possibilités d'en ajouter
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button bt_add;
    ListView lv_group;
    ArrayList<Groupe> groups;
    GroupPersistance persistance;
    AdapterGroup adapter;
    String pu_groupName = "";
    String pu_groupPassword = "";
    int pu_nbParticipant = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_add = (Button) findViewById(R.id.bt_ma_add);
        bt_add.setOnClickListener(this);

        lv_group = (ListView) findViewById(R.id.lv_ma_group);
        persistance = new GroupPersistance(this, "save.db", null, 1);
        //persistance.onUpgrade(persistance.getWritableDatabase(),3,4);
        //persistance.initData();
        Log.d("InitData",persistance.toString());
        groups = persistance.getAllGroups();
        /*for(Groupe g: groups){
            persistance.delGroup(g);
        }*/
        //persistance.initData();

        adapter = new AdapterGroup(this, R.layout.groupe, groups);
        lv_group.setAdapter(adapter);
        lv_group.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                askForPass(position);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_ma_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Nom du nouveau groupe");
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText et_groupName = new EditText(this);
                et_groupName.setHint("Nom");
                et_groupName.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(et_groupName);
                final EditText et_password = new EditText(this);
                et_password.setHint("Mot de passe");
                et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(et_password);
                final EditText et_groupNbParticipant = new EditText(this);
                et_groupNbParticipant.setHint("Nombre de Participants");
                et_groupNbParticipant.setInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(et_groupNbParticipant);
                builder.setView(layout);

                builder.setPositiveButton("Créer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean alreadyExist = false;
                        pu_groupName = et_groupName.getText().toString();
                        pu_groupPassword = et_password.getText().toString();

                        // faire des vérifications si le nom n'existe pas déjà et non vide
                        for(Groupe g: groups){
                            if(pu_groupName.toLowerCase().equals(g.getName().toLowerCase())){
                                Toast.makeText(MainActivity.this, "Ce nom de groupe existe déjà.", Toast.LENGTH_SHORT).show();
                                alreadyExist = true;
                            }
                        }
                        if((pu_groupName.equals("") || (et_groupNbParticipant.getText().toString().equals("")))){
                            Toast.makeText(MainActivity.this, "Veuillez remplir le formulaire.", Toast.LENGTH_SHORT).show();
                        }else{
                            pu_nbParticipant = Integer.parseInt(et_groupNbParticipant.getText().toString());
                            if((pu_nbParticipant < 1) || (pu_nbParticipant > 50)){
                                Toast.makeText(MainActivity.this, "Le nombre de participants doit être compris entre 1 et 50.", Toast.LENGTH_SHORT).show();
                            }else if(!alreadyExist){
                                // new intent et ajout à la bdd
                                Groupe newG = new Groupe(pu_groupName, pu_groupPassword, pu_nbParticipant);
                                Log.d("groupInfo", newG.toString());
                                persistance.addNewGroup(newG);
                                Log.d("Group creation", "name: " + pu_groupName + "\n pass: " + pu_groupPassword + "\n nb: " + pu_nbParticipant);
                                Intent intent = new Intent(MainActivity.this, ManageActivity.class);
                                intent.putExtra("groupName", pu_groupName);
                                startActivity(intent);
                            }
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
                break;
            default:
                System.out.println("nothing");
                break;
        }

    }

    public void askForPass(int position){
        final Groupe gtmp = (Groupe) lv_group.getItemAtPosition(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mot de passe du groupe" + gtmp.getName());
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText et_password = new EditText(this);
        et_password.setHint("Mot de passe");
        et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(et_password);
        builder.setView(layout);

        builder.setPositiveButton("Entrer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passSaved = gtmp.getPassword();
                String passFilledUp = et_password.getText().toString();
                Sha hash = new Sha();
                try {
                    passFilledUp = hash.hash256(passFilledUp);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Log.d("hashError",e.toString() + ", passFilledUp: " + passFilledUp);
                }
                Log.d("hashTest", "rempli: " + passFilledUp + " \n saved: " + passSaved);
                if(passFilledUp.equals(passSaved)){
                    Intent intent = new Intent(MainActivity.this, ManageActivity.class);
                    intent.putExtra("groupName", gtmp.getName());
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Mot de passe éronné.", Toast.LENGTH_SHORT).show();
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
    }

    /**
     * Permet de mettre à jour l'adapter à chaque fois que l'utilisateur revient à cette activité
     */
    public  void onResume() {
        super.onResume();
        adapter.clear(); groups = persistance.getAllGroups(); adapter = new AdapterGroup(this, R.layout.groupe, groups);lv_group.setAdapter(adapter);
        Log.d("InitData",persistance.toString());
    }

}
