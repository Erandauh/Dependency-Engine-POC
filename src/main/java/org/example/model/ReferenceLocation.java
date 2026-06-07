package org.example.model;

/*
    Represents an exact reference coordinate in a source file
 */

public class ReferenceLocation {
    public String filePath;
    String component;
    String language;
    String logicalUnit;
    int line;
    String callerSymbol;

    public ReferenceLocation(String filePath, String component, String language,
                             String logicalUnit, int line, String callerSymbol) {
        this.filePath = filePath;
        this.component = component;
        this.language = language;
        this.logicalUnit = logicalUnit;
        this.line = line;
        this.callerSymbol = callerSymbol;
    }

    @Override
    public String toString() {
        return String.format("%s:%d [%s -> %s]", filePath, line, logicalUnit, callerSymbol);
    }
}