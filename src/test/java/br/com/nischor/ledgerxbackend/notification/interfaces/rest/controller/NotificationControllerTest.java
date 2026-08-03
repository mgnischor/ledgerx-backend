package br.com.nischor.ledgerxbackend.notification.interfaces.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.notification.application.dto.NotificationDto;
import br.com.nischor.ledgerxbackend.notification.application.mapper.NotificationMapper;
import br.com.nischor.ledgerxbackend.notification.application.usecase.MarkNotificationAsReadUseCase;
import br.com.nischor.ledgerxbackend.notification.domain.model.NotificationType;
import br.com.nischor.ledgerxbackend.notification.domain.repository.NotificationRepository;
import br.com.nischor.ledgerxbackend.shared.domain.exception.EntityNotFoundException;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.autoconfigure.web.DataWebAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = NotificationController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private NotificationMapper notificationMapper;

    @MockitoBean
    private MarkNotificationAsReadUseCase markNotificationAsReadUseCase;

    private final UUID notificationId = UUID.randomUUID();

    @Test
    @WithMockUser
    void listsAllNotificationsForAnyAuthenticatedUser() throws Exception {
        // Not scoped per user/company yet, a known gap: any authenticated caller sees every notification.
        when(notificationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void listsOnlyUnreadNotificationsWhenRequested() throws Exception {
        when(notificationRepository.findAllByReadFalseOrderByCreatedAtDesc()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/notifications").param("unreadOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void marksNotificationAsRead() throws Exception {
        var dto = new NotificationDto(notificationId, NotificationType.INVOICE_PAID, UUID.randomUUID(), "Invoice paid",
                Instant.now(), true);
        when(markNotificationAsReadUseCase.execute(notificationId)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/notifications/{notificationId}/read", notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    @WithMockUser
    void returns404WhenNotificationNotFound() throws Exception {
        when(markNotificationAsReadUseCase.execute(notificationId)).thenThrow(
                new EntityNotFoundException(br.com.nischor.ledgerxbackend.notification.domain.model.Notification.class,
                        notificationId));

        mockMvc.perform(patch("/api/v1/notifications/{notificationId}/read", notificationId))
                .andExpect(status().isNotFound());
    }
}
