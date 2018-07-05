package ru.eventflow.lcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkDTO {

    @JsonProperty
    private int source;

    @JsonProperty
    private int target;

    @JsonProperty("p")
    private String partition;

    @JsonProperty("t")
    private String type;

    public LinkDTO() {
    }

    public LinkDTO(int source, int target, String partition, String type) {
        this.source = source;
        this.target = target;
        this.partition = partition;
        this.type = type;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    public String getPartition() {
        return partition;
    }

    public String getType() {
        return type;
    }

}
