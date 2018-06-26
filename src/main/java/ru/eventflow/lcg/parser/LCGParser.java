package ru.eventflow.lcg.parser;

import ru.eventflow.lcg.dto.ParseDTO;
import ru.eventflow.lcg.sequent.Sequent;

public interface LCGParser {
    ParseDTO parse(Sequent sequent);
}
