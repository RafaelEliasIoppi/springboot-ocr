package com.exemplo.documento.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    private final ITesseract tesseract;

    public OcrService(@Value("${tessdata.path}") String tessdataPath) {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(tessdataPath);
        this.tesseract.setLanguage("por");
    }

    /**
     * Extrai todo o texto da imagem enviada.
     * @param imageFile arquivo de imagem (jpg, png, etc).
     * @return texto bruto extraído.
     */
    public String extractText(File imageFile) throws TesseractException {
        return tesseract.doOCR(imageFile);
    }
}
