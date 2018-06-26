package ru.eventflow.lcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryDTO {
    @JsonProperty("i")
    int index;

    @JsonProperty("c")
    String category;

    @JsonProperty("p")
    String polarity;

    public CategoryDTO(int index, String category, String polarity) {
        this.index = index;
        this.category = category;
        this.polarity = polarity;
    }
}
