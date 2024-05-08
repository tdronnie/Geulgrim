package com.geulgrim.community.crew.presentation;

import com.geulgrim.community.crew.application.dto.request.CrewBoardModifyRequest;
import com.geulgrim.community.crew.application.dto.request.CrewBoardRequest;
import com.geulgrim.community.crew.application.dto.request.CrewJoinRequest;
import com.geulgrim.community.crew.application.dto.request.CrewReply;
import com.geulgrim.community.crew.application.dto.response.CrewApplicant;
import com.geulgrim.community.crew.application.dto.response.CrewBoard;
import com.geulgrim.community.crew.application.dto.response.CrewBoardDetail;
import com.geulgrim.community.crew.application.service.CrewService;
import com.geulgrim.community.global.s3.AwsS3Service;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/community/crew")
@RestController
public class CrewController {

    private final CrewService crewService;
    private final AwsS3Service s3UploadService;


    @GetMapping("/search")
    @Operation(summary = "크루모집 게시판 검색", description = "크루 모집 게시판의 게시글을 검색합니다.")
    public ResponseEntity<List<CrewBoard>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {
        List<CrewBoard> crews = crewService.search(keyword, category);
        return ResponseEntity.ok(crews);
    }



    @GetMapping("/detail/{crew_id}")
    @Operation(summary = "크루모집 게시글 상세 조회", description = "크루모집 게시판의 게시글을 상세 조회합니다.")
    public ResponseEntity<CrewBoardDetail> getCrewBoardDetail(
            @PathVariable("crew_id") Long crewId
    ) {
        CrewBoardDetail detail = crewService.getCrewBoardDetail(crewId);
        return ResponseEntity.ok(detail);
    }


    @PostMapping("/{userId}")
    @Operation(summary = "크루모집 게시글 등록", description = "크루모집 게시판에 게시글을 등록합니다.")
    public ResponseEntity<Long> addCrewBoard(
            @PathVariable("userId") Long userId,
            @RequestPart CrewBoardRequest crewBoardRequest,
            @RequestPart(required = false) List<MultipartFile> files
    ) {
        crewBoardRequest.setImageList(files);
        Long crewId = crewService.addCrewBoard(userId, crewBoardRequest);
        return ResponseEntity.ok(crewId);
    }


    @PutMapping("{crewId}")
    @Operation(summary = "크루모집 게시글 수정", description = "크루모집 게시판의 게시글을 수정합니다.")
    public ResponseEntity<String> update(
            @PathVariable("crewId") Long crewId,
            @RequestBody CrewBoardModifyRequest modifyRequest
    ) {
        String result = crewService.update(crewId, modifyRequest);
        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/{crewId}")
    @Operation(summary = "크루모집 게시글 삭제", description = "크루모집 게시판의 게시글을 삭제합니다.")
    public ResponseEntity<String> delete(
            @PathVariable("crewId") Long crewId
    ) {
        String result = crewService.delete(crewId);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/request/{crewId}")
    @Operation(summary = "크루 지원", description = "크루에 지원합니다.")
    public ResponseEntity<Long> apply(
            @RequestBody CrewJoinRequest crewJoinRequest,
            @PathVariable("crewId") Long crewId
    ) {

        Long crewRequestId = crewService.apply(crewId, crewJoinRequest);
        return ResponseEntity.ok(crewRequestId);
    }



    @GetMapping("/request/{crew_id}")
    @Operation(summary = "크루 모집 신청자 조회", description = "크루 모집 신청자를 전체 조회합니다.")
    public ResponseEntity<List<CrewApplicant>> getCrewApplicants(
            @PathVariable("crew_id") Long crewId
    ) {
        List<CrewApplicant> crewApplicants = crewService.getCrewApplicants(crewId);
        return ResponseEntity.ok(crewApplicants);
    }


    @PutMapping("/request/reply/{crew_request_id}")
    @Operation(summary = "크루 지원에 대한 답변", description = "크루 신청을 승인 또는 거절합니다.")
    public ResponseEntity<Long> reply(
            @PathVariable("crew_request_id") Long requestId,
            @RequestBody CrewReply crewReply
    ) {
        Long crewId = crewService.reply(requestId, crewReply);
        return ResponseEntity.ok(crewId);
    }

}