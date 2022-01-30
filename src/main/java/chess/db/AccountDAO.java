package chess.db;

import chess.db.model.User;
import chess.db.model.UserRepository;
import chess.server.ServerStatus;
import chess.api.model.LoginModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

public class AccountDAO {

    private final UserRepository userRepository;

    @Autowired
    private ServerStatus serverStatus;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public AccountDAO(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public String registerNewUserAccount(LoginModel user){
        try{
            User newUser = new User();
            newUser.setLogin(user.getLogin());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(newUser);

            return "Saved";
        } catch (Exception e){
            serverStatus.updateServerStatus(HttpStatus.BAD_REQUEST.value());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad Request");
        }
    }

    /**
     * Looks for a user with given credentials
     * @param login login of a user
     * @param password password of a user
     * @return whether login was successful
     */
    public boolean validateCredentials (String login, String password) {

        boolean f = false;

        // Ensuring there is no time difference in response
        for (User user : userRepository.findAll()) {
            if(user.getLogin().equals(login) && passwordEncoder.matches(password, user.getPassword())){
                f = true;
            }
        }

        return f;
    }
}
