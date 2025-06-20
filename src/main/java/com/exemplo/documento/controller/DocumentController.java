package com.exemplo.documento.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exemplo.documento.model.Document;
import com.exemplo.documento.service.DocumentService;

import net.sourceforge.tess4j.TesseractException;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    /**
     * Recebe POST multipart/form-data com chave "file" para a imagem do documento.
     * Retorna JSON com os campos extraídos e o ID gerado.
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            Document saved = service.processAndSave(file);
            return ResponseEntity.ok(saved);
        } catch (IOException | TesseractException e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao processar imagem: " + e.getMessage());
        }
    }
}
