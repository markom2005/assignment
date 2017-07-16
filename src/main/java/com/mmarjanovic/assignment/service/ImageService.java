package com.mmarjanovic.assignment.service;

import com.mmarjanovic.assignment.constants.Constants;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;

@Service
public class ImageService {

    public byte[] getImageBytes(String imageName) {
        if (StringUtils.isEmpty(imageName)) {
            return null;
        }

        try {
            File storedFile = new File(new File(Constants.IMAGE_STORAGE_LOCATION).getAbsolutePath() + File.separator + imageName);
            if (storedFile != null && storedFile.exists()) {
                InputStream in = new FileInputStream(storedFile);
                return IOUtils.toByteArray(in);
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found. " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("IO Exception. " + e.getMessage());
        }

        return null;
    }
}