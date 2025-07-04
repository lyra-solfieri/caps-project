package com.lyra.caps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lyra.caps.service.IDocumentExtractor;

@RestController
@RequestMapping("/api/ocr")
public class DocumentExtratorOCRController {

    @Autowired
    private IDocumentExtractor documentExtractor;

    @PostMapping
    public ResponseEntity<String> extrairTexto(@RequestPart MultipartFile file) {
        try {
            Thread.sleep(3000);
            String text = documentExtractor.extrair(file);
            // adiconar ao banco de dados o texto extraído
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Texto extraído com sucesso!");
    }
}
