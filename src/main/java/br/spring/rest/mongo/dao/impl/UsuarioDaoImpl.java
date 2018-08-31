/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.spring.rest.mongo.dao.impl;

import br.spring.rest.mongo.dao.MongoDB;
import br.spring.rest.mongo.entidades.Usuario;
import br.spring.rest.mongo.services.ServiceGenericoMongo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author Renan
 */
@Component
public class UsuarioDaoImpl extends MongoDB<Usuario> implements ServiceGenericoMongo<Usuario> {

    public UsuarioDaoImpl() {
        super(Usuario.class);
    }

    public Usuario onLogar(Usuario usuario) {

        Map<String, Object> filtroAnd = new HashMap<>();
        filtroAnd.put("login", usuario.getLogin());
        Usuario usuarioBanco = super.buscaUnica(filtroAnd, null);

        if (usuarioBanco != null) {
            if (usuarioBanco.getSenha().equals(usuario.getSenha())) {
                return usuarioBanco;
            }
        }

        return null;
    }

}
