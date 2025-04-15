package banks.card.service.Impl.user;

import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.in.card.WithdrawalRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.card.TransferResponse;
import banks.card.dto.out.card.WithdrawalResponse;
import banks.card.entity.*;
import banks.card.exception.EntityNotFoundException;
import banks.card.exception.TransferException;
import banks.card.exception.WithdrawalException;
import banks.card.repository.CardRepository;
import banks.card.service.mapper.CardMapper;
import banks.card.service.security.JwtService;
import banks.card.service.services.TransactionService;
import banks.card.service.services.user.UserUserActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static banks.card.service.security.JwtService.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserUserActionService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card1;
    private Card card2;
    private CardFilterRequest filterRequest;
    private TransferRequest transferRequest;
    private WithdrawalRequest withdrawalRequest;
    private Pageable pageable;
    private String token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        card1 = new Card();
        card1.setId(1L);
        card1.setBalance(new BigDecimal("1000.00"));
        card1.setStatus(CardStatus.ACTIVE);
        card1.setUser(user);
        card1.setDailyLimit(new BigDecimal("100.00"));
        card1.setMonthlyLimit(new BigDecimal("2000.00"));
        card1.setSingleTransactionLimit(new BigDecimal("300.00"));
        card1.setDailyTransactionCountLimit(5);

        card2 = new Card();
        card2.setId(2L);
        card2.setBalance(new BigDecimal("500.00"));
        card2.setStatus(CardStatus.ACTIVE);
        card2.setUser(user);

        filterRequest = new CardFilterRequest();
        transferRequest = new TransferRequest();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(new BigDecimal("100.00"));

        withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setCardId(1L);
        withdrawalRequest.setAmount(new BigDecimal("100.00"));

        pageable = PageRequest.of(0, 10);
        token = BEARER_PREFIX + "jwt-token";

        cardService.setTransactionService(transactionService);
    }

    @Test
    void testFindById_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));

        Card result = cardService.findById(1L);

        assertNotNull(result);
        assertEquals(card1, result);
        verify(cardRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cardService.findById(1L);
        });
        assertEquals("Card not found by ID: 1", exception.getMessage());
        verify(cardRepository).findById(1L);
    }

    @Test
    void testGetCards_Success() {
        String jwtToken = "jwt-token";
        when(jwtService.extractEmail(jwtToken)).thenReturn("user@example.com");
        when(userService.findByEmail("user@example.com")).thenReturn(user);

        Page<Card> cardPage = new PageImpl<>(List.of(card1));
        when(cardRepository.findByUser(eq(user), any(Specification.class), eq(pageable))).thenReturn(cardPage);

        ListCardResponse response = new ListCardResponse();
        when(cardMapper.listEntityToListResponse(cardPage)).thenReturn(response);

        ListCardResponse result = cardService.getCards(token, filterRequest, pageable);

        assertEquals(response, result);
        verify(jwtService).extractEmail(jwtToken);
        verify(userService).findByEmail("user@example.com");
        verify(cardRepository).findByUser(eq(user), any(Specification.class), eq(pageable));
        verify(cardMapper).listEntityToListResponse(cardPage);
    }

    @Test
    void testGetCards_UserNotFound() {
        String jwtToken = "jwt-token";
        when(jwtService.extractEmail(jwtToken)).thenReturn("user@example.com");
        when(userService.findByEmail("user@example.com")).thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cardService.getCards(token, filterRequest, pageable);
        });
        assertEquals("User not found", exception.getMessage());
        verify(jwtService).extractEmail(jwtToken);
        verify(userService).findByEmail("user@example.com");
        verifyNoInteractions(cardRepository, cardMapper);
    }

    @Test
    void testBlockedCard_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));
        when(cardRepository.save(card1)).thenReturn(card1);
        CardResponse response = new CardResponse();
        when(cardMapper.entityToResponse(card1)).thenReturn(response);

        CardResponse result = cardService.blockedCard(1L, token);

        assertEquals(response, result);
        assertEquals(CardStatus.BLOCKED, card1.getStatus());
        verify(cardRepository).findById(1L);
        verify(cardRepository).save(card1);
        verify(cardMapper).entityToResponse(card1);
    }

    @Test
    void testBlockedCard_AlreadyBlocked() {
        card1.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cardService.blockedCard(1L, token);
        });
        assertEquals("Cannot block an expired card", exception.getMessage());
        verify(cardRepository).findById(1L);
        verifyNoMoreInteractions(cardRepository, cardMapper);
    }

    @Test
    void testBlockedCard_Expired() {
        card1.setStatus(CardStatus.EXPIRED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cardService.blockedCard(1L, token);
        });
        assertEquals("Cannot block an expired card", exception.getMessage());
        verify(cardRepository).findById(1L);
        verifyNoMoreInteractions(cardRepository, cardMapper);
    }

    @Test
    void testTransfer_Success() {
        TransferResponse response = new TransferResponse();
        response.setStatus(TransferStatus.SUCCESS);

        when(cardMapper.transferRequestToTransferResponse(transferRequest)).thenReturn(response);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card2));

        when(transactionService.findByCardAndTransactionDateAfterAndTypeIn(any(), any(), any())).thenReturn(List.of());
        when(transactionService.countByCardAndTransactionDateAfterAndTransactionTypeIn(any(), any(), any())).thenReturn(0L);

        when(cardRepository.save(card1)).thenReturn(card1);
        when(cardRepository.save(card2)).thenReturn(card2);

        Transaction withdrawal = new Transaction();
        Transaction deposit = new Transaction();
        when(transactionService.createAndSave(eq(card1), eq(card2), eq(new BigDecimal("100.00")), eq(TransferStatus.SUCCESS), eq(TransactionType.TRANSFER_OUT), anyString())).thenReturn(withdrawal);
        when(transactionService.createAndSave(eq(card2), eq(card1), eq(new BigDecimal("100.00")), eq(TransferStatus.SUCCESS), eq(TransactionType.TRANSFER_IN), anyString())).thenReturn(deposit);

        TransferResponse result = cardService.transfer(token, transferRequest);

        assertEquals(TransferStatus.SUCCESS, result.getStatus());
        assertEquals(new BigDecimal("900.00"), card1.getBalance());
        assertEquals(new BigDecimal("600.00"), card2.getBalance());
        verify(cardRepository, times(2)).findById(anyLong());
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transactionService, times(2)).createAndSave(any(), any(), any(), any(), any(), anyString());
        verify(cardMapper).transferRequestToTransferResponse(transferRequest);
    }

    @Test
    void testTransfer_SameCard() {
        transferRequest.setToCardId(1L);

        TransferResponse response = new TransferResponse();
        when(cardMapper.transferRequestToTransferResponse(transferRequest)).thenReturn(response);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));

        Transaction errorTransaction = new Transaction();
        when(transactionService.createAndSave(eq(card1), eq(card1), eq(new BigDecimal("100.00")), eq(TransferStatus.DECLINED), eq(TransactionType.TRANSFER_OUT), anyString())).thenReturn(errorTransaction);

        TransferException exception = assertThrows(TransferException.class, () -> {
            cardService.transfer(token, transferRequest);
        });

        assertEquals("Cannot transfer to the same card", exception.getMessage());
        assertEquals(TransferStatus.DECLINED, response.getStatus());
        verify(cardRepository, times(2)).findById(1L);
        verify(transactionService).createAndSave(any(), any(), any(), any(), any(), anyString());
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void testTransfer_InactiveCard() {
        card1.setStatus(CardStatus.BLOCKED);
        TransferResponse response = new TransferResponse();

        when(cardMapper.transferRequestToTransferResponse(transferRequest)).thenReturn(response);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card2));

        Transaction errorTransaction = new Transaction();
        when(transactionService.createAndSave(eq(card1), eq(card2), eq(new BigDecimal("100.00")), eq(TransferStatus.DECLINED), eq(TransactionType.TRANSFER_OUT), anyString())).thenReturn(errorTransaction);

        TransferException exception = assertThrows(TransferException.class, () -> {
            cardService.transfer(token, transferRequest);
        });

        assertEquals("Both cards must be active", exception.getMessage());
        assertEquals(TransferStatus.DECLINED, response.getStatus());
        verify(cardRepository, times(2)).findById(anyLong());
        verify(transactionService).createAndSave(any(), any(), any(), any(), any(), anyString());
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void testTransfer_InsufficientBalance() {
        transferRequest.setAmount(new BigDecimal("2000.00"));

        TransferResponse response = new TransferResponse();
        when(cardMapper.transferRequestToTransferResponse(transferRequest)).thenReturn(response);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card2));

        Transaction errorTransaction = new Transaction();
        when(transactionService.createAndSave(eq(card1), eq(card2), eq(new BigDecimal("2000.00")), eq(TransferStatus.DECLINED), eq(TransactionType.TRANSFER_OUT), anyString())).thenReturn(errorTransaction);

        TransferException exception = assertThrows(TransferException.class, () -> {
            cardService.transfer(token, transferRequest);
        });

        assertEquals("Insufficient balance on source card", exception.getMessage());
        assertEquals(TransferStatus.DECLINED, response.getStatus());
        verify(cardRepository, times(2)).findById(anyLong());
        verify(transactionService).createAndSave(any(), any(), any(), any(), any(), anyString());
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void testWithdraw_Success() {
        WithdrawalResponse response = new WithdrawalResponse();
        response.setAmount(new BigDecimal("100.00"));
        response.setStatus(TransferStatus.SUCCESS);

        when(cardMapper.withdrawalRequestToWithdrawalResponse(withdrawalRequest)).thenReturn(response);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));
        when(transactionService.findByCardAndTransactionDateAfterAndTypeIn(any(), any(), any())).thenReturn(List.of());
        when(transactionService.countByCardAndTransactionDateAfterAndTransactionTypeIn(any(), any(), any())).thenReturn(0L);

        Transaction transaction = new Transaction();
        when(transactionService.createAndSave(eq(card1), eq(new BigDecimal("100.00")), eq(TransferStatus.SUCCESS), eq(TransactionType.WITHDRAWAL), anyString())).thenReturn(transaction);
        when(cardRepository.save(card1)).thenReturn(card1);

        WithdrawalResponse result = cardService.withdraw(withdrawalRequest, token);

        assertEquals(TransferStatus.SUCCESS, result.getStatus());
        assertEquals(new BigDecimal("900.00"), card1.getBalance());
        verify(cardRepository).findById(1L);
        verify(cardRepository).save(eq(card1));
        verify(transactionService).createAndSave(eq(card1), eq(new BigDecimal("100.00")), eq(TransferStatus.SUCCESS), eq(TransactionType.WITHDRAWAL), anyString());
        verify(cardMapper).withdrawalRequestToWithdrawalResponse(withdrawalRequest);
    }

    @Test
    void testWithdraw_InactiveCard() {
        card1.setStatus(CardStatus.BLOCKED);

        WithdrawalResponse response = new WithdrawalResponse();
        when(cardMapper.withdrawalRequestToWithdrawalResponse(any())).thenReturn(response);
        when(cardRepository.findById(anyLong())).thenReturn(Optional.of(card1));

        Transaction transaction = new Transaction();
        when(transactionService.createAndSave(any(), any(), any(), any(), anyString())).thenReturn(transaction);

        WithdrawalException exception = assertThrows(WithdrawalException.class, () -> {
            cardService.withdraw(withdrawalRequest, token);
        });

        assertEquals("Card is not active", exception.getMessage());
        assertEquals(TransferStatus.DECLINED, response.getStatus());
        verify(cardRepository).findById(1L);
        verify(transactionService).createAndSave(eq(card1), eq(new BigDecimal("100.00")), eq(TransferStatus.DECLINED), eq(TransactionType.WITHDRAWAL), anyString());
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void testWithdraw_DailyLimitExceeded() {
        Transaction pastTransaction = new Transaction();
        pastTransaction.setAmount(new BigDecimal("400.00"));
        pastTransaction.setTransferStatus(TransferStatus.SUCCESS);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));
        when(transactionService.findByCardAndTransactionDateAfterAndTypeIn(any(), any(), any())).thenReturn(List.of(pastTransaction));

        WithdrawalResponse response = new WithdrawalResponse();
        when(cardMapper.withdrawalRequestToWithdrawalResponse(any())).thenReturn(response);

        Transaction errorTransaction = new Transaction();
        when(transactionService.createAndSave(any(),any(), any(), any(), anyString())).thenReturn(errorTransaction);

        WithdrawalException exception = assertThrows(WithdrawalException.class, () -> {
            cardService.withdraw(withdrawalRequest, token);
        });

        assertEquals("Daily limit exceeded", exception.getMessage());
        assertEquals(TransferStatus.DECLINED, response.getStatus());
        verify(cardRepository).findById(1L);
        verify(transactionService).createAndSave(eq(card1), eq(new BigDecimal("100.00")), eq(TransferStatus.DECLINED), eq(TransactionType.WITHDRAWAL), anyString());
        verifyNoMoreInteractions(cardRepository);
    }
}
