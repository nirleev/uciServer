package chess.swagger.api;

import chess.db.model.Engine;
import chess.db.model.EngineRepository;
import chess.engine.EngineHandler;
import chess.swagger.model.EngineModel;
import chess.swagger.model.InfoModel;
import chess.swagger.model.StartEngineModel;
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
    private EngineHandler engineHandler;

    @PostMapping(path="/add", consumes=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Add new engine")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=401, message="Unauthorized") })
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
            @ApiResponse(code=401, message="Unauthorized") })
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
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=404, message="Not Found") })
    public @ResponseBody
    InfoModel start(@RequestBody StartEngineModel engine) {

        for (Engine e : engineRepository.findAll()) {
            if(e.getName().equals(engine.name)){
                engineHandler.startEngine(e);
                return new InfoModel(true, String.format("Engine %s started on %s", e.getName(), engine.name));
            }
        }
        return new InfoModel(false, "Can`t start engine " + engine.name);
    }

    /**
     * This method stops currently running engine.
     * @return whether the engine was stopped.
     */
    @PostMapping(value = "/stop", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Stop engine")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=401, message="Unauthorized") })
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
            @ApiResponse(code=401, message="Unauthorized") })
    public @ResponseBody InfoModel send(@RequestParam String command) {

        engineHandler.processRawCommand(command);
        return new InfoModel(true, "command sent");
    }
}


