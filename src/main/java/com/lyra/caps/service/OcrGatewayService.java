package com.lyra.caps.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OcrGatewayService {

    private final RabbitTemplate rabbitTemplate;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @Value("${ocr.queue.request}")
    private String requestQueue;

    public OcrGatewayService(RabbitTemplate rabbitTemplate,
                             FileStorageService fileStorageService,
                             ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
    }

    public String enviarParaOcr(MultipartFile file, Long pacienteId) {
        String correlationId = UUID.randomUUID().toString();

        String filePath = fileStorageService.salvar(file, correlationId);
        
        // Envia para o Rabbit
        var mensagem = new HashMap<String, Object>();
        mensagem.put("correlationId", correlationId);
        mensagem.put("filePath", filePath);
        mensagem.put("originalFileName", file.getOriginalFilename());
        mensagem.put("pacienteId", pacienteId);
        mensagem.put("timestamp", Instant.now().toString());

        
        rabbitTemplate.convertAndSend(requestQueue, mensagem);

        return correlationId;
    }
}
