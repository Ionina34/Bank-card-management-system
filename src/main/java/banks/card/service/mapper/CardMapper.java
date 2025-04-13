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

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = TransactionMapper.class)
public interface CardMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "cardHolder", target = "cardHolder")
    @Mapping(source = "status", target = "status")
    CardUpsertResponse entityToUpsertResponse(Card card);

    Card requestUpdateToEntity(UpdateCardLimitRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "encryptedCardNumber", target = "encryptedCardNumber", qualifiedByName = "mascCardNumber")
    CardResponse entityToResponse(Card card);

    @Mapping(target = "date", expression = "java(getNowDateTime())")
    TransferResponse transferRequestToTransferResponse(TransferRequest request);

    @Mapping(target = "date", expression = "java(getNowDateTime())")
    WithdrawalResponse withdrawalRequestToWithdrawalResponse(WithdrawalRequest request);

    default ListCardResponse listEntityToListResponse(Iterable<Card> cards) {
        ListCardResponse response = new ListCardResponse();
        response.setCards(StreamSupport.stream(cards.spliterator(), false)
                .map(this::entityToResponse)
                .toList()
        );
        return response;
    }

    @Named("mascCardNumber")
    default String mascCardNumber(String number) throws Exception {
        String cardNum = CardMascEncryptor.decrypt(number);
        return "**** **** **** " + cardNum.substring(cardNum.length() - 4);
    }

    default Timestamp getNowDateTime() {
        return Timestamp.valueOf(LocalDateTime.now(ZoneId.systemDefault()));
    }
}
