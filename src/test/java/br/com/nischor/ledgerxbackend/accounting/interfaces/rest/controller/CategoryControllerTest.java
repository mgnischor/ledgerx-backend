package br.com.nischor.ledgerxbackend.accounting.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.nischor.ledgerxbackend.accounting.application.dto.CategoryDto;
import br.com.nischor.ledgerxbackend.accounting.application.mapper.CategoryMapper;
import br.com.nischor.ledgerxbackend.accounting.application.usecase.CreateCategoryUseCase;
import br.com.nischor.ledgerxbackend.accounting.domain.model.TransactionType;
import br.com.nischor.ledgerxbackend.accounting.domain.repository.CategoryRepository;
import br.com.nischor.ledgerxbackend.shared.infrastructure.config.SecurityConfig;
import br.com.nischor.ledgerxbackend.shared.infrastructure.security.JwtService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.autoconfigure.web.DataWebAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CategoryController.class, excludeAutoConfiguration = DataWebAutoConfiguration.class)
@Import(SecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private CategoryMapper categoryMapper;

    @MockitoBean
    private CreateCategoryUseCase createCategoryUseCase;

    private final UUID companyId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void createsCategoryWhenAuthorized() throws Exception {
        var dto = new CategoryDto(categoryId, companyId, "Groceries", TransactionType.EXPENSE);
        when(createCategoryUseCase.execute(any(), any(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/companies/{companyId}/categories", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Groceries\",\"type\":\"EXPENSE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Groceries"));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void rejectsCreateWithoutCreateAuthority() throws Exception {
        mockMvc.perform(post("/api/v1/companies/{companyId}/categories", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Groceries\",\"type\":\"EXPENSE\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void rejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/v1/companies/{companyId}/categories", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"type\":\"EXPENSE\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_READ")
    void listsCategoriesOfACompany() throws Exception {
        when(categoryRepository.findAllByCompanyId(companyId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/companies/{companyId}/categories", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
