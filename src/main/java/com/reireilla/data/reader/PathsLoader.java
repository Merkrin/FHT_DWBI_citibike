package com.reireilla.data.reader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PathsLoader {
    public static List<Path> loadAllPathsFromFolderWithExtension(String extension, String folderName) throws
            URISyntaxException, IOException {
        return getPathsFromResources(folderName).stream().filter(Objects::nonNull)
                .filter(path -> path.toString().endsWith(extension)).toList();
    }

    private static List<Path> getPathsFromResources(String folderName) throws URISyntaxException, IOException {
        List<Path> paths;

        URL rosourceUrl = PathsLoader.class.getClassLoader().getResource(folderName);

        paths = Files.walk(Paths.get(rosourceUrl.toURI())).filter(Files::isRegularFile)
                .collect(Collectors.toList());

        return paths;
    }
}
