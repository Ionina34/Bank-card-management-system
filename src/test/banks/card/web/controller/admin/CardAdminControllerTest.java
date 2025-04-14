package banks.card.web.controller.admin;

import banks.card.dto.in.card.CardInsertRequest;
import banks.card.dto.in.card.CardUpdateStatusRequest;
import banks.card.dto.in.card.UpdateCardLimitRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.in.filter.TransactionFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.CardUpsertResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.services.amin.CardAdminActionService;
import banks.card.service.services.TransactionService;
import banks.card.web.controller.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CardAdminControllerTest extends AbstractControllerTest {

    @Mock
    private CardAdminActionService cardService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CardAdminController cardAdminController;

    private CardInsertRequest cardInsertRequest;
    private CardUpdateStatusRequest cardUpdateStatusRequest;
    private UpdateCardLimitRequest updateLimitRequest;

    @BeforeEach
    public void init() {
        cardInsertRequest = new CardInsertRequest("0000 0000 0000 0000", "IVAN IVANOV", LocalDate.of(2026,3,3), BigDecimal.ZERO);
        cardUpdateStatusRequest = new CardUpdateStatusRequest();
        updateLimitRequest = new UpdateCardLimitRequest();

        MockitoAnnotations.openMocks(this);
        setupMockMvc(cardAdminController);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAll_ShouldReturnListCardResponse_WhenAuthorized() throws Exception {
        ListCardResponse response = new ListCardResponse();
        when(cardService.getAllCards(any(CardFilterRequest.class), any(PageRequest.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("number", "1234")
                        .param("status", "ACTIVE")
                        .param("min-balance", "100.00")
                        .param("max-balance", "1000.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());

        verify(cardService).getAllCards(any(CardFilterRequest.class), eq(PageRequest.of(0, 10)));
    }

    @Test
    public void getAll_ShouldReturnForbidden_WhenNotAuthorized() throws Exception {
        mockMvc.perform(get("/api/v1/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(cardService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getCardTransactions_ShouldReturnListTransactionResponse_WhenCardExists() throws Exception {
        ListTransactionResponse response = new ListTransactionResponse();
        when(transactionService.getCardTransactions(eq(1L), any(TransactionFilterRequest.class), any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/cards/1/transactions")
                        .param("page", "0")
                        .param("size", "10")
                        .param("type", "TRANSFER_OUT")
                        .param("status", "SUCCESS")
                        .param("min-amount", "50.00")
                        .param("max-amount", "500.00")
                        .param("date-from", "2023-01-01T00:00:00")
                        .param("date-to", "2023-12-31T23:59:59")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());

        verify(transactionService).getCardTransactions(eq(1L), any(TransactionFilterRequest.class), eq(PageRequest.of(0, 10)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getCardTransactions_ShouldReturnNotFound_WhenCardDoesNotExist() throws Exception {
        when(transactionService.getCardTransactions(eq(1L), any(TransactionFilterRequest.class), any(PageRequest.class)))
                .thenThrow(new EntityNotFoundException("Card not found"));

        mockMvc.perform(get("/api/v1/admin/cards/1/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Card not found"));

        verify(transactionService).getCardTransactions(eq(1L), any(TransactionFilterRequest.class), any(PageRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addCard_ShouldReturnCreated_WhenUserExists() throws Exception {
        CardUpsertResponse response = new CardUpsertResponse();
        when(cardService.create(eq("user@example.com"), any(CardInsertRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/cards/user/user@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInsertRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());

        verify(cardService).create(eq("user@example.com"), any(CardInsertRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addCard_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(cardService.create(eq("user@example.com"), any(CardInsertRequest.class)))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(post("/api/v1/admin/cards/user/user@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardInsertRequest))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(cardService).create(eq("user@example.com"), any(CardInsertRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateStatus_ShouldReturnOk_WhenCardExists() throws Exception {
        CardUpsertResponse response = new CardUpsertResponse();
        when(cardService.updateStatus(any(CardUpdateStatusRequest.class), eq(1L))).thenReturn(response);

        mockMvc.perform(put("/api/v1/admin/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardUpdateStatusRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());

        verify(cardService).updateStatus(any(CardUpdateStatusRequest.class), eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateStatus_ShouldReturnNotFound_WhenCardDoesNotExist() throws Exception {
        when(cardService.updateStatus(any(CardUpdateStatusRequest.class), eq(1L)))
                .thenThrow(new EntityNotFoundException("Card not found"));

        mockMvc.perform(put("/api/v1/admin/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardUpdateStatusRequest))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Card not found"));

        verify(cardService).updateStatus(any(CardUpdateStatusRequest.class), eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateLimits_ShouldReturnOk_WhenCardExists() throws Exception {
        CardResponse response = new CardResponse();
        when(cardService.updateLimit(eq(1L), any(UpdateCardLimitRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/admin/cards/1/limits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateLimitRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());

        verify(cardService).updateLimit(eq(1L), any(UpdateCardLimitRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateLimits_ShouldReturnNotFound_WhenCardDoesNotExist() throws Exception {
        when(cardService.updateLimit(eq(1L), any(UpdateCardLimitRequest.class)))
                .thenThrow(new EntityNotFoundException("Card not found"));

        mockMvc.perform(patch("/api/v1/admin/cards/1/limits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateLimitRequest))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Card not found"));

        verify(cardService).updateLimit(eq(1L), any(UpdateCardLimitRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_ShouldReturnNoContent_WhenCardExists() throws Exception {
        doNothing().when(cardService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/admin/cards/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(cardService).delete(eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_ShouldReturnNotFound_WhenCardDoesNotExist() throws Exception {
        doThrow(new EntityNotFoundException("Card not found")).when(cardService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/admin/cards/1")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Card not found"));

        verify(cardService).delete(eq(1L));
    }
}