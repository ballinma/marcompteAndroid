package fr.utt.if26.marcompte.transaction;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fr.utt.if26.marcompte.R;

public class AdapterTransaction extends ArrayAdapter<Transaction> {

    ArrayList<Transaction> transactions;
    Context contexte;
    int ressource;

    public AdapterTransaction(@NonNull Context context, int resource, ArrayList<Transaction> data) {
        super(context, resource, data);
        this.transactions = data;
        this.contexte = context;
        this.ressource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) contexte).getLayoutInflater();
        View v = inflater.inflate(ressource, parent, false);

        Transaction transaction = transactions.get(position);

        TextView tv_type = (TextView) v.findViewById(R.id.tv_t_type);
        tv_type.setText(transaction.getType());

        TextView tv_name = (TextView) v.findViewById(R.id.tv_t_name);
        tv_name.setText(transaction.getName());

        TextView tv_price = (TextView) v.findViewById(R.id.tv_t_price);
        tv_price.setText(String.format("%.2f",transaction.getPrice()));

        return v;

    }
}
