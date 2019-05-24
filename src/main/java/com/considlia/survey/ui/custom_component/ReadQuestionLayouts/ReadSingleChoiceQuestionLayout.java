package com.considlia.survey.ui.custom_component.ReadQuestionLayouts;

import com.considlia.survey.model.answer.Answers;
import com.considlia.survey.model.answer.ChosenAnswer;
import com.considlia.survey.model.answer.RadioAnswer;
import com.considlia.survey.model.question.MultiQuestion;
import com.considlia.survey.ui.custom_component.ReadQuestionComponent;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import java.util.ArrayList;
import java.util.List;

public class ReadSingleChoiceQuestionLayout extends ReadQuestionLayout implements
    ReadQuestionComponent {

  private RadioButtonGroup<String> radioButtons;
  private RadioAnswer singleChoiceAnswer;
  private Binder<RadioAnswer> binder;

  public ReadSingleChoiceQuestionLayout(MultiQuestion question) {
    super(question);
    radioButtons = new RadioButtonGroup<>();
    binder = new Binder<>(RadioAnswer.class);
    singleChoiceAnswer = new RadioAnswer();
    binder.setBean(singleChoiceAnswer);

    radioButtons.setItems(question.getStringAlternatives());
    binder.forField(radioButtons).bind(RadioAnswer::getChosenAnswer, RadioAnswer::setChosenAnswer);

    add(radioButtons);
    radioButtons.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
  }


  @Override
  public Answers gatherResponse() throws ValidationException {
    singleChoiceAnswer.setQuestion(getQuestion());
    getLOGGER().info("Logging question: '{}'", getQuestion());
    binder.writeBean(singleChoiceAnswer);
    getLOGGER().info("Logging answer: '{}'", singleChoiceAnswer);

    return singleChoiceAnswer;
  }
}