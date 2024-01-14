package arbuzica.exchange.database.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import arbuzica.exchange.database.entities.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
}
