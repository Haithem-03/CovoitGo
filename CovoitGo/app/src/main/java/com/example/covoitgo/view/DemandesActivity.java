package com.example.covoitgo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.AvisController;
import com.example.covoitgo.controller.ReservationController;
import com.example.covoitgo.controller.ResultCallback;
import com.example.covoitgo.model.Reservation;
import com.example.covoitgo.model.Trajet;
import com.example.covoitgo.model.Utilisateur;

import java.util.ArrayList;
import java.util.List;

/** Vue : Traiter une reservation (conducteur) + evaluer / signaler un passager */
public class DemandesActivity extends AppCompatActivity {

    private ProgressBar progress;
    private TextView tvVide;
    private ListView listView;
    private final List<Reservation> reservations = new ArrayList<>();
    private ReservationController reservationController;
    private AvisController avisController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);
        reservationController = new ReservationController(this);
        avisController = new AvisController(this);

        ((TextView) findViewById(R.id.tvTitre)).setText("Demandes reçues");
        ((TextView) findViewById(R.id.tvSousTitre)).setText("Acceptez ou refusez les demandes des passagers.");
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
        reservationController.chargerDemandesRecues(new ResultCallback<List<Reservation>>() {
            @Override
            public void onSuccess(List<Reservation> resultat) {
                Ui.chargement(progress, false);
                reservations.clear();
                if (resultat != null) reservations.addAll(resultat);
                Ui.listeVide(tvVide, reservations.isEmpty(), "Aucune demande de réservation pour l'instant.");
                listView.setAdapter(new CarteAdapter<>(DemandesActivity.this, R.layout.item_carte, reservations,
                        DemandesActivity.this::remplir));
            }

            @Override
            public void onError(String message) {
                Ui.chargement(progress, false);
                Ui.listeVide(tvVide, true, message);
            }
        });
    }

    private void remplir(View v, Reservation r) {
        VueCarte c = VueCarte.de(v);
        Trajet t = r.getTrajet();
        Utilisateur passager = r.getPassager();
        String statut = r.getStatut();
        boolean passe = Ui.estPasse(t.getDateDepart());

        c.tvHaut.setText(Ui.jourCourt(t.getJour()) + " · " + t.getHeureDepart());
        Ui.badge(c.tvBadge, statut);
        c.tvTitre.setText(Ui.lieuCourt(t.getVilleDepart()) + " → " + Ui.lieuCourt(t.getVilleArrivee()));
        c.tvLigne1.setText(passager.getNomCourt() + " · " + Ui.places(r.getNbPlaces())
                + String.format(java.util.Locale.FRANCE, " · ★ %.1f", passager.getNoteMoyenne()));
        c.tvPrix.setText(Ui.prix(t.getPrixParPlace() * r.getNbPlaces()));

        if ("ACCEPTEE".equals(statut)) {
            c.ligne2("+216 " + passager.getTelephone());
            if (passe) {
                c.bouton1("Évaluer", x -> Ui.dialogueEvaluation(this, passager.getNomCourt(), (note, com) ->
                        avisController.evaluer(passager.getIdUtilisateur(), t.getIdTrajet(), note, com,
                                message("Évaluation envoyée"))));
                c.bouton2("Signaler", x -> Ui.dialogueTexte(this, "Signaler " + passager.getNomCourt(),
                        "Décrivez le problème", motif -> avisController.signaler(passager.getIdUtilisateur(), motif,
                                message("Signalement envoyé à l'administration"))));
            }
        } else if ("EN_ATTENTE".equals(statut) && !passe) {
            c.bouton1("Refuser", x -> reservationController.refuser(r, resultat("Demande refusée")));
            c.bouton2("Accepter", x -> reservationController.accepter(r, resultat("Demande acceptée")));
        } else if (!"EN_ATTENTE".equals(statut)) {
            c.attenuer();
        }
    }

    private ResultCallback<Reservation> resultat(String succes) {
        return new ResultCallback<Reservation>() {
            @Override
            public void onSuccess(Reservation r) {
                Ui.toast(DemandesActivity.this, succes);
                charger();
            }

            @Override
            public void onError(String message) {
                Ui.toast(DemandesActivity.this, message);
            }
        };
    }

    private ResultCallback<Object> message(String succes) {
        return new ResultCallback<Object>() {
            @Override
            public void onSuccess(Object o) { Ui.toast(DemandesActivity.this, succes); }

            @Override
            public void onError(String message) { Ui.toast(DemandesActivity.this, message); }
        };
    }
}
