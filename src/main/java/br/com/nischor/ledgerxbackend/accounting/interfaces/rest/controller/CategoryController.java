package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import br.com.nischor.ledgerxbackend.accounting.application.dto.CategoryDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.CategoryMapper;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.CreateCategoryUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.accounting.interfaces.rest.dto.CreateCategoryRequest;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies/{companyId}/categories")
@Tag(name = "Categories", description = "Income/expense categories used to classify transactions")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CreateCategoryUseCase createCategoryUseCase;

    public CategoryController(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
            CreateCategoryUseCase createCategoryUseCase) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.createCategoryUseCase = createCategoryUseCase;
    }

    /** BR-053..BR-056: name is required (max 60 chars) and type must be INCOME, EXPENSE or TRANSFER. */
    @Operation(summary = "Create a category", description = "BR-053..BR-056.")
    @ApiResponse(responseCode = "201", description = "Category created")
    @ApiResponse(responseCode = "400", description = "Validation failure (blank name, missing type, etc.)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<CategoryDto> create(@PathVariable UUID companyId,
            @Valid @RequestBody CreateCategoryRequest request) {
        var dto = createCategoryUseCase.execute(companyId, request.name(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "List categories of a company")
    @ApiResponse(responseCode = "200", description = "Categories listed")
    @GetMapping
    public List<CategoryDto> listByCompany(@PathVariable UUID companyId) {
        return categoryRepository.findAllByCompanyId(companyId).stream().map(categoryMapper::toDto).toList();
    }
}
