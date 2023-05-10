package com.example.jobapp.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobapp.services.interfaces.IFilesStorageService;

/**
 * A fájlok kezelésére szolgáló műveleteket mevalósító osztály.
 * 
 * @author Norbert Csomor
 */
@Service
public class FilesStorageService implements IFilesStorageService {

    private final Path root = Paths.get("./cvs");

    /**
     * Fájl feltöltése a szerver egy adott mappájába.
     *
     * @param fileName a feltöltendő fájl neve.
     * @param file     a feltöltendő fájl.
     * @return a már létező fájl felülírásának tényét közlő állapotváltozó.
     */
    public boolean saveFile(String fileName, MultipartFile file) {

        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException("Nem sikerült a mappa létrehozása!");
            }
        }

        Path filePath = this.root.resolve(fileName);

        boolean isExistingFileOverwritten = false;

        if (Files.exists(filePath)) {
            isExistingFileOverwritten = true;
        }

        try {
            Files.copy(file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);
            return isExistingFileOverwritten;
        } catch (IOException e) {
            return isExistingFileOverwritten;
        }
    }

    /**
     * Fájl letöltése a szerverről.
     * 
     * @param filename a letöltendő fájl neve.
     * @return a letöltendő fájl.
     */
    public Resource loadFile(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Nem sikerült a fájl olvasása!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    /**
     * Fájl törlése a szerverről.
     * 
     * @param filename a törlendő fájl neve.
     * @return a törlés sikerességét jelző állapotváltozó.
     */
    public boolean deleteFile(String filename) {
        try {
            Path file = root.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Hiba történt: " + e.getMessage());
        }
    }
}
