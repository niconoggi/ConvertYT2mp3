package de.noggi.convertyt2mp3.convert;

import java.nio.file.Path;

public class ConversionDto {

    private Path filePath;
    private String errorMsg;

    public ConversionDto() {}

    public ConversionDto(final Path filePath) {
        this.filePath = filePath;
    }

    public ConversionDto(final String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Path getFilePath() {return filePath;}
    public void setFilePath(Path filePath) {this.filePath = filePath;}

    public String getErrorMsg() {return errorMsg;}
    public void setErrorMsg(String errorMsg) {this.errorMsg = errorMsg;}

    @Override
    public String toString() {
        return "filePath = " + filePath + ", errorMsg = " + errorMsg;
    }
}
