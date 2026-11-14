package in.ashokit.repository;

import in.ashokit.entity.ShippingAddressEntity;
import in.ashokit.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<ShippingAddressEntity, Integer> {
    public List<ShippingAddressEntity> findByUserUserId(Integer userId);

    Integer user(UserEntity user);
}
