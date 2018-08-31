package br.spring.rest.mongo.controle;

import br.spring.rest.mongo.entidades.Usuario;
import br.spring.rest.mongo.entidades.enuns.TipoUsuario;
import br.spring.rest.mongo.generic.response.GenericResponse;
import br.spring.rest.mongo.services.impl.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    GenericResponse<String> login(@RequestBody(required = true) Map<String, String> usuarioMap) {
        Usuario user = new Usuario();
        user.setLogin(usuarioMap.get("usuario"));
        user.setSenha(usuarioMap.get("senha"));

        return userService.autenticar(user);
    }

    @RequestMapping(value = "/cadastrar", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    GenericResponse<String> cadastrar(@RequestBody(required = true) Map<String, String> usuarioMap) {
        Usuario user = new Usuario();
        user.setLogin(usuarioMap.get("usuario"));
        user.setSenha(usuarioMap.get("senha"));
        String tipoString = usuarioMap.get("tipo"); 
        user.setTipoUsuario(TipoUsuario.valueOf(tipoString));

        return userService.cadastrar(user);
    }

    @RequestMapping(value = "/listar", method = RequestMethod.GET)
    public @ResponseBody
    GenericResponse<List<Usuario>> listar() {
        return userService.listar();
    }

}
