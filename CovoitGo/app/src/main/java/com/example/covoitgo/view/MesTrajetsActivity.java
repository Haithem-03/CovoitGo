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
import java.util.List;

/** Vue : Gerer les trajets (mes trajets publies + annulation) */
public class MesTrajetsActivity extends AppCompatActivity {

    private ProgressBar progress;
    private TextView tvVide;
    private ListView listView;
    private final List<Trajet> trajets = new ArrayList<>();
    private TrajetController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);
        controller = new TrajetController(this);

        ((TextView) findViewById(R.id.tvTitre)).setText("Mes trajets");
        ((TextView) findViewById(R.id.tvSousTitre)).setText("Les trajets que vous avez publiés.");
        progress = findViewById(R.id.progress);
        tvVide = findViewById(R.id.tvVide);
        listView = findViewById(R.id.listView);
        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        charger();
    }

    private void charger() {
        Ui.chargement(progress, true);
        controller.chargerMesTrajets(new ResultCallback<List<Trajet>>() {
            @Override
            public void onSuccess(List<Trajet> resultat) {
                Ui.chargement(progress, false);
                trajets.clear();
                if (resultat != null) trajets.addAll(resultat);
                Ui.listeVide(tvVide, trajets.isEmpty(), "Vous n'avez publié aucun trajet.");
                listView.setAdapter(new CarteAdapter<>(MesTrajetsActivity.this, R.layout.item_carte, trajets,
                        MesTrajetsActivity.this::remplir));
            }

            @Override
            public void onError(String message) {
                Ui.chargement(progress, false);
                Ui.listeVide(tvVide, true, message);
            }
        });
    }

    private void remplir(View v, Trajet t) {
        VueCarte c = VueCarte.de(v);
        boolean passe = Ui.estPasse(t.getDateDepart());
        c.tvHaut.setText(Ui.jourCourt(t.getJour()) + " · " + t.getHeureDepart()
                + (t.getJoursRecurrence() != null ? " · récurrent" : ""));
        if (passe && !"ANNULE".equals(t.getStatut())) {
            Ui.badge(c.tvBadge, "TERMINE");
            c.tvBadge.setText("Terminé");
        } else {
            Ui.badge(c.tvBadge, t.getStatut());
        }
        c.tvTitre.setText(Ui.lieuCourt(t.getVilleDepart()) + " → " + Ui.lieuCourt(t.getVilleArrivee()));
        c.tvLigne1.setText(Ui.places(t.getPlacesRestantes()) + (t.getPlacesRestantes() > 1 ? " restantes" : " restante")
                + " sur " + t.getNbPlaces());
        c.ligne2("Arrivée estimée " + t.getHeureArrivee());
        c.tvPrix.setText(Ui.prix(t.getPrixParPlace()));

        if ("ANNULE".equals(t.getStatut())) {
            c.carte.setAlpha(0.75f);
        } else if (!passe) {
            c.bouton1("Demandes", x -> startActivity(new Intent(this, DemandesActivity.class)));
            c.bouton2("Annuler le trajet", x -> Ui.confirmer(this, "Annuler ce trajet ?",
                    "Les réservations en cours seront annulées.", () -> annuler(t)));
        }
    }

    private void annuler(Trajet t) {
        controller.annuler(t, new ResultCallback<Trajet>() {
            @Override
            public void onSuccess(Trajet maj) {
                Ui.toast(MesTrajetsActivity.this, "Trajet annulé");
                charger();
            }

            @Override
            public void onError(String message) {
                Ui.toast(MesTrajetsActivity.this, message);
            }
        });
    }
}
