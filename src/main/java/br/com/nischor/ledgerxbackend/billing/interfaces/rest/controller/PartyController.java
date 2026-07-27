package br.com.nischor.ledgerxbackend.billing.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.billing.application.dto.PartyDto;
import br.com.nischor.ledgerxbackend.billing.application.mapper.PartyMapper;
import br.com.nischor.ledgerxbackend.billing.application.usecase.CreatePartyUseCase;
import br.com.nischor.ledgerxbackend.billing.domain.repository.PartyRepository;
import br.com.nischor.ledgerxbackend.billing.interfaces.rest.dto.CreatePartyRequest;
import br.com.nischor.ledgerxbackend.shared.domain.valueobject.DocumentNumber;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.Authorizations;
import br.com.nischor.ledgerxbackend.shared.infrastructure.web.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing endpoints to create and list the customers/suppliers (parties) of a
 * company.
 */
@RestController
@RequestMapping("/api/v1/companies/{companyId}/parties")
@Tag(name = "Parties", description = "Customers and suppliers a company transacts with")
public class PartyController {

    private final PartyRepository partyRepository;
    private final PartyMapper partyMapper;
    private final CreatePartyUseCase createPartyUseCase;

    /**
     * Creates the controller.
     *
     * @param partyRepository repository used to look up parties for read-only endpoints
     * @param partyMapper mapper used to convert parties to their DTO representation
     * @param createPartyUseCase use case used to create new parties
     */
    public PartyController(PartyRepository partyRepository, PartyMapper partyMapper,
            CreatePartyUseCase createPartyUseCase) {
        this.partyRepository = partyRepository;
        this.partyMapper = partyMapper;
        this.createPartyUseCase = createPartyUseCase;
    }

    /**
     * Handles {@code POST /api/v1/companies/{companyId}/parties} to create a customer or
     * supplier for the given company.
     *
     * <p>BR-062..BR-065: name, document (CPF/CNPJ check-digit, matching the declared document
     * type), email and party type are enforced by {@link CreatePartyRequest}'s bean validation
     * constraints, including the class-level {@code @ValidPartyDocument} check.
     *
     * @param companyId the identifier of the company the party will belong to
     * @param request the validated request body describing the party to create
     * @return a {@code 201 Created} response with the created party
     */
    @Operation(summary = "Create a customer or supplier",
            description = "Document must be a valid CPF or CNPJ matching the declared documentType. "
                    + "BR-062..BR-065.")
    @ApiResponse(responseCode = "201", description = "Party created")
    @ApiResponse(responseCode = "400", description = "Validation failure (invalid document, invalid email, etc.)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PreAuthorize(Authorizations.CREATE)
    @PostMapping
    public ResponseEntity<PartyDto> create(@PathVariable UUID companyId,
            @Valid @RequestBody CreatePartyRequest request) {
        var document = request.documentType() == DocumentNumber.DocumentType.CPF
                ? DocumentNumber.cpf(request.document())
                : DocumentNumber.cnpj(request.document());
        var dto = createPartyUseCase.execute(companyId, request.name(), document, request.email(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Handles {@code GET /api/v1/companies/{companyId}/parties} to list all parties of a company.
     *
     * @param companyId the identifier of the company whose parties are listed
     * @return the parties belonging to the company, as DTOs
     */
    @Operation(summary = "List parties of a company")
    @ApiResponse(responseCode = "200", description = "Parties listed")
    @PreAuthorize(Authorizations.READ)
    @GetMapping
    public List<PartyDto> listByCompany(@PathVariable UUID companyId) {
        return partyRepository.findAllByCompanyId(companyId).stream().map(partyMapper::toDto).toList();
    }
}
