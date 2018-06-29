package ru.eventflow.lcg.parser;

import ru.eventflow.lcg.dto.ParseDTO;

public interface LCGParser {
    ParseDTO parse(Sequent sequent);
}
