package com.example.game.exceptions;

public enum GameExceptionCode {

    FUNCTION_VERSION_TOO_LOW("EEE000000"),//当前功能版本低
	XML_PARSING_ERROR("XML-ERROR"),
    REQUEST_TOO_FAST("EE000000"),
	INVALID_OPT("E000000"),
	USERGOLD_IS_NOT_ENOUGH("E100001"),//金币不足
	USERRESOURCE_IS_NOT_ENOUGH("E100002"),//资源不足

    ;


	GameExceptionCode(String errorCode) {
		this.exceptionCode = errorCode;
	}

	/**
	 * @return the exceptionCode
	 */
	public String getCode() {
        return exceptionCode;
    }

	private String exceptionCode;
}
