package chess.swagger.api;

import chess.db.model.Engine;
import chess.db.model.EngineRepository;
import chess.engine.EngineHandler;
import chess.server.ServerStatus;
import chess.swagger.model.EngineModel;
import chess.swagger.model.InfoModel;
import chess.swagger.model.StartEngineModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private EngineHandler engineHandler;

    @Autowired
    private ServerStatus serverStatus;

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
        return engineRepository.findAll();
    }

    /**
     * Starts engine with name
     * @param engine StartEngineModel with name of the engine that user wants to start.
     * @return information whether engine was started.
     */
    @PostMapping(value = "/start", consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Start engine")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=404, message="Not Found"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=415, message="Unsupported Media Type"),
            @ApiResponse(code=503, message="Service Unavailable") })
    public @ResponseBody
    InfoModel start(@RequestBody StartEngineModel engine) {

        for (Engine e : engineRepository.findAll()) {
            if(e.getName().equals(engine.name)){
                engineHandler.startEngine(e);
                boolean info = engineHandler.waitForEngine();

                if(info){
                    return new InfoModel(true, String.format("Engine %s started", e.getName()));
                } else {
                    return new InfoModel(false, String.format("Can't start engine %s", e.getName()));
                }
            }
        }
        serverStatus.updateServerStatus(HttpStatus.NOT_FOUND.value());
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Not Found");
    }

    /**
     * This method stops currently running engine.
     * @return whether the engine was stopped.
     */
    @PostMapping(value = "/stop", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Stop engine")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=503, message="Service Unavailable") })
    public @ResponseBody InfoModel stop() {

        engineHandler.stopEngine();
        return new InfoModel(true, "engine stopped");
    }

    /**
     * This method send command to currently running engine.
     * @return whether the engine was stopped.
     */
    @PostMapping(value = "/send")
    @ApiOperation(value="Send command")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=415, message="Unsupported Media Type"),
            @ApiResponse(code=503, message="Service Unavailable")})
    public @ResponseBody InfoModel send(@RequestParam String command) {

        if(engineHandler.isEngineRunning()){
            engineHandler.processRawCommand(command);
            return new InfoModel(true, "command sent");
        } else {
            return new InfoModel(false, "engine is not running");
        }
    }
}


