package com.example.covoitgo.view;

import android.content.Intent;
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
import com.example.covoitgo.model.Vehicule;

import java.util.ArrayList;
import java.util.List;

/** Vue (maquette 4) : Gerer les reservations (passager) + evaluer / signaler le conducteur */
public class MesReservationsActivity extends AppCompatActivity {

    private TextView ongletAVenir, ongletPassees, tvVide;
    private ProgressBar progress;
    private ListView listView;
    private boolean aVenir = true;

    private final List<Reservation> toutes = new ArrayList<>();
    private final List<Reservation> affichees = new ArrayList<>();
    private ReservationController reservationController;
    private AvisController avisController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_reservations);
        Navigation.installer(this, R.id.nav_reservations);
        reservationController = new ReservationController(this);
        avisController = new AvisController(this);

        ongletAVenir = findViewById(R.id.ongletAVenir);
        ongletPassees = findViewById(R.id.ongletPassees);
        tvVide = findViewById(R.id.tvVide);
        progress = findViewById(R.id.progress);
        listView = findViewById(R.id.listView);

        ongletAVenir.setOnClickListener(v -> { aVenir = true; afficher(); });
        ongletPassees.setOnClickListener(v -> { aVenir = false; afficher(); });
    }

    @Override
    protected void onResume() {
        super.onResume();
        charger();
    }

    private void charger() {
        Ui.chargement(progress, true);
        reservationController.chargerMesReservations(new ResultCallback<List<Reservation>>() {
            @Override
            public void onSuccess(List<Reservation> resultat) {
                Ui.chargement(progress, false);
                toutes.clear();
                if (resultat != null) toutes.addAll(resultat);
                afficher();
            }

            @Override
            public void onError(String message) {
                Ui.chargement(progress, false);
                Ui.listeVide(tvVide, true, message);
            }
        });
    }

    private void afficher() {
        Ui.onglet(ongletAVenir, aVenir);
        Ui.onglet(ongletPassees, !aVenir);
        affichees.clear();
        for (Reservation r : toutes) {
            if (Ui.estPasse(r.getTrajet().getDateDepart()) != aVenir) affichees.add(r);
        }
        Ui.listeVide(tvVide, affichees.isEmpty(), aVenir
                ? "Aucune réservation à venir.\nRecherchez un trajet pour réserver une place."
                : "Aucune réservation passée.");
        listView.setAdapter(new CarteAdapter<>(this, R.layout.item_carte, affichees, this::remplir));
    }

    private void remplir(View v, Reservation r) {
        VueCarte c = VueCarte.de(v);
        Trajet t = r.getTrajet();
        Utilisateur conducteur = t.getConducteur();
        String statut = r.getStatut();

        c.tvHaut.setText(Ui.jourCourt(t.getJour()) + " · " + t.getHeureDepart());
        Ui.badge(c.tvBadge, statut);
        c.tvTitre.setText(Ui.lieuCourt(t.getVilleDepart()) + " → " + Ui.lieuCourt(t.getVilleArrivee()));
        c.tvPrix.setText(Ui.prix(t.getPrixParPlace() * r.getNbPlaces()));
        String places = r.getNbPlaces() > 1 ? " · " + Ui.places(r.getNbPlaces()) : "";

        if ("ACCEPTEE".equals(statut)) {
            Vehicule veh = t.getVehicule();
            c.tvLigne1.setText(conducteur.getNomCourt()
                    + (veh == null ? "" : " · " + veh.getMarque() + " " + veh.getModele()
                    + (veh.getCouleur() == null ? "" : " " + veh.getCouleur())) + places);
            c.ligne2("+216 " + conducteur.getTelephone());
        } else if ("EN_ATTENTE".equals(statut)) {
            c.tvLigne1.setText(conducteur.getNomCourt() + " · réponse attendue" + places);
        } else {
            c.attenuer();
            c.lien("Chercher un autre trajet", x -> {
                Intent i = new Intent(this, RechercheActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
            });
            return;
        }

        if (aVenir) {
            c.bouton1("Annuler la réservation", x -> annuler(r));
        } else if ("ACCEPTEE".equals(statut)) {
            c.bouton1("Évaluer", x -> Ui.dialogueEvaluation(this, conducteur.getNomCourt(), (note, com) ->
                    avisController.evaluer(conducteur.getIdUtilisateur(), t.getIdTrajet(), note, com,
                            message("Merci pour votre avis !"))));
            c.bouton2("Signaler", x -> Ui.dialogueTexte(this, "Signaler " + conducteur.getNomCourt(),
                    "Décrivez le problème", motif -> avisController.signalerTrajet(t, motif,
                            message("Signalement envoyé à l'administration"))));
        }
    }

    private void annuler(Reservation r) {
        Ui.confirmer(this, "Annuler la réservation ?", r.getTrajet().getItineraire(), () ->
                reservationController.annuler(r, new ResultCallback<Reservation>() {
                    @Override
                    public void onSuccess(Reservation maj) {
                        Ui.toast(MesReservationsActivity.this, "Réservation annulée");
                        charger();
                    }

                    @Override
                    public void onError(String message) {
                        Ui.toast(MesReservationsActivity.this, message);
                    }
                }));
    }

    private ResultCallback<Object> message(String succes) {
        return new ResultCallback<Object>() {
            @Override
            public void onSuccess(Object o) { Ui.toast(MesReservationsActivity.this, succes); }

            @Override
            public void onError(String message) { Ui.toast(MesReservationsActivity.this, message); }
        };
    }
}
