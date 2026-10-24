package com.analyzerbackend.backend.Service;

import com.analyzerbackend.backend.Dto.MedicineAlternativeDto;
import com.analyzerbackend.backend.Entity.Medicine;

import java.util.List;

public interface AlternativeService {

    List<MedicineAlternativeDto> findAlternatives(
            Medicine prescribedMedicine,
            String parsedSalt,
            String parsedStrength,
            String parsedForm,
            String parsedReleaseType,
            Double inputPrice
    );
}
