package com.lyra.caps.service;

import org.springframework.web.multipart.MultipartFile;

public interface IDocumentExtractor {

    public String extrair(MultipartFile file);

}
