package in.natchapol.deliveryfoodapi.repository;

import in.natchapol.deliveryfoodapi.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity,String> {

//optionalคือการจัดการค่าnullจะมีหรือไม่มีค่าก็ได้

   Optional<UserEntity> findByEmail(String email);
}
