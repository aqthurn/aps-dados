package br.unip.aps.queimadas.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
public class TratadorErros {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> requisicaoInvalida(IllegalArgumentException e) {
        return erro(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return erro(HttpStatus.BAD_REQUEST, "Valor inválido para '" + e.getName() + "': " + e.getValue());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> parametroAusente(MissingServletRequestParameterException e) {
        return erro(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório ausente: " + e.getParameterName());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> arquivoAusente(MissingServletRequestPartException e) {
        return erro(HttpStatus.BAD_REQUEST, "Selecione um arquivo.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> arquivoGrande(MaxUploadSizeExceededException e) {
        return erro(HttpStatus.PAYLOAD_TOO_LARGE, "Arquivo muito grande (máximo 100 MB).");
    }

    @ExceptionHandler({IOException.class, InterruptedException.class})
    public ResponseEntity<Map<String, String>> falhaLeitura(Exception e) {
        return erro(HttpStatus.BAD_GATEWAY, "Falha ao ler ou baixar os dados: " + e.getMessage());
    }

    private static ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", String.valueOf(mensagem)));
    }
}
