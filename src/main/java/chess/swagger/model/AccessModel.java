package chess.swagger.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AccessModel {

    public boolean success;

    /**
     * JWT token
     */
    public String token;

    public AccessModel(boolean success, String token) {
        this.success = success;
        this.token = token;

        showResponse();
    }

    private ResponseEntity<?> showResponse(){
        if(success){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
