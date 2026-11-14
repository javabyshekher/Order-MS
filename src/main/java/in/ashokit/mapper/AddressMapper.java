package in.ashokit.mapper;

import in.ashokit.dto.ShippingAddressDto;
import in.ashokit.entity.ShippingAddressEntity;
import org.modelmapper.ModelMapper;

public class AddressMapper {
    public static final ModelMapper mapper= new ModelMapper();

    public static ShippingAddressDto entityToDto(ShippingAddressEntity entity) {
        return mapper.map(entity, ShippingAddressDto.class);
    }

    public static ShippingAddressEntity dtoToEntity(ShippingAddressDto dto) {
        return mapper.map(dto, ShippingAddressEntity.class);
    }
}
