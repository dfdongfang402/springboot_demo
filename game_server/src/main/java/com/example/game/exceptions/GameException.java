package com.example.game.exceptions;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(GameException.class);

    private GameExceptionCode exceptionCode;
    private JSONObject retObj;

    public GameException() {
        super("Unknown Error");
        exceptionCode = GameExceptionCode.INVALID_OPT;
    }

    public GameException(GameExceptionCode exceptionCode) {
        super("Unknown Error");
        this.exceptionCode = exceptionCode;
    }

    public GameException(GameExceptionCode exceptionCode, String exceptionDesc) {
        super(exceptionDesc);
        this.exceptionCode = exceptionCode;
    }

    public GameException(GameExceptionCode exceptionCode, JSONObject retObj, String exceptionDesc) {
        super(exceptionDesc);
        this.retObj = retObj;
        this.exceptionCode = exceptionCode;
    }

    public GameExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public JSONObject getRetObj() {
        return retObj;
    }

    public void setRetObj(JSONObject retObj) {
        this.retObj = retObj;
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("errorCode", exceptionCode.getCode());
        obj.put("errorMsg", getMessage());
        if (retObj != null && retObj.size() > 0) {
            obj.put("errorData", retObj);
        }
        return obj.toJSONString();
    }
}
