package in.natchapol.deliveryfoodapi.repository;

import in.natchapol.deliveryfoodapi.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends MongoRepository<OrderEntity, String> {
    List<OrderEntity> findByUserId(String userId);

    void deleteByUserId(String userId);
}
