package pl.edu.prz.weii.ChinesePostmanProblem.application;

import org.junit.Test;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;


public class RouteFinderTest {




    private FileContent fileContent = new FileContent(Arrays.asList(
            "3", "1 2 4 2", "2 3 3 4", "3 1 2 3"
    ));

    @Test
    public void getAsEdges() throws Exception {
        Set<Edge> edges = fileContent.getEdges();
        Set<Integer> nodes = fileContent.getNodes();
        Route route = new Route(edges, Arrays.asList(1,2,3,1));
        List<Edge> asEdges = route.getAsEdges();
        assertEquals(3, asEdges.size());
    }

    @Test
    public void test() {
        RouteFinder routeFinder = new RouteFinder(fileContent);
        Route best = routeFinder.findBest();
        System.out.println(best.getAsEdges());
        System.out.println(best.getVisitedNodes());
        System.out.println(best.getWeight());
        assertEquals(9.0, best.getWeight(), 0.0);
    }

}