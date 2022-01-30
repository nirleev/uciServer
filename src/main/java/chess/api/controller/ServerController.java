package chess.api.controller;

import chess.db.model.Engine;
import chess.db.model.EngineRepository;
import chess.server.ServerLogger;
import chess.server.ServerStatus;
import chess.api.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

/**
 * Handles all server related routes
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/server")
@Api(value = "Server", tags = { "Server" })
public class ServerController {

    @Autowired
    private ServerStatus serverStatus;

    @Autowired
    private EngineRepository engineRepository;

    private final ServerListModel serverListModel = new ServerListModel();
    private final ServerLogger logger =
            new ServerLogger(this.getClass().getName(), true);

    @GetMapping(path="/heartbeat")
    @ApiOperation(value="Check connections with Chess Servers")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=503, message="Service Unavailable") })
    public @ResponseBody String connect() {

        ArrayList<String> responses = new ArrayList<>();
        logger.log("info", "Check connections request");

        for(ServerModel server: serverListModel.serverList){
            HttpClient client = HttpClient.newBuilder().build();
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(
                                new URI(server.getUrl()+"/receiver/hello")).
                        header("authorization", "Bearer " + serverStatus.getCurrentUserToken()).
                        GET().build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                responses.add(server.getName() + " says " + response.body());
            } catch (URISyntaxException | IOException | InterruptedException e) {
                responses.add(server.getName() + " unavailable");
            }
        }

        StringBuilder res = new StringBuilder();
        for(String s: responses){
            res.append("\n").append(s);
        }
        return res.toString();
    }

    @PostMapping(path="/start-engine", consumes=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Start engine on Chess Server")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=404, message="Not Found"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=415, message="Unsupported Media Type"),
            @ApiResponse(code=503, message="Service Unavailable")})
    public @ResponseBody String startEngine(@RequestBody StartEngineOnServerModel startEngineOnServerModel) {

        String res = "";
        JSONObject engine = new JSONObject();
        logger.log("info", "Start engine request");

        for (Engine e : engineRepository.findAll()) {
            if(e.getName().equals(startEngineOnServerModel.engineName)){
                engine.put("name", e.getName());
                engine.put("path", e.getPath());
                engine.put("description", e.getDescription());
                break;
            }
        }

        if(! engine.has("name")){
            logger.log("error", "Requested engine not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Engine Not Found");
        }

        HttpClient client = HttpClient.newBuilder().build();
        try {
            String url = serverListModel.getUrlByName(startEngineOnServerModel.serverName);
            if(url == null){
                serverStatus.updateServerStatus(HttpStatus.NOT_FOUND.value());
                logger.log("error", "Requested server " + startEngineOnServerModel.serverName + " not found");
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Not Found");
            } else {
                HttpRequest request = HttpRequest.newBuilder().uri(
                                new URI(serverListModel.getUrlByName(
                                        startEngineOnServerModel.serverName) + "/engine/start")).
                        POST(HttpRequest.BodyPublishers.ofString(engine.toString())).
                        header("Authorization", "Bearer " + serverStatus.getCurrentUserToken()).
                        header("Content-Type", "application/json").build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                res = response.body();
                logger.log("info", "Server  " + startEngineOnServerModel.serverName + " response: " + res);
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            serverStatus.updateServerStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable");
        }

        return res;
    }

    @PostMapping(path="/stop-engine")
    @ApiOperation(value="Stop engine on Chess Server")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=404, message="Not Found"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=415, message="Unsupported Media Type"),
            @ApiResponse(code=503, message="Service Unavailable")})
    public @ResponseBody String stopEngine(@RequestBody String serverName) {

        String res = "";
        logger.log("info", "Stop engine request");

        HttpClient client = HttpClient.newBuilder().build();
        try {
            String url = serverListModel.getUrlByName(serverName);
            if(url == null){
                serverStatus.updateServerStatus(HttpStatus.NOT_FOUND.value());
                logger.log("error", "Requested server " + serverName + " not found");
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Not Found");
            } else {
                HttpRequest request = HttpRequest.newBuilder().uri(
                                new URI(serverListModel.getUrlByName(
                                        serverName) + "/engine/stop")).
                        header("Authorization", "Bearer " + serverStatus.getCurrentUserToken()).
                        GET().build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                res = response.body();
                logger.log("info", "Server  " + serverName + " response: " + res);
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            serverStatus.updateServerStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable");
        }

        return res;
    }

    @PostMapping(path="/add", consumes=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Add Chess Server")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=415, message="Unsupported Media Type"),
            @ApiResponse(code=503, message="Service Unavailable") })
    public @ResponseBody String add(@RequestBody ServerModel server) {

        logger.log("info", "Add server request");
        String info = "";

        if(! serverListModel.urlExist(server.getUrl())){
            if(! serverListModel.nameExist(server.getName())) {
                serverListModel.addServer(server);
                logger.log("info", "Saved server " + server.getName());
                return "Saved";
            }
            info = "Server with name " + server.getName() + " already exists - request ignored";
            logger.log("info", info);
            return info;
        }
        info = "Given URL (" + server.getUrl() + ") has already been added - request ignored";
        logger.log("info", info);
        return info;
    }

    @PostMapping(path="/delete")
    @ApiOperation(value="Delete Chess Server")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=404, message="Not Found"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=415, message="Unsupported Media Type"),
            @ApiResponse(code=503, message="Service Unavailable") })
    public @ResponseBody String delete(@RequestBody String name) {

        logger.log("info", "Delete server request");
        if(serverListModel.deleteServer(name)){
            return "Deleted server " + name;
        }

        logger.log("info", "Requested server (" + name + ") not found");
        serverStatus.updateServerStatus(HttpStatus.NOT_FOUND.value());
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Not Found");
    }

    @GetMapping(path="/all", produces=MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value="Get all Chess Servers")
    @ApiResponses(value = {
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request"),
            @ApiResponse(code=401, message="Unauthorized"),
            @ApiResponse(code=405, message="Method Not Allowed"),
            @ApiResponse(code=503, message="Service Unavailable") })
    public @ResponseBody ArrayList<ServerModel> getAll() {

        logger.log("info", "Available servers request");
        return serverListModel.serverList;
    }

}
