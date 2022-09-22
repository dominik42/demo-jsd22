package de.hub28.jsd22.views.actor;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.text.CaseUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudEditorPosition;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Route;

import de.hub28.jsd22.generated.Tables;
import de.hub28.jsd22.generated.tables.pojos.Actor;
import de.hub28.jsd22.generated.tables.records.ActorRecord;
import de.hub28.jsd22.views.MainLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Route(value = "/actorcrud", layout = MainLayout.class)
@RequiredArgsConstructor
@Slf4j
public class ActorCrudView extends VerticalLayout {


    private final DSLContext jooq;

    @PostConstruct
    public void createContent() {
        H1 h1 = new H1("Actor CRUD");

        Crud<ActorRecord> crud = new Crud<>(ActorRecord.class, createEditor());
        setupGrid(crud.getGrid());

        crud.setEditorPosition(CrudEditorPosition.OVERLAY);
        crud.setDataProvider(createDataProvider());

        crud.addSaveListener(e -> {
            save(e.getItem());
            crud.getDataProvider().refreshAll();
        });

        crud.addDeleteListener(e -> {
            delete(e.getItem());
            crud.getGrid().getDataProvider().refreshAll();
        });

        add(h1, crud);
    }

    private void setupGrid(Grid<ActorRecord> grid) {
        List<String> visibleColumns = Arrays.asList(
            CaseUtils.toCamelCase(Tables.ACTOR.FIRST_NAME.getName(), false, '_'),
            CaseUtils.toCamelCase(Tables.ACTOR.LAST_NAME.getName(), false, '_'),
            "vaadin-crud-edit-column");

        log.debug("set columns {}", visibleColumns.toString());

        grid.getColumns().forEach(column -> {
        String key = column.getKey();
        log.debug("check column {}", key);
        if (!visibleColumns.contains(key)) {
            grid.removeColumn(column);
        }
        });

    }

    private DataProvider<ActorRecord, CrudFilter> createDataProvider() {
        return DataProvider.fromFilteringCallbacks(query -> {
            CrudFilter filter = query.getFilter().orElse(null);
            if (filter != null) {
                log.debug(filter.getConstraints().toString());
            }
            return jooq.select(Tables.ACTOR.fields())
                .from(Tables.ACTOR)
                .orderBy(Tables.ACTOR.LAST_NAME)
                .limit(query.getLimit())
                .offset(query.getOffset())
                .fetchStreamInto(ActorRecord.class);
        }, query -> {
            return jooq.fetchCount(DSL.selectFrom(Tables.ACTOR));
        });
    }

    private void delete(ActorRecord item) {
        item.delete();
    }

    private void save(ActorRecord item) {
        if (item.getActorId() != null) {
            item.store();
        } else {
            var newItem = jooq.newRecord(Tables.ACTOR);
            newItem.from(item);
            newItem.store();
        }
    }

    private CrudEditor<ActorRecord> createEditor() {
        TextField firstname = new TextField("Vorname");
        TextField lastname = new TextField("Nachname");

        FormLayout form = new FormLayout(firstname, lastname);
        Binder<ActorRecord> binder = new Binder<>(ActorRecord.class);

        binder.forField(firstname)
            .asRequired()
            .bind(ActorRecord::getFirstName, ActorRecord::setFirstName);
        binder.forField(lastname)
            .asRequired()
            .bind(ActorRecord::getLastName, ActorRecord::setLastName);

        return new BinderCrudEditor<>(binder, form);
    }

}
