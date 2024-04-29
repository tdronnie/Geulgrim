package com.geulgrim.common.crew.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.geulgrim.common.crew.domain.entity.enums.BoardStatus;
import lombok.Getter;

@Getter
public class CrewBoardRequest {

    @JsonProperty("project_name")
    private String projectName;

    private String content;
    private Integer pen;
    private Integer color;
    private Integer bg;
    private Integer pd;
    private Integer story;
    private Integer conti;
    private BoardStatus status;

}
