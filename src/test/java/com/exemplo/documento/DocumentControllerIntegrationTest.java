package com.exemplo.documento;

import com.exemplo.documento.service.OcrService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OcrService ocrService;

    @Test
    @DisplayName("POST /api/documents deve extrair nome, cpf e data corretamente")
    void uploadCnhMockandoOcrService() throws Exception {
        // 1) Mock do OCR: texto com acentos e formatação realista
        String fakeOcr = """
            República Federativa do Brasil
            Carteira Nacional de Habilitação
            Nome: Rafael Elias Ioppi
            CPF: 123.456.789-00
            02/11/1980
            """;
        given(ocrService.extractText(any(File.class))).willReturn(fakeOcr);

        // 2) Arquivo dummy (conteúdo irrelevante, OCR está mockado)
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "cnh.jpeg",
            MediaType.IMAGE_JPEG_VALUE,
            new byte[]{0,1,2,3}
        );

        // 3) Executa o endpoint e valida o JSON de saída
        mockMvc.perform(multipart("/api/documents")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            // id foi gerado
            .andExpect(jsonPath("$.id").isNumber())
            // rawText mantém o texto completo do OCR
            .andExpect(jsonPath("$.rawText", containsString("Nome: Rafael Elias Ioppi")))
            // agora nome e cpf são extraídos corretamente
            .andExpect(jsonPath("$.nome").value("Rafael Elias Ioppi"))
            .andExpect(jsonPath("$.cpf").value("123.456.789-00"))
            // data dd/MM/yyyy → yyyy-MM-dd
            .andExpect(jsonPath("$.dataNascimento").value("1980-11-02"));
    }
}
