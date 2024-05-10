package com.geulgrim.recruit.job.application.service;

import com.geulgrim.recruit.job.application.dto.request.*;
import com.geulgrim.recruit.job.application.dto.response.*;
import com.geulgrim.recruit.job.domain.entity.*;
import com.geulgrim.recruit.job.domain.entity.Enums.EducationStatus;
import com.geulgrim.recruit.job.domain.entity.Enums.OpenStatus;
import com.geulgrim.recruit.job.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final PositionRepository positionRepository;
    private final ResumePositionRepository resumePositionRepository;
    private final ResumePorfolioRepository resumePorfolioRepository;
    private final EducationRepository educationRepository;
    private final WorkRepository workRepository;
    private final AwardRepository awardRepository;
    private final ExperienceRepository experienceRepository;

    // 구인구직 등록

    // 구인구직 리스트 조회

    // 내가 작성한 구인구직 리스트 조회

    // 구인구직 상세 조회

    // 구인구직 수정 (3순위)

    // 구인구직 삭제 (2순위)

    // 구인구직 포지션 등록

    // 구인구직 포지션 삭제

    // 구인구직 신청

    // 지원자 이력서 리스트 조회

    // 지원자 합격여부 수정

    // 구인구직 관심 등록

    // 나의 구인구직 관심 리스트조회

    // 구인구직 관심 삭제





















    // 내 이력서 등록
    public Map<String, Long> createResume(
            HttpHeaders headers,
            CreateResumeRequest createResumeRequest) {

        // 이력서 저장 파트
        Resume resume = Resume.builder()
                .userId(Long.parseLong(headers.get("user_id").get(0)))
                .resumeTitle(createResumeRequest.getResumeTitle())
                .essay(createResumeRequest.getEssay())
                .openStatus(OpenStatus.valueOf(createResumeRequest.getOpenStatus()))
                .fileUrl(createResumeRequest.getFileUrl())
                .build();
        resumeRepository.save(resume);

        // 이력서 포지션 저장 파트
        List<Long> positionIds = createResumeRequest.getPositionIds();
        for (Long positionId : positionIds) {
            Position position = positionRepository.findByPositionId(positionId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 positionId 입니다."));
            ResumePosition resumePosition = ResumePosition.builder()
                    .resume(resume)
                    .position(position)
                    .build();
            resumePositionRepository.save(resumePosition);
        }

        // 이력서 포트폴리오 저장 파트
        List<Long> pofolIds = createResumeRequest.getPofolIds();
        for (Long pofolId : pofolIds) {
            ResumePortfolio resumePortfolio = ResumePortfolio.builder()
                    .resume(resume)
                    .pofolId(pofolId)
                    .build();
            resumePorfolioRepository.save(resumePortfolio);
        }

        // 이력서 학력사항 저장 파트
        List<CreateEducationRequest> createEducationRequests = createResumeRequest.getCreateEducationRequests();
        for (CreateEducationRequest createEducationRequest : createEducationRequests) {
            Education education = Education.builder()
                    .resume(resume)
                    .institutionName(createEducationRequest.getInsitutionName())
                    .startDate(createEducationRequest.getStartDate())
                    .endDate(createEducationRequest.getEndDate())
                    .educationStatus(EducationStatus.valueOf(createEducationRequest.getEducationStatus()))
                    .gpa(createEducationRequest.getGpa())
                    .build();
            educationRepository.save(education);
        }

        // 이력서 경력사항 저장 파트
        List<CreateWorkRequest> createWorkRequests = createResumeRequest.getCreateWorkRequests();
        for (CreateWorkRequest createWorkRequest : createWorkRequests) {
            Work work = Work.builder()
                    .resume(resume)
                    .company(createWorkRequest.getCompany())
                    .startDate(createWorkRequest.getStartDate())
                    .endDate(createWorkRequest.getEndDate())
                    .content(createWorkRequest.getContent())
                    .build();
            workRepository.save(work);
        }

        // 이력서 수상사항 저장 파트
        List<CreateAwardRequest> createAwardRequests = createResumeRequest.getCreateAwardRequests();
        for (CreateAwardRequest createAwardRequest : createAwardRequests) {
            Award award = Award.builder()
                    .resume(resume)
                    .awardName(createAwardRequest.getAwardName())
                    .acquisitionDate(createAwardRequest.getAcquisitionDate())
                    .institution(createAwardRequest.getInstitution())
                    .score(createAwardRequest.getScore())
                    .build();
            awardRepository.save(award);
        }

        // 이력서 경험사항 저장 파트
        List<CreateExperienceRequest> createExperienceRequests = createResumeRequest.getCreateExperienceRequests();
        for (CreateExperienceRequest createExperienceRequest : createExperienceRequests) {
            Experience experience = Experience.builder()
                    .resume(resume)
                    .experienceTitle(createExperienceRequest.getExperienceTitle())
                    .experienceContent(createExperienceRequest.getExperienceContent())
                    .startDate(createExperienceRequest.getStartDate())
                    .endDate(createExperienceRequest.getEndDate())
                    .build();
             experienceRepository.save(experience);
        }

        Map<String, Long> map = Map.of("resumeId", resume.getResumeId());
        return map;
    }

    // 내 이력서 전체 조회
    public GetResumesResponses getResumes(
            HttpHeaders headers) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        List<GetResumesResponse> getResumeResponses = new ArrayList<>();
        for(Resume resume : resumes) {
            Optional<List<ResumePosition>> resumePositions = resumePositionRepository.findByResume(resume);
            List<GetResumePositionResponse> getResumePositionResponses = new ArrayList<>();

            if(resumePositions.isPresent()) {
                for (ResumePosition resumePosition : resumePositions.get()) {
                    Position position = resumePosition.getPosition();
                    GetResumePositionResponse getResumePositionResponse = GetResumePositionResponse.builder()
                            .resumePositionId(resumePosition.getPositionResumeId())
                            .positionId(position.getPositionId())
                            .build();
                    getResumePositionResponses.add(getResumePositionResponse);
                }
            }

            GetResumesResponse getResumeResponse = GetResumesResponse.builder()
                .resumeId(resume.getResumeId())
                .resumeTitle(resume.getResumeTitle())
                .essay(resume.getEssay())
                .openStatus(resume.getOpenStatus().name())
                .fileUrl(resume.getFileUrl())
                .getResumePositionResponses(getResumePositionResponses)
                .build();
            getResumeResponses.add(getResumeResponse);
        }
        return GetResumesResponses.builder()
            .getResumesResponse(getResumeResponses)
            .build();
    }

    // 내 이력서 상세 조회
    public GetResumeResponse getResume(
            HttpHeaders headers, Long resumeId) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));
        Resume resume = resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력서 입니다."));

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        // 이력서 포지션 조회 파트
        Optional<List<ResumePosition>> resumePositions = resumePositionRepository.findByResume(resume);
        List<GetResumePositionResponse> getResumePositionResponses = new ArrayList<>();
        if(resumePositions.isPresent()) {
            for (ResumePosition resumePosition : resumePositions.get()) {
                GetResumePositionResponse getResumePositionResponse = GetResumePositionResponse.builder()
                        .resumePositionId(resumePosition.getPositionResumeId())
                        .positionId(resumePosition.getPosition().getPositionId())
                        .build();
                getResumePositionResponses.add(getResumePositionResponse);
            }
        }

        // 이력서 포트폴리오 조회 파트
        Optional<List<ResumePortfolio>> resumePortfolios = resumePorfolioRepository.findByResume(resume);
        List<GetResumePortfolioResponse> getResumePortfolioResponses = new ArrayList<>();
        if(resumePortfolios.isPresent()) {
            for (ResumePortfolio resumePortfolio : resumePortfolios.get()) {
                GetResumePortfolioResponse getResumePortfolioResponse = GetResumePortfolioResponse.builder()
                        .resumePofolId(resumePortfolio.getResumePofolId())
                        .pofolId(resumePortfolio.getPofolId())
                        .build();
                getResumePortfolioResponses.add(getResumePortfolioResponse);
            }
        }

        // 이력서 학력사항 조회 파트
        Optional<List<Education>> educations = educationRepository.findByResume(resume);
        List<GetEducationResponse> getEducationResponses = new ArrayList<>();
        if(educations.isPresent()) {
            for (Education education : educations.get()) {
                GetEducationResponse getEducationResponse = GetEducationResponse.builder()
                        .educationId(education.getEducationId())
                        .institutionName(education.getInstitutionName())
                        .startDate(education.getStartDate())
                        .endDate(education.getEndDate())
                        .educationStatus(education.getEducationStatus().name())
                        .gpa(education.getGpa())
                        .build();
                getEducationResponses.add(getEducationResponse);
            }
        }

        // 이력서 경력사항 조회 파트
        Optional<List<Work>> works = workRepository.findByResume(resume);
        List<GetWorkResponse> getWorkResponses = new ArrayList<>();
        if(works.isPresent()) {
            for (Work work : works.get()) {
                GetWorkResponse getWorkResponse = GetWorkResponse.builder()
                        .workId(work.getWorkId())
                        .companyName(work.getCompany())
                        .startDate(work.getStartDate())
                        .endDate(work.getEndDate())
                        .content(work.getContent())
                        .build();
                getWorkResponses.add(getWorkResponse);
            }
        }


        // 이력서 자격/어학/수상 조회 파트
        Optional<List<Award>> awards = awardRepository.findByResume(resume);
        List<GetAwardResponse> getAwardResponses = new ArrayList<>();
        if(awards.isPresent()) {
            for(Award award : awards.get()) {
                GetAwardResponse getAwardResponse = GetAwardResponse.builder()
                        .awardId(award.getAwardId())
                        .awardName(award.getAwardName())
                        .acquisitionDate(award.getAcquisitionDate())
                        .institution(award.getInstitution())
                        .score(award.getScore())
                        .build();
                getAwardResponses.add(getAwardResponse);
            }
        }

        // 이력서 경험/활동/교육 조회 파트
        Optional<List<Experience>> experiences = experienceRepository.findByResume(resume);
        List<GetExperienceResponse> getExperienceResponses = new ArrayList<>();
        if(experiences.isPresent()) {
            for(Experience experience : experiences.get()) {
                GetExperienceResponse getExperienceResponse = GetExperienceResponse.builder()
                        .experienceId(experience.getExperienceId())
                        .experienceTitle(experience.getExperienceTitle())
                        .experienceContent(experience.getExperienceContent())
                        .startDate(experience.getStartDate())
                        .endDate(experience.getEndDate())
                        .build();
                getExperienceResponses.add(getExperienceResponse);
            }
        }


        GetResumeResponse getResumeResponse = GetResumeResponse.builder()
                .resumeId(resume.getResumeId())
                .resumeTitle(resume.getResumeTitle())
                .essay(resume.getEssay())
                .openStatus(resume.getOpenStatus().name())
                .fileUrl(resume.getFileUrl())
                .resumePositionResponses(getResumePositionResponses)
                .resumePortfolioResponses(getResumePortfolioResponses)
                .educationResponses(getEducationResponses)
                .workResponses(getWorkResponses)
                .awardResponses(getAwardResponses)
                .experienceResponses(getExperienceResponses)
                .build();

        return getResumeResponse;
    }


    // 내 이력서 수정 (3순위)

    // 내 이력서 삭제 (2순위)

    // 이력서 포지션 생성
    public String createResumePosition(
            HttpHeaders headers, Long resumeId, Long positionId) {

        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Resume resume = resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력서 입니다."));

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        Position position = positionRepository.findByPositionId(positionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 positionId 입니다."));

        // 이미 생성되어있는지 확인하는 절차
        Optional<ResumePosition> resumePositionOptional = resumePositionRepository.findByResumeAndPosition(resume, position);
        if(resumePositionOptional.isPresent()) {    // 생성되어 있을 시
            return "생성실패"; // 이미 생성되어 있습니다.
        }

        // 생성되어 있지 않을 시
        ResumePosition resumePosition = ResumePosition.builder()
                .resume(resume)
                .position(position)
                .build();

        resumePositionRepository.save(resumePosition);

        return "생성완료";
    }

    // 포지션 조회
    public GetPositionsResponse getPositions() {
        List<Position> positions = positionRepository.findAll();
        return GetPositionsResponse.builder()
                .positions(positions)
                .build();
    }

    // 이력서 포지션 삭제
    public String deleteResumePosition(
            HttpHeaders headers, Long resumeId, Long positionId) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Resume resume = resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력서 입니다."));

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        Position position = positionRepository.findByPositionId(positionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 positionId 입니다."));

        // 삭제할 포지션을 찾는 절차
        Optional<ResumePosition> resumePositionOptional = resumePositionRepository.findByResumeAndPosition(resume, position);
        if(resumePositionOptional.isEmpty()) {
            return "삭제실패"; // 삭제할 포지션이 존재하지 않습니다.
        }

        // 삭제할 포지션을 삭제하는 절차
        resumePositionRepository.delete(resumePositionOptional.get());
        return "삭제완료";
    }

    // 이력서 포토폴리오 생성
    public String createResumePortfolio(
            HttpHeaders headers, Long resumeId, Long pofolId) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Resume resume = resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력서 입니다."));

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        // 이미 생성되어있는지 확인하는 절차
        Optional<ResumePortfolio> resumePortfolioOptional = resumePorfolioRepository.findByResumeAndPofolId(resume, pofolId);
        if(resumePortfolioOptional.isPresent()) {    // 생성되어 있을 시
            return "생성실패"; // 이미 생성되어 있습니다.
        }

        // 생성되어 있지 않을 시
        ResumePortfolio resumePortfolio = ResumePortfolio.builder()
                .resume(resume)
                .pofolId(pofolId)
                .build();

        resumePorfolioRepository.save(resumePortfolio);

        return "생성완료";
    }

    // 이력서 포토폴리오 삭제
    public String deleteResumePortfolio(
            HttpHeaders headers, Long resumeId, Long pofolId) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Resume resume = resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력서 입니다."));

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        // 삭제할 포트폴리오를 찾는 절차
        Optional<ResumePortfolio> resumePortfolioOptional = resumePorfolioRepository.findByResumeAndPofolId(resume, pofolId);
        if (resumePortfolioOptional.isEmpty()) {
            return "삭제실패"; // 삭제할 포트폴리오가 존재하지 않습니다.
        }

        // 삭제할 포트폴리오를 삭제하는 절차
        resumePorfolioRepository.delete(resumePortfolioOptional.get());
        return "삭제완료";

    }

    // 학력사항 생성
    public Map<String, Long> createEducation (
            HttpHeaders headers, Long resumeId, CreateEducationRequest createEducationRequest) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Resume resume = resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력서 입니다."));

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        Education education = Education.builder()
                .resume(resume)
                .institutionName(createEducationRequest.getInsitutionName())
                .startDate(createEducationRequest.getStartDate())
                .endDate(createEducationRequest.getEndDate())
                .educationStatus(EducationStatus.valueOf(createEducationRequest.getEducationStatus()))
                .gpa(createEducationRequest.getGpa())
                .build();

        educationRepository.save(education);

        return Map.of("educationId", education.getEducationId());
    }

    // 학력사항 수정(3순위)


    // 학력사항 삭제
    public String deleteEducation (
            HttpHeaders headers, Long educationId) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학력사항 입니다."));

        Resume resume = education.getResume();

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        educationRepository.delete(education);

        return "삭제완료";
    }

    // 경력사항 생성
    public Map<String, Long> createWork (
            HttpHeaders headers, Long resumeId, CreateWorkRequest createWorkRequest) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Resume resume = resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력서 입니다."));

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        Work work = Work.builder()
                .resume(resume)
                .company(createWorkRequest.getCompany())
                .startDate(createWorkRequest.getStartDate())
                .endDate(createWorkRequest.getEndDate())
                .content(createWorkRequest.getContent())
                .build();

        workRepository.save(work);

        return Map.of("workId", work.getWorkId());
    }

    // 경력사항 수정 (3순위)

    // 경력사항 삭제
    public String deleteWork (
            HttpHeaders headers, Long workId) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경력사항 입니다."));

        Resume resume = work.getResume();

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        workRepository.delete(work);

        return "삭제완료";
    }

    // 자격/어학/수상 생성
    public Map<String, Long> createAward (
            HttpHeaders headers, Long resumeId, CreateAwardRequest createAwardRequest) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Resume resume = resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력서 입니다."));

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        Award award = Award.builder()
                .resume(resume)
                .awardName(createAwardRequest.getAwardName())
                .acquisitionDate(createAwardRequest.getAcquisitionDate())
                .institution(createAwardRequest.getInstitution())
                .score(createAwardRequest.getScore())
                .build();

        awardRepository.save(award);

        return Map.of("awardId", award.getAwardId());
    }


    // 자격/어학/수상 수정 (3순위)


    // 자격/어학/수상 삭제
    public String deleteAward(
            HttpHeaders headers, Long awardId) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Award award = awardRepository.findById(awardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자격/어학/수상 입니다."));

        Resume resume = award.getResume();

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        awardRepository.delete(award);

        return "삭제완료";
    }


    // 경험/활동/교육 생성
    public Map<String, Long> createExperience(
            HttpHeaders headers, Long resumeId, CreateExperienceRequest createExperienceRequest) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Resume resume = resumeRepository.findByResumeId(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력서 입니다."));

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        Experience experience = Experience.builder()
                .resume(resume)
                .experienceTitle(createExperienceRequest.getExperienceTitle())
                .experienceContent(createExperienceRequest.getExperienceContent())
                .startDate(createExperienceRequest.getStartDate())
                .endDate(createExperienceRequest.getEndDate())
                .build();

        experienceRepository.save(experience);

        return Map.of("experienceId", experience.getExperienceId());
    }

    // 경험/활동/교육 수정 (3순위)

    // 경험/활동/교육 삭제
    public String deleteExperience(
            HttpHeaders headers, Long experienceId) {
        Long userId = Long.parseLong(headers.get("user_id").get(0));

        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경험/활동/교육 입니다."));

        Resume resume = experience.getResume();

        if (!resume.getUserId().equals(userId)) throw new IllegalArgumentException("접근 권한이 없습니다.");

        experienceRepository.delete(experience);

        return "삭제완료";
    }


}
