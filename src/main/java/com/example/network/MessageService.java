package com.example.network;


import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.google.common.collect.Maps;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * 处理cmdId和proto数据对象以及处理方法的映射
 * Created by wdf on 2018/9/29.
 */

public enum MessageService {

    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private static final String MSG_CFG_PATH = "protobuff/msgcfg";

    private Map<Short, Parser<? extends Message>> msgId2ProtoParser = Maps.newHashMap();
    private Map<Class<? extends Message>, Short> class2CmdId = Maps.newHashMap();
    private Map<Short, AbstractMsgHandler> msgId2Handler = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public void init() {
        // 解析books.xml文件
        // 创建SAXReader的对象reader
        SAXReader reader = new SAXReader();
        try {
            File dir = new File(MSG_CFG_PATH);
            if (!dir.isDirectory()) {
                logger.error("MSG_CFG_PATH is not a dir");
                throw new RuntimeException("MSG_CFG_PATH is not a dir");
            }
            File[] fs = dir.listFiles(file -> file.getName().endsWith(".xml"));
            if (fs == null) {
                logger.error("no msg cfg file");
                throw new RuntimeException("no msg cfg file");
            }
            for (File file : fs) {
                Document document = reader.read(file);
                Element root = document.getRootElement();
                // 通过element对象的elementIterator方法获取迭代器
                Iterator it = root.elementIterator();
                // 遍历迭代器，获取根节点中的信息
                while (it.hasNext()) {
                    Element element = (Element) it.next();
                    Short cmdId = Short.parseShort(element.attributeValue("id"));
                    Class<? extends Message> clazz = (Class<? extends Message>) Class.forName(element.attributeValue("proto"));
                    msgId2ProtoParser.put(cmdId, getProtoParse(clazz));
                    if(element.attributeValue("type").equals("client")) {
                        continue;
                    }
                    msgId2Handler.put(cmdId, instanceHandler(element.attributeValue("handler")));
                }
            }

        } catch (DocumentException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    private AbstractMsgHandler instanceHandler(String handlerClassName) throws ClassNotFoundException {
        Class<? extends AbstractMsgHandler> clazz = (Class<? extends AbstractMsgHandler>) Class.forName(handlerClassName);
        ConstructorAccess<? extends AbstractMsgHandler> constructorAccess = ConstructorAccess.get(clazz);
        return constructorAccess.newInstance();
    }

    @SuppressWarnings("unchecked")
    private Parser<? extends Message> getProtoParse(Class<? extends Message> clazz) throws ClassNotFoundException {
        ConstructorAccess<? extends Message> constructorAccess = ConstructorAccess.get(clazz);
        return constructorAccess.newInstance().getParserForType();
    }

    public Message getProtoMessage(short cmdId, byte[] data, int offset, int len) throws InvalidProtocolBufferException {
        Parser<? extends Message> parser = msgId2ProtoParser.get(cmdId);
        return parser.parseFrom(data, offset, len);
    }

    public Optional<AbstractMsgHandler> getMsgHandler(short cmdId) {
        return Optional.ofNullable(msgId2Handler.get(cmdId));
    }

    public short getCmdIdByMsgClass(Class<? extends Message> clazz) {
        if(class2CmdId.containsKey(clazz)) {
            return class2CmdId.get(clazz);
        }

        return 0;
    }

}
