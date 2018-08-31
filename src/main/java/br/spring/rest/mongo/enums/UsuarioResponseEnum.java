/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.spring.rest.mongo.enums;

/**
 *
 * @author Renan
 */
public enum UsuarioResponseEnum {
    CADASTRO_OK(00, "Usuário cadastrado com sucesso"),
    CADASTRO_ERROR(10, "Não foi possivel cadastrar o usuário tente novamente mais tarde."),
    CADASTRO_LOGIN_EXIST(20, "Nome de login já cadastrado");

    public int returnCode;
    public String returnMessage;

    UsuarioResponseEnum(int returnCode, String returnMessage) {
        this.returnCode = returnCode;
        this.returnMessage = returnMessage;
    }

    public int getReturnCode() {
        return this.returnCode;
    }

    public String getReturnMessage() {
        return this.returnMessage;
    }
}
