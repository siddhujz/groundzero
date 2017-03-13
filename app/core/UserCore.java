package core;

import controllers.routes;
import models.User;
import models.UserLabRole;
import org.apache.commons.mail.EmailException;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;
import utils.Hash;

import javax.persistence.Query;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class UserCore {

  public static UserLabRole authenticate(JPAApi jpaApi, String email, String password) {
    Query q = jpaApi.em().createQuery("SELECT u FROM User u WHERE u.email = :email");
    q.setParameter("email", email);
    try {
      User user = (User) q.getSingleResult();
      if(user != null) {
        if (Hash.checkPassword(password, user.passwordHash)) {
          Query q1 = jpaApi.em().createQuery("SELECT r FROM UserLabRole r WHERE r.userId.id = :userId");
          q1.setParameter("userId", user.id);
          UserLabRole userLabRole = (UserLabRole) q1.getSingleResult();
          return  userLabRole;
        }
      }
      return null;
    } catch(Exception e){
      System.out.println("Exception e = " + e.getMessage());
      return null;
    }

  }

  public static User doRegister(JPAApi jpaApi, User user) {

    Query q = jpaApi.em().createQuery("SELECT u FROM User u WHERE u.email = :email");
    q.setParameter("email", user.email);
      try {
        ArrayList<User> users= (ArrayList<User>) q.getResultList();
        if(users.size() > 0)
            return null;
        jpaApi.em().persist(user);
        return user;
      } catch (Exception e) {
        Logger.error("Authorize.save error", e);
      }
      return null;

  }







  /**
   * Find user by confirmationToken
   */
  public static User findByConfirmationToken(JPAApi jpaApi, String confirmationToken) {
    Query q = jpaApi.em().createQuery("SELECT u FROM User u WHERE u.confirmationToken = :confirmationToken");
    q.setParameter("confirmationToken", confirmationToken);
    try {
      User user = (User) q.getSingleResult();
      if (user != null) {
        return user;
      }
    } catch(Exception e){
      System.out.println("Exception e = " + e.getMessage());
      return null;
    }
    return null;
  }

  /**
   * Find user by email
   */
  public static User findByEmail(JPAApi jpaApi, String email) {
    Query q = jpaApi.em().createQuery("SELECT u FROM User u WHERE u.email = :email");
    q.setParameter("email", email);
    try {
      User user = (User) q.getSingleResult();
      if (user != null) {
        return user;
      }
    } catch(Exception e){
      System.out.println("Exception e = " + e.getMessage());
      return null;
    }
    return null;
  }

  /**
   * Confirms an account.
   *
   * @return true if confirmed, false otherwise.
   * @throws Exception
   */
  public static boolean confirm(JPAApi jpaApi, User user) throws Exception {
    if (user == null) {
      return false;
    }

    user.confirmationToken = null;
    user.validated = true;
    jpaApi.em().persist(user);
    return true;
  }

}

