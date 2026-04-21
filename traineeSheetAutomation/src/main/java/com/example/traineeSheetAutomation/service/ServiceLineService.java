package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.ServiceLineRequestDTO;
import com.example.traineeSheetAutomation.dto.ServiceLineResponseDTO;
import com.example.traineeSheetAutomation.entity.ServiceLine;
import com.example.traineeSheetAutomation.repository.ServiceLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceLineService {

    private final ServiceLineRepository serviceLineRepository;

    public ServiceLineResponseDTO createServiceLine(ServiceLineRequestDTO request) {

        if (serviceLineRepository.existsByDepartment(request.getDepartment())) {
            throw new RuntimeException("Service Line already exists for department: " + request.getDepartment());
        }

        ServiceLine serviceLine = new ServiceLine();
        serviceLine.setDepartment(request.getDepartment());

        return convertToDTO(serviceLineRepository.save(serviceLine));
    }

    public List<ServiceLineResponseDTO> getAllServiceLines() {

        List<ServiceLineResponseDTO> serviceLines = serviceLineRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (serviceLines.isEmpty()) {
            throw new RuntimeException("No service lines found");
        }

        return serviceLines;
    }

    public ServiceLineResponseDTO getServiceLineById(Long id) {

        ServiceLine serviceLine = serviceLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Service Line not found with ID: " + id));

        return convertToDTO(serviceLine);
    }

    public ServiceLineResponseDTO updateServiceLine(Long id, ServiceLineRequestDTO request) {

        ServiceLine serviceLine = serviceLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Service Line not found with ID: " + id));

        if (!serviceLine.getDepartment().equals(request.getDepartment()) &&
                serviceLineRepository.existsByDepartment(request.getDepartment())) {
            throw new RuntimeException("Service Line already exists for department: " + request.getDepartment());
        }

        serviceLine.setDepartment(request.getDepartment());

        return convertToDTO(serviceLineRepository.save(serviceLine));
    }

    public void deleteServiceLine(Long id) {

        if (!serviceLineRepository.existsById(id)) {
            throw new RuntimeException("Service Line not found with ID: " + id);
        }

        serviceLineRepository.deleteById(id);
    }

    private ServiceLineResponseDTO convertToDTO(ServiceLine serviceLine) {
        return ServiceLineResponseDTO.builder()
                .serviceLineId(serviceLine.getServiceLineID())
                .department(serviceLine.getDepartment())
                .build();
    }
}
