package pl.edu.prz.weii.ChinesePostmanProblem.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.dnd.FileDropTarget;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.file.FileToFileContent;
import pl.edu.prz.weii.ChinesePostmanProblem.domain.graph.Edge;

import java.util.ArrayList;
import java.util.Collection;


@SpringUI
@Theme("valo")
public class MainUI extends UI {


    @Override
    protected void init(VaadinRequest request) {

        Label dropArea = new Label("Drop file here");
        dropArea.setHeight("100%");
        dropArea.setWidth("100%");

        Grid<Edge> grid = new Grid<>();
        grid.setItems(new ArrayList<>());
        grid.addColumn(Edge::getNodeA).setCaption("Node A");
        grid.addColumn(Edge::getNodeB).setCaption("Node B");
        grid.addColumn(Edge::getWeightFromAToB).setCaption("A to B weight");
        grid.addColumn(Edge::getWeightFromBToA).setCaption("B to A weight");
        grid.setHeight("100%");
        grid.setWidth("100%");

        Panel panel = new Panel("Chinese Postman Problem");
        panel.setHeight("100%");
        VerticalLayout vertical = new VerticalLayout();
        vertical.setHeight("100%");
        vertical.addComponent(dropArea);
        vertical.setComponentAlignment(dropArea, Alignment.MIDDLE_CENTER);

        FileDropTarget<Label> dropTarget = new FileDropTarget<>(dropArea, event -> {
            Collection<Html5File> files = event.getFiles();
            files.forEach(file -> {
                file.setStreamVariable(new FileToFileContent() {
                    @Override
                    public void process(FileContent fileContent) {
                        vertical.removeAllComponents();
                        grid.setItems(fileContent.getEdges());
                        vertical.addComponent(grid);

                    }
                });
            });
        });




        panel.setContent(vertical);
        setContent(panel);
    }
}
