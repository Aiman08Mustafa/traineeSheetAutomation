package com.example.traineeSheetAutomation.service;

import com.example.traineeSheetAutomation.dto.TemplateRequestDTO;
import com.example.traineeSheetAutomation.dto.TemplateResponseDTO;
import com.example.traineeSheetAutomation.entity.ServiceLine;
import com.example.traineeSheetAutomation.entity.Template;
import com.example.traineeSheetAutomation.entity.User;
import com.example.traineeSheetAutomation.entity.enums.Department;
import com.example.traineeSheetAutomation.entity.enums.Title;
import com.example.traineeSheetAutomation.repository.ServiceLineRepository;
import com.example.traineeSheetAutomation.repository.TemplateRepository;
import com.example.traineeSheetAutomation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceLineRepository serviceLineRepository;

    @InjectMocks
    private TemplateService templateService;

    private User sampleUser;
    private ServiceLine sampleServiceLine;
    private Template sampleTemplate;
    private TemplateRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("Aiman");
        sampleUser.setEmail("abc@example.com");

        sampleServiceLine = new ServiceLine();
        sampleServiceLine.setServiceLineID(1L);
        sampleServiceLine.setDepartment(Department.DEVELOPMENT);

        sampleTemplate = new Template();
        sampleTemplate.setTemplateId(1L);
        sampleTemplate.setTitle(Title.JAVA);
        sampleTemplate.setDescription("Sample Java template description");
        sampleTemplate.setCreatedBy(sampleUser);
        sampleTemplate.setServiceLine(sampleServiceLine);
        sampleTemplate.setCreatedAt(LocalDateTime.now());
        sampleTemplate.setUpdatedAt(LocalDateTime.now());

        sampleRequest = TemplateRequestDTO.builder()
                .title(Title.JAVA)
                .description("Sample Java template description")
                .createdBy(1L)
                .serviceLineId(1L)
                .build();
    }

    // ───── createTemplate ─────

    @Test
    void createTemplate_whenCreated_returnsSuccess() {
        when(templateRepository.existsByTitle(Title.JAVA)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(serviceLineRepository.findById(1L)).thenReturn(Optional.of(sampleServiceLine));
        when(templateRepository.save(any(Template.class))).thenReturn(sampleTemplate);

        TemplateResponseDTO result = templateService.createTemplate(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTemplateId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo(Title.JAVA);
        assertThat(result.getDescription()).isEqualTo("Sample Java template description");
        assertThat(result.getCreatedBy()).isEqualTo(1L);
        assertThat(result.getCreatedByName()).isEqualTo("Aiman");
        assertThat(result.getServiceLineId()).isEqualTo(1L);
        assertThat(result.getServiceLineDepartment()).isEqualTo("DEVELOPMENT");

        verify(templateRepository, times(1)).save(any(Template.class));
    }

    @Test
    void createTemplate_whenTitleAlreadyExists_throwsException() {
        when(templateRepository.existsByTitle(Title.JAVA)).thenReturn(true);

        assertThatThrownBy(() -> templateService.createTemplate(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template with title already exists: JAVA");

        verify(templateRepository, never()).save(any(Template.class));
    }

    @Test
    void createTemplate_whenUserNotFound_throwsException() {
        when(templateRepository.existsByTitle(Title.JAVA)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.createTemplate(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 1");

        verify(templateRepository, never()).save(any(Template.class));
    }

    @Test
    void createTemplate_whenServiceLineNotFound_throwsException() {
        when(templateRepository.existsByTitle(Title.JAVA)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(serviceLineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.createTemplate(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service Line not found with ID: 1");

        verify(templateRepository, never()).save(any(Template.class));
    }

    @Test
    void createTemplate_mapsAllFieldsCorrectly() {
        when(templateRepository.existsByTitle(Title.JAVA)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(serviceLineRepository.findById(1L)).thenReturn(Optional.of(sampleServiceLine));
        when(templateRepository.save(any(Template.class))).thenReturn(sampleTemplate);

        TemplateResponseDTO result = templateService.createTemplate(sampleRequest);

        assertThat(result.getTemplateId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo(Title.JAVA);
        assertThat(result.getCreatedByName()).isEqualTo("Aiman");
        assertThat(result.getServiceLineDepartment()).isEqualTo("DEVELOPMENT");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    // ───── getAllTemplates ─────

    @Test
    void getAllTemplates_whenSuccessful_returnsList() {
        when(templateRepository.findAll()).thenReturn(List.of(sampleTemplate));

        List<TemplateResponseDTO> result = templateService.getAllTemplates();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(Title.JAVA);
        assertThat(result.get(0).getCreatedByName()).isEqualTo("Aiman");
    }

    @Test
    void getAllTemplates_whenEmpty_throwsException() {
        when(templateRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> templateService.getAllTemplates())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No templates found");
    }

    @Test
    void getAllTemplates_whenMultipleExist_returnsAll() {
        User user2 = new User();
        user2.setUserId(2L);
        user2.setName("Sara");

        ServiceLine sl2 = new ServiceLine();
        sl2.setServiceLineID(2L);
        sl2.setDepartment(Department.QA);

        Template template2 = new Template();
        template2.setTemplateId(2L);
        template2.setTitle(Title.REACT);
        template2.setDescription("React template");
        template2.setCreatedBy(user2);
        template2.setServiceLine(sl2);
        template2.setCreatedAt(LocalDateTime.now());
        template2.setUpdatedAt(LocalDateTime.now());

        when(templateRepository.findAll()).thenReturn(List.of(sampleTemplate, template2));

        List<TemplateResponseDTO> result = templateService.getAllTemplates();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TemplateResponseDTO::getTitle)
                .containsExactlyInAnyOrder(Title.JAVA, Title.REACT);
    }

    // ───── getTemplateById ─────

    @Test
    void getTemplateById_whenExists_returnsTemplate() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));

        TemplateResponseDTO result = templateService.getTemplateById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTemplateId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo(Title.JAVA);
    }

    @Test
    void getTemplateById_whenNotFound_throwsException() {
        when(templateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.getTemplateById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template not found with ID: 99");
    }

    @Test
    void getTemplateById_mapsAllFieldsCorrectly() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));

        TemplateResponseDTO result = templateService.getTemplateById(1L);

        assertThat(result.getTemplateId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo(Title.JAVA);
        assertThat(result.getDescription()).isEqualTo("Sample Java template description");
        assertThat(result.getCreatedBy()).isEqualTo(1L);
        assertThat(result.getCreatedByName()).isEqualTo("Aiman");
        assertThat(result.getServiceLineId()).isEqualTo(1L);
        assertThat(result.getServiceLineDepartment()).isEqualTo("DEVELOPMENT");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    // ───── getTemplatesByUser ─────

    @Test
    void getTemplatesByUser_whenExists_returnsList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(templateRepository.findByCreatedBy_UserId(1L)).thenReturn(List.of(sampleTemplate));

        List<TemplateResponseDTO> result = templateService.getTemplatesByUser(1L);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCreatedBy()).isEqualTo(1L);
        assertThat(result.get(0).getCreatedByName()).isEqualTo("Aiman");
    }

    @Test
    void getTemplatesByUser_whenUserNotFound_throwsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> templateService.getTemplatesByUser(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");

        verify(templateRepository, never()).findByCreatedBy_UserId(anyLong());
    }

    @Test
    void getTemplatesByUser_whenNoTemplatesForUser_throwsException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(templateRepository.findByCreatedBy_UserId(1L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> templateService.getTemplatesByUser(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No templates found for user ID: 1");
    }

    // ───── updateTemplate ─────

    @Test
    void updateTemplate_whenValidData_returnsSuccess() {
        TemplateRequestDTO updateRequest = TemplateRequestDTO.builder()
                .title(Title.REACT)
                .description("Updated description")
                .createdBy(1L)
                .serviceLineId(1L)
                .build();

        Template updatedTemplate = new Template();
        updatedTemplate.setTemplateId(1L);
        updatedTemplate.setTitle(Title.REACT);
        updatedTemplate.setDescription("Updated description");
        updatedTemplate.setCreatedBy(sampleUser);
        updatedTemplate.setServiceLine(sampleServiceLine);
        updatedTemplate.setCreatedAt(sampleTemplate.getCreatedAt());
        updatedTemplate.setUpdatedAt(LocalDateTime.now());

        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(templateRepository.existsByTitle(Title.REACT)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(serviceLineRepository.findById(1L)).thenReturn(Optional.of(sampleServiceLine));
        when(templateRepository.save(any(Template.class))).thenReturn(updatedTemplate);

        TemplateResponseDTO result = templateService.updateTemplate(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(Title.REACT);
        assertThat(result.getDescription()).isEqualTo("Updated description");
        verify(templateRepository, times(1)).save(any(Template.class));
    }

    @Test
    void updateTemplate_whenSameTitle_doesNotCheckDuplicate() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(serviceLineRepository.findById(1L)).thenReturn(Optional.of(sampleServiceLine));
        when(templateRepository.save(any(Template.class))).thenReturn(sampleTemplate);

        TemplateResponseDTO result = templateService.updateTemplate(1L, sampleRequest);

        assertThat(result).isNotNull();
        verify(templateRepository, never()).existsByTitle(Title.JAVA);
    }

    @Test
    void updateTemplate_whenNotFound_throwsException() {
        when(templateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.updateTemplate(99L, sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template not found with ID: 99");

        verify(templateRepository, never()).save(any(Template.class));
    }

    @Test
    void updateTemplate_whenTitleTakenByAnother_throwsException() {
        TemplateRequestDTO updateRequest = TemplateRequestDTO.builder()
                .title(Title.REACT)
                .description("desc")
                .createdBy(1L)
                .serviceLineId(1L)
                .build();

        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(templateRepository.existsByTitle(Title.REACT)).thenReturn(true);

        assertThatThrownBy(() -> templateService.updateTemplate(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template with title already exists: REACT");

        verify(templateRepository, never()).save(any(Template.class));
    }

    @Test
    void updateTemplate_whenUserNotFound_throwsException() {
        TemplateRequestDTO updateRequest = TemplateRequestDTO.builder()
                .title(Title.REACT)
                .description("desc")
                .createdBy(99L)
                .serviceLineId(1L)
                .build();

        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(templateRepository.existsByTitle(Title.REACT)).thenReturn(false);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.updateTemplate(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with ID: 99");

        verify(templateRepository, never()).save(any(Template.class));
    }

    @Test
    void updateTemplate_whenServiceLineNotFound_throwsException() {
        TemplateRequestDTO updateRequest = TemplateRequestDTO.builder()
                .title(Title.REACT)
                .description("desc")
                .createdBy(1L)
                .serviceLineId(99L)
                .build();

        when(templateRepository.findById(1L)).thenReturn(Optional.of(sampleTemplate));
        when(templateRepository.existsByTitle(Title.REACT)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(serviceLineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.updateTemplate(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service Line not found with ID: 99");

        verify(templateRepository, never()).save(any(Template.class));
    }

    // ───── deleteTemplate ─────

    @Test
    void deleteTemplate_whenExists_success() {
        when(templateRepository.existsById(1L)).thenReturn(true);
        doNothing().when(templateRepository).deleteById(1L);

        templateService.deleteTemplate(1L);

        verify(templateRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTemplate_whenNotFound_throwsException() {
        when(templateRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> templateService.deleteTemplate(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template not found with ID: 99");

        verify(templateRepository, never()).deleteById(anyLong());
    }
}
