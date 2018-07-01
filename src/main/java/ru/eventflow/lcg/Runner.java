package ru.eventflow.lcg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import ru.eventflow.lcg.dto.ParseDTO;
import ru.eventflow.lcg.parser.ChartLCGParser;
import ru.eventflow.lcg.parser.LCGParser;
import ru.eventflow.lcg.parser.Sequent;
import ru.eventflow.lcg.parser.SequentBuilder;

public class Runner {

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption("a", "antecedent", true, "an antecedent (a comma-separated list of syntactic categories");
        options.addOption("s", "succedent", true, "a succedent (a single syntactic category)");
        options.addOption("v", "verbose", false, "verbose");

        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("antecedent") && line.hasOption("succedent")) {

                boolean verbose = line.hasOption("verbose");
                String[] antecedent = line.getOptionValue("antecedent").split(",");
                String[] succedent = line.getOptionValue("succedent").split(",");

                LCGParser lcgParser = new ChartLCGParser(verbose);

                Sequent sequent = SequentBuilder.builder().setAntecedent(antecedent).setSuccedent(succedent).build();
                ParseDTO parseDTO = lcgParser.parse(sequent);
                print(parseDTO);
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("simple-lcg", options);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void print(ParseDTO parse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String s = mapper.writeValueAsString(parse);
            System.out.println(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
