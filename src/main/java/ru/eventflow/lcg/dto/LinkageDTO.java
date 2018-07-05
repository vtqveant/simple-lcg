package ru.eventflow.lcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LinkageDTO {
    @JsonProperty("l")
    private List<LinkDTO> links;

    public LinkageDTO() {
    }

    public LinkageDTO(List<LinkDTO> links) {
        this.links = links;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }
}