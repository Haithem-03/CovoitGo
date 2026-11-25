package com.example.covoitgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.AvisController;
import com.example.covoitgo.controller.ReservationController;
import com.example.covoitgo.controller.ResultCallback;
import com.example.covoitgo.controller.TrajetController;
import com.example.covoitgo.model.Reservation;
import com.example.covoitgo.model.Trajet;
import com.example.covoitgo.model.Utilisateur;
import com.example.covoitgo.model.Vehicule;

/** Vue (maquette 3) : detail d'un trajet et demande de place */
public class DetailTrajetActivity extends AppCompatActivity {

    private Trajet trajet;
    private int places;
    private Button btnDemander;
    private ReservationController reservationController;
    private AvisController avisController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trajet);
        reservationController = new ReservationController(this);
        avisController = new AvisController(this);
        places = getIntent().getIntExtra(RechercheActivity.EXTRA_PLACES, 1);
        btnDemander = findViewById(R.id.btnDemander);
        btnDemander.setEnabled(false);
        if (places > 1) btnDemander.setText("Demander " + places + " places");

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
        btnDemander.setOnClickListener(v -> demander());
        findViewById(R.id.tvSignaler).setOnClickListener(v -> signaler());

        long id = getIntent().getLongExtra(ResultatsActivity.EXTRA_ID_TRAJET, -1);
        new TrajetController(this).trouver(id, new ResultCallback<Trajet>() {
            @Override
            public void onSuccess(Trajet t) {
                trajet = t;
                afficher(t);
                btnDemander.setEnabled(true);
            }

            @Override
            public void onError(String message) {
                Ui.toast(DetailTrajetActivity.this, message);
                finish();
            }
        });
    }

    private void afficher(Trajet t) {
        Utilisateur c = t.getConducteur();
        texte(R.id.tvJour, Ui.jourTitre(t.getJour()));
        texte(R.id.tvHeureDepart, t.getHeureDepart());
        texte(R.id.tvVilleDepart, Ui.lieuCourt(t.getVilleDepart()));
        texte(R.id.tvPrecisionDepart, Ui.precisionLieu(t.getVilleDepart(), true));
        texte(R.id.tvHeureArrivee, t.getHeureArrivee());
        texte(R.id.tvVilleArrivee, Ui.lieuCourt(t.getVilleArrivee()));
        texte(R.id.tvPrecisionArrivee, Ui.precisionLieu(t.getVilleArrivee(), false));
        texte(R.id.tvPrix, Ui.prix(t.getPrixParPlace()));
        texte(R.id.tvInitiales, c.getInitiales());
        texte(R.id.tvConducteur, c.getNomCourt());
        texte(R.id.tvNote, Ui.note(c, true));
        texte(R.id.tvVerifie, c.estVerifie() ? "Conducteur validé · e-mail Esprit vérifié" : "E-mail Esprit vérifié");
        Vehicule v = t.getVehicule();
        texte(R.id.tvVehicule, v == null ? "Véhicule non précisé"
                : v.getMarque() + " " + v.getModele() + (v.getCouleur() == null || v.getCouleur().isEmpty() ? "" : " · " + v.getCouleur()));
        texte(R.id.tvPlacesRestantes, Ui.places(t.getPlacesRestantes()) + (t.getPlacesRestantes() > 1 ? " restantes" : " restante")
                + " sur " + t.getNbPlaces());
    }

    private void demander() {
        btnDemander.setEnabled(false);
        reservationController.reserver(trajet, places, new ResultCallback<Reservation>() {
            @Override
            public void onSuccess(Reservation r) {
                Ui.toast(DetailTrajetActivity.this, "Demande envoyée à " + trajet.getConducteur().getNomCourt());
                Intent i = new Intent(DetailTrajetActivity.this, MesReservationsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                finish();
            }

            @Override
            public void onError(String message) {
                btnDemander.setEnabled(true);
                Ui.toast(DetailTrajetActivity.this, message);
            }
        });
    }

    private void signaler() {
        if (trajet == null) return;
        Ui.dialogueTexte(this, "Signaler ce trajet", "Décrivez le problème", motif ->
                avisController.signalerTrajet(trajet, motif, new ResultCallback<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                Ui.toast(DetailTrajetActivity.this, "Signalement envoyé à l'administration");
                            }

                            @Override
                            public void onError(String message) {
                                Ui.toast(DetailTrajetActivity.this, message);
                            }
                        }));
    }

    private void texte(int id, String valeur) {
        ((TextView) findViewById(id)).setText(valeur);
    }
}
