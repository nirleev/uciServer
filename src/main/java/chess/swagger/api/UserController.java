package chess.swagger.api;

import chess.Constants;
import chess.db.model.User;
import chess.db.model.UserRepository;
import chess.server.ServerStatus;
import chess.swagger.model.AccessModel;
import chess.swagger.model.LoginModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

/**
 * This controller is responsible for handling all routes connected with user management.
 */
@RestController
@RequestMapping("/user")
@SuppressWarnings("unused")
@Api(value = "User", tags = { "User" })
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServerStatus serverStatus;

    @Autowired
    private Constants constantsProperties;

    @PostMapping(path="/add", consumes=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Add new user")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=401, message="Unauthorized") })
    public @ResponseBody String addNewUser (
            @RequestBody LoginModel user) {

        User n = new User();
        n.setLogin(user.getLogin());
        n.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        userRepository.save(n);
        return "Saved";
    }

    @PostMapping(path = "/login", consumes="application/json",
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Get access")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK") })
    public @ResponseBody
    AccessModel getAccess(@RequestBody LoginModel user) {

        if (!loginUser(user.getLogin(), user.getPassword())) {
            return new AccessModel(false, null);
        }

        String jwtSecretKey = constantsProperties.getJWT_SECRET_KEY();

        String jwt = Jwts.builder()
                .setSubject(user.getLogin())
                .claim("roles", "user")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();

        return new AccessModel(true, jwt);
    }

    /**
     * Looks for a user with given credentials
     * @param login login of a user
     * @param password password of a user
     * @return whether login was successful
     */
    public boolean loginUser (String login, String password) {

        boolean f = false;

        for (User user : userRepository.findAll()) {

            if(user.getLogin().equals(login) && user.getPassword().equals(DigestUtils.sha256Hex(password))){
                f = true;
            }
        }

        return f;
    }

    /**
     * Logs out a user
     */
    @PostMapping(path = "/logout")
    @ApiOperation(value="Leave")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=401, message="Unauthorized") })
    public String leave() {
        return serverStatus.userLoggedOut();
    }
}



