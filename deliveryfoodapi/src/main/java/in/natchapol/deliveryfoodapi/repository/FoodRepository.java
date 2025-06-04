package in.natchapol.deliveryfoodapi.repository;

import in.natchapol.deliveryfoodapi.entity.FoodEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends MongoRepository<FoodEntity,String> {

}
