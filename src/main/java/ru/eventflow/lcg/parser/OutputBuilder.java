package ru.eventflow.lcg.parser;

import ru.eventflow.lcg.category.Category;
import ru.eventflow.lcg.dto.*;
import ru.eventflow.lcg.frame.Edge;
import ru.eventflow.lcg.frame.Linkage;
import ru.eventflow.lcg.frame.Vertex;

import java.util.*;

public class OutputBuilder {

    public ParseDTO build(Sequent sequent, Set<Linkage> linkages, List<Vertex> order) {

        Map<Integer, Integer> indices = new HashMap<>();

        List<CategoryDTO> axioms = new ArrayList<>();
        int i = 0;
        for (Vertex v : order) {
            indices.put(v.getId(), i);
            CategoryDTO axiom = new CategoryDTO(i, v.getCategory().getSymbol(), v.getPolarity().getOrthography());
            axioms.add(axiom);
            i++;
        }

        String antecedent = sequent.getAntecedent().stream().map(Category::getSymbol).reduce((s, s2) -> s + "," + s2).orElse("");
        String succedent = sequent.getSuccedent().stream().map(Category::getSymbol).reduce((s, s2) -> s + "," + s2).orElse("");
        SequentDTO sequentDTO = new SequentDTO(antecedent, succedent);

        List<LinkageDTO> ls = new ArrayList<>();
        for (Linkage linkage : linkages) {

            List<Edge> orderedEdges = new ArrayList<>(linkage.getEdges());
            orderedEdges.sort(Comparator.comparingInt(value -> value.getSource().getId()));
            orderedEdges.sort(Comparator.comparingInt(value -> value.getTarget().getId()));

            List<LinkDTO> links = new ArrayList<>();
            for (Edge e : orderedEdges) {
                Vertex source = e.getSource();
                Vertex target = e.getTarget();
                links.add(new LinkDTO(indices.get(source.getId()), indices.get(target.getId()), e.getPartition().name().toLowerCase(), e.getType().name().toLowerCase()));
            }

            ls.add(new LinkageDTO(links));
        }

        return new ParseDTO(sequentDTO, axioms, ls);
    }

}
