package com.example.demo.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.ReflectionUtils;
import org.apache.el.util.ReflectionUtil;
import org.springframework.security.core.Authentication;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User saveOrUpdate(User user) {
        return userRepository.save(user);
    }

    public User update(Long id, User userObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> fields = objectMapper.convertValue(userObject, Map.class);
        fields.remove("id");

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

                if (fieldValue != null)
                    try {
                        Field field = User.class.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(user, fieldValue);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException("Error al actualizar el campo: " + fieldName, e);
                    }
            }
            return userRepository.save(user);
        }
        throw new RuntimeException("Not Found");
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User findUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
