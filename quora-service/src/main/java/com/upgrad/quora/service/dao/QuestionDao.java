package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

  @PersistenceContext private EntityManager entityManager;

  /**
   * Persist the question in the DB.
   *
   * @param questionEntity question to be persisted.
   * @return Persisted question.
   */
  public QuestionEntity createQuestion(QuestionEntity questionEntity) {
    entityManager.persist(questionEntity);
    return questionEntity;
  }

  /**
   * Fetch all the questions from the DB.
   *
   * @return List of QuestionEntity
   */
  public List<QuestionEntity> getAllQuestions() {
    return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
  }

  /**
   * Get the question for the given id.
   *
   * @param questionId id of the required question.
   * @return QuestionEntity if question with given id is found else null.
   */
  public QuestionEntity getQuestionById(final String questionId) {
    try {
      return entityManager
          .createNamedQuery("getQuestionById", QuestionEntity.class)
          .setParameter("uuid", questionId)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * Update the question
   *
   * @param questionEntity question entity to be updated.
   */
  public void updateQuestion(QuestionEntity questionEntity) {
    entityManager.merge(questionEntity);
  }

  /**
   * Delete the question
   *
   * @param questionEntity question entity to be deleted.
   */
  public void deleteQuestion(QuestionEntity questionEntity) {
    entityManager.remove(questionEntity);
  }

  /**
   * Fetch all the questions from the DB.
   *
   * @param userId userId of the user whose list of asked questions has to be retrieved
   * @return List of QuestionEntity
   */
  public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userId) {
    return entityManager
        .createNamedQuery("getQuestionByUser", QuestionEntity.class)
        .setParameter("user", userId)
        .getResultList();
  }
}
