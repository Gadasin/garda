package org.example.functions;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class GetHTMLFilesList {
    public List<Path> ResultList(Path startDir) throws IOException{
        List<Path> fileList = new ArrayList<>();
        Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().toLowerCase().endsWith(".html")) {
                        fileList.add(file.toAbsolutePath());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            return fileList;
    }
}
