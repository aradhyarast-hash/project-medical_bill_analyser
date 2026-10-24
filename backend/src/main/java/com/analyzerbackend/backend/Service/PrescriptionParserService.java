package com.analyzerbackend.backend.Service;

import com.analyzerbackend.backend.Dto.BillItemDto;
import com.analyzerbackend.backend.Dto.InputMedicineDto;

import java.util.List;

public interface PrescriptionParserService {
    List<InputMedicineDto> parseTextToCandidateMedicines(String text);
    InputMedicineDto parseSingleMedicineQuery(String medicineQuery);
}
