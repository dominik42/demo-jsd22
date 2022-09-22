package de.hub28.jsd22.views.actor;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import de.hub28.jsd22.generated.Tables;
import de.hub28.jsd22.generated.tables.pojos.Actor;
import de.hub28.jsd22.views.MainLayout;
import lombok.RequiredArgsConstructor;

@Route(value = "/actors", layout = MainLayout.class)
@RequiredArgsConstructor
public class ActorListView extends VerticalLayout {


    private final DSLContext jooq;

    @PostConstruct
    public void createContent() {
        setHeightFull();
        add(new H1("Actors"));

        var grid = new Grid<Actor>();
        grid.addColumn(Actor::getFirstName).setHeader("Firstname");
        grid.addColumn(Actor::getLastName).setHeader("Lastname");
        grid.setItems(loadActors());
        //grid.setAllRowsVisible(true);
        grid.setHeight("10%");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);


        add(grid);
    }

    private List<Actor> loadActors() {
        return jooq.selectFrom(Tables.ACTOR).fetchInto(Actor.class);
    }
}