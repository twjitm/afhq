package com.example.afhq.utils;


import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by 文江 on 2018/1/30.
 * 端口扫描
 */
public class ScannerPortUtils {
    private Session session;
    private Channel channel;

    private String host;
    private String username;
    private String password;
    private int port;
    private FileOutputStream fos = null;

    //private Logger log = LoggerFactory.getLogger(ScannerPortUtils.class);

    /**
     * 对外可访问 ChannelSftp对象提供的所有底层方法
     */
    public static ChannelSftp chnSftp;

    /**
     * 文件类型
     */
    public static final int FILE_TYPE = 1;
    /**
     * 目录类型
     */
    public static final int DIR_TYPE = 2;

    /**
     * 说明:构造函数
     * @param
     * @throws Exception
     */
    public ScannerPortUtils(String username, String password, String host, int port) throws Exception {
        super();
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;

    }

    public void open() {
        try {
            this.connect(this.getHost(), this.getPort(), this.getUsername(), this.getPassword());
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }

    /**
     * 连接SFTP
     *
     * @param host
     * @param port
     * @param username
     * @param password
     * @throws JSchException
     * @throws SftpException
     */
    public void connect(String host, int port, String username, String password) throws JSchException, SftpException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);
        //System.out.println("Session created.");
        session.setPassword(password);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        session.setConfig(sshConfig);
        session.connect();
        //System.out.println("Session connected.");
        channel = session.openChannel("sftp");
        //System.out.println("Channel is Opened!");
        channel.connect();
        //System.out.println("Channel is connected!");
        chnSftp = (ChannelSftp) channel;
        //System.out.println("Connected to " + host + "  is sucess!");
    }

    /**
     * 进入指定的目录并设置为当前目录
     * @param sftpPath    "/dcds/abc/" ,将abc目录设置成当前目录
     * @throws Exception
     */
    public void cd (String sftpPath) throws SftpException {
        chnSftp.cd(sftpPath);
    }
    /**
     * 得到当前用户当前工作目录地址
     * @return 返回当前工作目录地址
     * @throws SftpException
     *
     */
    public String pwd () throws SftpException {
        return chnSftp.pwd();
    }

    /**
     * 根据目录地址,文件类型返回文件或目录列表
     * @param directory 如:/home/newtouch/kftesthis/201006/08/
     * @param fileType  如：FILE_TYPE  传 1   或者DIR_TYPE  传2
     * @return 文件或者目录列表 List
     * @throws SftpException
     * @throws Exception
     *
     */
    public List<String> listFiles (String directory, int fileType) throws SftpException {
        List<String> fileList = new ArrayList<String>();
        if (isDirExist(directory)) {
            boolean itExist = false;
            @SuppressWarnings("rawtypes")
            Vector vector;
            vector = chnSftp.ls(directory);
            for (int i = 0; i < vector.size(); i++) {
                Object obj = vector.get(i);
                String str = obj.toString().trim();
                int tag = str.lastIndexOf(":") + 3;
                String strName = str.substring(tag).trim();
                itExist = isDirExist(directory + "/" + strName);
                if (fileType == FILE_TYPE) {
                    if (!(itExist)) {
                        fileList.add(directory + "/" + strName);
                    }
                }
                if (fileType == DIR_TYPE) {
                    if (itExist) {
                        //目录列表中去掉目录名为.和..
                        if (!(strName.equals(".") || strName.equals(".."))) {
                            fileList.add(directory + "/" + strName);
                        }
                    }
                }

            }
        }
        return fileList;
    }
    /**
     * 判断目录是否存在
     * @param directory
     * @return
     * @throws SftpException
     */
    public boolean isDirExist (String directory) throws SftpException {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = chnSftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }
    /**
     * 获取远程文件的流文件
     * @param sftpFilePath
     * @return
     * @throws SftpException
     */
    public InputStream getFile (String sftpFilePath) throws SftpException {
        if (isFileExist(sftpFilePath)) {
            return chnSftp.get(sftpFilePath);
        }
        return null;
    }

    /**
     * 获取远程文件流
     * @param sftpFilePath
     * @return
     * @throws SftpException
     */
    public InputStream getInputStreamFile (String sftpFilePath) throws SftpException {
        return getFile(sftpFilePath);
    }

    /**
     * 获取远程文件字节流
     * @param sftpFilePath
     * @return
     * @throws SftpException
     * @throws IOException
     */
    public ByteArrayInputStream getByteArrayInputStreamFile (String sftpFilePath) throws SftpException,IOException {
        if (isFileExist(sftpFilePath)) {
            byte[] srcFtpFileByte = inputStreamToByte(getFile(sftpFilePath));
            ByteArrayInputStream srcFtpFileStreams = new ByteArrayInputStream(srcFtpFileByte);
            return srcFtpFileStreams;
        }
        return null;
    }

    /**
     * 获取远程文件的字节数组(将文件流转换成字节数组)
     * @param sftpFilePath
     * @return
     * @throws SftpException
     * @throws IOException
     */
    public byte[] getByteArray (String sftpFilePath) throws SftpException,IOException {
        if (isFileExist(sftpFilePath)) {
            byte[] srcFtpFileByte = inputStreamToByte(getFile(sftpFilePath));
            return srcFtpFileByte;
        }
        return null;
    }

    /**
     * 删除远程文件
     * 说明:返回信息定义以:分隔第一个为代码，第二个为返回信息
     * @param sftpFilePath
     * @return
     * @throws SftpException
     */
    public String delFile (String sftpFilePath) throws SftpException {
        String retInfo = "";
        if (isFileExist(sftpFilePath)) {
            chnSftp.rm(sftpFilePath);
            retInfo = "1:File deleted.";
        }
        else {
            retInfo = "2:Delete file error,file not exist.";
        }
        return retInfo;
    }

    /**
     * 移动远程文件到目标目录
     * @param srcSftpFilePath
     * @param distSftpFilePath
     * @return 返回移动成功或者失败代码和信息
     * @throws SftpException
     * @throws IOException
     */
    public String moveFile (String srcSftpFilePath, String distSftpFilePath) throws SftpException,IOException {
        String retInfo = "";
        boolean dirExist = false;
        boolean fileExist = false;
        fileExist = isFileExist(srcSftpFilePath);
        dirExist = isDirExist(distSftpFilePath);
        if (!fileExist) {
            //文件不存在直接反回.
            return "0:file not exist !";
        }
        if (!(dirExist)) {
            //1建立目录
            createDir(distSftpFilePath);
            //2设置dirExist为true
            dirExist = true;
        }
        if (dirExist && fileExist) {

            String fileName = srcSftpFilePath.substring(srcSftpFilePath.lastIndexOf("/"), srcSftpFilePath.length());
            ByteArrayInputStream srcFtpFileStreams = getByteArrayInputStreamFile(srcSftpFilePath);
            //二进制流写文件
            chnSftp.put(srcFtpFileStreams, distSftpFilePath + fileName);
            chnSftp.rm(srcSftpFilePath);
            retInfo = "1:move success!";
        }
        return retInfo;
    }
    /**
     * 复制远程文件到目标目录
     * @param srcSftpFilePath
     * @param distSftpFilePath
     * @return
     * @throws SftpException
     * @throws IOException
     */
    public String copyFile (String srcSftpFilePath, String distSftpFilePath) throws SftpException,IOException {
        String retInfo = "";
        boolean dirExist = false;
        boolean fileExist = false;
        fileExist = isFileExist(srcSftpFilePath);
        dirExist = isDirExist(distSftpFilePath);
        if (!fileExist) {
            //文件不存在直接反回.
            return "0:file not exist !";
        }
        if (!(dirExist)) {
            //1建立目录
            createDir(distSftpFilePath);
            //2设置dirExist为true
            dirExist = true;
        }
        if (dirExist && fileExist) {

            String fileName = srcSftpFilePath.substring(srcSftpFilePath.lastIndexOf("/"), srcSftpFilePath.length());
            ByteArrayInputStream srcFtpFileStreams = getByteArrayInputStreamFile(srcSftpFilePath);
            //二进制流写文件
            chnSftp.put(srcFtpFileStreams, distSftpFilePath + fileName);
            retInfo = "1:copy file success!";
        }
        return retInfo;
    }

    /**
     * 创建远程目录
     * @param sftpDirPath
     * @return 返回创建成功或者失败的代码和信息
     * @throws SftpException
     */
    public String createDir (String sftpDirPath) throws SftpException {
        this.cd("/");
        if (this.isDirExist(sftpDirPath)) {
            return "0:dir is exist !";
        }
        String pathArry[] = sftpDirPath.split("/");
        for (String path : pathArry) {
            if (path.equals("")) {
                continue;
            }
            if (isDirExist(path)) {
                this.cd(path);
            }
            else {
                //建立目录
                chnSftp.mkdir(path);
                //进入并设置为当前目录
                chnSftp.cd(path);
            }
        }
        this.cd("/");
        return "1:创建目录成功";
    }

    /**
     * 判断远程文件是否存在
     * @param srcSftpFilePath
     * @return
     * @throws SftpException
     */
    public boolean isFileExist (String srcSftpFilePath) throws SftpException {
        boolean isExitFlag = false;
        // 文件大于等于0则存在文件
        if (getFileSize(srcSftpFilePath) >= 0) {
            isExitFlag = true;
        }
        return isExitFlag;
    }

    /** 得到远程文件大小
     * @see
     * @param srcSftpFilePath
     * @return 返回文件大小，如返回-2 文件不存在，-1文件读取异常
     * @throws SftpException
     */
    public static long getFileSize (String srcSftpFilePath) throws SftpException {
        long filesize = 0;//文件大于等于0则存在
        try {
            SftpATTRS sftpATTRS = chnSftp.lstat(srcSftpFilePath);
            filesize = sftpATTRS.getSize();
        } catch (Exception e) {
            filesize = -1;//获取文件大小异常
            if (e.getMessage().toLowerCase().equals("no such file")) {
                filesize = -2;//文件不存在
            }
        }
        return filesize;
    }

    /**
     * 关闭资源
     * @param fos  文件输出流
     */
    public void close (FileOutputStream fos) {

        try {
            if (fos != null) {
                fos.close();
            }
            if (channel.isConnected()) {
                channel.disconnect();


            }
            if (session.isConnected()) {
                session.disconnect();

            }
        } catch (IOException e) {
        }

    }

    /**
     * inputStream类型转换为byte类型
     * @param iStrm
     * @return
     * @throws IOException
     */
    public byte[] inputStreamToByte (InputStream iStrm) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = iStrm.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }


    /**
     * 下载文件
     * @param ftpFileName 具体的远程路径+文件名称
     * @param localDir  存放本地文件的路径
     * @throws SftpException
     * @throws JSchException
     */
    public void down(String remotePath ,String ftpFileName, String localDir) throws JSchException, SftpException {
        open();
        downFileOrDir(remotePath,ftpFileName, localDir);
        close(fos);
    }

    /**
     * 下载文件
     *
     * @param remotepath  远程文件的目录路径  "/dcds/abc/"
     * @param ftpFileName 远程文件名称  "abc.txt"
     * @param localDir 将远程文件下载后 ，存放文件的路径（本地）"d://temp"
     */
    private void downFileOrDir(String remotepath ,String ftpFileName, String localDir) {
        try {


            //1 得到当前工作目录地址
            System.out.println("操作1 得到当前工作目录地址："+pwd());
            //2 改变目录为配置的远程目录
            cd(remotepath);
            System.out.println("操作2 改变目录为配置的远程目录："+pwd());

            File localfile = new File(localDir + File.separator +ftpFileName);
            localfile.createNewFile();

            byte[] b = getByteArray(ftpFileName);
            fos = new FileOutputStream(localfile);
            fos.write(b);

            System.out.println("下载文件到"+localDir+"成功！");
        } catch (Exception e) {
            System.out.println("下载失败！");
        }

    }

    public static void main(String[] args) {
        try {
            ScannerPortUtils sftp = new ScannerPortUtils("root", "twjitm520", "67.209.190.39", 26482);
            sftp.down("/usr/tomcat/","apache-tomcat-8.5.16.tar.gz", "c://");


        } catch (Exception e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
    }





    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
