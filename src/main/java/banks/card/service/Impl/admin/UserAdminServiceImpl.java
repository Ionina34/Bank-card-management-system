package banks.card.service.Impl.admin;

import banks.card.dto.in.user.UserCreateRequest;
import banks.card.dto.in.user.UserPasswordResetRequest;
import banks.card.dto.in.user.UserUpdateRequest;
import banks.card.dto.out.MessageResponse;
import banks.card.dto.out.user.ListUserResponse;
import banks.card.dto.out.user.UserResponse;
import banks.card.entity.Role;
import banks.card.entity.User;
import banks.card.exception.EntityNotFoundException;
import banks.card.repository.UserRepository;
import banks.card.service.services.amin.UserAdminActionService;
import banks.card.service.mapper.UserMapper;
import banks.card.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminActionService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found with ID: " + id)
                );
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        User user = userMapper.createRequestToEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User newUser = userRepository.save(user);
        return userMapper.entityToResponse(newUser);
    }

    @Override
    public ListUserResponse getAll(Pageable pageable, String role) {
        Page<User> users;

        if (role != null && !role.isEmpty()) {
            Role userRole = Role.valueOf(role.toUpperCase());
            users = userRepository.findByRole(userRole, pageable);
            return userMapper.listUserToListResponse(users);
        }
        users = userRepository.findAll(pageable);
        return userMapper.listUserToListResponse(users);
    }

    @Override
    public UserResponse update(Long userId, UserUpdateRequest request) throws EntityNotFoundException {
        User actualUser = findById(userId);
        User user = userMapper.updateRequestToEntity(request);

        BeanUtils.copyNotNullProperties(user, actualUser);

        User updateUser = userRepository.save(actualUser);
        return userMapper.entityToResponse(updateUser);
    }

    @Override
    public MessageResponse passwordReset(Long userId, UserPasswordResetRequest request) throws EntityNotFoundException {
        User actualUser = findById(userId);
        actualUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(actualUser);
        return new MessageResponse("Password reset successfully");
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
