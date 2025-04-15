package banks.card.service.Impl.user;

import banks.card.dto.in.filter.TransactionFilterRequest;
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.entity.Card;
import banks.card.entity.Transaction;
import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import banks.card.exception.EntityNotFoundException;
import banks.card.repository.TransactionRepository;
import banks.card.service.mapper.TransactionMapper;
import banks.card.service.services.user.CardUserActionService;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionUserServiceImplTest{

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardUserActionService cardService;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionUserServiceImpl transactionService;

    private Card card;
    private Card toCard;
    private Transaction transaction;
    private TransactionFilterRequest filterRequest;
    private Pageable pageable;
    private String token;
    private Timestamp timestamp;

    @BeforeEach
    void setUp() {
        card = new Card();
        card.setId(1L);

        toCard = new Card();
        toCard.setId(2L);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setCard(card);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setTransferStatus(TransferStatus.SUCCESS);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);

        filterRequest = new TransactionFilterRequest();
        pageable = PageRequest.of(0, 10);
        token = "Bearer jwt-token";
        timestamp = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
    }

    @Test
    void testCreateAndSave_WithToCard_Success() {
        BigDecimal amount = new BigDecimal("100.00");
        TransferStatus status = TransferStatus.SUCCESS;
        TransactionType type = TransactionType.TRANSFER_OUT;
        String message = "Transfer completed";

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createAndSave(card, toCard, amount, status, type, message);

        assertNotNull(result);
        assertEquals(transaction, result);
        verify(transactionRepository).save(any(Transaction.class));
        verifyNoMoreInteractions(transactionRepository, cardService, transactionMapper);
    }

    @Test
    void testCreateAndSave_WithoutToCard_Success() {
        BigDecimal amount = new BigDecimal("50.00");
        TransferStatus status = TransferStatus.SUCCESS;
        TransactionType type = TransactionType.WITHDRAWAL;
        String message = "Withdrawal completed";

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createAndSave(card, amount, status, type, message);

        assertNotNull(result);
        assertEquals(transaction, result);
        verify(transactionRepository).save(any(Transaction.class));
        verifyNoMoreInteractions(transactionRepository, cardService, transactionMapper);
    }

    @Test
    void testGetUserTransactions_Success() {
        Long cardId = 1L;
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        ListTransactionResponse response = new ListTransactionResponse();

        when(cardService.findById(cardId)).thenReturn(card);
        when(transactionRepository.findByCard(eq(card), any(Specification.class), eq(pageable))).thenReturn(transactionPage);
        when(transactionMapper.listEntityToResponseEntity(transactionPage)).thenReturn(response);

        ListTransactionResponse result = transactionService.getUserTransactions(cardId, token, filterRequest, pageable);

        assertEquals(response, result);
        verify(cardService).findById(cardId);
        verify(transactionRepository).findByCard(eq(card), any(Specification.class), eq(pageable));
        verify(transactionMapper).listEntityToResponseEntity(transactionPage);
        verifyNoMoreInteractions(cardService, transactionRepository, transactionMapper);
    }

    @Test
    void testGetUserTransactions_CardNotFound() {
        Long cardId = 1L;
        when(cardService.findById(cardId)).thenThrow(new EntityNotFoundException("Card not found by ID: 1"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            transactionService.getUserTransactions(cardId, token, filterRequest, pageable);
        });

        assertEquals("Card not found by ID: 1", exception.getMessage());
        verify(cardService).findById(cardId);
        verifyNoInteractions(transactionRepository, transactionMapper);
    }

    @Test
    void testGetCardTransactions_Success() {
        Long cardId = 1L;
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        ListTransactionResponse response = new ListTransactionResponse();

        when(cardService.findById(cardId)).thenReturn(card);
        when(transactionRepository.findByCard(eq(card), any(Specification.class), eq(pageable))).thenReturn(transactionPage);
        when(transactionMapper.listEntityToResponseEntity(transactionPage)).thenReturn(response);

        ListTransactionResponse result = transactionService.getCardTransactions(cardId, filterRequest, pageable);

        assertEquals(response, result);
        verify(cardService).findById(cardId);
        verify(transactionRepository).findByCard(eq(card), any(Specification.class), eq(pageable));
        verify(transactionMapper).listEntityToResponseEntity(transactionPage);
        verifyNoMoreInteractions(cardService, transactionRepository, transactionMapper);
    }

    @Test
    void testGetCardTransactions_CardNotFound() {
        Long cardId = 1L;
        when(cardService.findById(cardId)).thenThrow(new EntityNotFoundException("Card not found by ID: 1"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            transactionService.getCardTransactions(cardId, filterRequest, pageable);
        });

        assertEquals("Card not found by ID: 1", exception.getMessage());
        verify(cardService).findById(cardId);
        verifyNoInteractions(transactionRepository, transactionMapper);
    }

    @Test
    void testFindByCardAndTransactionDateAfterAndTypeIn_Success() {
        List<TransactionType> types = List.of(TransactionType.WITHDRAWAL, TransactionType.TRANSFER_OUT);
        List<Transaction> transactions = List.of(transaction);

        when(transactionRepository.findByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types))
                .thenReturn(transactions);

        List<Transaction> result = transactionService.findByCardAndTransactionDateAfterAndTypeIn(card, timestamp, types);

        assertEquals(transactions, result);
        verify(transactionRepository).findByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types);
        verifyNoMoreInteractions(transactionRepository);
        verifyNoInteractions(cardService, transactionMapper);
    }

    @Test
    void testFindByCardAndTransactionDateAfterAndTypeIn_EmptyList() {
        List<TransactionType> types = List.of(TransactionType.WITHDRAWAL);
        when(transactionRepository.findByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types))
                .thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.findByCardAndTransactionDateAfterAndTypeIn(card, timestamp, types);

        assertTrue(result.isEmpty());
        verify(transactionRepository).findByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types);
        verifyNoMoreInteractions(transactionRepository);
        verifyNoInteractions(cardService, transactionMapper);
    }

    @Test
    void testCountByCardAndTransactionDateAfterAndTransactionTypeIn_Success() {
        List<TransactionType> types = List.of(TransactionType.WITHDRAWAL, TransactionType.TRANSFER_OUT);
        long count = 5L;

        when(transactionRepository.countByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types))
                .thenReturn(count);

        long result = transactionService.countByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types);

        assertEquals(count, result);
        verify(transactionRepository).countByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types);
        verifyNoMoreInteractions(transactionRepository);
        verifyNoInteractions(cardService, transactionMapper);
    }

    @Test
    void testCountByCardAndTransactionDateAfterAndTransactionTypeIn_ZeroCount() {
        List<TransactionType> types = List.of(TransactionType.WITHDRAWAL);
        when(transactionRepository.countByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types))
                .thenReturn(0L);

        long result = transactionService.countByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types);

        assertEquals(0L, result);
        verify(transactionRepository).countByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types);
        verifyNoMoreInteractions(transactionRepository);
        verifyNoInteractions(cardService, transactionMapper);
    }
}
