package vn.hoidanit.laptopshop.mapper;

import org.springframework.stereotype.Component;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.dto.RegisterDTO;

@Component
public class UserMapper {
    public User registerDTOToUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        return user;
    }
}
