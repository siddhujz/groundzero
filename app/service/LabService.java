package service;
import com.fasterxml.jackson.databind.JsonNode;
import core.LabCore;
import core.RoleCore;
import core.UserCore;
import models.Lab;
import models.Role;
import models.User;
import models.UserLabRole;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.mailer.MailerClient;
import utils.Constants;
import utils.Hash;

import javax.inject.Inject;
import java.util.Date;
import java.util.UUID;
import java.util.Random;
/**
 * Created by aniketchitale7 on 3/27/17.
 */
public class LabService {

  public String addLabs(JPAApi jpaApi ,JsonNode json , String managerEmail){
    Lab lab = new Lab();
    UserLabRole userLabRole =  new UserLabRole();
//    lab.labName = json.findPath("lab_name").textValue();
//    lab.labPi = json.findPath("lab_pi").textValue();
    User user = UserCore.findByEmail(jpaApi , managerEmail);
    if(user == null)
    {
      user = new User();
      user.firstName = json.findPath("manager_fname").textValue();
      user.lastName =json.findPath("manager_lname").textValue();
      user.email = json.findPath("email").textValue();
      user.dateCreation = new Date();
      user.validated = false;
      char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
      StringBuilder sb = new StringBuilder();
      Random random = new Random();
      for (int i = 0; i < 10; i++) {
        char c = chars[random.nextInt(chars.length)];
        sb.append(c);
      }
      String output = sb.toString();
      try {
        user.passwordHash = Hash.createPassword(output);

      }
      catch(Exception e)
      {
        user.passwordHash ="";
      }
      user.confirmationToken = UUID.randomUUID().toString();
      user = UserCore.doRegister(jpaApi, user);
    }

    if(user == null) return Constants.REGISTRATION_FAILURE;

    lab.labName = json.findPath("lab_name").textValue();
    lab.labPi = json.findPath("pi_name").textValue();
    lab = LabCore.insert(jpaApi, lab);
    if(lab==null)
      return Constants.LAB_EXISTS;

    userLabRole.labId = lab;
    userLabRole.roleId = RoleCore.GetRole(jpaApi, 1);
    userLabRole.userId = user;
    userLabRole =  LabCore.insertRoleMapper(jpaApi, userLabRole);
    return Constants.REGISTRATION_SUCCESS;
  }
}