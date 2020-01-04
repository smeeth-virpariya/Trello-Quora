package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthDao {

  @PersistenceContext private EntityManager entityManager;

  /**
   * Gets the user auth information based on the access token.
   *
   * @param accessToken access token of the user auth whose details is to be fetched.
   * @return A single user auth object or null
   */
  public UserAuthEntity getUserAuthByToken(final String accessToken) {
    try {
      return entityManager
          .createNamedQuery("userAuthByAccessToken", UserAuthEntity.class)
          .setParameter("accessToken", accessToken)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * Persist UserAuthEntity object in DB.
   *
   * @param userAuthEntity to be persisted in the DB.
   * @return Persisted UserAuthEntity object
   */
  public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity) {
    entityManager.persist(userAuthEntity);
    return userAuthEntity;
  }

  /**
   * Updates the UserAuthEntity object present in the DB.
   *
   * @param updatedUserAuthEntity Persisted UserAuthEntity object
   */
  public void updateUserAuth(final UserAuthEntity updatedUserAuthEntity) {
    entityManager.merge(updatedUserAuthEntity);
  }
}
