package net.sparkminds.librarymanagement.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.MemberDto;
import net.sparkminds.librarymanagement.payload.response.MemberResponse;
import net.sparkminds.librarymanagement.service.MemberService;
import net.sparkminds.librarymanagement.service.criteria.MemberCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
public class MemberController {
    private final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    @Operation(summary = "Search member", description = "Search member by member Criteria", tags = {"Member functions"})
    @GetMapping("/members")
    public ResponseEntity<Page<MemberResponse>> searchMembers(MemberCriteria criteria, Pageable pageable) {
        logger.debug("REST request to search member : {}, {}", criteria, pageable);
        return new ResponseEntity<>(memberService.searchMember(criteria, pageable), HttpStatus.OK);
    }

    @Operation(summary = "Add new member", description = "Add new member", tags = {"Member functions"})
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> addMember(@Valid @RequestBody MemberDto memberDto) {
        logger.debug("REST request to save Member : {}", memberDto);
        return new ResponseEntity<>(memberService.save(memberDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update member", description = "Update member", tags = {"Member functions"})
    @PutMapping("/members/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable(value = "id") final Long id,
                                                       @Valid @RequestBody MemberDto memberDto) {
        logger.debug("REST request to update Member : {}, {}", id, memberDto);
        return new ResponseEntity<>(memberService.update(id, memberDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete member", description = "Delete member(Update flag)", tags = {"Member functions"})
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        logger.debug("REST request to delete Member : {}", id);
        memberService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
