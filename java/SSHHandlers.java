package com.me.ems.ssh;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;

public class SSHHandlers {
    private final String host;
    private final String user;
    private final String password;
    private final Session session;


    public SSHHandlers(String host, String user, String password) throws JSchException {
        this.host = host;
        this.user = user;
        this.password = password;
        this.session = connect();
    }

    private Session connect() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        return session;
    }


    public void copyFile(String sourceFile, String destinationFile) throws JSchException, FileNotFoundException, SftpException {
        ChannelSftp sftpChannel = (ChannelSftp) this.session.openChannel("sftp");
        sftpChannel.connect();
        File localFile = new File(sourceFile);
        if (!localFile.exists()) {
            throw new FileNotFoundException("Local file does not exist: " + sourceFile);
        }
        sftpChannel.put(convertToSSHSupportedPathFormat(sourceFile), convertToSSHSupportedPathFormat(destinationFile));
        sftpChannel.run();
    }

    public void disconnect() {
        this.session.disconnect();
    }


    public static String convertToSSHSupportedPathFormat(String path) {

        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        String sshPath = path.replace("\\", "/");
        if (sshPath.matches("^[A-Za-z]:.*")) {
            sshPath = "/" + sshPath;
        }
        return sshPath;
    }

    public static void main(String[] args) throws JSchException, IOException, SftpException {
        String host = "remotemachine";
        String user = "admin";
        String password = "password";
        SSHHandlers sshHandlers = new SSHHandlers(host, user, password);
        String sourceFilePath = "D:\\Development\\test.pdf";
		String destinationFilePath = "C:\\users\\admin\\downloads\\test.pdf";
        sshHandlers.copyFile(sourceFilePath, destinationFilePath);
        sshHandlers.disconnect();
    }

}
