package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();

    user.setId(1L);
    user.setUsername("firstname@lastname");
    user.setPassword("Password");
    user.setStatus(false);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].token", is(user.getToken())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus())))
        .andExpect(jsonPath("$[0].createTime", is(user.getCreateTime())))
        .andExpect(jsonPath("$[0].birthDate", is(user.getBirthDate())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("password");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(true);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("password");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.token", is(user.getToken())))
            .andExpect(jsonPath("$.status", is(user.getStatus())))
            .andExpect(jsonPath("$.createTime", is(user.getCreateTime())))
            .andExpect(jsonPath("$.birthDate", is(user.getBirthDate())));

  }

  @Test
  public void createUser_duplicateName_thenReturnConflict() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setPassword("password");
      user.setUsername("testUsername");
      user.setToken("1");
      user.setStatus(true);

      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setPassword("password");
      userPostDTO.setUsername("testUsername");

      Exception conflict_excp = new ResponseStatusException(HttpStatus.CONFLICT);
      given(userService.createUser(Mockito.any())).willThrow(conflict_excp);

      MockHttpServletRequestBuilder getRequest = post("/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPostDTO));

      mockMvc.perform(getRequest).andExpect(status().isConflict());
  }

  @Test
  public void givenUser_whenGetUser_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();

    user.setId(1L);
    user.setUsername("firstname@lastname");
    user.setPassword("Password");
    user.setToken("1");
    user.setStatus(false);


    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUser(user.getId())).willReturn(user);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/"+user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(user));

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.token", is(user.getToken())))
            .andExpect(jsonPath("$.status", is(user.getStatus())))
            .andExpect(jsonPath("$.createTime", is(user.getCreateTime())))
            .andExpect(jsonPath("$.birthDate", is(user.getBirthDate())));
}

  @Test
  public void givenUser_whenGetUser_thenReturnNotFound() throws Exception {
      Exception not_found_excp = new ResponseStatusException(HttpStatus.NOT_FOUND);
      given(userService.getUser(1L)).willThrow(not_found_excp);

      MockHttpServletRequestBuilder getRequest = get("/users/"+1L)
            .contentType(MediaType.APPLICATION_JSON);

      mockMvc.perform(getRequest).andExpect(status().isNotFound());
  }

  @Test
  public void updateUser_validInput_userUpdated() throws Exception {
    // given
    User user = new User();

    user.setId(1L);
    user.setUsername("firstname@lastname");
    user.setPassword("Password");
    user.setToken("1");
    user.setStatus(false);

    Date birthDate = new Date();
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setBirthDate(birthDate);
    userPostDTO.setUsername("testUsername");

    User inputUser = new User();
    inputUser.setBirthDate(birthDate);
    inputUser.setUsername("testUsername");

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.editUser(inputUser, user.getId())).willReturn(user);

    // when
    MockHttpServletRequestBuilder putRequest = put("/users/"+user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(putRequest).andExpect(status().isNoContent());
}

  @Test
  public void updateUser_whenGetUser_thenReturnNotFound() throws Exception {

      Date birthDate = new Date();
      User inputUser = new User();
      inputUser.setBirthDate(birthDate);
      inputUser.setUsername("testUsername");
      inputUser.setId(10L);

      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setBirthDate(inputUser.getBirthDate());
      userPostDTO.setUsername(inputUser.getUsername());

      Exception not_found_excp = new ResponseStatusException(HttpStatus.NOT_FOUND);
      given(userService.editUser(Mockito.any(), anyLong())).willThrow(not_found_excp);

      // when
      MockHttpServletRequestBuilder putRequest = put("/users/"+11L)
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPostDTO));

      mockMvc.perform(putRequest).andExpect(status().isNotFound());
  }


  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}