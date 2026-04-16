package com.learnmesh.repository;

import com.learnmesh.entity.LearningPreference;
import com.learnmesh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LearningPreferenceRepository extends JpaRepository<LearningPreference, Long> {
    List<LearningPreference> findByUser(User user);
}
