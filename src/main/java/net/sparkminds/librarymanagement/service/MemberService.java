package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.request.MemberDto;
import net.sparkminds.librarymanagement.payload.response.MemberResponse;
import net.sparkminds.librarymanagement.service.criteria.MemberCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    /**
     * Search member by memberCriteria
     *
     * @param memberCriteria
     * @param pageable
     * @return list of books
     */
    Page<MemberResponse> searchMember(MemberCriteria memberCriteria, Pageable pageable);

    /**
     * Save new member
     *
     * @param memberDto entity member
     * @return
     */
    MemberResponse save(MemberDto memberDto);

    /**
     * Update member by {userId}
     *
     * @param userId    id of user
     * @param memberDto
     * @return
     */
    MemberResponse update(Long userId, MemberDto memberDto);

    /**
     * Delete member by {userId}
     *
     * @param userId id of user
     */
    void delete(Long userId);
}
