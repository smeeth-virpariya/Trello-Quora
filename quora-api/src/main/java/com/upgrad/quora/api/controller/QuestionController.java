package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

  @Autowired private QuestionService questionService;

  /**
   * Create a question
   *
   * @param questionRequest This object has the content i.e the question.
   * @param accessToken access token to authenticate user.
   * @return UUID of the question created in DB.
   * @throws AuthorizationFailedException In case the access token is invalid.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      path = "/question/create",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionResponse> createQuestion(
      @RequestHeader("authorization") final String accessToken, QuestionRequest questionRequest)
      throws AuthorizationFailedException {
    QuestionEntity questionEntity = new QuestionEntity();
    questionEntity.setContent(questionRequest.getContent());
    questionEntity = questionService.createQuestion(questionEntity, accessToken);
    QuestionResponse questionResponse = new QuestionResponse();
    questionResponse.setId(questionEntity.getUuid());
    questionResponse.setStatus("QUESTION CREATED");
    return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
  }

  /**
   * Get all questions posted by any user.
   *
   * @param accessToken access token to authenticate user.
   * @return List of QuestionDetailsResponse
   * @throws AuthorizationFailedException In case the access token is invalid.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      path = "/question/all",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException {
    List<QuestionEntity> questions = questionService.getAllQuestions(accessToken);
    List<QuestionDetailsResponse> questionDetailResponses = new ArrayList<>();
    for (QuestionEntity questionEntity : questions) {
      QuestionDetailsResponse questionDetailResponse = new QuestionDetailsResponse();
      questionDetailResponse.setId(questionEntity.getUuid());
      questionDetailResponse.setContent(questionEntity.getContent());
      questionDetailResponses.add(questionDetailResponse);
    }
    return new ResponseEntity<List<QuestionDetailsResponse>>(
        questionDetailResponses, HttpStatus.OK);
  }

  /**
   * Edit a question
   *
   * @param accessToken access token to authenticate user.
   * @param questionId id of the question to be edited.
   * @param questionEditRequest new content for the question.
   * @return Id and status of the question edited.
   * @throws AuthorizationFailedException In case the access token is invalid.
   * @throws InvalidQuestionException if question with questionId doesn't exist.
   */
  @RequestMapping(
      method = RequestMethod.PUT,
      path = "/question/edit/{questionId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionEditResponse> editQuestion(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("questionId") final String questionId,
      QuestionEditRequest questionEditRequest)
      throws AuthorizationFailedException, InvalidQuestionException {
    QuestionEntity questionEntity =
        questionService.editQuestion(accessToken, questionId, questionEditRequest.getContent());
    QuestionEditResponse questionEditResponse = new QuestionEditResponse();
    questionEditResponse.setId(questionEntity.getUuid());
    questionEditResponse.setStatus("QUESTION EDITED");
    return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
  }

  /**
   * Delete a question
   *
   * @param accessToken access token to authenticate user.
   * @param questionId id of the question to be edited.
   * @return Id and status of the question deleted.
   * @throws AuthorizationFailedException In case the access token is invalid.
   * @throws InvalidQuestionException if question with questionId doesn't exist.
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}")
  public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("questionId") final String questionId)
      throws AuthorizationFailedException, InvalidQuestionException {

    QuestionEntity questionEntity = questionService.deleteQuestion(accessToken, questionId);
    QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
    questionDeleteResponse.setId(questionEntity.getUuid());
    questionDeleteResponse.setStatus("QUESTION DELETED");
    return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
  }

  /**
   * Get all questions posted by a user with given userId.
   *
   * @param userId of the user for whom we want to see the questions asked by him
   * @param accessToken access token to authenticate user.
   * @return List of QuestionDetailsResponse
   * @throws AuthorizationFailedException In case the access token is invalid.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      path = "question/all/{userId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getQuestionByUserId(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("userId") String userId)
      throws AuthorizationFailedException, UserNotFoundException {

    List<QuestionEntity> questions = questionService.getAllQuestionsByUser(userId, accessToken);
    List<QuestionDetailsResponse> questionDetailResponses = new ArrayList<>();
    for (QuestionEntity questionEntity : questions) {
      QuestionDetailsResponse questionDetailResponse = new QuestionDetailsResponse();
      questionDetailResponse.setId(questionEntity.getUuid());
      questionDetailResponse.setContent(questionEntity.getContent());
      questionDetailResponses.add(questionDetailResponse);
    }
    return new ResponseEntity<List<QuestionDetailsResponse>>(
        questionDetailResponses, HttpStatus.OK);
  }
}
