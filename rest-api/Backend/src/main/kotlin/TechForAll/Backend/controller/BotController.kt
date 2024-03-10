package TechForAll.Backend.controller

import TechForAll.Backend.model.BotRequisicao
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.ModelType
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.completion.chat.ChatMessageRole
import com.theokanning.openai.service.OpenAiService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/bot")
class BotController {

    val listaRequisicoes: MutableList<BotRequisicao> = mutableListOf()

    @PostMapping("/pergunta")
    fun processarPergunta(@RequestBody mensagensUsuario: String): ResponseEntity<BotRequisicao> {
        val tokensPergunta = contarTokens(mensagensUsuario)
        val custoPergunta = calcularCusto(tokensPergunta)

        val respostaBot = enviarRequisicao(mensagensUsuario, tokensPergunta, custoPergunta)

        val botRequisicao = BotRequisicao(
            usuario = "Exemplo", //criar logica para buscar o nome do usuario solicitante
            pergunta = mensagensUsuario,
            resposta = respostaBot,
            linguagem = "Java",
            dataPergunta = LocalDateTime.now(),
            tokensPergunta = tokensPergunta,
            tokensResposta = contarTokens(respostaBot),
            custoPergunta = custoPergunta,
            custoResposta = calcularCusto(contarTokens(respostaBot))
        )

        listaRequisicoes.add(botRequisicao)

        return ResponseEntity.status(HttpStatus.CREATED).body(botRequisicao)
    }

    @GetMapping("/requisicoes")
    fun obterRequisicoes(): ResponseEntity<List<BotRequisicao>> {
        return ResponseEntity.ok(listaRequisicoes)
    }

    private fun contarTokens(texto: String): Int {
        val registry = Encodings.newDefaultEncodingRegistry()
        val enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO)
        return enc.countTokens(texto)
    }

    private fun calcularCusto(quantidadeTokens: Int): BigDecimal {
        return BigDecimal(quantidadeTokens)
            .divide(BigDecimal("1000"))
            .multiply(BigDecimal("0.0010"))
    }

    private fun enviarRequisicao(
        mensagensUsuario: String, tokensPergunta: Int, custoPergunta: BigDecimal,
    ): String {

        val modelo = "gpt-3.5-turbo"
        val tamanhoRespostaEsperada = 2048
        val promptSistema = """   
                 REGRAS: 
                 não responda nada fora do tema de tecnologia ou linguagens de programação e codigos, qualquer coisa 
                 fora isso diga que você não esta autorizado a responder 
                 """.trimIndent();

        val quantidadeTokensMensagens = contarTokens(mensagensUsuario)
        var modeloUtilizado = modelo

        if (quantidadeTokensMensagens > 4096 - tamanhoRespostaEsperada) {
            modeloUtilizado = "gpt-3.5-turbo-16k"
        }

        val mensagens = listOf(
            ChatMessage(ChatMessageRole.USER.value(), mensagensUsuario),
            ChatMessage(ChatMessageRole.SYSTEM.value(), promptSistema)
        )

        val request = ChatCompletionRequest.builder()
            .model(modeloUtilizado)
            .maxTokens(tamanhoRespostaEsperada)
            .messages(mensagens)
            .n(1)
            .build()

        val token = System.getenv("API_KEY_AI")
        val service = OpenAiService(token, Duration.ofSeconds(60))

        try {
            val response = service.createChatCompletion(request)
            return response.choices[0].message.content
        } catch (e: Exception) {
            System.err.println("Erro na requisição para a API do OpenAI: ${e.message}")
            return "Erro na requisição para a API do OpenAI."
        }
    }
}
