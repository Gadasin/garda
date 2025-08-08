package org.example.functions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.example.models.TegData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GetHTMLFilesData {

    public Element findTargetPreElement(Path path) throws IOException {
        Document doc = Jsoup.parse(path.toFile(), "UTF-8");
        Elements tables = doc.select("body > table");
        if (tables.size() < 2) return null;

        return tables.get(1).selectFirst("pre.source:last-of-type");
    }

    public List<TegData> processFile(Path file) throws IOException {
        Element preElement = findTargetPreElement(file);
        return preElement != null ? processAnchorElements(preElement) : new ArrayList<>();
    }

    public static List<TegData> processAnchorElements(Element preElement) {
        List<TegData> result = new ArrayList<>();
        if (preElement == null) return result;

        for (Element anchor : preElement.select("a")) {
            result.add(createTegDataFromAnchor(anchor));
        }
        return result;
    }

    private static TegData createTegDataFromAnchor(Element anchor) {
        TegData info = new TegData();
        String rawName = anchor.attr("name");
        String fullText = anchor.text();

        // 1. Обработка lineNumber
        Element lineNumElement = anchor.selectFirst("span.lineNum");
        info.setLineNumber(lineNumElement != null ? lineNumElement.text().trim() : "");

        // 2. Проверка на наличие нулевого покрытия
        Element noCovElement = anchor.selectFirst("span.lineNoCov");
        boolean hasZeroCoverage = noCovElement != null && noCovElement.text().contains("0");

        // 3. Обработка основного текста
        String[] parts = fullText.split(":", 2);
        if (parts.length == 2) {
            info.setName(hasZeroCoverage ? "-" + rawName : rawName);
            info.setCodeValue(parts[1].trim());
        } else {
            info.setName(rawName);
            info.setCodeValue(fullText);
        }

        info.setFullText(fullText);
        return info;
    }

    public static void writeToMarkdown(Path outputPath, List<TegData> dataList) throws IOException {
        Files.createDirectories(outputPath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            // Таблица с 3 колонками, где после Line идет двоеточие
            writer.write("```diff\n");
            for (TegData data : dataList) {
                writer.write(String.format(" %s  %s : %s \n",
                        data.getName(),
                        data.getLineNumber(),
                        escapeMarkdown(data.getCodeValue())));
            }

            writer.write("```");
        }
    }

    private static String escapeMarkdown(String text) {
        return text.replace("|", "\\|")
                .replace("`", "\\`")
                .replace("*", "\\*");
    }
}