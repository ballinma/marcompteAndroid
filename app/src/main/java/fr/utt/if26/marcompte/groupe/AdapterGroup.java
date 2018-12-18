package fr.utt.if26.marcompte.groupe;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fr.utt.if26.marcompte.R;

/**
 * AdapterGroup: permet d'afficher le nom et le total de chaque groupe dans une listView
 */
public class AdapterGroup extends ArrayAdapter<Groupe> {

    ArrayList<Groupe> groups;
    Context contexte;
    int ressource;

    /**
     * Constructeur de AdapterGroup
     * @param context Activité où l'adapter va être utiliser
     * @param resource Le layout utilisé pour afficher la liste des groupes
     * @param data Le dataset que l'on veut afficher
     */
    public AdapterGroup(Context context, int resource, ArrayList<Groupe> data) {
        super(context, resource,data);
        this.groups = data;
        this.contexte = context;
        this.ressource = resource;
    }

    /**
     * Permet d'afficher l'adapter (appelé automatiquement)
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) contexte).getLayoutInflater();
        View v = inflater.inflate(ressource, parent, false);


        Groupe group = groups.get(position);

        TextView tv_name = (TextView) v.findViewById(R.id.tv_g_name);
        tv_name.setText(group.getName());

        TextView tv_total = (TextView) v.findViewById(R.id.tv_g_total);
        tv_total.setText(String.format("%.2f",group.getTotalTransaction()));


        return v;

    }
}
