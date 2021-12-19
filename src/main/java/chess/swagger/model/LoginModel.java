package chess.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class LoginModel {
    @ApiModelProperty(notes="Login of the User",name="login",required=true,value="login")
    @JsonProperty("login")
    public String login;
    @ApiModelProperty(notes="Password of the User",name="password",required=true,value="password")
    @JsonProperty("password")
    public String password;

    public LoginModel(String login, String password){
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
