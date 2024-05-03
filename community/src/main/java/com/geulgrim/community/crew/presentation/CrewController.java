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
@RequestMapping("/api/v1/crew")
@RestController
public class CrewController {

    private final CrewService crewService;
    private final AwsS3Service s3UploadService;

    // 크루 검색
    @GetMapping("/search")
    public ResponseEntity<List<CrewBoard>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {
        List<CrewBoard> crews = crewService.search(keyword, category);
        return ResponseEntity.ok(crews);
    }


    // 크루 모집 상세 조회
    @GetMapping("/detail/{crew_id}")
    public ResponseEntity<CrewBoardDetail> getCrewBoardDetail(
            @PathVariable("crew_id") Long crewId
    ) {
        CrewBoardDetail detail = crewService.getCrewBoardDetail(crewId);
        return ResponseEntity.ok(detail);
    }

    // 크루 모집 게시글 등록
    @PostMapping("/{userId}")
    public ResponseEntity<Long> addCrewBoard(
            @RequestBody CrewBoardRequest crewBoardRequest,
            @PathVariable("userId") Long userId
    ) {

        Long crewId = crewService.addCrewBoard(userId, crewBoardRequest);
        return ResponseEntity.ok(crewId);
    }

    // 크루 모집 이미지 등록
    @PostMapping("/image/{crewId}")
    public ResponseEntity<String> addCrewBoardImages(
            @PathVariable("crewId") Long crewId,
            @RequestPart(value = "crewBoardImg", required = false) ArrayList<MultipartFile> multipartFiles
    ) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return ResponseEntity.badRequest().body("No files uploaded");
        }

        ArrayList<String> fileUrls = new ArrayList<>();

        // 유저 아이디 수정
        long userId = 1;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        for (MultipartFile file : multipartFiles) {
            String fileName = s3UploadService.uploadFile(userId, file, timestamp, "crew");
            fileUrls.add(fileName);
            System.out.println("Uploaded file URL: " + fileName);
        }
        crewService.addCrewBoardImages(crewId, fileUrls);
        return ResponseEntity.ok("이미지를 성공적으로 저장했습니다.");

    }

    // 크루 모집 수정
    @PutMapping("{crewId}")
    public ResponseEntity<String> update(
            @PathVariable("crewId") Long crewId,
            @RequestBody CrewBoardModifyRequest modifyRequest

    ) {
        String result = crewService.update(crewId, modifyRequest);
        return ResponseEntity.ok(result);
    }

    // 크루 모집 삭제
    @DeleteMapping("/{crewId}")
    public ResponseEntity<String> delete(
            @PathVariable("crewId") Long crewId
    ) {
        String result = crewService.delete(crewId);
        return ResponseEntity.ok(result);
    }

    // 크루 모집 신청
    @PostMapping("/request/{crewId}")
    public ResponseEntity<Long> apply(
            @RequestBody CrewJoinRequest crewJoinRequest,
            @PathVariable("crewId") Long crewId
    ) {

        Long crewRequestId = crewService.apply(crewId, crewJoinRequest);
        return ResponseEntity.ok(crewRequestId);
    }


    // 크루 모집 신청자 전체 조회
    @GetMapping("/request/{crew_id}")
    public ResponseEntity<List<CrewApplicant>> getCrewApplicants(
            @PathVariable("crew_id") Long crewId
    ) {
        List<CrewApplicant> crewApplicants = crewService.getCrewApplicants(crewId);
        return ResponseEntity.ok(crewApplicants);
    }

    // 크루 모집 신청에 대한 답변
    @PutMapping("/request/reply/{crew_request_id}")
    public ResponseEntity<Long> reply(
            @PathVariable("crew_request_id") Long requestId,
            @RequestBody CrewReply crewReply
    ) {
        Long crewId = crewService.reply(requestId, crewReply);
        return ResponseEntity.ok(crewId);
    }

}