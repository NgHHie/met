package ptit.dblab.app.securityConfig;

import ptit.dblab.shared.securityConfig.UserDetailCustom;
import ptit.dblab.app.interfaceProjection.UserDetailCustomInf;
import ptit.dblab.app.repository.UserRepository;
import ptit.dblab.app.utils.SequenceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceCustom implements UserDetailsService {

    private final UserRepository userRepository;
    private final SequenceUtil sequenceUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailCustomInf userCustom= userRepository.findByUsername(username);
        if (userCustom == null) {
            throw new UsernameNotFoundException("User not found");
        }
        String sessionPrefix = sequenceUtil.generateSessionPrefix();
        return UserDetailCustom.builder()
                .id(userCustom.getId())
                .username(userCustom.getUsername())
                .password(userCustom.getPassword())
                .userCode(userCustom.getUserCode())
                .userPrefix(userCustom.getUserPrefix())
                .role(userCustom.getRole())
                .sessionPrefix(sessionPrefix)
                .isPremium(userCustom.getIsPremium())
                .build();
    }
}
