package br.spring.rest.mongo.enums;

public enum DefaultResponseEnum {
	OK(00, "Requisição ok."),
	ERROR(99, "Ocorreu um erro inesperado.");
	
	public int returnCode;
	public String returnMessage;
	
	DefaultResponseEnum(int returnCode, String returnMessage) {
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
