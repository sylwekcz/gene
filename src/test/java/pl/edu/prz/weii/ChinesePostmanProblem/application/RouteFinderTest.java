package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.junit.Test;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class RouteFinderTest {


    private FileContent fileContent = new FileContent(Arrays.asList(
            "6", "1 2 5 8", "1 3 6 9", "1 5 5 8", "2 3 3 4", "3 4 2 3", "4 5 4 5"
    ));

    @Test
    public void test() {
        RouteFinder routeFinder = new RouteFinder(fileContent);
        Route best = routeFinder.findBest();
        System.out.println(best.getAsEdges());
        System.out.println(best.getVisitedNodes());
        System.out.println(best.getWeight());
        assertEquals(37.0, best.getWeight(), 0.0);
    }

}