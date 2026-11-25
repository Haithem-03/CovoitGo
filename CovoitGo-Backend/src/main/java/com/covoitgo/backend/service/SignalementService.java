package com.covoitgo.backend.service;

import com.covoitgo.backend.dto.SignalementRequest;
import com.covoitgo.backend.exception.ApiException;
import com.covoitgo.backend.model.Signalement;
import com.covoitgo.backend.model.StatutSignalement;
import com.covoitgo.backend.repository.SignalementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SignalementService {

    private final SignalementRepository signalementRepository;
    private final UtilisateurService utilisateurService;

    public SignalementService(SignalementRepository signalementRepository, UtilisateurService utilisateurService) {
        this.signalementRepository = signalementRepository;
        this.utilisateurService = utilisateurService;
    }

    // UC : Signaler un comportement
    public Signalement signaler(SignalementRequest req) {
        if (req.motif() == null || req.motif().isBlank()) {
            throw ApiException.invalide("Le motif est obligatoire");
        }
        if (req.idAuteur().equals(req.idCible())) {
            throw ApiException.invalide("Vous ne pouvez pas vous signaler vous-meme");
        }
        Signalement s = new Signalement();
        s.setAuteur(utilisateurService.trouver(req.idAuteur()));
        s.setCible(utilisateurService.trouver(req.idCible()));
        s.setMotif(req.motif().trim());
        s.setStatut(StatutSignalement.EN_ATTENTE);
        s.setDateSignalement(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return signalementRepository.save(s);
    }

    // UC : Gerer les reclamations (admin)
    public List<Signalement> tous() {
        return signalementRepository.findAllByOrderByIdSignalementDesc();
    }

    public Signalement traiter(Long id, String decision) {
        Signalement s = signalementRepository.findById(id)
                .orElseThrow(() -> ApiException.introuvable("Signalement introuvable"));
        if (decision == null || decision.isBlank()) {
            throw ApiException.invalide("La decision est obligatoire");
        }
        s.setDecision(decision.trim());
        s.setStatut(StatutSignalement.TRAITE);
        return signalementRepository.save(s);
    }
}
