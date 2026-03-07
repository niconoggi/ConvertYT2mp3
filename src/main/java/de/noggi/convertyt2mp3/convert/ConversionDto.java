package de.noggi.convertyt2mp3.convert;

import lombok.Data;

import java.nio.file.Path;

@Data
public class ConversionDto {

    private Path filePath;
    private String errorMsg;

    public ConversionDto(final Path filePath) {
        this.filePath = filePath;
    }

    public ConversionDto(final String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
