package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.ServiceLineRequestDTO;
import com.example.traineeSheetAutomation.dto.ServiceLineResponseDTO;
import com.example.traineeSheetAutomation.entity.ServiceLine;
import com.example.traineeSheetAutomation.entity.enums.Department;
import com.example.traineeSheetAutomation.repository.ServiceLineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceLineServiceTest {

    @Mock
    private ServiceLineRepository serviceLineRepository;

    @InjectMocks
    private ServiceLineService serviceLineService;

    private ServiceLine sampleServiceLine;
    private ServiceLineRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleServiceLine = new ServiceLine();
        sampleServiceLine.setServiceLineID(1L);
        sampleServiceLine.setDepartment(Department.DEVELOPMENT);

        sampleRequest = new ServiceLineRequestDTO(Department.DEVELOPMENT);
    }


    @Test
    void createServiceLine_whenCreated_returnsSuccess() {
        when(serviceLineRepository.existsByDepartment(Department.DEVELOPMENT)).thenReturn(false);
        when(serviceLineRepository.save(any(ServiceLine.class))).thenReturn(sampleServiceLine);

        ServiceLineResponseDTO result = serviceLineService.createServiceLine(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getServiceLineId()).isEqualTo(1L);
        assertThat(result.getDepartment()).isEqualTo(Department.DEVELOPMENT);

        verify(serviceLineRepository, times(1)).save(any(ServiceLine.class));
    }

    @Test
    void createServiceLine_whenDepartmentAlreadyExists_throwsException() {
        when(serviceLineRepository.existsByDepartment(Department.DEVELOPMENT)).thenReturn(true);

        assertThatThrownBy(() -> serviceLineService.createServiceLine(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service Line already exists for department: DEVELOPMENT");

        verify(serviceLineRepository, never()).save(any(ServiceLine.class));
    }

    @Test
    void createServiceLine_mapsDepartmentCorrectly() {
        when(serviceLineRepository.existsByDepartment(Department.DEVELOPMENT)).thenReturn(false);
        when(serviceLineRepository.save(any(ServiceLine.class))).thenReturn(sampleServiceLine);

        ServiceLineResponseDTO result = serviceLineService.createServiceLine(sampleRequest);

        assertThat(result.getDepartment()).isEqualTo(Department.DEVELOPMENT);
    }


    @Test
    void getAllServiceLines_whenSuccessful_returnsList() {
        when(serviceLineRepository.findAll()).thenReturn(List.of(sampleServiceLine));

        List<ServiceLineResponseDTO> result = serviceLineService.getAllServiceLines();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartment()).isEqualTo(Department.DEVELOPMENT);
    }

    @Test
    void getAllServiceLines_whenEmpty_throwsException() {
        when(serviceLineRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> serviceLineService.getAllServiceLines())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No service lines found");
    }

    @Test
    void getAllServiceLines_whenMultipleExist_returnsAll() {
        ServiceLine qa = new ServiceLine();
        qa.setServiceLineID(2L);
        qa.setDepartment(Department.QA);

        when(serviceLineRepository.findAll()).thenReturn(List.of(sampleServiceLine, qa));

        List<ServiceLineResponseDTO> result = serviceLineService.getAllServiceLines();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ServiceLineResponseDTO::getDepartment)
                .containsExactlyInAnyOrder(Department.DEVELOPMENT, Department.QA);
    }


    @Test
    void getServiceLineById_whenExists_returnsServiceLine() {
        when(serviceLineRepository.findById(1L)).thenReturn(Optional.of(sampleServiceLine));

        ServiceLineResponseDTO result = serviceLineService.getServiceLineById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getServiceLineId()).isEqualTo(1L);
        assertThat(result.getDepartment()).isEqualTo(Department.DEVELOPMENT);
    }

    @Test
    void getServiceLineById_whenNotFound_throwsException() {
        when(serviceLineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceLineService.getServiceLineById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service Line not found with ID: 99");
    }

    @Test
    void getServiceLineById_mapsAllFieldsCorrectly() {
        when(serviceLineRepository.findById(1L)).thenReturn(Optional.of(sampleServiceLine));

        ServiceLineResponseDTO result = serviceLineService.getServiceLineById(1L);

        assertThat(result.getServiceLineId()).isEqualTo(1L);
        assertThat(result.getDepartment()).isEqualTo(Department.DEVELOPMENT);
    }


    @Test
    void updateServiceLine_whenValidData_returnsSuccess() {
        ServiceLineRequestDTO updateRequest = new ServiceLineRequestDTO(Department.QA);

        ServiceLine updatedServiceLine = new ServiceLine();
        updatedServiceLine.setServiceLineID(1L);
        updatedServiceLine.setDepartment(Department.QA);

        when(serviceLineRepository.findById(1L)).thenReturn(Optional.of(sampleServiceLine));
        when(serviceLineRepository.existsByDepartment(Department.QA)).thenReturn(false);
        when(serviceLineRepository.save(any(ServiceLine.class))).thenReturn(updatedServiceLine);

        ServiceLineResponseDTO result = serviceLineService.updateServiceLine(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getDepartment()).isEqualTo(Department.QA);
        verify(serviceLineRepository, times(1)).save(any(ServiceLine.class));
    }

    @Test
    void updateServiceLine_whenSameDepartment_doesNotCheckDuplicate() {
        // updating with same department should skip duplicate check and just save
        when(serviceLineRepository.findById(1L)).thenReturn(Optional.of(sampleServiceLine));
        when(serviceLineRepository.save(any(ServiceLine.class))).thenReturn(sampleServiceLine);

        ServiceLineResponseDTO result = serviceLineService.updateServiceLine(1L, sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getDepartment()).isEqualTo(Department.DEVELOPMENT);
        verify(serviceLineRepository, never()).existsByDepartment(Department.DEVELOPMENT);
    }

    @Test
    void updateServiceLine_whenNotFound_throwsException() {
        when(serviceLineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceLineService.updateServiceLine(99L, sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service Line not found with ID: 99");

        verify(serviceLineRepository, never()).save(any(ServiceLine.class));
    }

    @Test
    void updateServiceLine_whenDepartmentTakenByAnother_throwsException() {
        ServiceLineRequestDTO updateRequest = new ServiceLineRequestDTO(Department.QA);

        when(serviceLineRepository.findById(1L)).thenReturn(Optional.of(sampleServiceLine));
        when(serviceLineRepository.existsByDepartment(Department.QA)).thenReturn(true);

        assertThatThrownBy(() -> serviceLineService.updateServiceLine(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service Line already exists for department: QA");

        verify(serviceLineRepository, never()).save(any(ServiceLine.class));
    }


    @Test
    void deleteServiceLine_whenExists_success() {
        when(serviceLineRepository.existsById(1L)).thenReturn(true);
        doNothing().when(serviceLineRepository).deleteById(1L);

        serviceLineService.deleteServiceLine(1L);

        verify(serviceLineRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteServiceLine_whenNotFound_throwsException() {
        when(serviceLineRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> serviceLineService.deleteServiceLine(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service Line not found with ID: 99");

        verify(serviceLineRepository, never()).deleteById(anyLong());
    }
}