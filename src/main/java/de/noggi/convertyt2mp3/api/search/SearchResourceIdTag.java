package de.noggi.convertyt2mp3.api.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResourceIdTag implements Serializable {

    @Serial
    private static final long serialVersionUID = -7586305370134596086L;

    private String kind;
    private String videoId;
    private String channelId;
    private String playlistId;
}
