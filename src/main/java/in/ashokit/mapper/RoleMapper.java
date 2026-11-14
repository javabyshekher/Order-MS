package in.ashokit.mapper;

import in.ashokit.dto.RoleDto;
import in.ashokit.entity.RoleEntity;
import org.modelmapper.ModelMapper;

public class RoleMapper {
    public static final ModelMapper mapper= new ModelMapper();

    public static RoleDto entityToDto(RoleEntity entity) {
        return mapper.map(entity, RoleDto.class);
    }

    public static RoleEntity dtoToEntity(RoleDto dto) {
        return mapper.map(dto, RoleEntity.class);
    }
}
