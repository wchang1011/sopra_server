package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;

import java.util.Date;

public class UserGetDTO {

  private Long id;
//  private String password;
  private String username;
  private String token;
  private boolean status;
  private Date createTime;
  private Date birthDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
//  public String getPassword() {
//    return password;
//  }
//
//  public void setPassword(String password) {
//    this.password = password;
//  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean getStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public Date getCreateTime() {
        return createTime;
    }

  public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate){this.birthDate = birthDate;}

}
