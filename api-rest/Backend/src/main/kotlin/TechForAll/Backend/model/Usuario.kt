package TechForAll.Backend.model

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.Date

data class Usuario(
    val id: Long,
    var nome: String,
    var email: String,
    var senha: String,
    val dataNascimento: Date,
    var telefone: Long,
    val tipoUsuario: Int,
    val dataCriacao: LocalDateTime = LocalDateTime.now(),
) {
    init {
        validarCampos()

    }

    fun validarCampos() {
        if (nome.isBlank()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Você deve inserir um nome"
            )
        }

        if (!validarEmail(email)) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "E-mail inválido"
            )
        }

        if (senha.length < 6) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "A senha deve ter pelo menos 6 caracteres"
            )
        }


        if (telefone.toString().length != 11) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Número de telefone inválido"
            )
        }

        if (tipoUsuario < 1 || tipoUsuario > 2) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Tipo de usuário inválido"
            )
        }

    }

    private fun validarEmail(email: String): Boolean {
        return email.contains("@")
    }

    companion object {
        fun obterProximoId(listaUsuarios: List<Usuario>): Long {
            return if (listaUsuarios.isEmpty()) {
                1
            } else {
                listaUsuarios.last().id + 1
            }
        }
    }
    //O companion object é uma construção do Kotlin que permite criar membros estáticos dentro de uma classe.
}


