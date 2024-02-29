package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Role;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.payload.request.MemberDto;
import net.sparkminds.librarymanagement.payload.response.MemberResponse;
import net.sparkminds.librarymanagement.repository.RoleRepository;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.service.MemberQueryService;
import net.sparkminds.librarymanagement.service.MemberService;
import net.sparkminds.librarymanagement.service.criteria.MemberCriteria;
import net.sparkminds.librarymanagement.utils.RoleName;
import net.sparkminds.librarymanagement.utils.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberQueryService memberQueryService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    @Override
    public Page<MemberResponse> searchMember(MemberCriteria criteria, Pageable pageable) {
        logger.debug("Request to search members: {}, {}", criteria, pageable);

        return memberQueryService.findByCriteria(criteria, pageable)
                .map((User user) -> {
                    MemberResponse memberResponse = new MemberResponse();
                    BeanUtils.copyProperties(user, memberResponse);
                    return memberResponse;
                });
    }

    @Override
    public MemberResponse save(MemberDto memberDto) {
        logger.debug("Request to save new member: {}", memberDto);

        User newUser = User.builder()
                .username(memberDto.getUsername())
                .email(memberDto.getEmail())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .status(Status.ACTIVE)
                .firstName(memberDto.getFirstName())
                .lastName(memberDto.getLastName())
                .address(memberDto.getAddress())
                .phoneNumber(memberDto.getPhoneNumber())
                .build();

        // set role is USER by default
        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not existed", "user.role.role-not-existed"));
        newUser.setRole(role);

        // save user entity to db
        userRepository.save(newUser);

        MemberResponse memberResponse = new MemberResponse();
        BeanUtils.copyProperties(newUser, memberResponse);

        return memberResponse;
    }

    @Override
    public MemberResponse update(Long userId, MemberDto memberDto) {
        logger.debug("Request to update member: {}, {}", userId, memberDto);

        if (userId == null) {
            throw new ResourceInvalidException("Invalid id", "user.id.id-is-invalid");
        }

        User updateUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Id member not found", "user.id.id-not-found"));
        if (!updateUser.getIsAvailable()) {
            throw new ResourceNotFoundException("user not available", "user.user-not-found");
        }

        updateUser.setEmail(memberDto.getEmail());
        updateUser.setUsername(memberDto.getUsername());
        updateUser.setFirstName(memberDto.getFirstName());
        updateUser.setLastName(memberDto.getLastName());
        updateUser.setPhoneNumber(memberDto.getPhoneNumber());

        userRepository.save(updateUser);

        MemberResponse memberResponse = new MemberResponse();
        BeanUtils.copyProperties(updateUser, memberResponse);

        return memberResponse;
    }

    @Override
    public void delete(Long userId) {
        logger.debug("Request to delete member : {}", userId);

        User deleteUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Id member not found", "user.id.id-not-found"));
        deleteUser.setIsAvailable(false);
        userRepository.save(deleteUser);
    }
}
