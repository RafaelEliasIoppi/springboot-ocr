package com.exemplo.documento.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.exemplo.documento.model.Document;
import com.exemplo.documento.repository.DocumentRepository;

import net.sourceforge.tess4j.TesseractException;

@Service
public class DocumentService {

    private final OcrService ocrService;
    private final DocumentRepository repository;

    public DocumentService(OcrService ocrService, DocumentRepository repository) {
        this.ocrService = ocrService;
        this.repository = repository;
    }

    /**
     * Recebe o arquivo submetido, faz OCR, extrai campos e salva no H2.
     */
    public Document processAndSave(MultipartFile multipartFile) throws IOException, TesseractException {
        // 1) grava temporariamente
        File tempFile = File.createTempFile("upload-", "-" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);

        // 2) OCR
        String rawText = ocrService.extractText(tempFile);

        // 3) parse simples
        String nome = parseField(rawText, "Nome:");
        String cpf = parseRegex(rawText, "(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})");
        LocalDate dataNascimento = parseDate(rawText, "(\\d{2}/\\d{2}/\\d{4})", "dd/MM/yyyy");

        // 4) monta entidade e salva
        Document doc = new Document(rawText, nome, cpf, dataNascimento);
        return repository.save(doc);
    }

    private String parseField(String text, String label) {
        if (!text.contains(label)) return null;
        String[] parts = text.split(label, 2)[1].split("\\R", 2);
        return parts[0].trim();
    }

    private String parseRegex(String text, String regex) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private LocalDate parseDate(String text, String regex, String pattern) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(regex).matcher(text);
        if (m.find()) {
            return LocalDate.parse(m.group(1), DateTimeFormatter.ofPattern(pattern));
        }
        return null;
    }
}
