package ru.itis.marketplace.fileservice.service;

import java.io.InputStream;

public interface PhotoResizerService {
    InputStream resizeImage(InputStream image, int width, int height);
}
