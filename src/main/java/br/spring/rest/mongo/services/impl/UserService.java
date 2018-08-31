package br.spring.rest.mongo.services.impl;

import br.spring.rest.mongo.dao.impl.UsuarioDaoImpl;
import br.spring.rest.mongo.entidades.Usuario;
import br.spring.rest.mongo.enums.DefaultResponseEnum;
import br.spring.rest.mongo.enums.LoginResponseEnum;
import br.spring.rest.mongo.enums.UsuarioResponseEnum;
import br.spring.rest.mongo.exceptions.MongoDBException;
import br.spring.rest.mongo.generic.response.GenericResponse;
import br.spring.rest.mongo.seguranca.TokenJWTUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UsuarioDaoImpl usuarioRepositorio;

    public GenericResponse<String> autenticar(Usuario user) {
        int returnCode = LoginResponseEnum.BAD_CREDENTIALS.getReturnCode();
        String returnMessage = LoginResponseEnum.BAD_CREDENTIALS.getReturnMessage();
        String token = null;

        Map<String, Object> filtrosAnd = new HashMap<>();
        filtrosAnd.put("login", user.getLogin());
        filtrosAnd.put("senha", user.getSenha());

        Usuario usuarioBanco = usuarioRepositorio.buscaUnica(filtrosAnd, null);

        if (usuarioBanco != null) {
            List<String> permissoes = new ArrayList<>();
            permissoes.add(usuarioBanco.getTipoUsuario().toString());
            token = TokenJWTUtil.gerarToken(usuarioBanco.getId().toHexString(), permissoes);
            returnCode = LoginResponseEnum.OK.getReturnCode();
            returnMessage = LoginResponseEnum.OK.getReturnMessage();
        }

        return new GenericResponse<>(returnCode, returnMessage, "Bearer " + token);
    }

    public GenericResponse<String> cadastrar(Usuario user) {
        int returnCode = UsuarioResponseEnum.CADASTRO_ERROR.getReturnCode();
        String returnMessage = UsuarioResponseEnum.CADASTRO_ERROR.getReturnMessage();

        Map<String, Object> filtroAnd = new HashMap<>();
        filtroAnd.put("login", user.getLogin());

        if (usuarioRepositorio.countLinhas(filtroAnd, null) > 0) {
            returnCode = UsuarioResponseEnum.CADASTRO_LOGIN_EXIST.getReturnCode();
            returnMessage = UsuarioResponseEnum.CADASTRO_LOGIN_EXIST.getReturnMessage();
        } else {
            try {
                usuarioRepositorio.onSalvar(user);
                returnCode = UsuarioResponseEnum.CADASTRO_OK.getReturnCode();
                returnMessage = UsuarioResponseEnum.CADASTRO_OK.getReturnMessage();
            } catch (MongoDBException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return new GenericResponse<>(returnCode, returnMessage, null);
    }

    public GenericResponse<List<Usuario>> listar() {
        int returnCode = DefaultResponseEnum.OK.getReturnCode();
        String returnMessage = DefaultResponseEnum.OK.getReturnMessage();

        List<Usuario> retorno = usuarioRepositorio.findAll();

        return new GenericResponse<>(returnCode, returnMessage, retorno);
    }

}
