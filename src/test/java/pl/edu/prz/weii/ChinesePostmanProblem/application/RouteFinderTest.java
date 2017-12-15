package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.junit.Test;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Route;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;


public class RouteFinderTest {


    private FileContent fileContent = new FileContent(Arrays.asList(
            "6", "1 2 5 8", "1 3 6 9", "1 5 5 8", "2 3 3 4", "3 4 2 3", "4 5 4 5"
    ));

    @Test
    public void test() {
        RouteFinder routeFinder = new RouteFinder(fileContent);
        Route best = routeFinder.findBest();
        System.out.println(best);
        System.out.println(routeFinder.fitness(best));
        assertEquals(37.0, best.getWeight(), 0.0);
    }

    @Test
    public void test2() throws IOException {
        List<String> lines = Files.lines(Paths.get("data")).collect(Collectors.toList());
        RouteFinder routeFinder = new RouteFinder(new FileContent(lines));
        Route best = routeFinder.findBest();
        System.out.println(best);
        System.out.println(routeFinder.fitness(best));
        assertEquals(1225.0, best.getWeight(), 0.0);
    }

}