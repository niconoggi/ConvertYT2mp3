package de.noggi.convertyt2mp3.api.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeSearchResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -2894257545826607025L;

    private List<YouTubeSearchResource> items;
}
