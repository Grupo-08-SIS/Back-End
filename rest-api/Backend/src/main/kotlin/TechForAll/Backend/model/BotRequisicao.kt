package TechForAll.Backend.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class BotRequisicao (
    val usuario: String,
    val pergunta: String,
    val resposta: String,
    val linguagem: String,
    val dataPergunta: LocalDateTime,
    val tokensPergunta: Int,
    val tokensResposta: Int,
    val custoPergunta: BigDecimal,
    val custoResposta: BigDecimal
)

