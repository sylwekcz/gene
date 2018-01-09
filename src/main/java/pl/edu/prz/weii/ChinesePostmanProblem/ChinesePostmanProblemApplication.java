package pl.edu.prz.weii.ChinesePostmanProblem;

import pl.edu.prz.weii.ChinesePostmanProblem.application.RouteFinder;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ChinesePostmanProblemApplication {

    public static void main(String[] args) throws IOException {

        List<String> lines = Files.lines(Paths.get(args[0])).collect(Collectors.toList());
        FileContent fileContent = new FileContent(lines);
        RouteFinder routeFinder = new RouteFinder(fileContent, 10000,
                10, 10, 1000, 0.1, 0.1, true);
        Route best = routeFinder.findBest();
        System.out.println(best);
        System.out.println(routeFinder.fitness(best));

    }
}
