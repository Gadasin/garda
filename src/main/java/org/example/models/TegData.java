package org.example.models;

public class TegData {
    private String name;       // Будет содержать либо "308", либо "-309"
    private String codeValue;  // Текст после двоеточия
    private String lineNumber; // Номер строки из span.lineNum
    private String fullText;   // Полный текст тега <a>

    // Геттеры
    public String getName() { return name; }
    public String getCodeValue() { return codeValue; }
    public String getLineNumber() { return lineNumber; }
    public String getFullText() { return fullText; }

    // Сеттеры
    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue != null ? codeValue.trim() : "";
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber != null ? lineNumber.trim() : "";
    }

    public void setFullText(String fullText) {
        this.fullText = fullText != null ? fullText.trim() : "";
    }

    @Override
    public String toString() {
        return String.format("Name: %s | Line: %s | Code: %s",
                name, lineNumber, codeValue);
    }
}