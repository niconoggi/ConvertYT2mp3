package de.noggi.convertyt2mp3.api.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnippetResourceThumbnail implements Serializable {

    @Serial
    private static final long serialVersionUID = 8410752861723638625L;

    private String url;
    private Integer width;
    private Integer height;
}
