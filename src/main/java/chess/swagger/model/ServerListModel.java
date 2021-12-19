package chess.swagger.model;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerListModel {

    public ArrayList<ServerModel> serverList;

    public ServerListModel(){
        this.serverList = new ArrayList<ServerModel>();
    }

    public void addServer(ServerModel serverModel){
        this.serverList.add(serverModel);
    }

    public void deleteServer(String serverName){
        this.serverList.removeIf(server -> server.getName().equals(serverName));
    }

    public String getUrlByName(String name){

        for(ServerModel server: serverList){
            if(server.getName().equals(name)){
                return server.getUrl();
            }
        }

        return " ";
    }
}
