package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

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
     * @return List of QuestionEntity
     */
    public List<QuestionEntity> getAllQuestions() {
        return entityManager.createNamedQuery("getAllQuestions").getResultList();
    }
}
