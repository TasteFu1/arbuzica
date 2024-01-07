package arbuzica.exchange.database.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

import arbuzica.exchange.database.entities.Account;

public interface AccountRepository extends MongoRepository<Account, String> {

    Account findByDiscordId(String discordId);

    Account findByRecoveryCode(String recoveryCode);
}
