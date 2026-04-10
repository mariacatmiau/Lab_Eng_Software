package com.tcc.desperdicio_alimentos.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.desperdicio_alimentos.repository.DoacaoRepository;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:doacao-it;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DoacaoAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoacaoRepository doacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void cleanDatabase() {
        doacaoRepository.deleteAll();
        produtoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void deveRestringirFluxoDeDoacaoPorUsuarioAutenticado() throws Exception {
        long suffix = System.currentTimeMillis();

        LoginResult funcionario = registrarELogar(
                "Mercado Fluxo",
                "func" + suffix + "@teste.com",
                "FUNCIONARIO",
                "11999990001"
        );

        LoginResult ongDestino = registrarELogar(
                "ONG Destino",
                "ong1" + suffix + "@teste.com",
                "ONG",
                "11999990002"
        );

        LoginResult ongIntrusa = registrarELogar(
                "ONG Intrusa",
                "ong2" + suffix + "@teste.com",
                "ONG",
                "11999990003"
        );

        Long produtoId = criarProduto(funcionario.token, ongDestino.id);
        Long doacaoId = criarDoacao(funcionario.token, produtoId, ongDestino.id);

        mockMvc.perform(put("/api/doacoes/" + doacaoId + "/aceitar")
                        .header("Authorization", "Bearer " + ongIntrusa.token))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/doacoes/" + doacaoId + "/aceitar")
                        .header("Authorization", "Bearer " + ongDestino.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACEITA"));

        mockMvc.perform(get("/api/doacoes/por-ong/" + ongDestino.id)
                        .header("Authorization", "Bearer " + ongDestino.token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/doacoes/por-ong/" + ongDestino.id)
                        .header("Authorization", "Bearer " + ongIntrusa.token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/doacoes/por-criador/" + funcionario.id)
                        .header("Authorization", "Bearer " + ongDestino.token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/doacoes/por-criador/" + funcionario.id)
                        .header("Authorization", "Bearer " + funcionario.token))
                .andExpect(status().isOk());
    }

    private LoginResult registrarELogar(String nome, String email, String tipo, String telefone) throws Exception {
        String registerBody = "{" +
                "\"nome\":\"" + nome + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"senha\":\"123456\"," +
                "\"tipo\":\"" + tipo + "\"," +
                "\"telefone\":\"" + telefone + "\"," +
                "\"endereco\":\"Avenida Paulista, 1000, Bela Vista, Sao Paulo, SP\"" +
                "}";

        mockMvc.perform(post("/api/usuarios/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

        String loginBody = "{" +
                "\"email\":\"" + email + "\"," +
                "\"senha\":\"123456\"" +
                "}";

        MvcResult loginResult = mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = json.get("token").asText();
        Long id = json.get("usuario").get("id").asLong();

        return new LoginResult(id, token);
    }

        private Long criarProduto(String token, Long ongId) throws Exception {
        String produtoBody = "{" +
                "\"nome\":\"Feijao Teste\"," +
                "\"categoria\":\"Alimento\"," +
                "\"quantidade\":4," +
                                "\"dataValidade\":\"2026-12-31\"," +
                                "\"tipoOferta\":\"DOACAO\"," +
                                "\"ongId\":" + ongId +
                "}";

        MvcResult result = mockMvc.perform(post("/api/produtos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private Long criarDoacao(String token, Long produtoId, Long ongId) throws Exception {
        String body = "{" +
                "\"produtoId\":" + produtoId + "," +
                "\"ongId\":" + ongId + "," +
                "\"quantidade\":2" +
                "}";

        MvcResult result = mockMvc.perform(post("/api/doacoes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private static class LoginResult {
        private final Long id;
        private final String token;

        private LoginResult(Long id, String token) {
            this.id = id;
            this.token = token;
        }
    }
}
