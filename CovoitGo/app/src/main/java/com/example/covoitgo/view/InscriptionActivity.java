package com.example.covoitgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covoitgo.R;
import com.example.covoitgo.controller.AuthController;
import com.example.covoitgo.controller.ResultCallback;
import com.example.covoitgo.model.Utilisateur;

/** Vue : S'inscrire (e-mail @esprit.tn obligatoire) */
public class InscriptionActivity extends AppCompatActivity {

    private EditText etNom, etPrenom, etEmail, etTelephone, etMotDePasse, etConfirmation;
    private ProgressBar progress;
    private Button btnInscrire;
    private AuthController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        controller = new AuthController(this);

        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etEmail = findViewById(R.id.etEmail);
        etTelephone = findViewById(R.id.etTelephone);
        etMotDePasse = findViewById(R.id.etMotDePasse);
        etConfirmation = findViewById(R.id.etConfirmation);
        progress = findViewById(R.id.progress);
        btnInscrire = findViewById(R.id.btnInscrire);

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
        btnInscrire.setOnClickListener(v -> inscrire());
    }

    private void inscrire() {
        Ui.chargement(progress, true);
        btnInscrire.setEnabled(false);
        controller.inscrire(Ui.texte(etNom), Ui.texte(etPrenom), Ui.texte(etEmail), Ui.texte(etTelephone),
                etMotDePasse.getText().toString(), etConfirmation.getText().toString(),
                new ResultCallback<Utilisateur>() {
                    @Override
                    public void onSuccess(Utilisateur u) {
                        Ui.toast(InscriptionActivity.this, "Bienvenue " + u.getPrenom() + " !");
                        Intent i = new Intent(InscriptionActivity.this, RechercheActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }

                    @Override
                    public void onError(String message) {
                        Ui.chargement(progress, false);
                        btnInscrire.setEnabled(true);
                        Ui.toast(InscriptionActivity.this, message);
                    }
                });
    }
}
