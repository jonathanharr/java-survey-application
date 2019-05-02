package com.considLia.survey.ui;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.considLia.survey.custom_component.ConfirmDialog;
import com.considLia.survey.model.Survey;
import com.considLia.survey.repositories.SurveyRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

  private Grid<Survey> grid;
  private List<Survey> surveyList;
  private ListDataProvider<Survey> dataProvider;
  private TextField idField, titleField, creatorField, dateField;
  private SurveyRepository surveyRepository;

  public MainView(SurveyRepository surveyRepository) {
    this.surveyRepository = surveyRepository;

    surveyList = new ArrayList<>();

    // Hämtar lista från Databasen
    surveyList = surveyRepository.findAll();

    grid = new Grid<>();
    Grid.Column<Survey> idColumn =
        grid.addColumn(Survey::getSurveyId).setHeader("Id").setWidth("1px");
    Grid.Column<Survey> titleColumn =
        grid.addColumn(Survey::getSurveyTitle).setHeader("Title").setFlexGrow(4);
    Grid.Column<Survey> creatorColumn = grid.addColumn(Survey::getCreator).setHeader("Creator");
    Grid.Column<Survey> dateColumng = grid.addColumn(Survey::getDate).setHeader("Date");
    grid.addComponentColumn(item -> showButtons(grid, item));

    if (surveyList.isEmpty()) {
      grid.setHeight("80px");
    }
    grid.setItems(surveyList);
    add(grid);

    dataProvider = new ListDataProvider<>(surveyList);
    grid.setDataProvider(dataProvider);

    HeaderRow filterRow = grid.appendHeaderRow();

    // ID filter
    idField = new TextField();
    // idField.addValueChangeListener(e -> search(idField.getValue(), "idField"));
    idField.addValueChangeListener(e -> idSearcher(idField.getValue()));
    idField.setValueChangeMode(ValueChangeMode.EAGER);
    filterRow.getCell(idColumn).setComponent(idField);
    idField.setPlaceholder("Filter");

    // Title filter
    titleField = new TextField();
    // titleField.addValueChangeListener(e -> search(titleField.getValue(), "titleField"));
    titleField.addValueChangeListener(e -> titleSearcher(titleField.getValue()));
    titleField.setValueChangeMode(ValueChangeMode.EAGER);
    filterRow.getCell(titleColumn).setComponent(titleField);
    titleField.setPlaceholder("Filter");

    // Creator filter
    creatorField = new TextField();
    // creatorField.addValueChangeListener(e -> search(creatorField.getValue(), "creatorField"));
    creatorField.addValueChangeListener(e -> creatorSearcher(creatorField.getValue()));
    creatorField.setValueChangeMode(ValueChangeMode.EAGER);
    filterRow.getCell(creatorColumn).setComponent(creatorField);
    creatorField.setPlaceholder("Filter");

    // Date filter
    dateField = new TextField();
    dateField.addValueChangeListener(e -> dateSearcher(dateField.getValue()));
    dateField.setValueChangeMode(ValueChangeMode.EAGER);
    filterRow.getCell(dateColumng).setComponent(dateField);
    dateField.setPlaceholder("Filter");

  }

  private void idSearcher(String fromFilter) {
    if (!fromFilter.equalsIgnoreCase(null) && !fromFilter.equalsIgnoreCase("")) {
      titleField.clear();
      creatorField.clear();
      dateField.clear();
      List<Survey> filteredList = new ArrayList<Survey>();
      for (Survey s : surveyList) {
        if (Long.parseLong(fromFilter) == s.getSurveyId()) {
          filteredList.add(s);
        }
        grid.setItems(filteredList);
      }
    } else {
      grid.setItems(surveyList);
    }
  }

  private void titleSearcher(String fromFilter) {
    if (!fromFilter.equalsIgnoreCase(null) && !fromFilter.equalsIgnoreCase("")) {
      idField.clear();
      creatorField.clear();
      dateField.clear();
      List<Survey> filteredList = new ArrayList<Survey>();
      for (Survey s : surveyList) {
        if (s.getSurveyTitle().toUpperCase().contains(fromFilter.toUpperCase())) {
          filteredList.add(s);
        }
        grid.setItems(filteredList);
      }
    } else {
      grid.setItems(surveyList);
    }
  }

  private void creatorSearcher(String fromFilter) {
    if (!fromFilter.equalsIgnoreCase(null) && !fromFilter.equalsIgnoreCase("")) {
      idField.clear();
      titleField.clear();
      dateField.clear();
      List<Survey> filteredList = new ArrayList<Survey>();
      for (Survey s : surveyList) {
        if (s.getCreator().toUpperCase().contains(fromFilter.toUpperCase())) {
          filteredList.add(s);
        }
        grid.setItems(filteredList);
      }
    } else {
      grid.setItems(surveyList);
    }
  }

  private void dateSearcher(String fromFilter) {
    if (!fromFilter.equalsIgnoreCase(null) && !fromFilter.equalsIgnoreCase("")) {
      idField.clear();
      titleField.clear();
      creatorField.clear();
      List<Survey> filteredList = new ArrayList<Survey>();
      for (Survey s : surveyList) {
        String datePattern = "yyyy-MM-dd";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        String dateString = dateFormatter.format(s.getDate());
        if (dateString.contains(fromFilter)) {
          filteredList.add(s);
        }
        grid.setItems(filteredList);
      }
    } else {
      grid.setItems(surveyList);
    }
  }

  public HorizontalLayout showButtons(Grid<Survey> grid, Survey item) {
    Button showSurvey = new Button(new Icon(VaadinIcon.EYE), openSurvey -> {
        // Navigate to ShowSurveyView, Id being parameter.
        getUI().ifPresent(ui -> ui.navigate(ShowSurveyView.class, item.getSurveyId()));
    });
    Button editSurvey = new Button(new Icon(VaadinIcon.PENCIL));
    Button deleteSurvey = new Button(new Icon(VaadinIcon.TRASH), onDelete -> {
      ConfirmDialog confirmDialog = new ConfirmDialog("Confirm delete",
          "Are you sure you want to delete the item?", surveyRepository, grid, item);
    });

    return new HorizontalLayout(showSurvey, editSurvey, deleteSurvey);
  }
}
