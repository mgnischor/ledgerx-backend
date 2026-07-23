package br.com.nischor.ledgerxbackend.notification.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.notification.application.dto.NotificationDto;
import br.com.nischor.ledgerxbackend.notification.application.mapper.NotificationMapper;
import br.com.nischor.ledgerxbackend.notification.application.usecase.MarkNotificationAsReadUseCase;
import br.com.nischor.ledgerxbackend.notification.domain.repository.NotificationRepository;
import br.com.nischor.ledgerxbackend.shared.infrastructure.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-side feed for notifications created by the RabbitMQ listeners in each bounded context
 * (see {@code UserRegisteredMessageListener}, {@code TransactionRecordedMessageListener},
 * {@code InvoicePaidMessageListener}). Not scoped per user/company yet — a known gap consistent
 * with the rest of the API's lack of a wired authentication provider (see README/BUSINESS_RULES.md).
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "In-app notification feed populated from domain events via RabbitMQ")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final MarkNotificationAsReadUseCase markNotificationAsReadUseCase;

    public NotificationController(NotificationRepository notificationRepository,
            NotificationMapper notificationMapper, MarkNotificationAsReadUseCase markNotificationAsReadUseCase) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.markNotificationAsReadUseCase = markNotificationAsReadUseCase;
    }

    /** BR-116: results are ordered most-recent first; {@code unreadOnly=true} filters out read notifications. */
    @Operation(summary = "List notifications", description = "BR-116.")
    @ApiResponse(responseCode = "200", description = "Notifications listed, most recent first")
    @GetMapping
    public List<NotificationDto> list(@RequestParam(defaultValue = "false") boolean unreadOnly) {
        var notifications = unreadOnly ? notificationRepository.findAllByReadFalseOrderByCreatedAtDesc()
                : notificationRepository.findAllByOrderByCreatedAtDesc();
        return notifications.stream().map(notificationMapper::toDto).toList();
    }

    /** BR-117: marking as read is idempotent; marking an already-read notification again is a no-op. */
    @Operation(summary = "Mark a notification as read", description = "BR-117.")
    @ApiResponse(responseCode = "200", description = "Notification marked as read")
    @ApiResponse(responseCode = "404", description = "Notification not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PatchMapping("/{notificationId}/read")
    public NotificationDto markAsRead(@PathVariable UUID notificationId) {
        return markNotificationAsReadUseCase.execute(notificationId);
    }
}
