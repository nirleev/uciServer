package chess.swagger.api;

import chess.Constants;
import chess.db.AccountDAO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private Constants constantsProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServerStatus serverStatus;

    @PostMapping(path="/add", consumes=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Add new user")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=415, message="Unsupported Media Type"),
            @ApiResponse(code=503, message="Service Unavailable") })
    public @ResponseBody String addNewUser (
            @RequestBody LoginModel user) {

        return new AccountDAO(userRepository).registerNewUserAccount(user);
    }

    @PostMapping(path = "/login", consumes="application/json",
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Get access")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=415, message="Unsupported Media Type") })
    public @ResponseBody
    AccessModel getAccess(@RequestBody LoginModel user) {

        if (! new AccountDAO(userRepository).validateCredentials(user.getLogin(), user.getPassword())) {
            return new AccessModel(false, null);
        }

        String jwtSecretKey = constantsProperties.getJWT_SECRET_KEY();

        String jwt = Jwts.builder()
                .setSubject(user.getLogin())
                .claim("roles", "user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600*1000))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();

        return new AccessModel(true, jwt);
    }

    /**
     * Logs out a user
     */
    @PostMapping(path = "/logout")
    @ApiOperation(value="Leave")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=503, message="Service Unavailable") })
    public String leave() {
        return serverStatus.userLoggedOut();
    }
}



