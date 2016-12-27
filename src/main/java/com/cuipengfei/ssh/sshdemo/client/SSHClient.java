package com.cuipengfei.ssh.sshdemo.client;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by cuipengfei on 16-12-27.
 */
public class SSHClient {

    private Session session;
    private String cmd = "cd /data/logs && ls";// 要运行的命令

    /**
     * 初始化链接ssh建立session
     *
     * @throws Exception
     */
    public void init() throws Exception {
        JSch jsch = new JSch(); // 创建JSch对象
        String userName = "root";// 用户名
        String password = "root";// 密码
        String host = "10.10.33.162";// 服务器地址
        int port = 22;// 端口号
        session = jsch.getSession(userName, host, port); // 根据用户名，主机ip，端口获取一个Session对象
        session.setPassword(password); // 设置密码
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config); // 为Session对象设置properties
        int timeout = 60000000;
        session.setTimeout(timeout); // 设置timeout时间
        session.connect(); // 通过Session建立链接
    }

    /**
     * 执行命令
     *
     * @param cmd
     * @return
     * @throws Exception
     */
    public String excute(String cmd) throws Exception {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(cmd);
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        InputStream in = channelExec.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        String buf = null;
        StringBuffer sb = new StringBuffer();
        while ((buf = reader.readLine()) != null) {
            String bufff=buf.substring(buf.indexOf(":")+1,buf.length());
            sb.append(bufff+",");
            System.out.println(bufff);// 打印控制台输出
        }
        reader.close();
        channelExec.disconnect();
        if(sb.length()>0){
            return sb.substring(0,sb.length()-1).toString();
        }else{
            return sb.toString();
        }

    }

    /**
     * 销毁
     */
    public void destroy() {
        if (null != session) {
            session.disconnect();
        }
    }

}
