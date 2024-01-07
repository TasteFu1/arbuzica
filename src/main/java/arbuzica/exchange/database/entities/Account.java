package arbuzica.exchange.database.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

import arbuzica.exchange.utilities.java.StringUtility;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "users")
public class Account {

    @Id
    private String id;

    private String discordId;
    private Integer balance;
    private String recoveryCode;

    @Builder
    private Account(String discordId) {
        this.discordId = discordId;
        this.balance = 0;
        this.recoveryCode = StringUtility.randomCode(4);
    }
}
