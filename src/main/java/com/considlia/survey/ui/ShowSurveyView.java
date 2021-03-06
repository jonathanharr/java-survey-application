package com.considlia.survey.ui;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import com.considlia.survey.model.Survey;
import com.considlia.survey.model.SurveyResponse;
import com.considlia.survey.model.answer.Answer;
import com.considlia.survey.repositories.ResponseRepository;
import com.considlia.survey.repositories.SurveyRepository;
import com.considlia.survey.security.CustomUserService;
import com.considlia.survey.security.SecurityUtils;
import com.considlia.survey.ui.custom_component.ConfirmDialog;
import com.considlia.survey.ui.custom_component.ConfirmDialog.ConfirmDialogBuilder;
import com.considlia.survey.ui.custom_component.showsurveycomponents.ShowQuestionFactory;
import com.considlia.survey.ui.custom_component.showsurveycomponents.SurveyLoader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

/**
 * Designated View for loading Surveys. Takes a parameter being the Survey ID Each survey loaded
 * generates a set of components.
 *
 * <p>
 * To gather response, every component implements the interface ShowQuestionComponent, which has the
 * method GatherResponse. Call this method for each component when gathering all answers to the
 * Survey. Link: http://localhost:8080/showsurvey/1 written by: Jonathan Harr
 */
@Route(value = "showsurvey", layout = MainLayout.class)
public class ShowSurveyView extends BaseView implements HasUrlParameter<Long>, BeforeLeaveObserver {

  // -- Private Variables --
  // -- Containers --
  VerticalLayout headerVerticalLayout = new VerticalLayout();
  protected VerticalLayout surveyVerticalLayout = new VerticalLayout();

  private H1 h1;
  private H5 h5 = new H5();
  protected Button saveButton;
  private SurveyRepository surveyRepository;
  private ResponseRepository responseRepository;
  protected Survey survey;
  protected boolean containsMandatory = false;
  private LocalDateTime start = LocalDateTime.now();
  protected boolean isPreviewMode = false;
  protected ShowQuestionFactory showQuestionFactory;

  @Autowired
  protected CustomUserService customUserService;

  /**
   * Constructs view.
   *
   * @param surveyRepository for loading Surveys.
   * @param responseRepository for storing SurveyResponse.
   */
  public ShowSurveyView(SurveyRepository surveyRepository, ResponseRepository responseRepository) {
    this.surveyRepository = surveyRepository;
    this.responseRepository = responseRepository;
    SurveyLoader surveyLoader = new SurveyLoader();
    this.showQuestionFactory = surveyLoader;
    h1 = new H1("PlaceHolder // Survey Not Actually Found, Text not Updated");
    saveButton = new Button();
    saveButton.setText("Send");
  }

  /**
   * Initiates page GUI.
   */
  private void initUI() {

    headerVerticalLayout.setId("createheader");
    surveyVerticalLayout.setId("questionpackage");
    h1.setMinWidth("70%");

    headerVerticalLayout.add(h1, h5);

    if (containsMandatory) {
      Label mandatoryLabel = new Label("* = Mandatory question");
      add(headerVerticalLayout, mandatoryLabel, surveyVerticalLayout);
    } else {
      add(headerVerticalLayout, surveyVerticalLayout);
    }
    add(saveButton);
  }

  /**
   * Checks if page is meant to load a Survey, if parameter is null, sets text and button into
   * having not found a Survey.
   *
   * @param event initiates before load.
   * @param parameter ID of Survey, value determining event.
   */
  @Override
  public void setParameter(BeforeEvent event, Long parameter) {
    if (parameter != null) {
      if (surveyRepository.findById(parameter).isPresent()) {
        survey = surveyRepository.getSurveyById(parameter);
        h1.setText(survey.getTitle());
        h5.setText(survey.getDescription());
        loadSurvey();
      } else {
        add(new H5("ERROR, no Survey by the ID of: " + parameter + " exists."));
        goHome();
      }
    }
  }

  /**
   * Loads Questions all questions connected with Survey.
   */
  public void loadSurvey() {
    this.surveyVerticalLayout = showQuestionFactory.getSurveyLayout(survey);
    if (isPreviewMode) {
      if (customUserService.getUser().getId().equals(survey.getUser().getId())) {
        for (int i = 0; i < this.surveyVerticalLayout.getComponentCount(); i++) {
          Component componet = this.surveyVerticalLayout.getComponentAt(i);
          if (componet instanceof HasEnabled) {
            HasEnabled hasEnabled = (HasEnabled) componet;
            hasEnabled.setEnabled(false);
          }
        }
      } else {
        this.surveyVerticalLayout = new VerticalLayout();
        h1.setText("Restricted Access!");
        h5.setText(
            "It seems you have stumbled to a faulty URL. If you are looking to preview a survey, "
                + "please go to My Profile, and from there choose the Survey you wish to preview");
      }
    }
    saveButton.addClickListener(e -> {
      if (showQuestionFactory.isComplete().isConflict()) {
        saveResponse();
        navigateToSuccessView(ConfirmSuccessView.SURVEY_RESPONDED_STRING);
      } else {
        ConfirmDialog<SurveyResponse> confirmDialog =
            new ConfirmDialogBuilder<SurveyResponse>().with($ -> {
              $.addHeaderText("You aren't finished!");
              $.addMissingFieldsList(showQuestionFactory.isComplete());
              $.allFieldsCorrectlyFilledIn = showQuestionFactory.isComplete().isConflict();
              $.addSimpleCloseButton("Got it!");
            }).createConfirmDialog();
        confirmDialog.open();
      }
    });
    initUI();
  }

  /**
   * Creates a SurveyResponse and stores all Answer(s). For each readQuestionComponent inside
   * readQuestionList, gatherResponse() method is called storing each Answer into respective Bean,
   * connecting them to each Question of origin. Saves SurveyResponse to ResponseRepository.
   *
   * @throws ValidationException
   */
  public void saveResponse() {
    SurveyResponse surveyResponse = new SurveyResponse();
    surveyResponse.setTime(start.until(LocalDateTime.now(), ChronoUnit.SECONDS));

    surveyResponse.setAnswers((Set<Answer>) showQuestionFactory.getList());

    // If User is logged in, User is stored, else not.
    if (SecurityUtils.isUserLoggedIn()) {
      surveyResponse.setUser(customUserService.getUser());
    }
    // Connect SurveyResponse to Survey.
    surveyResponse.setSurvey(survey);
    responseRepository.save(surveyResponse);

    navigateToSuccessView(ConfirmSuccessView.SURVEY_RESPONDED_STRING);
  }

  /**
   * If no survey is loaded, saveButton changes function, offers a way back to homeview for User.
   */
  public void goHome() {
    saveButton.setText("Go To Mainview");
    saveButton.addClickListener(e -> navigateBackToHomeView());
    add(saveButton);
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    if (showQuestionFactory.isComplete().isHasChanges()) {
      ContinueNavigationAction continueNavigationAction = event.postpone();

      ConfirmDialog<Survey> confirmDialog = new ConfirmDialogBuilder<Survey>().with($ -> {
        $.action = continueNavigationAction;
        $.runnable = this::saveResponse;
        $.addHeaderText("You aren't finished!");
        $.addMissingFieldsList(showQuestionFactory.isComplete());
        $.allFieldsCorrectlyFilledIn = showQuestionFactory.isComplete().isConflict();
        $.addSaveDiscardCancelAlternatives();
      }).createConfirmDialog();
      confirmDialog.open();
    }
  }
}
