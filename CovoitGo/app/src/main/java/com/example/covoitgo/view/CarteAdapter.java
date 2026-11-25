package com.example.covoitgo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/** Adapter generique : une carte (layout XML) par element de la liste. */
public class CarteAdapter<T> extends BaseAdapter {

    public interface Remplisseur<T> {
        void remplir(View carte, T element);
    }

    private final Context context;
    private final int layout;
    private final List<T> elements;
    private final Remplisseur<T> remplisseur;

    public CarteAdapter(Context context, int layout, List<T> elements, Remplisseur<T> remplisseur) {
        this.context = context;
        this.layout = layout;
        this.elements = elements != null ? elements : new ArrayList<>();
        this.remplisseur = remplisseur;
    }

    public void setElements(List<T> nouveauxElements) {
        this.elements.clear();
        if (nouveauxElements != null) {
            this.elements.addAll(nouveauxElements);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return elements.size(); }

    @Override
    public T getItem(int position) { return elements.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView != null ? convertView : LayoutInflater.from(context).inflate(layout, parent, false);
        remplisseur.remplir(v, elements.get(position));
        return v;
    }
}