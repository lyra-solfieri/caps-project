package com.lyra.caps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/*
 * Como usar :
 *  
 *  private static final LogPersonalizado log = LogPersonalizado.getLogger(SeuNomeDeClasse.class);

 *  LogPersonalizado.getLogger(this.getClass()).info("Mensagem");
 * 
 *  LogPersonalizado.getLogger(this.getClass()).error("Mensagem", new Exception());
 * 
 *  LogPersonalizado.getLogger(this.getClass()).error("Mensagem");
 * 
 * Saida Esperada :
 * 
 *  [CAPS-SYSTEM] - 2025-07-06T13:00:01.123 - INFO - com.lyra.caps.service.DocumentoService#processarDocumento():10 - Iniciando o processamento do documento
 *  [CAPS-SYSTEM] - 2025-07-06T13:00:01.234 - INFO - com.lyra.caps.service.DocumentoService#processarDocumento():13 - Processamento concluído com sucesso
 * 
 * 
 * 
 *  @author Bruno Lyra
 */
public class LogPersonalizado {

    private final Logger logger;

    private LogPersonalizado(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static LogPersonalizado getLogger(Class<?> clazz) {
        return new LogPersonalizado(clazz);
    }

    public void info(String mensagem) {
        logger.info(formatar("INFO", mensagem));
    }

    public void warn(String mensagem) {
        logger.warn(formatar("WARN", mensagem));
    }

    public void error(String mensagem, Throwable throwable) {
        logger.error(formatar("ERROR", mensagem), throwable);
    }

    public void error(String mensagem) {
        logger.error(formatar("ERROR", mensagem));
    }

    private String formatar(String tipo, String mensagem) {
        StackTraceElement caller = encontrarChamadaExterna();

        String nomeClasse = caller != null ? caller.getClassName() : "ClasseDesconhecida";
        String nomeMetodo = caller != null ? caller.getMethodName() : "MetodoDesconhecido";
        int linha = caller != null ? caller.getLineNumber() : -1;

        return String.format("[CAPS-SYSTEM] - %s - %s - %s#%s():%d - %s",
                LocalDateTime.now(),
                tipo,
                nomeClasse,
                nomeMetodo,
                linha,
                mensagem);
    }

    private StackTraceElement encontrarChamadaExterna() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        for (StackTraceElement ste : stack) {
            String className = ste.getClassName();
            if (!className.equals(Thread.class.getName()) &&
                !className.equals(this.getClass().getName())) {
                return ste;
            }
        }

        return null;
    }
}
