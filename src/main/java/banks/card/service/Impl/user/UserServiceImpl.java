package banks.card.service.Impl.user;

import banks.card.entity.User;
import banks.card.exception.EntityExistsException;
import banks.card.exception.EntityNotFoundException;
import banks.card.repository.UserRepository;
import banks.card.service.services.user.UserUserActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserUserActionService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException("User already exists with Email: " + user.getEmail());
        }

        return save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Email: " + email));
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::findByEmail;
    }
}
