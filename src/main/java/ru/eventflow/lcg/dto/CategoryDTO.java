package ru.eventflow.lcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryDTO {
    @JsonProperty("i")
    private int index;

    @JsonProperty("c")
    private String category;

    @JsonProperty("p")
    private String polarity;

    public CategoryDTO() {
    }

    public CategoryDTO(int index, String category, String polarity) {
        this.index = index;
        this.category = category;
        this.polarity = polarity;
    }

    public int getIndex() {
        return index;
    }

    public String getCategory() {
        return category;
    }

    public String getPolarity() {
        return polarity;
    }
}
