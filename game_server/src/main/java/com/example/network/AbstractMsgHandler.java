package com.example.network;

import com.alibaba.fastjson.JSONObject;
import com.example.game.core.SpringContextUtil;
import com.example.game.core.session.ISession;
import com.example.game.core.session.LinkUser;
import com.example.game.core.session.LinkUserManager;
import com.example.game.exceptions.GameException;
import com.example.game.exceptions.GameExceptionCode;
import com.example.game.utils.Constants;
import com.example.game.utils.StringBuilderHolder;
import com.example.pb.CommonMsg.SGameException;
import com.google.protobuf.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public abstract class AbstractMsgHandler {

    private static Logger logger = LoggerFactory.getLogger(AbstractMsgHandler.class);
    private static final ThreadLocal<StringBuilderHolder> tlStringBuilder = ThreadLocal.withInitial(() -> new StringBuilderHolder(256));

    public void handleClientRequest(Request request) {
        if(inTransaction()) {
            handleWithTransaction(request);
        } else {
            handleNoTransaction(request);
        }
    }

    private void handleWithTransaction(Request request) {
        long startTime = System.currentTimeMillis();
        //如果当前是未登录状态user为空
        LinkUser user = LinkUserManager.INSTANCE.getLinkUserBySession(request.getSession());
        String retStr = "";
        //开启事务
        PlatformTransactionManager transactionManager = SpringContextUtil.getBean(PlatformTransactionManager.class);
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            Message retObj = handle(user, request);
            sendResponse(retObj, request.getSession());
            if (retObj != null && needLogRet()) {
                retStr = retObj.toString();
            }
            transactionManager.commit(status);
        } catch (GameException e) {
            transactionManager.rollback(status);
            retStr = e.toString();
            rtnException(request.getId(),request.getSession(), e);
            logger.error("msg throw GameException error", e);
        } catch (Throwable e) {
            transactionManager.rollback(status);
            retStr = "exception: "+ e.getMessage();
            rtnException(request.getId(), request.getSession(), GameExceptionCode.INVALID_OPT);
            logger.error("msg throw Exception", e);
        } finally {
            logRequest(user, request.getMsg().getClass().getSimpleName(),request.getMsg().toString(), retStr, startTime, "");
        }
    }

    private void handleNoTransaction(Request request) {
        long startTime = System.currentTimeMillis();
        //如果当前是未登录状态user为空
        LinkUser user = LinkUserManager.INSTANCE.getLinkUserBySession(request.getSession());
        String retStr = "";
        try {
            Message retObj = handle(user, request);
            sendResponse(retObj, request.getSession());
            if (retObj != null && needLogRet()) {
                retStr = retObj.toString();
            }
        } catch (GameException e) {
            retStr = e.toString();
            rtnException(request.getId(),request.getSession(), e);
            logger.error("msg throw GameException error", e);
        } catch (Throwable e) {
            retStr = "exception: "+ e.getMessage();
            rtnException(request.getId(), request.getSession(), GameExceptionCode.INVALID_OPT);
            logger.error("msg throw Exception", e);
        } finally {
            logRequest(user, request.getMsg().getClass().getSimpleName(),request.getMsg().toString(), retStr, startTime, "");
        }
    }

    /**
     * 返回数据
     *
     * @param retObj
     */
    public void sendResponse(Message retObj, ISession session) {
        if (retObj == null) {
            return;
        }

        session.writeResponse(retObj);
        if (logger.isDebugEnabled()) {
            logger.debug(retObj.toString());
        }
    }

    /**
     * 异常返回
     *
     * @param requestId
     * @param e
     * @param session
     */
    public void rtnException(int requestId, ISession session, GameException e) {
        JSONObject errorObj;
        if (e.getRetObj() != null) {
            errorObj = e.getRetObj();
        } else {
            errorObj = new JSONObject();
        }
        SGameException exception = SGameException.newBuilder().setReqId(requestId).setErrorCode(e.getExceptionCode().getCode())
                .setErrorData(errorObj.toJSONString()).build();

        sendResponse(exception, session);
    }

    /**
     * 异常返回
     *
     * @param requestId
     * @param errorCode
     * @param session
     */
    public void rtnException(int requestId, ISession session, GameExceptionCode errorCode) {
        SGameException exception = SGameException.newBuilder().setReqId(requestId).setErrorCode(errorCode.getCode())
                .build();

        sendResponse(exception, session);
    }

    public boolean needLogRet() {
        return true;
    }

    /**
     * 请求日志
     * @param params
     * @param startTime
     * @return
     */
    public static void logRequest(LinkUser user,String requestName, String params, String retStr, long startTime, String importantData) {
        if (requestName.equals("PingTest")) {
            return;
        }
        try {
            long costTime = System.currentTimeMillis() - startTime;
            String paramStr = StringUtils.replace(params, Constants.LOG_SEPARATOR, Constants.LOG_SEPARATOR_REPLACE);
            retStr = StringUtils.replace(retStr, Constants.LOG_SEPARATOR, Constants.LOG_SEPARATOR_REPLACE);
            StringBuilder logMsg = tlStringBuilder.get().getStringBuilder();
            logMsg.append(Constants.SERVER_ID)
                    .append(Constants.LOG_SEPARATOR).append("REQUEST")
                    .append(Constants.LOG_SEPARATOR).append(requestName)
                    .append(Constants.LOG_SEPARATOR).append(user == null ? 0 : user.getPlayerId())
                    .append(Constants.LOG_SEPARATOR).append(costTime)
                    .append(Constants.LOG_SEPARATOR).append(paramStr)
                    .append(Constants.LOG_SEPARATOR).append(retStr)
                    .append(Constants.LOG_SEPARATOR).append(importantData);
            logger.info(logMsg.toString());
        } catch (Exception e) {
            logger.error("record player request error", e);
        }
    }

    abstract protected Message handle(LinkUser user, Request request) throws GameException;

    /**
     * handle处理是否需要开启事务，默认开启
     * @return
     */
    protected boolean inTransaction() {
        return true;
    }
}
