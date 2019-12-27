package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetch a single user by given id from the DB.
     *
     * @param userId Id of the user whose information is to be fetched.
     * @return User details if exist in the DB else null.
     */
    public UserEntity getUserById(final String userId) {
        try {
            return entityManager.createNamedQuery("userByUserId", UserEntity.class).setParameter("userId", userId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Delete a user by given id from the DB.
     *
     * @param userId Id of the user whose information is to be fetched.
     * @return User details which is to be deleted if exist in the DB else null.
     */

    public UserEntity deleteUser(final String userId){
        try {
            UserEntity deleteUser = this.entityManager.createNamedQuery( "userByUserId", UserEntity.class ).
                    setParameter( "userId", userId )
                    .getSingleResult();
            this.entityManager.remove( deleteUser );
            return deleteUser;
        } catch (NoResultException nre){
            return null;
        }

    }
}
