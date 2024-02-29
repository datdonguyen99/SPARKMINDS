package net.sparkminds.librarymanagement.service;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.entity.User_;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.service.criteria.MemberCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService extends QueryService<User> {
    private final Logger logger = LoggerFactory.getLogger(MemberQueryService.class);

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<User> findByCriteria(MemberCriteria criteria, Pageable pageable) {
        logger.debug("find by criteria : {}", criteria);
        final Specification<User> spec = createSpecification(criteria);

        return userRepository.findAll(spec, pageable);
    }

    protected Specification<User> createSpecification(MemberCriteria criteria) {
        Specification<User> specification = Specification.where(null);

        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }

            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), User_.id));
            }

            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), User_.email));
            }

            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), User_.firstName));
            }

            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), User_.lastName));
            }

            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), User_.phoneNumber));
            }

            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isAvailable"), true));
        }

        return specification;
    }
}
