package banks.card.service.mapper;

import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.in.card.UpdateCardLimitRequest;
import banks.card.dto.in.card.WithdrawalRequest;
import banks.card.dto.out.card.*;
import banks.card.entity.Card;
import banks.card.utils.CardMascEncryptor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.StreamSupport;

/**
 * Интерфейс маппера для преобразования объектов, связанных с картами.
 * Выполняет преобразование между сущностями и DTO для операций с картами,
 * включая создание, обновление, переводы и снятие средств.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = TransactionMapper.class)
public interface CardMapper {

    /**
     * Преобразует сущность карты в DTO для операций создания/обновления.
     *
     * @param card объект {@link Card}
     * @return объект {@link CardUpsertResponse}
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "cardHolder", target = "cardHolder")
    @Mapping(source = "status", target = "status")
    CardUpsertResponse entityToUpsertResponse(Card card);

    /**
     * Преобразует запрос на обновление лимитов карты в сущность карты.
     *
     * @param request объект {@link UpdateCardLimitRequest}
     * @return объект {@link Card}
     */
    Card requestUpdateToEntity(UpdateCardLimitRequest request);

    /**
     * Преобразует сущность карты в DTO для ответа.
     *
     * @param card объект {@link Card}
     * @return объект {@link CardResponse}
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "encryptedCardNumber", target = "encryptedCardNumber", qualifiedByName = "mascCardNumber")
    CardResponse entityToResponse(Card card);

    /**
     * Преобразует запрос на перевод в DTO ответа.
     *
     * @param request объект {@link TransferRequest}
     * @return объект {@link TransferResponse}
     */
    @Mapping(target = "date", expression = "java(getNowDateTime())")
    TransferResponse transferRequestToTransferResponse(TransferRequest request);

    /**
     * Преобразует запрос на снятие средств в DTO ответа.
     *
     * @param request объект {@link WithdrawalRequest}
     * @return объект {@link WithdrawalResponse}
     */
    @Mapping(target = "date", expression = "java(getNowDateTime())")
    WithdrawalResponse withdrawalRequestToWithdrawalResponse(WithdrawalRequest request);

    /**
     * Преобразует список сущностей карт в DTO ответа со списком карт.
     *
     * @param cards итерируемый список объектов {@link Card}
     * @return объект {@link ListCardResponse}
     */
    default ListCardResponse listEntityToListResponse(Iterable<Card> cards) {
        ListCardResponse response = new ListCardResponse();
        response.setCards(StreamSupport.stream(cards.spliterator(), false)
                .map(this::entityToResponse)
                .toList()
        );
        return response;
    }

    /**
     * Маскирует номер карты, оставляя видимыми только последние 4 цифры.
     *
     * @param number зашифрованный номер карты
     * @return замаскированный номер карты в формате "**** **** **** XXXX"
     * @throws Exception если произошла ошибка при расшифровке
     */
    @Named("mascCardNumber")
    default String mascCardNumber(String number) throws Exception {
        String cardNum = CardMascEncryptor.decrypt(number);
        return "**** **** **** " + cardNum.substring(cardNum.length() - 4);
    }

    /**
     * Возвращает текущую дату и время в формате Timestamp.
     *
     * @return объект {@link Timestamp} с текущей датой и временем
     */
    default Timestamp getNowDateTime() {
        return Timestamp.valueOf(LocalDateTime.now(ZoneId.systemDefault()));
    }
}
