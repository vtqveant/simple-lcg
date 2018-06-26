package ru.eventflow.lcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SequentDTO {

    @JsonProperty
    String antecedent;

    @JsonProperty
    String succedent;

    public SequentDTO(String antecedent, String succedent) {
        this.antecedent = antecedent;
        this.succedent = succedent;
    }

}
