package chess.api.controller;

import chess.db.model.Engine;
import chess.db.model.EngineRepository;
import chess.server.ServerLogger;
import chess.server.ServerStatus;
import chess.api.model.EngineModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Handles all engine related routes
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/engine")
@Api(value = "Engine", tags = { "Engine" })
public class EngineController {

    @Autowired
    private EngineRepository engineRepository;

    @Autowired
    private ServerStatus serverStatus;

    private final ServerLogger logger = new ServerLogger(this.getClass().getName(), true);

    @PostMapping(path="/add", consumes=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Add new engine")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=415, message="Unsupported Media Type"),
            @ApiResponse(code=503, message="Service Unavailable")})
    public @ResponseBody String addNewEngine (@RequestBody EngineModel engine) {

        Engine e = new Engine();
        e.setName(engine.name);
        e.setPath(engine.path);
        e.setDescription(engine.description);
        engineRepository.save(e);
        logger.log("info", "Saved new engine");
        return "Saved";
    }

    @GetMapping(path="/available", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Get all engines")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=503, message="Service Unavailable") })
    public @ResponseBody Iterable<Engine> getAllEngines() {
        // This returns a JSON or XML with the engines
        logger.log("info", "Available engines request");
        return engineRepository.findAll();
    }
}


