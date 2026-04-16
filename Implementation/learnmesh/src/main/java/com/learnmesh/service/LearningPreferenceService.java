package com.learnmesh.service;

import com.learnmesh.entity.LearningPreference;
import com.learnmesh.entity.User;
import com.learnmesh.repository.LearningPreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningPreferenceService {

    private final LearningPreferenceRepository preferenceRepo;

    public LearningPreferenceService(LearningPreferenceRepository preferenceRepo) {
        this.preferenceRepo = preferenceRepo;
    }

    public void savePreference(LearningPreference preference, User user) {
        preference.setUser(user);
        preferenceRepo.save(preference);
    }

    public List<LearningPreference> getPreferencesForUser(User user) {
        return preferenceRepo.findByUser(user);
    }

    public LearningPreference getById(Long id) {
        return preferenceRepo.findById(id).orElse(null);
    }

    public void deletePreference(Long id, User user) {
        LearningPreference pref = preferenceRepo.findById(id).orElse(null);

        if (pref != null && pref.getUser().getId().equals(user.getId())) {
            preferenceRepo.delete(pref);
        }
    }

    public void updatePreference(LearningPreference updated, User user) {
        LearningPreference existing = preferenceRepo.findById(updated.getId()).orElse(null);

        if (existing != null && existing.getUser().getId().equals(user.getId())) {
            existing.setBranch(updated.getBranch());
            existing.setTechnology(updated.getTechnology());
            existing.setKnowledgeLevel(updated.getKnowledgeLevel());
            existing.setPreferredMeetingMode(updated.getPreferredMeetingMode());
            existing.setPreferredStartTime(updated.getPreferredStartTime());
            existing.setPreferredEndTime(updated.getPreferredEndTime());
            preferenceRepo.save(existing);
        }
    }
}
