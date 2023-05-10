package com.example.jobapp.services.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * A fájlok kezelésre szolgáló műveletek körét leíró általános
 * interfész.
 * 
 * @author Norbert Csomor
 */
public interface IFilesStorageService {
    public boolean saveFile(String fileName, MultipartFile file);

    public Resource loadFile(String filename);

    public boolean deleteFile(String filename);

}
