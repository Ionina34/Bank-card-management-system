package banks.card.service.mapper;

import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.dto.out.transaction.TransactionResponse;
import banks.card.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.stream.StreamSupport;

/**
 * Маппер для преобразования сущностей {@link Transaction} в DTO и обратно.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    /**
     * Преобразует сущность {@link Transaction} в объект ответа {@link TransactionResponse}.
     *
     * @param transaction сущность транзакции
     * @return объект ответа {@link TransactionResponse}
     */
    @Mapping(source = "card.id", target = "fromCardId")
    @Mapping(source = "counterpartCard.id", target = "toCardId")
    TransactionResponse entityToResponse(Transaction transaction);

    /**
     * Преобразует список сущностей {@link Transaction} в объект ответа {@link ListTransactionResponse}.
     *
     * @param transactions список транзакций
     * @return объект ответа {@link ListTransactionResponse}, содержащий список преобразованных транзакций
     */
    default ListTransactionResponse listEntityToResponseEntity(Iterable<Transaction> transactions) {
        ListTransactionResponse response = new ListTransactionResponse();
        response.setResponses(StreamSupport.stream(transactions.spliterator(), false)
                .map(this::entityToResponse)
                .toList()
        );
        return response;
    }
}
