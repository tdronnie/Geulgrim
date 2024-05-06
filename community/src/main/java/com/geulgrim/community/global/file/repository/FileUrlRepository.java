package com.geulgrim.community.global.file.repository;

import com.geulgrim.community.global.file.entity.FileUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUrlRepository extends JpaRepository<FileUrl, Long> {

    @Query("SELECT f FROM FileUrl f LEFT JOIN BoardImage b " +
            "WHERE b.board.boardId = :boardId " +
            "GROUP BY f.fileUrlId")
    List<FileUrl> findFileUrlByBoardId(long boardId);

    @Query("SELECT f FROM FileUrl f LEFT JOIN ShareImage s " +
            "WHERE s.share.shareId = :shareId " +
            "GROUP BY f.fileUrlId")
    List<FileUrl> findFileUrlByShareId(long shareId);

}