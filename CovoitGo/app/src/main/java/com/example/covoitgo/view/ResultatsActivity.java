package com.example.covoitgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.ResultCallback;
import com.example.covoitgo.controller.TrajetController;
import com.example.covoitgo.model.Trajet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Vue (maquette 2) : trajets disponibles + tris */
public class ResultatsActivity extends AppCompatActivity {

    public static final String EXTRA_ID_TRAJET = "idTrajet";

    private static final int TRI_HEURE = 0, TRI_PRIX = 1, TRI_NOTE = 2;

    private String depart, arrivee, date;
    private int places;
    private int tri = TRI_HEURE;

    private TextView tvNombre, tvVide, triHeure, triPrix, triNote;
    private ProgressBar progress;
    private ListView listView;
    private final List<Trajet> trajets = new ArrayList<>();
    private TrajetController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultats);
        Navigation.installer(this, R.id.nav_rechercher);
        controller = new TrajetController(this);

        depart = valeur(RechercheActivity.EXTRA_DEPART);
        arrivee = valeur(RechercheActivity.EXTRA_ARRIVEE);
        date = getIntent().getStringExtra(RechercheActivity.EXTRA_DATE);
        places = getIntent().getIntExtra(RechercheActivity.EXTRA_PLACES, 1);

        TextView tvDestination = findViewById(R.id.tvDestination);
        tvDestination.setText(arrivee.isEmpty() ? "Tous les trajets" : "Vers " + Ui.lieuCourt(arrivee));
        ((TextView) findViewById(R.id.tvCriteres)).setText(
                (depart.isEmpty() ? "" : "Depuis " + Ui.lieuCourt(depart) + " · ")
                        + Ui.jourCourt(date) + " · " + Ui.passagers(places));

        tvNombre = findViewById(R.id.tvNombre);
        tvVide = findViewById(R.id.tvVide);
        progress = findViewById(R.id.progress);
        listView = findViewById(R.id.listView);
        triHeure = findViewById(R.id.triHeure);
        triPrix = findViewById(R.id.triPrix);
        triNote = findViewById(R.id.triNote);

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
        triHeure.setOnClickListener(v -> trier(TRI_HEURE));
        triPrix.setOnClickListener(v -> trier(TRI_PRIX));
        triNote.setOnClickListener(v -> trier(TRI_NOTE));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(this, DetailTrajetActivity.class);
            i.putExtra(EXTRA_ID_TRAJET, trajets.get(position).getIdTrajet());
            i.putExtra(RechercheActivity.EXTRA_PLACES, places);
            startActivity(i);
        });
        afficherTri();
    }

    @Override
    protected void onResume() {
        super.onResume();
        charger();
    }

    private String valeur(String cle) {
        String v = getIntent().getStringExtra(cle);
        return v == null ? "" : v.trim();
    }

    private void charger() {
        Ui.chargement(progress, true);
        controller.rechercher(depart, arrivee, date, places, new ResultCallback<List<Trajet>>() {
            @Override
            public void onSuccess(List<Trajet> resultat) {
                Ui.chargement(progress, false);
                trajets.clear();
                if (resultat != null) trajets.addAll(resultat);
                trier(tri);
            }

            @Override
            public void onError(String message) {
                Ui.chargement(progress, false);
                Ui.listeVide(tvVide, true, message);
            }
        });
    }

    private void trier(int nouveauTri) {
        tri = nouveauTri;
        Comparator<Trajet> c;
        if (tri == TRI_PRIX) c = (a, b) -> Double.compare(a.getPrixParPlace(), b.getPrixParPlace());
        else if (tri == TRI_NOTE) c = (a, b) -> Double.compare(b.getConducteur().getNoteMoyenne(), a.getConducteur().getNoteMoyenne());
        else c = (a, b) -> a.getDateDepart().compareTo(b.getDateDepart());
        Collections.sort(trajets, c);
        afficherTri();

        int n = trajets.size();
        tvNombre.setText(n + (n > 1 ? " trajets disponibles" : " trajet disponible"));
        Ui.listeVide(tvVide, n == 0, "Aucun trajet ne correspond à votre recherche.\nEssayez une autre date ou un autre départ.");
        listView.setAdapter(new CarteAdapter<>(this, R.layout.item_trajet, trajets, this::remplir));
    }

    private void afficherTri() {
        Ui.puce(triHeure, tri == TRI_HEURE);
        Ui.puce(triPrix, tri == TRI_PRIX);
        Ui.puce(triNote, tri == TRI_NOTE);
    }

    private void remplir(View v, Trajet t) {
        ((TextView) v.findViewById(R.id.tvHeureDepart)).setText(t.getHeureDepart());
        ((TextView) v.findViewById(R.id.tvVilleDepart)).setText(Ui.lieuCourt(t.getVilleDepart()));
        ((TextView) v.findViewById(R.id.tvHeureArrivee)).setText(t.getHeureArrivee());
        ((TextView) v.findViewById(R.id.tvVilleArrivee)).setText(Ui.lieuCourt(t.getVilleArrivee()));
        ((TextView) v.findViewById(R.id.tvPrix)).setText(Ui.prix(t.getPrixParPlace()));
        ((TextView) v.findViewById(R.id.tvInitiales)).setText(t.getConducteur().getInitiales());
        ((TextView) v.findViewById(R.id.tvConducteur)).setText(t.getConducteur().getNomCourt());
        ((TextView) v.findViewById(R.id.tvNote)).setText(Ui.note(t.getConducteur(), false));
        ((TextView) v.findViewById(R.id.tvPlaces)).setText(Ui.places(t.getPlacesRestantes()));
    }
}
