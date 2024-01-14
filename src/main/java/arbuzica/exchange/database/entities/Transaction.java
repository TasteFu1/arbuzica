package arbuzica.exchange.database.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document
@Builder
public class Transaction {

    @Id
    private String id;

    private String accountId;
    private Long date;
    private String transactionId;
    private Double fee;
    private String address;
    private Double amount;
    private Double rate;
}
