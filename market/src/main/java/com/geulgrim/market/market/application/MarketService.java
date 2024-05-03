package com.geulgrim.market.market.application;

import com.geulgrim.market.commonserver.piece.application.PieceService;
import com.geulgrim.market.global.s3.S3UploadService;
import com.geulgrim.market.market.application.dto.request.MarketCreateRequestDto;
import com.geulgrim.market.market.application.dto.request.MarketUpdateRequestDto;
import com.geulgrim.market.market.application.dto.request.ThumbnailUploadDto;
import com.geulgrim.market.market.application.dto.response.MarketResponseDto;
import com.geulgrim.market.market.domain.Market;
import com.geulgrim.market.market.domain.MarketLog;
import com.geulgrim.market.market.domain.SearchAndOrderType;
import com.geulgrim.market.market.domain.SearchType;
import com.geulgrim.market.market.domain.repository.MarketRepository;
import com.geulgrim.market.market.exception.NoMarketExistException;
import com.geulgrim.market.market.exception.NotSupportSuchTypeException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MarketService {

    private final MarketRepository marketRepository;

    private final S3UploadService s3UploadService;

//    private final PieceFeignClient pieceFeignClient;

//    private final PieceService pieceService;

    public Long create(MultipartFile image, MarketCreateRequestDto dto) throws IOException {
        Market market = dto.toEntity();
        if (image != null && !image.isEmpty()) {
            uploadImage(market, image);
        }
        marketRepository.save(market);
        return market.getId();
    }

    public List<MarketResponseDto> searchAndOrderMarkets(String type, String keyword, boolean isOrderViewCount) {

        List<Market> markets = null;
        log.info("type ={}", type);
        log.info("keyword ={}", keyword);

        if (type != null && keyword != null) {
            try {
                //조회수 정렬 체크 된 경우
                if (isOrderViewCount) {
                    SearchAndOrderType searchAndOrderType = SearchAndOrderType.valueOf(type);
                    markets = searchAndOrderType.getListBySearchTypeAndOrder(marketRepository, keyword);

                } else { //조회수 체크 되지 않은 경우
                    SearchType searchType = SearchType.valueOf(type);
                    markets = searchType.getListBySearchType(marketRepository, keyword);

                }
            } catch (IllegalArgumentException e) {
                throw new NotSupportSuchTypeException();
            }
        } else {
            if (isOrderViewCount) {
                markets = marketRepository.findAllByOrderByViewCountDesc();
            } else {
                markets = marketRepository.findAll();
            }
        }
        return markets.stream().map(MarketResponseDto::from).toList();
    }

    public MarketResponseDto detail(Long id) {
        Market market = marketRepository.findById(id).orElseThrow(NoMarketExistException::new);
        return MarketResponseDto.from(market);
    }

//    로그인 유저의 판매 게시글 조회, 이후 요청 시 넘어오는 로그인 유저 정보 활용
//    public List<MarketResponseDto> findAllByUserId(Long userId) {
//        User user = userFeignClient.findById(userId);
//        List<Market> markets = marketRepository.findAllBySellerId(user.getId());
//        return markets.stream()
//                .map(MarketResponseDto::from)
//                .toList();
//    }

    public MarketResponseDto update(Long id, MultipartFile image, MarketUpdateRequestDto dto) throws IOException {
//        checkIsWriter(dto.getId()); //로그인한 유저가 작성자인지 확인 필요
        Market market = marketRepository.findById(id).orElseThrow(NoMarketExistException::new);
        if (image != null && !image.isEmpty()) {
            uploadImage(market, image);
        }
        market.updateMarket(dto);
        return MarketResponseDto.from(market);
    }

    public Market findById(Long id) {
        return marketRepository.findById(id).orElseThrow(NoMarketExistException::new);
    }

//    유레카 연결되면 piece 가져오기
//    public PieceResponseDto findPieceFromPieceService(Long pieceId) {
//        return pieceService.findPieceByIdFromCommon(pieceId);
//    }

    public List<MarketLog> findMarketLogByPieceId(Long pieceId) {
        return marketRepository.findMarketLogsByPieceId(pieceId);
    }


    public void uploadImage(Market market, MultipartFile image) throws IOException {
        String s3Url = s3UploadService.saveFile(image);
        market.uploadThumbnail(s3Url);
    }

}
