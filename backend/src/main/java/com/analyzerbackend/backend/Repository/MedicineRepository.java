package com.analyzerbackend.backend.Repository;

import com.analyzerbackend.backend.Entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findFirstByBrandNameNormalizedIgnoreCase(String brandNameNormalized);

    Optional<Medicine> findFirstByBrandNameIgnoreCase(String brandName);

    Optional<Medicine> findFirstByMatchKeyIgnoreCase(String matchKey);

    List<Medicine> findBySaltNormalizedIgnoreCase(String saltNormalized);

    List<Medicine> findBySaltNormalizedIgnoreCaseAndStrengthIgnoreCaseAndDosageFormIgnoreCase(
            String saltNormalized, String strength, String dosageForm);

    List<Medicine> findBySaltNormalizedIgnoreCaseAndStrengthIgnoreCase(
            String saltNormalized, String strength);

    @Query("SELECT m FROM Medicine m WHERE LOWER(m.brandName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(m.saltNormalized) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY m.brandName ASC")
    List<Medicine> searchMedicines(@Param("query") String query);

    @Query("SELECT m FROM Medicine m WHERE LOWER(m.brandName) LIKE LOWER(CONCAT(:query, '%')) ORDER BY m.brandName ASC")
    List<Medicine> autocompleteMedicines(@Param("query") String query);
}
