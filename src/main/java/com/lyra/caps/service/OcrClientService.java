package com.lyra.caps.service;

import java.io.IOException;
import java.util.List;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
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
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(Timeout.ofSeconds(10))
                        .setResponseTimeout(Timeout.ofSeconds(120))
                        .build())
                .build();
        this.restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    public OcrResultMessage processar(MultipartFile file, String correlationId) {
        String url = ocrServiceUrl + "/v1/raas/ocr";

        if (file == null) {
            return falha(correlationId, "Nenhum arquivo foi enviado.", url, "null");
        }

        String filename = normalizarNomeArquivo(file);

        log.info(String.format(
                "Enviando arquivo ao microserviço de OCR | correlationId=%s | nomeOriginal=%s | nomeEnviado=%s | tamanho=%d bytes | contentType=%s",
                correlationId, file.getOriginalFilename(), filename, file.getSize(), file.getContentType()));

        try {
            byte[] bytes = file.getBytes();

            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("file", new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
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
                return falha(correlationId, "O microserviço de OCR retornou resposta vazia.", url, filename);
            }

            log.info(String.format(
                    "Microserviço de OCR respondeu | correlationId=%s | status=%s | confidence=%s | erros=%s",
                    correlationId, result.getStatus(), result.getConfidenceGeral(), result.getErros()));
            return result;
        } catch (IOException e) {
            log.error(String.format(
                    "Erro ao ler o arquivo antes do envio | correlationId=%s | nomeArquivo=%s | tamanho=%d | causaPossivel=arquivo corrompido ou sem permissão de leitura",
                    correlationId, filename, file.getSize()), e);
            return falha(correlationId, "Não foi possível ler o arquivo enviado. Verifique se o arquivo não está corrompido.", url, filename);
        } catch (RestClientResponseException e) {
            String corpo = e.getResponseBodyAsString();
            log.error(String.format(
                    "Microserviço de OCR rejeitou a requisição | correlationId=%s | nomeArquivo=%s | tamanho=%d | contentType=%s | httpStatus=%s | corpo=%s | causaPossivel=part 'file' sem filename, arquivo vazio, formato não suportado ou erro de validação no OCR",
                    correlationId, filename, file.getSize(), file.getContentType(), e.getStatusCode(), corpo));
            return falha(correlationId, "O serviço não conseguiu processar o documento "
                    + e.getStatusCode().value()
                    + ". Verifique se o arquivo é um PDF ou imagem válida e tente novamente.", url, filename);
        } catch (Exception e) {
            log.error(String.format(
                    "Falha inesperada ao chamar o microserviço de OCR | correlationId=%s | nomeArquivo=%s | url=%s | causaPossivel=OCR fora do ar, timeout ou problema de rede",
                    correlationId, filename, url), e);
            return falha(correlationId, "Ocorreu um erro inesperado ao contatar o microserviço de OCR. Tente novamente em instantes.", url, filename);
        }
    }

    private String normalizarNomeArquivo(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (StringUtils.hasText(original)) {
            return original;
        }
        String contentType = file.getContentType();
        log.warn(String.format(
                "Arquivo recebido sem nome original | contentType=%s | tamanho=%d | causaPossivel=frontend enviou Blob sem nome no FormData.append('file', ...)",
                contentType, file.getSize()));
        return "arquivo" + extensaoParaContentType(contentType);
    }

    private String extensaoParaContentType(String contentType) {
        if (contentType == null) {
            return ".bin";
        }
        switch (contentType) {
            case "application/pdf":
                return ".pdf";
            case "image/png":
                return ".png";
            case "image/jpeg":
                return ".jpg";
            case "image/tiff":
                return ".tiff";
            default:
                return ".bin";
        }
    }

    private OcrResultMessage falha(String correlationId, String mensagem, String url, String filename) {
        log.error(String.format(
                "OCR falhou em %s | correlationId=%s | nomeArquivo=%s | motivo=%s",
                url, correlationId, filename, mensagem));
        OcrResultMessage result = new OcrResultMessage();
        result.setCorrelationId(correlationId);
        result.setStatus("FALHA");
        result.setConfidenceGeral(0.0);
        result.setErros(List.of(mensagem));
        return result;
    }
}