package de.noggi.convertyt2mp3.api.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeSearchResource implements Serializable {

    @Serial
    private static final long serialVersionUID = -7736718222853390696L;

    private String kind;
    private SearchResourceIdTag id;
    private SearchResourceSnippet snippet;
}
