package TechForAll.Backend.controller

import TechForAll.Backend.model.Usuario
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/usuario")
class UsuarioController {

    val listaUsuarios: MutableList<Usuario> = mutableListOf()

    @PostMapping
    fun cadastrar(@RequestBody usuario: Usuario): ResponseEntity<Usuario> {

        val novoUsuario = Usuario(
            id = usuario.id,
            nome = usuario.nome,
            email = usuario.email,
            senha = usuario.senha,
            dataNascimento = usuario.dataNascimento,
            telefone = usuario.telefone,
            tipoUsuario = usuario.tipoUsuario,
            dataCriacao = usuario.dataCriacao
        )


        novoUsuario.validarCampos()
        listaUsuarios.add(novoUsuario)

        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario)
    }


}




