package ru.itis.marketplace.fileservice.service;

import java.io.InputStream;

public interface ImageResizerService {
    InputStream resizeImage(InputStream image, int width, int height, String formatName);
}
