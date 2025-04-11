package banks.card.service.mapper;

import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.dto.out.transaction.TransactionResponse;
import banks.card.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.stream.StreamSupport;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

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
