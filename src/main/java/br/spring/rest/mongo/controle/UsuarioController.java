package br.spring.rest.mongo.controle;

import br.spring.rest.mongo.entidades.Usuario;
import br.spring.rest.mongo.entidades.enuns.TipoUsuario;
import br.spring.rest.mongo.generic.response.GenericResponse;
import br.spring.rest.mongo.services.impl.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody
    GenericResponse<String> login(@RequestParam("usuario") String usuario, @RequestParam("senha") String senha) {
        Usuario user = new Usuario();
        user.setLogin(usuario);
        user.setSenha(senha);

        return userService.autenticar(user);
    }

    @RequestMapping(value = "/cadastrar", method = RequestMethod.POST)
    public @ResponseBody
    GenericResponse<String> cadastrar(@RequestParam(name = "usuario", required = true) String usuario,
            @RequestParam(name = "senha", required = true) String senha) {
        Usuario user = new Usuario();
        user.setLogin(usuario);
        user.setSenha(senha);
        user.setTipoUsuario(TipoUsuario.USUARIO_FUNCIONARIO);

        return userService.cadastrar(user);
    }

    @RequestMapping(value = "/listar", method = RequestMethod.GET)
    public @ResponseBody
    GenericResponse<List<Usuario>> listar() {
        return userService.listar();
    }

}
