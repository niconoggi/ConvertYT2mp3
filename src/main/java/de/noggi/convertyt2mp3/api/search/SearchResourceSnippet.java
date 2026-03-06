package de.noggi.convertyt2mp3.api.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResourceSnippet implements Serializable {

    @Serial
    private static final long serialVersionUID = -8963759594104242200L;

    private String channelId;
    private String title;
    private String description;
    private String channelTitle;
    private Map<String, SnippetResourceThumbnail> thumbnails;
}
