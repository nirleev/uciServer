package chess.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class EngineModel {
    @ApiModelProperty(notes="Name of the Engine",name="name",required=true,value="Stockfish")
    @JsonProperty("name")
    public String name;
    @ApiModelProperty(notes="File path of the engine",name="path",required=true,value="stockfish")
    @JsonProperty("path")
    public String path;
    @ApiModelProperty(notes="Description of the engine",name="description",required=true,value="Stockfish Chess Engine")
    @JsonProperty("description")
    public String description;
}
