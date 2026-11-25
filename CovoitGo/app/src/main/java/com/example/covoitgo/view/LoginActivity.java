package com.example.covoitgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.AuthController;
import com.example.covoitgo.controller.ResultCallback;
import com.example.covoitgo.model.Utilisateur;

/** Vue (maquette 6) : S'authentifier */
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etMotDePasse;
    private ProgressBar progress;
    private Button btnConnexion;
    private AuthController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new AuthController(this);

        Utilisateur connecte = controller.getUtilisateurConnecte();
        if (connecte != null) {
            ouvrirAccueil(connecte);
            return;
        }

        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etMotDePasse = findViewById(R.id.etMotDePasse);
        progress = findViewById(R.id.progress);
        btnConnexion = findViewById(R.id.btnConnexion);

        btnConnexion.setOnClickListener(v -> seConnecter());
        findViewById(R.id.tvInscription).setOnClickListener(v ->
                startActivity(new Intent(this, InscriptionActivity.class)));
        findViewById(R.id.tvMotDePasseOublie).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Mot de passe oublié")
                .setMessage("Contactez l'administration CovoitGo d'Esprit pour réinitialiser votre mot de passe.")
                .setPositiveButton("OK", null)
                .show());
    }

    private void seConnecter() {
        Ui.chargement(progress, true);
        btnConnexion.setEnabled(false);
        controller.seConnecter(Ui.texte(etEmail), etMotDePasse.getText().toString(),
                new ResultCallback<Utilisateur>() {
                    @Override
                    public void onSuccess(Utilisateur u) {
                        ouvrirAccueil(u);
                    }

                    @Override
                    public void onError(String message) {
                        Ui.chargement(progress, false);
                        btnConnexion.setEnabled(true);
                        Ui.toast(LoginActivity.this, message);
                    }
                });
    }

    private void ouvrirAccueil(Utilisateur u) {
        Intent i = new Intent(this, u.estAdmin() ? AdminActivity.class : RechercheActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
