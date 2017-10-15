package org.fengfei.lanproxy.protocol;

import java.util.Arrays;

/**
 * 代理客户端与代理服务器消息交换协议
 *
 * @author fengfei
 *
 */
public class ProxyMessage {

    /** 心跳消息 */
    public static final byte TYPE_HEARTBEAT = 0x07;

    /** 认证消息，检测clientKey是否正确 */
    public static final byte TYPE_AUTH = 0x01;

    /** 保活确认消息 */
    public static final byte TYPE_ACK = 0x02;

    /** 代理后端服务器建立连接消息 */
    public static final byte TYPE_CONNECT = 0x03;

    /** 代理后端服务器断开连接消息 */
    public static final byte TYPE_DISCONNECT = 0x04;

    /** 代理数据传输 */
    public static final byte TYPE_TRANSFER = 0x05;

    /** 用户与代理服务器以及代理客户端与真实服务器连接是否可写状态同步 */
    public static final byte TYPE_WRITE_CONTROL = 0x06;

    /** 消息类型 */
    private byte type;

    /** 消息流水号 */
    private long serialNumber;

    /** 消息命令请求信息 */
    private String uri;

    /** 消息传输数据 */
    private byte[] data;

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getType() {
        return type;
    }

    public String convertTypeName(){
        String typeStr = "";
        switch (this.getType()) {
            case ProxyMessage.TYPE_CONNECT:
                typeStr= "代理建立连接";
                break;
            case ProxyMessage.TYPE_DISCONNECT:
                typeStr= "断开连接消息";
                break;
            case ProxyMessage.TYPE_TRANSFER:
                typeStr= "代理数据传输";
                break;
            case ProxyMessage.TYPE_WRITE_CONTROL:
                typeStr= "代理之间的同步TYPE_WRITE_CONTROL";
                break;
            default:
                break;
        }
        return typeStr;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "ProxyMessage [type=" + type + ", serialNumber=" + serialNumber + ", uri=" + uri + ", data="
                + Arrays.toString(data) + "]";
    }

}
