package org.example;

import org.example.functions.GetHTMLFilesData;
import org.example.functions.GetHTMLFilesList;
import org.example.models.TegData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HtmlFileFinder {

    public static void main(String[] args) {
        // Проверяем аргументы командной строки
        if (args.length == 0) {
            System.err.println("Использование: java HtmlFileFinder <начальная_директория>");
            System.err.println("Пример: java HtmlFileFinder /home/user/coverage_data");
            System.exit(1);
        }

        Path startDir = Paths.get(args[0]);
        Path outputDir = startDir.resolve("results_md");

        try {
            // Проверяем существует ли директория
            if (!Files.exists(startDir)) {
                System.err.println("Ошибка: Директория не существует - " + startDir);
                System.exit(1);
            }

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

                        // Создаем директорию для результатов, если ее нет
                        Files.createDirectories(outputDir);

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
            System.exit(1);
        }
    }
}