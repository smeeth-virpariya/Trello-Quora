package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

  @Autowired private UserAuthDao userAuthDao;

  @Autowired private UserDao userDao;

  @Autowired private QuestionDao questionDao;

  /**
   * Creates question in the DB if the accessToken is valid.
   *
   * @param accessToken accessToken of the user for valid authentication.
   * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if
   *     the user has already signed out.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity createQuestion(QuestionEntity questionEntity, final String accessToken)
      throws AuthorizationFailedException {
    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to post a question");
    }
    questionEntity.setDate(ZonedDateTime.now());
    questionEntity.setUuid(UUID.randomUUID().toString());
    questionEntity.setUserEntity(userAuthEntity.getUserEntity());
    return questionDao.createQuestion(questionEntity);
  }

  /**
   * Gets all the questions in the DB.
   *
   * @param accessToken accessToken of the user for valid authentication.
   * @return List of QuestionEntity
   * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if
   *     the user has already signed out.
   */
  public List<QuestionEntity> getAllQuestions(final String accessToken)
      throws AuthorizationFailedException {
    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to get all questions");
    }
    return questionDao.getAllQuestions();
  }

  /**
   * * Edit the question
   *
   * @param accessToken accessToken of the user for valid authentication.
   * @param questionId id of the question to be edited.
   * @param content new content for the existing question.
   * @return QuestionEntity
   * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if
   *     the user has already signed out.
   * @throws InvalidQuestionException if the question with id doesn't exist.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity editQuestion(
      final String accessToken, final String questionId, final String content)
      throws AuthorizationFailedException, InvalidQuestionException {
    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to edit the question");
    }
    QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
    if (questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }
    if (!questionEntity
        .getUserEntity()
        .getUuid()
        .equals(userAuthEntity.getUserEntity().getUuid())) {
      throw new AuthorizationFailedException(
          "ATHR-003", "Only the question owner can edit the question");
    }
    questionEntity.setContent(content);
    questionDao.updateQuestion(questionEntity);
    return questionEntity;
  }

  /**
   * * Delete the question
   *
   * @param accessToken accessToken of the user for valid authentication.
   * @param questionId id of the question to be edited.
   * @return QuestionEntity
   * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if
   *     the user has already signed out.
   * @throws InvalidQuestionException if the question with id doesn't exist.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity deleteQuestion(final String accessToken, final String questionId)
      throws AuthorizationFailedException, InvalidQuestionException {
    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to delete the question");
    }
    QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
    if (questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }
    if (!questionEntity.getUserEntity().getUuid().equals(userAuthEntity.getUserEntity().getUuid())
        && !userAuthEntity.getUserEntity().getRole().equals("admin")) {
      throw new AuthorizationFailedException(
          "ATHR-003", "Only the question owner or admin can delete the question");
    }

    questionDao.deleteQuestion(questionEntity);
    return questionEntity;
  }

  /**
   * Gets all the questions posted by a specific user.
   *
   * @param userId userId of the user whose posted questions have to be retrieved
   * @param accessToken accessToken of the user for valid authentication.
   * @return List of QuestionEntity
   * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if
   *     the user has already signed out.
   */
  public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String accessToken)
      throws AuthorizationFailedException, UserNotFoundException {
    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002",
          "User is signed out.Sign in first to get all questions posted by a specific user");
    }
    UserEntity user = userDao.getUserById(userId);
    if (user == null) {
      throw new UserNotFoundException(
          "USR-001", "User with entered uuid whose question details are to be seen does not exist");
    }
    return questionDao.getAllQuestionsByUser(user);
  }
}
