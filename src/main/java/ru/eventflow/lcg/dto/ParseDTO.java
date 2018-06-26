package ru.eventflow.lcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ParseDTO {

    @JsonProperty
    private SequentDTO sequent;

    @JsonProperty("frame")
    private List<CategoryDTO> categories;

    @JsonProperty("parses")
    private List<LinkageDTO> parses;

    public ParseDTO(SequentDTO sequent, List<CategoryDTO> categories, List<LinkageDTO> parses) {
        this.sequent = sequent;
        this.categories = categories;
        this.parses = parses;
    }

    public SequentDTO getSequent() {
        return sequent;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public List<LinkageDTO> getParses() {
        return parses;
    }
}
