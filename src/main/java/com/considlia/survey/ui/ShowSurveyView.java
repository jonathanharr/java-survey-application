package com.considlia.survey.ui;

import com.considlia.survey.custom_component.ReadMultiQuestionLayout;
import com.considlia.survey.custom_component.ReadTextQuestionLayout;
import com.considlia.survey.model.MultiQuestion;
import com.considlia.survey.model.Question;
import com.considlia.survey.model.Survey;
import com.considlia.survey.repositories.SurveyRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

/*
 * http://localhost:8080/showsurvey/1
 */
@StyleSheet("css/app.css")
@Route(value = "showsurvey", layout = MainLayout.class)
public class ShowSurveyView extends VerticalLayout implements HasUrlParameter<Long> {

  // -- Private Variables --
  // -- Containers --
  private VerticalLayout mainVerticalLayout = new VerticalLayout();
  private VerticalLayout headerVerticalLayout = new VerticalLayout();
  private VerticalLayout surveyVerticalLayout = new VerticalLayout();

  // -- TextContainers --
  private H1 h1;

  // -- Buttons --
  private Button saveButton;

  // -- Serializement --
  private SurveyRepository surveyRepository;
  private Binder<Question> binder = new Binder<>(Question.class);

  private Survey survey;

  // -- Layout --
  public ShowSurveyView(SurveyRepository surveyRepository) {
    // Using same ID as CreateSurveyView as of now.
    setId("createsurvey");
    this.surveyRepository = surveyRepository;
    this.h1 = new H1("PlaceHolder // Survey Not Actually Found, Text not Updated");
    this.saveButton = new Button();
    saveButton.setText("Send");
    saveButton.addClickListener(e -> saveResponse());

    initUI();
  }

  // -- UI method, adding, etc.
  private void initUI() {

    mainVerticalLayout.setMinWidth("60%");
    mainVerticalLayout.setMaxWidth("80%");

    headerVerticalLayout.setClassName("createheader");
    surveyVerticalLayout.setClassName("questionpackage");

    headerVerticalLayout.add(h1);
    mainVerticalLayout.add(headerVerticalLayout);
    mainVerticalLayout.add(surveyVerticalLayout);
    add(mainVerticalLayout);
  }

  // -- Data methods --
  // -- Parameter Method, finding Survey --
  @Override
  public void setParameter(BeforeEvent event, Long parameter) {
    try {
      if (parameter == null) {
        // Null Pointer Exception? (Survey Null Pointer Exception)
        h1.setText("ERROR, no survey ID parameter given.");

        goHome();
      }
      if (surveyRepository.findById(parameter).isPresent()) {
        survey = surveyRepository.getSurveyById(parameter);
        h1.setText(survey.getTitle());

        loadSurvey(survey);
      } else {
        // Or Create SurveyNotFoundException
        h1.setText("ERROR, no Survey by the ID of: " + parameter + " exists.");

        goHome();
      }
    } catch (NullPointerException e) {
      h1.setText(e.getMessage());
    }
  }

  // -- Loading Survey to Layout
  public void loadSurvey(Survey survey) {
    // Should work outside of constructor as well, then methods outside with save etc should work
    // perfectly fine.
    // Binder<MultiQuestionAlternative> binderAlternatives =
    // new Binder<>(MultiQuestionAlternative.class);

    // for(Question q : surveyRepository.findByQuestionOrderPosition(survey, 0)){
    for (Question q : survey.getQuestions()) {
      if (q instanceof MultiQuestion) {
        MultiQuestion mq = (MultiQuestion) q;
        // surveyVerticalLayout.add(new H2(q.getQuestionTitle()));
        HorizontalLayout horLayout = new HorizontalLayout();
        // CheckboxGroup questionCheckBoxGroup = new CheckboxGroup();
        //
        // if (mq.getQuestionType() == 2) {
        // CheckboxGroup<MultiQuestionAlternative> multiQuestionCheckboxes = new CheckboxGroup<>();
        // multiQuestionCheckboxes.setItems(mq.getAlternativeList());
        // VerticalLayout checkBoxVertContainer = new VerticalLayout();
        // checkBoxVertContainer.add(multiQuestionCheckboxes);
        //
        // horLayout.add(checkBoxVertContainer);
        //
        // // else, if we want radiobutton
        // } else if (mq.getQuestionType() == 1) {
        //
        // RadioButtonGroup<MultiQuestionAlternative> multiQuestionRadioButtons =
        // new RadioButtonGroup<>();
        // multiQuestionRadioButtons.setItems(mq.getAlternativeList());
        // VerticalLayout checkBoxVertContainer = new VerticalLayout();
        // checkBoxVertContainer.add(multiQuestionRadioButtons);
        //
        // horLayout.add(checkBoxVertContainer);
        //
        // }
        // mainVerticalLayout.add(horLayout);

        ReadMultiQuestionLayout readMultiQuestionLayout = new ReadMultiQuestionLayout(mq);
        horLayout.add(readMultiQuestionLayout);
        surveyVerticalLayout.add(horLayout);
      } else {
        ReadTextQuestionLayout readTextQuestionLayout = new ReadTextQuestionLayout(q);

        // binder.forField(textField2).bind(Question::getQuestionTitle, Question::setQuestionTitle);

        // binder.readBean(q);
        surveyVerticalLayout.add(readTextQuestionLayout);
      }
    }

    surveyVerticalLayout.add(saveButton);

    // headerVerticalLayout.add(h1);
    // mainVerticalLayout.add(headerVerticalLayout);
    // mainVerticalLayout.add(surveyVerticalLayout);
    // add(mainVerticalLayout);
  }

  // -- Public Button Methods --
  public void saveResponse() {
    saveButton.setText("Survey Response cannot be saved yet!");
  }

  public void goHome() {
    saveButton.setText("Go To Mainview");
    saveButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));
    add(saveButton);
  }
}
