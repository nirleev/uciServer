package chess.swagger.api;

import chess.Constants;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles all config related routes
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/config")
@Api(value = "Configuration", tags = { "Configuration" })
public class ConfigurationController {

    @Autowired
    private Constants constantsProperties;

    // to do later
}
