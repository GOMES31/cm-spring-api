package dev.edugomes.springapi.user;
import dev.edugomes.springapi.auth.request.RegisterRequest;
import dev.edugomes.springapi.auth.response.AuthResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterRequest registerRequest);
    AuthResponse toDto(User user);
}
