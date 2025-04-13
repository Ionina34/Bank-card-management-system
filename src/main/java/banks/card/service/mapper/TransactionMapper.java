package banks.card.service.mapper;

import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.dto.out.transaction.TransactionResponse;
import banks.card.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.stream.StreamSupport;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(source = "card.id", target = "fromCardId")
    @Mapping(source = "counterpartCard.id", target = "toCardId")
    TransactionResponse entityToResponse(Transaction transaction);

    default ListTransactionResponse listEntityToResponseEntity(Iterable<Transaction> transactions) {
        ListTransactionResponse response = new ListTransactionResponse();
        response.setResponses(StreamSupport.stream(transactions.spliterator(), false)
                .map(this::entityToResponse)
                .toList()
        );
        return response;
    }
}
