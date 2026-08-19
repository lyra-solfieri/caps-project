package com.lyra.caps.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import com.lyra.caps.dto.OcrResultMessage;
import com.lyra.caps.util.LogPersonalizado;


@Service
public class OcrClientService {

    private final RestClient restClient;
    private final LogPersonalizado log = LogPersonalizado.getLogger(this.getClass());

    @Value("${ocr.service.url}")
    private String ocrServiceUrl;

    public OcrClientService() {
        this.restClient = RestClient.builder().build();
    }

    public OcrResultMessage processar(MultipartFile file, String correlationId) {
        String url = ocrServiceUrl + "/v1/raas/ocr";
        try {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            parts.add("correlationId", correlationId);

            OcrResultMessage result = restClient.post()
                    .uri(url)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(parts)
                    .retrieve()
                    .body(OcrResultMessage.class);

            if (result == null) {
                return falha("Microserviço de OCR retornou resposta vazia.", url);
            }
            return result;
        } catch (IOException e) {
            return falha("Erro ao ler o arquivo enviado: " + e.getMessage(), url);
        } catch (RestClientResponseException e) {
            return falha("Microserviço de OCR respondeu HTTP " + e.getStatusCode() + ": "
                    + e.getResponseBodyAsString(), url);
        } catch (Exception e) {
            log.error("Falha ao chamar o microserviço de OCR em " + url, e);
            return falha("Falha ao chamar o microserviço de OCR: " + e.getMessage(), url);
        }
    }

    private OcrResultMessage falha(String mensagem, String url) {
        log.error("OCR falhou em " + url + ": " + mensagem);
        OcrResultMessage result = new OcrResultMessage();
        result.setStatus("FALHA");
        result.setConfidenceGeral(0.0);
        result.setErros(List.of(mensagem));
        return result;
    }
}