package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User getUser(long id) {
    return this.userRepository.getById(id);
  }

  public User createUser(User newUser) {
    Date createTime = new Date();
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
    newUser.setCreateTime(createTime);

    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    newUser.setStatus(UserStatus.ONLINE);
    return newUser;
  }

  public User loginUser(User inputUser) {
    User userByUsername = checkIfUserRegistered(inputUser);

    String password = inputUser.getPassword();
    String dbPassword = userByUsername.getPassword();

    String passwordErrorMessage = "Your password is incorrect.";

    if(!password.equals(dbPassword)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format(passwordErrorMessage));
    }else{
        userByUsername.setStatus(UserStatus.ONLINE);
        return userByUsername;
    }
  }

  public User logoutUser(long id) {
    User userById = userRepository.getById(id);
    userById.setStatus(UserStatus.OFFLINE);

    return userById;
  }

    public User editUser(User inputUser, long id) {
      User userById = userRepository.getById(id);

      if(inputUser.getBirthDate()!=null & inputUser.getUsername()==null) {
          userById.setBirthDate(inputUser.getBirthDate());
      }else if(inputUser.getUsername()!=null & inputUser.getBirthDate()==null) {
          checkIfNameUnique(inputUser);
          userById.setUsername(inputUser.getUsername());
      }else{
          checkIfNameUnique(inputUser);
          userById.setUsername(inputUser.getUsername());
          userById.setBirthDate(inputUser.getBirthDate());
      }
      userById = userRepository.save(userById);
      return userById;
    }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    }
  }

  private void checkIfNameUnique(User userToBeEdited) {
    User userByUsername = userRepository.findByUsername(userToBeEdited.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Please choose a new username!";
    if (userByUsername != null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    }
  }
  private User checkIfUserRegistered(User userToBeLoggedIn) {
    User userByUsername = userRepository.findByUsername(userToBeLoggedIn.getUsername());

    String baseErrorMessage = "The %s provided does not exist. Do you want to create a new user?";
    if (userByUsername == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              String.format(baseErrorMessage, "username"));
    }
    return userByUsername;
  }
}
