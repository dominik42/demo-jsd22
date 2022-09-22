package de.hub28.jsd22.views.film;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import de.hub28.jsd22.generated.Tables;
import de.hub28.jsd22.generated.tables.pojos.Film;
import de.hub28.jsd22.views.MainLayout;
import lombok.RequiredArgsConstructor;

@Route(value = "/films", layout = MainLayout.class)
@RequiredArgsConstructor
public class FilmListView extends VerticalLayout {

    private final DSLContext jooq;

    @PostConstruct
    public void createContent() {
        setHeightFull();
        add(new H1("Films"));

        var grid = new Grid<Film>();
        grid.addColumn(Film::getTitle)
            .setHeader("Title")
            .setAutoWidth(true)
            .setFlexGrow(0)
            .setResizable(true)
            .setSortable(true);

        grid.addColumn(Film::getDescription).setHeader("Description");
        grid.addColumn(Film::getReleaseYear)
            .setHeader("released");


        grid.setItems(loadFilms());
        grid.setHeight("400px");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS);


        add(grid);
    }

    private List<Film> loadFilms() {
        return jooq.selectFrom(Tables.FILM).fetchInto(Film.class);
    }

}