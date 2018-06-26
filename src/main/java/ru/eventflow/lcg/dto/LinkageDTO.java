package ru.eventflow.lcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LinkageDTO {
    @JsonProperty("l")
    List<LinkDTO> links;

    public LinkageDTO(List<LinkDTO> links) {
        this.links = links;
    }
}