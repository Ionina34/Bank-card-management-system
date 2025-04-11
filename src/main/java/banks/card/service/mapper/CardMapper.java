package banks.card.service.mapper;

import banks.card.dto.in.card.UpdateCardLimitRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.CardUpsertResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.entity.Card;
import banks.card.utils.CardMascEncryptor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.stream.StreamSupport;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "cardHolder", target = "cardHolder")
    @Mapping(source = "status", target = "status")
    CardUpsertResponse entityToUpsertResponse(Card card);

    Card requestUpdateToEntity(UpdateCardLimitRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "encryptedCardNumber", target = "encryptedCardNumber", qualifiedByName = "mascCardNumber")
    CardResponse entityToResponse(Card card);

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
}
