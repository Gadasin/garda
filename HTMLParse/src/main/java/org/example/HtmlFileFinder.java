package org.example;

import org.example.functions.GetHTMLFilesData;
import org.example.functions.GetHTMLFilesList;
import org.example.models.TegData;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HtmlFileFinder {

    public static void main(String[] args) {
        Path startDir = Paths.get("D:\\Файлы\\Работа Гарда\\Покрытие");
        Path outputDir = startDir.resolve("results_md");

        try {
            // Инициализация
            GetHTMLFilesList filesList = new GetHTMLFilesList();
            GetHTMLFilesData filesData = new GetHTMLFilesData();
            List<Path> htmlFiles = filesList.ResultList(startDir);

            // Счетчики
            AtomicInteger processedFiles = new AtomicInteger();
            AtomicInteger totalTags = new AtomicInteger();

            // Обработка файлов
            htmlFiles.parallelStream().forEach(file -> {
                try {
                    List<TegData> tagData = filesData.processFile(file);
                    if (!tagData.isEmpty()) {
                        processedFiles.incrementAndGet();
                        totalTags.addAndGet(tagData.size());

                        // Сохранение в MD
                        Path mdFile = outputDir.resolve(
                                file.getFileName().toString()
                                        .replace(".html", ".md")
                                        .replace(".htm", ".md"));

                        GetHTMLFilesData.writeToMarkdown(mdFile, tagData);
                        System.out.printf("Обработан: %s (%d тегов)%n",
                                file.getFileName(), tagData.size());
                    }
                } catch (IOException e) {
                    System.err.printf("Ошибка в файле %s: %s%n",
                            file.getFileName(), e.getMessage());
                }
            });

            // Итоговая статистика
            System.out.printf("%nОБРАБОТКА ЗАВЕРШЕНА%n"
                            + "Файлов: %d%n"
                            + "Тегов: %d%n"
                            + "Результаты в: %s%n",
                    processedFiles.get(), totalTags.get(), outputDir);

        } catch (IOException e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
        }
    }
}