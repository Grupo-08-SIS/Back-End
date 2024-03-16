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
    fun cadastrar(@RequestBody usuario: Usuario): ResponseEntity<Any> {

        if (listaUsuarios.any { it.email == usuario.email }) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-mail já cadastrado")
        }

        val proximoId = Usuario.obterProximoId(listaUsuarios)

        val novoUsuario = Usuario(
            id  = proximoId,
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
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
        return ResponseEntity.status(HttpStatus.OK).body(listaUsuarios)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<Usuario> {
        val usuarioEncontrado = listaUsuarios.find { it.id == id }
        if (usuarioEncontrado != null) {
            return  ResponseEntity.status(HttpStatus.OK).body(usuarioEncontrado)
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @GetMapping("/tipousuario/{tipo}")
    fun buscarUsuariosPorTipo(@PathVariable tipo: Long): ResponseEntity<List<Usuario>> {
        val usuariosEncontrados = listaUsuarios.filter { it.tipoUsuario.toLong() == tipo }
        return if (usuariosEncontrados.isNotEmpty()) {
            ResponseEntity.status(HttpStatus.OK).body(usuariosEncontrados)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping("/{id}")
    fun deletarPorId(@PathVariable id: Long): ResponseEntity<Void> { //Void: indica q o codigo não irá retornar um corpo
        val usuarioEncontrado = listaUsuarios.find { it.id == id }   // Ou seja, será entregue só o status.
        if (usuarioEncontrado != null) {
            listaUsuarios.remove(usuarioEncontrado)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
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

    @PatchMapping("/{id}")
    fun atualizarInformacao(@PathVariable id: Long, @RequestBody atualizacao: Map<String, Any>): ResponseEntity<Any> {
        val usuarioEncontrado = listaUsuarios.find { it.id == id }
        if (usuarioEncontrado != null) {
            val camposValidos = setOf("nome", "email", "telefone", "senha")
            for ((campo, valor) in atualizacao) {
                if (camposValidos.contains(campo)) {
                    when (campo) {
                        "nome" -> usuarioEncontrado.nome = valor.toString()
                        "email" -> usuarioEncontrado.email = valor.toString()
                        "telefone" -> usuarioEncontrado.telefone = valor.toString().toLong()
                        "senha" -> usuarioEncontrado.senha = valor.toString()
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Campo inválido: $campo")
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body("Informações do usuário atualizadas com sucesso")
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }





}




