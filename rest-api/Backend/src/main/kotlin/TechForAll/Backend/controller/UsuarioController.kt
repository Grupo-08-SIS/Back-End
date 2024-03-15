package TechForAll.Backend.controller

import TechForAll.Backend.model.Usuario
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


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

    @GetMapping()
    fun listar(): ResponseEntity<List<Usuario>> {
        if (listaUsuarios.isEmpty()) {
            return ResponseEntity.status(204).build()
        }
        return ResponseEntity.status(200).body(listaUsuarios)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<Usuario> {
        val usuarioEncontrado = listaUsuarios.find { it.id == id }
        if (usuarioEncontrado != null) {
            return ResponseEntity.ok(usuarioEncontrado)
        }
        return ResponseEntity.notFound().build()
    }

    @GetMapping("/tipousuario/{tipo}")
    fun buscarUsuariosPorTipo(@PathVariable tipo: Long): ResponseEntity<List<Usuario>> {
        val usuariosEncontrados = listaUsuarios.filter { it.tipoUsuario.toLong() == tipo }
        return if (usuariosEncontrados.isNotEmpty()) {
            ResponseEntity.ok(usuariosEncontrados)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deletarPorId(@PathVariable id: Long): ResponseEntity<Void> { //Void: indica q o codigo não irá retornar um corpo
        val usuarioEncontrado = listaUsuarios.find { it.id == id }   // Ou seja, será entregue só o status.
        if (usuarioEncontrado != null) {
            listaUsuarios.remove(usuarioEncontrado)
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build()
    }

    fun existeUsuario(indice: Int): Boolean {
        return indice >= 0 && indice < listaUsuarios.size
    }

 @PutMapping("/{id}")
    fun atualizar(@PathVariable id: Long, @RequestBody usuarioAtualizado: Usuario): ResponseEntity<Any> {
        val usuarioEncontrado = listaUsuarios.find { it.id == id }

        return if (usuarioEncontrado != null) {
            val indiceUsuarioEncontrado = listaUsuarios.indexOf(usuarioEncontrado)
            listaUsuarios[indiceUsuarioEncontrado] = usuarioAtualizado.copy(id = id)
            ResponseEntity.status(HttpStatus.OK).body("Usuário atualizado com sucesso")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado")
        }
    }



}




