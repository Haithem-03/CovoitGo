package com.example.covoitgo.view;

import android.view.View;
import android.widget.TextView;

import com.example.covoitgo.R;
import com.google.android.material.button.MaterialButton;

/** Acces aux elements de item_carte.xml, remis a zero avant chaque utilisation. */
public class VueCarte {

    public final View carte, separateur, zoneDetail, zoneBoutons;
    public final TextView tvHaut, tvBadge, tvTitre, tvLigne1, tvLigne2, tvPrix, tvLien;
    public final MaterialButton btnAction1, btnAction2;

    private VueCarte(View v) {
        carte = v;
        separateur = v.findViewById(R.id.separateur);
        zoneDetail = v.findViewById(R.id.zoneDetail);
        zoneBoutons = v.findViewById(R.id.zoneBoutons);
        tvHaut = v.findViewById(R.id.tvHaut);
        tvBadge = v.findViewById(R.id.tvBadge);
        tvTitre = v.findViewById(R.id.tvTitre);
        tvLigne1 = v.findViewById(R.id.tvLigne1);
        tvLigne2 = v.findViewById(R.id.tvLigne2);
        tvPrix = v.findViewById(R.id.tvPrix);
        tvLien = v.findViewById(R.id.tvLien);
        btnAction1 = v.findViewById(R.id.btnAction1);
        btnAction2 = v.findViewById(R.id.btnAction2);
    }

    public static VueCarte de(View v) {
        VueCarte c = new VueCarte(v);
        c.carte.setAlpha(1f);
        Ui.visible(c.separateur, true);
        Ui.visible(c.zoneDetail, true);
        Ui.visible(c.tvBadge, true);
        Ui.visible(c.tvLigne2, false);
        Ui.visible(c.tvLien, false);
        Ui.visible(c.zoneBoutons, false);
        Ui.visible(c.btnAction1, false);
        Ui.visible(c.btnAction2, false);
        c.tvPrix.setText("");
        c.btnAction1.setOnClickListener(null);
        c.btnAction2.setOnClickListener(null);
        c.tvLien.setOnClickListener(null);
        return c;
    }

    public void ligne2(String texte) {
        tvLigne2.setText(texte);
        Ui.visible(tvLigne2, texte != null && !texte.isEmpty());
    }

    public void bouton1(String texte, View.OnClickListener action) {
        Ui.visible(zoneBoutons, true);
        Ui.visible(btnAction1, true);
        btnAction1.setText(texte);
        btnAction1.setOnClickListener(action);
    }

    public void bouton2(String texte, View.OnClickListener action) {
        Ui.visible(zoneBoutons, true);
        Ui.visible(btnAction2, true);
        btnAction2.setText(texte);
        btnAction2.setOnClickListener(action);
    }

    public void lien(String texte, View.OnClickListener action) {
        Ui.visible(tvLien, true);
        tvLien.setText(texte);
        tvLien.setOnClickListener(action);
    }

    /** Carte grisee sans detail (reservation refusee / annulee) */
    public void attenuer() {
        carte.setAlpha(0.75f);
        Ui.visible(separateur, false);
        Ui.visible(zoneDetail, false);
    }
}
