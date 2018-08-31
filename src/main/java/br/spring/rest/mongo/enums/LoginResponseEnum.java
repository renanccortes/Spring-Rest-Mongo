package br.spring.rest.mongo.enums;

public enum LoginResponseEnum {
    OK(00, "Login ok"),
    BAD_CREDENTIALS(01, "Usuário e/ou senha inválido(s)");

    public int returnCode;
    public String returnMessage;

    LoginResponseEnum(int returnCode, String returnMessage) {
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
