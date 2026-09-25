package cm.kfokam48.presencepair.web.dto;

import cm.kfokam48.presencepair.domain.Etudiant;

public record EtudiantDto(Long id, String nom, Long promotionId) {

    public static EtudiantDto de(Etudiant etudiant) {
        return new EtudiantDto(etudiant.getId(), etudiant.getNom(), etudiant.getPromotion().getId());
    }
}
