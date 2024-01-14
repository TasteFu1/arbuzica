package arbuzica.exchange.database.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import arbuzica.exchange.database.entities.Serial;

public interface SerialRepository extends MongoRepository<Serial, String> {

    Serial findByCode(String code);
}
