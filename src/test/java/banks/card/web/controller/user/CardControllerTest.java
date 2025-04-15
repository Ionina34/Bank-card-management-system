package banks.card.web.controller.user;

import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.in.card.WithdrawalRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.in.filter.TransactionFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.card.TransferResponse;
import banks.card.dto.out.card.WithdrawalResponse;
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.dto.out.transaction.TransactionResponse;
import banks.card.entity.CardStatus;
import banks.card.entity.TransferStatus;
import banks.card.exception.EntityNotFoundException;
import banks.card.exception.TransferException;
import banks.card.service.services.TransactionService;
import banks.card.service.services.user.CardUserActionService;
import banks.card.web.controller.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static banks.card.service.security.JwtService.HEADER_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CardControllerTest extends AbstractControllerTest {

    @Mock
    private CardUserActionService userActionService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CardController cardController;

    private CardFilterRequest cardFilterRequest;
    private TransactionFilterRequest transactionFilterRequest;
    private TransferRequest transferRequest;
    private WithdrawalRequest withdrawalRequest;
    private final String jwtToken = "Bearer valid-jwt-token";

    @BeforeEach
    public void init() {
        cardFilterRequest = new CardFilterRequest("123", null, null, null);
        transactionFilterRequest = new TransactionFilterRequest(null, null, null, BigDecimal.valueOf(1100), null, null);
        transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(500));
        withdrawalRequest = new WithdrawalRequest(5L, BigDecimal.valueOf(300));

        MockitoAnnotations.openMocks(this);
        setupMockMvc(cardController);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCards_ValidRequest_ReturnsOk() throws Exception {
        ListCardResponse response = new ListCardResponse(List.of(new CardResponse(), new CardResponse()));

        when(userActionService.getCards(eq(jwtToken), any(CardFilterRequest.class), eq(PageRequest.of(0, 10))))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/cards")
                        .header(HEADER_NAME, jwtToken)
                        .param("page", "0")
                        .param("size", "10")
                        .param("number", "1234 5678 9012 3456")
                        .param("status", "ACTIVE")
                        .param("min-balance", "0")
                        .param("max-balance", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(userActionService, times(1)).getCards(eq(jwtToken), any(CardFilterRequest.class), eq(PageRequest.of(0, 10)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCards_UserNotFound_ReturnsNotFound() throws Exception {
        when(userActionService.getCards(eq(jwtToken), any(CardFilterRequest.class), any()))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/cards")
                        .header(HEADER_NAME, jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userActionService, times(1)).getCards(eq(jwtToken), any(CardFilterRequest.class), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTransactions_ValidRequest_ReturnsOk() throws Exception {
        ListTransactionResponse response = new ListTransactionResponse(List.of(new TransactionResponse(), new TransactionResponse()));

        when(transactionService.getUserTransactions(eq(1L), eq(jwtToken), any(TransactionFilterRequest.class), eq(PageRequest.of(0, 10))))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/cards/1/transactions")
                        .header(HEADER_NAME, jwtToken)
                        .param("page", "0")
                        .param("size", "10")
                        .param("type", "TRANSFER_OUT")
                        .param("status", "SUCCESS")
                        .param("min-amount", "10")
                        .param("max-amount", "100")
                        .param("date-from", "2023-01-01T00:00:00")
                        .param("date-to", "2023-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(transactionService, times(1)).getUserTransactions(eq(1L), eq(jwtToken), any(TransactionFilterRequest.class), eq(PageRequest.of(0, 10)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTransactions_AccessDenied_ReturnsForbidden() throws Exception {
        when(transactionService.getUserTransactions(eq(1L), eq(jwtToken), any(TransactionFilterRequest.class), any()))
                .thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/api/v1/cards/1/transactions")
                        .header(HEADER_NAME, jwtToken))
                .andExpect(status().isForbidden());

        verify(transactionService, times(1)).getUserTransactions(eq(1L), eq(jwtToken), any(TransactionFilterRequest.class), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void blockCard_ValidRequest_ReturnsOk() throws Exception {
        CardResponse response =
                new CardResponse(1L, 1L,
                        "**** **** **** 0000", "IVAN IVANOV",
                        LocalDate.now(), CardStatus.ACTIVE,
                        BigDecimal.ZERO, null, null, null,
                        null);

        when(userActionService.blockedCard(eq(1L), eq(jwtToken))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cards/1/block")
                        .header(HEADER_NAME, jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(userActionService, times(1)).blockedCard(eq(1L), eq(jwtToken));
    }

    @Test
    @WithMockUser(roles = "USER")
    void blockCard_AlreadyBlocked_ReturnsConflict() throws Exception {
        when(userActionService.blockedCard(eq(1L), eq(jwtToken)))
                .thenThrow(new IllegalStateException("Card already blocked"));

        mockMvc.perform(post("/api/v1/cards/1/block")
                        .header(HEADER_NAME, jwtToken))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Card already blocked"));

        verify(userActionService, times(1)).blockedCard(eq(1L), eq(jwtToken));
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_ValidRequest_ReturnsOk() throws Exception {
        TransferResponse response = new TransferResponse(1L, 2L,
                BigDecimal.valueOf(100), Timestamp.valueOf(LocalDateTime.now()),
                TransferStatus.SUCCESS);

        when(userActionService.transfer(eq(jwtToken), any(TransferRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cards/transfer")
                        .header(HEADER_NAME, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(userActionService, times(1)).transfer(eq(jwtToken), any(TransferRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_InvalidRequest_ReturnsBadRequest() throws Exception {
        TransferRequest invalidRequest = new TransferRequest();

        mockMvc.perform(post("/api/v1/cards/transfer")
                        .header(HEADER_NAME, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());

        verify(userActionService, never()).transfer(anyString(), any(TransferRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void withdrawal_ValidRequest_ReturnsOk() throws Exception {
        WithdrawalResponse response = new WithdrawalResponse(5L, BigDecimal.valueOf(1000), Timestamp.valueOf(LocalDateTime.now()), TransferStatus.SUCCESS);
        when(userActionService.withdraw(any(WithdrawalRequest.class), eq(jwtToken))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cards/withdrawal")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(userActionService, times(1)).withdraw(any(WithdrawalRequest.class), eq(jwtToken));
    }

    @Test
    @WithMockUser(roles = "USER")
    void withdrawal_InsufficientFunds_ReturnsBadRequest() throws Exception {
        TransferResponse response = new TransferResponse();
        when(userActionService.withdraw(any(WithdrawalRequest.class), eq(jwtToken)))
                .thenThrow(new TransferException("Insufficient funds", response));

        mockMvc.perform(post("/api/v1/cards/withdrawal")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Insufficient funds"));

        verify(userActionService, times(1)).withdraw(any(WithdrawalRequest.class), eq(jwtToken));
    }
}
