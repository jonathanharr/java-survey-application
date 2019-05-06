package com.considlia.survey.custom_component;

import com.considlia.survey.ui.CreateSurveyView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@StyleSheet("css/app.css")
public class TextQuestionWithButtons extends HorizontalLayout {

  private static final int MOVE_UP = -1;
  private static final int MOVE_DOWN = 1;

  private String question;
  private H5 title;

  public TextQuestionWithButtons(String question, CreateSurveyView survey) {
    setId("textcustom");

    this.question = question;

    setWidth("100%");
    title = new H5(question);
    title.setHeightFull();
    title.setWidth("90%");

    add(title);
    add(new Button(new Icon(VaadinIcon.ARROW_UP),
        event -> survey.moveQuestion(event.getSource(), MOVE_UP)));
    add(new Button(new Icon(VaadinIcon.ARROW_DOWN),
        event -> survey.moveQuestion(event.getSource(), MOVE_DOWN)));
    add(new Button(new Icon(VaadinIcon.PENCIL), event -> survey.editQuestion(event.getSource())));
    add(new Button(new Icon(VaadinIcon.TRASH), event -> survey.removeQuestion(event.getSource())));

  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;

    H5 updatedTitle = new H5(question);
    updatedTitle.setWidth("90%");

    replace(title, updatedTitle);
  }

}