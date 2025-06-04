package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.repository.UserByRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserByRepository userByRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        log.info("loadUserByUsername : " + username);

        Optional<UserBy> result = userByRepository.getWithRoles(username);

        if(result.isEmpty()){
            throw new UsernameNotFoundException("username not found.........");
        }

        UserBy userBy = result.get();

        UserBySecurityDTO userBySecurityDTO =
                new UserBySecurityDTO(
                        userBy.getUId(),
                        userBy.getUPassword(),
                        userBy.getUName(),
                        userBy.getUAddress(),
                        userBy.getUserType(),
                        userBy.getUserJob(),
                        userBy.getUEmail(),
                        userBy.getUPhone(),
                        userBy.getUBirthDay(),
                        userBy.getStatus(),
                        userBy.getRoleSet()
                                .stream().map(memberRole ->
                                        new SimpleGrantedAuthority("ROLE_"+memberRole.name()))
                                .collect(Collectors.toList())
                );

        log.info("userBySecurityDTO");
        log.info(userBySecurityDTO);

        return userBySecurityDTO;
    }
}
