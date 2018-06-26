package ru.eventflow.lcg.frame;

import java.util.List;

public interface Frame {

    List<Vertex> getAxiomatic();

    Vertex getAxiom(int index);

    Linkage getLinkage();

    int getIndex(Vertex vertex);

}
