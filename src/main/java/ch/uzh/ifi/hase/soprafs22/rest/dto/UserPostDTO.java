package ch.uzh.ifi.hase.soprafs22.rest.dto;
import java.util.Date;

public class UserPostDTO {

  private String username;

  private String password;

  private Date birthDate;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
      return password;
  }

  public void setPassword(String password){this.password = password;}


    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate){this.birthDate = birthDate;}
}
