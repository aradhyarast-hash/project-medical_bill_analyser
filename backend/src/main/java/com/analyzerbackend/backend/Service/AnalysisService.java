package com.analyzerbackend.backend.Service;

import com.analyzerbackend.backend.Dto.AnalysisResponseDto;
import com.analyzerbackend.backend.Dto.BillAnalysisResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface AnalysisService {

    AnalysisResponseDto analyzeSingle(String medicineName, MultipartFile prescription);

    BillAnalysisResponseDto auditBill(MultipartFile billFile, String medicineName);
}
