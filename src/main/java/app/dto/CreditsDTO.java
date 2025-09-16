package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class CreditsDTO {
    private int id;
    @JsonProperty("cast")
    private List<ActorDTO> actors;
    @JsonProperty("crew")
    private List<CrewMemberDTO> crew;
}