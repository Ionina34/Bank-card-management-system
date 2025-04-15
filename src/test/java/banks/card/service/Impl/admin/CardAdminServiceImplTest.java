package banks.card.service.Impl.admin;

import banks.card.dto.in.card.CardInsertRequest;
import banks.card.dto.in.card.CardUpdateStatusRequest;
import banks.card.dto.in.card.UpdateCardLimitRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.CardUpsertResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.entity.Card;
import banks.card.entity.CardStatus;
import banks.card.entity.User;
import banks.card.exception.EntityNotFoundException;
import banks.card.repository.CardRepository;
import banks.card.service.mapper.CardMapper;
import banks.card.service.services.user.UserUserActionService;
import banks.card.utils.CardMascEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardAdminServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserUserActionService userService;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardAdminServiceImpl cardAdminService;

    private Card card;
    private User user;
    private CardInsertRequest insertRequest;
    private CardUpdateStatusRequest statusRequest;
    private UpdateCardLimitRequest limitRequest;
    private CardFilterRequest filterRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        card = new Card();
        card.setId(1L);
        card.setEncryptedCardNumber("encrypted-1234");
        card.setCardHolder("John Doe");
        card.setExpiryDate(LocalDate.of(2025, 12, 31));
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);

        insertRequest = new CardInsertRequest();
        insertRequest.setCardNumber("1234567812345678");
        insertRequest.setCardHolder("John Doe");
        insertRequest.setExpiryDate(LocalDate.of(2025, 12, 31));

        statusRequest = new CardUpdateStatusRequest();
        statusRequest.setStatus(CardStatus.BLOCKED);

        limitRequest = new UpdateCardLimitRequest();
        limitRequest.setDailyLimit(BigDecimal.valueOf(1000.0));

        filterRequest = new CardFilterRequest();
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testFindById_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        Card result = cardAdminService.findById(1L);

        assertNotNull(result);
        assertEquals(card, result);
        verify(cardRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cardAdminService.findById(1L);
        });
        assertEquals("Card not found with ID: 1", exception.getMessage());
        verify(cardRepository).findById(1L);
    }

    @Test
    void testCreate_Success() throws Exception {
        try (MockedStatic<CardMascEncryptor> encryptorMockedStatic = mockStatic(CardMascEncryptor.class)) {
            encryptorMockedStatic.when(()->CardMascEncryptor.encrypt(anyString())).thenReturn("encrypted-1234");
            when(userService.findByEmail(anyString())).thenReturn(user);
            when(cardRepository.save(any(Card.class))).thenReturn(card);
            CardUpsertResponse response = new CardUpsertResponse();
            when(cardMapper.entityToUpsertResponse(any())).thenReturn(response);

            CardUpsertResponse result = cardAdminService.create("user@example.com", insertRequest);

            assertEquals(response, result);
            verify(userService).findByEmail("user@example.com");
            verify(cardRepository).save(any(Card.class));
            verify(cardMapper).entityToUpsertResponse(eq(card));
        }
    }

    @Test
    void testCreate_UserNotFound() {
        when(userService.findByEmail("user@example.com"))
                .thenThrow(new EntityNotFoundException("User not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cardAdminService.create("user@example.com", insertRequest);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userService).findByEmail("user@example.com");
        verifyNoInteractions(cardRepository, cardMapper);
    }

    @Test
    void testUpdateStatus_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        CardUpsertResponse response = new CardUpsertResponse();
        when(cardMapper.entityToUpsertResponse(card)).thenReturn(response);

        CardUpsertResponse result = cardAdminService.updateStatus(statusRequest, 1L);

        assertEquals(response, result);
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).findById(1L);
        verify(cardMapper).entityToUpsertResponse(card);
    }

    @Test
    void testUpdateStatus_CardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cardAdminService.updateStatus(statusRequest, 1L);
        });
        assertEquals("Card not found with ID: 1", exception.getMessage());
        verify(cardRepository).findById(1L);
        verifyNoInteractions(cardMapper);
    }

    @Test
    void testDelete_Success() {
        cardAdminService.delete(1L);

        verify(cardRepository).deleteById(1L);
    }

    @Test
    void testGetAllCards_Success() {
        Page<Card> cardPage = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);
        ListCardResponse response = new ListCardResponse();
        when(cardMapper.listEntityToListResponse(cardPage)).thenReturn(response);

        ListCardResponse result = cardAdminService.getAllCards(filterRequest, pageable);

        assertEquals(response, result);
        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
        verify(cardMapper).listEntityToListResponse(cardPage);
    }

    @Test
    void testUpdateLimit_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        Card updatedCard = new Card();
        updatedCard.setDailyLimit(BigDecimal.valueOf(1000.0));
        when(cardMapper.requestUpdateToEntity(limitRequest)).thenReturn(updatedCard);
        when(cardRepository.save(card)).thenReturn(card);
        CardResponse response = new CardResponse();
        when(cardMapper.entityToResponse(card)).thenReturn(response);

        CardResponse result = cardAdminService.updateLimit(1L, limitRequest);

        assertEquals(response, result);
        verify(cardRepository).findById(1L);
        verify(cardMapper).requestUpdateToEntity(limitRequest);
        verify(cardRepository).save(card);
        verify(cardMapper).entityToResponse(card);
    }

    @Test
    void testUpdateLimit_CardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cardAdminService.updateLimit(1L, limitRequest);
        });
        assertEquals("Card not found with ID: 1", exception.getMessage());
        verify(cardRepository).findById(1L);
        verifyNoInteractions(cardMapper);
    }
}
