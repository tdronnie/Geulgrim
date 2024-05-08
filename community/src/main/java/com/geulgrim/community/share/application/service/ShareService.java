package com.geulgrim.community.share.application.service;

import com.geulgrim.community.global.user.domain.repository.UserRepository;
import com.geulgrim.community.share.application.dto.request.ShareUpdateRequest;
import com.geulgrim.community.share.domain.entity.Share;
import com.geulgrim.community.share.domain.entity.ShareImage;
import com.geulgrim.community.share.domain.entity.Share;
import com.geulgrim.community.share.domain.entity.ShareImage;
import com.geulgrim.community.share.domain.entity.enums.ImageType;
import com.geulgrim.community.global.s3.AwsS3Service;
import com.geulgrim.community.share.application.dto.request.ShareWriteRequest;
import com.geulgrim.community.share.application.dto.response.ShareDetailResponse;
import com.geulgrim.community.share.application.dto.response.ShareListResponse;
import com.geulgrim.community.share.domain.repository.ShareCommentRepository;
import com.geulgrim.community.share.domain.repository.ShareImageRepository;
import com.geulgrim.community.share.domain.repository.ShareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ShareService {

    private final ShareRepository shareRepository;
    private final AwsS3Service awsS3Service;
    private final ShareImageRepository shareImageRepository;
    private final ShareCommentRepository shareCommentRepository;
    private final UserRepository userRepository;

    // 메인 페이지 신규 그림평가글 목록
    public List<ShareListResponse> mainShareNewList() {
        List<ShareListResponse> list = shareRepository.findShareResponseList();
        return list.subList(0, 6);
    }
    
    // 그림평가 게시판 전체 조회
    public List<ShareListResponse> shareList() {
        return shareRepository.findShareResponseList();
    }
    
    // 그림평가 게시판 상세조회
    public ShareDetailResponse shareDetail(long shareId) {
        return ShareDetailResponse.builder()
                .share(shareRepository.findByShareId(shareId))
                .commentList(shareCommentRepository.findAllByShareId(shareId))
                .imageList(shareImageRepository.findByShareId(shareId))
                .build();
    }
    
    // 그림공유 작성
    public Share writeShare(long userId, ShareWriteRequest shareWriteRequest) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<ShareImage> shareImageList = new ArrayList<>();
        for(MultipartFile image : shareWriteRequest.getImageList()) {
            String url = awsS3Service.uploadFile(userId, image, timestamp, "share");
            ShareImage shareImage = new ShareImage();
            shareImage.setFileUrl(url);
            shareImage.setImageType(ImageType.URL);
            shareImageList.add(shareImage);
            log.info("URL : {}", fileUrl.getFileUrl());
        }

        Share share = shareRepository.save(
                Share.builder()
                .user(userRepository.findUserByUserId(userId))
                .title(shareWriteRequest.getTitle())
                .content(shareWriteRequest.getContent())
                .imageList(shareImageList)
                .build()
        );

        for(ShareImage shareImage : shareImageList) {
            shareImage.setShare(share);
            shareImage.setImageType(ImageType.URL);
            shareImageRepository.save(shareImage);
        }

        return share;
    }

    // 그림평가 게시글 삭제
    public void deleteShare(Long shareId) {
        shareRepository.deleteById(shareId);
    }

    // 그림평가 게시글 수정
    public Share modifyShare(long userId, ShareUpdateRequest shareUpdateRequest) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<ShareImage> shareImageList = new ArrayList<>();
        for(MultipartFile image : shareUpdateRequest.getImageList()) {
            String url = awsS3Service.uploadFile(userId, image, timestamp, "share");
            ShareImage shareImage = new ShareImage();
            shareImage.setFileUrl(url);
            shareImage.setImageType(ImageType.URL);
            shareImageList.add(shareImage);
        }

        Share share = shareRepository.save(
                Share.builder()
                .shareId(shareUpdateRequest.getShareId())
                .user(userRepository.findUserByUserId(userId))
                .title(shareUpdateRequest.getTitle())
                .content(shareUpdateRequest.getContent())
                .imageList(shareImageList)
                .build()
        );

        for(ShareImage shareImage : shareImageList) {
            shareImage.setShare(share);
            shareImage.setImageType(ImageType.URL);
            shareImageRepository.save(shareImage);
        }
        return share;
    }
}
