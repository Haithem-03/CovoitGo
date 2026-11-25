package com.covoitgo.backend.config;

import com.covoitgo.backend.model.Role;
import com.covoitgo.backend.model.Utilisateur;
import com.covoitgo.backend.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Cree le compte administrateur au premier lancement. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder encoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository, BCryptPasswordEncoder encoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (!utilisateurRepository.existsByEmail("admin@esprit.tn")) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setPrenom("CovoitGo");
            admin.setEmail("admin@esprit.tn");
            admin.setMotDePasse(encoder.encode("admin123"));
            admin.setTelephone("00000000");
            admin.setRole(Role.ADMIN);
            utilisateurRepository.save(admin);
            System.out.println(">>> Compte admin cree : admin@esprit.tn / admin123");
        }
    }
}
