package com.example.network;

import com.alibaba.druid.sql.visitor.functions.Right;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 处理cmdId和proto数据对象以及处理方法的映射
 * Created by wdf on 2018/9/29.
 */
public enum  MessageService {
    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    //proto消息id和处理类映射配置文件目录
    private static final String MSG_CFG_PATH = "protobuff/msgcfg";

    private static Map<Integer, Class<?>> msgId2ProtoClass = Maps.newHashMap();
    private static Map<Integer, AbstractMsgHandler> msgId2Handler = Maps.newHashMap();

    public void init() {
        SAXReader reader = new SAXReader();
        File msgCfgDir = new File(MSG_CFG_PATH);
        Document document = null;
        try {
            if(!msgCfgDir.isDirectory()) {
                log.error("{} is a file need dir", MSG_CFG_PATH);
                throw new RuntimeException("is a file need dir");
            }

            document = reader.read(msgCfgDir);
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            for (Element child : childElements) {
                int cmdId = Integer.parseInt(child.attributeValue("id"));
                msgId2ProtoClass.put(cmdId, Class.forName(child.attributeValue("proto")));
                if(!StringUtils.isBlank(child.attributeValue("handler"))) {
                    msgId2Handler.put(cmdId, (AbstractMsgHandler) Class.forName(child.attributeValue("handler")).newInstance());
                }
            }
        } catch (DocumentException | ClassNotFoundException |IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

    }

}
