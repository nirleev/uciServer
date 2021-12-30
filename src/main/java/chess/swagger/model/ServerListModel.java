package chess.swagger.model;

import java.util.ArrayList;

public class ServerListModel {

    public ArrayList<ServerModel> serverList;

    public ServerListModel(){
        this.serverList = new ArrayList<ServerModel>();
    }

    public void addServer(ServerModel serverModel){
        this.serverList.add(serverModel);
    }

    public boolean deleteServer(String serverName){
        int count = serverList.size();
        this.serverList.removeIf(server -> server.getName().equals(serverName));

        return count != serverList.size();
    }

    public String getUrlByName(String name){

        for(ServerModel server: serverList){
            if(server.getName().equals(name)){
                return server.getUrl();
            }
        }

        return null;
    }
}
