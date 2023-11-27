package com.tannicarcher.burp;

import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.nio.charset.*;
import java.security.cert.*;
import java.security.*;
import javax.net.ssl.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class KeygenForm
{
    private static final String Version = "v1.3.3.7";
    private static JFrame frame;
    private static JButton btn_run;
    private static JTextField text_cmd;
    private static JTextField text_license_text;
    private static JTextArea text_license;
    private static JTextArea request;
    private static JTextArea response;
    private static JLabel label_copyright;
    private static JLabel label0_1;
    private static JPanel panel1;
    private static JPanel panel2;
    private static JPanel panel3;
    private static JCheckBox check_autorun;
    private static JCheckBox check_ignore;
    private static String LatestVersion;
    private static final String DownloadURL = "https://portswigger-cdn.net/burp/releases/download?product=pro&type=Jar&version=";
    private static final String ConfigFileName = "Config.TannicArcher";
    
    public static BufferedReader execCommand(final String[] command) {
        try {
            final Process proc = new ProcessBuilder(command).start();
            return new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private static int getJavaVersion(final String path) {
        final String[] command = { path, "-version" };
        final BufferedReader buf = execCommand(command);
        if (buf == null) {
            return 0;
        }
        while (true) {
            String line;
            try {
                line = buf.readLine();
            }
            catch (IOException e) {
                return 0;
            }
            if (line == null) {
                System.out.println("Warning: Cannot Get Java Version oF '" + path + "'!");
                return 0;
            }
            if (!line.contains("version")) {
                continue;
            }
            final String[] version = line.split("\"")[1].split("[.\\-]");
            if ("1".equals(version[0])) {
                return Integer.parseInt(version[1]);
            }
            return Integer.parseInt(version[0]);
        }
    }
    
    private static String getBurpPath() {
        String newest_file = "burpsuite_jar_not_found.jar";
        try {
            long newest_time = 0L;
            final File f = new File(KeygenForm.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            final String current_dir = f.isDirectory() ? f.getPath() : f.getParentFile().toString();
            final DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(current_dir, new String[0]), "burpsuite_*.jar");
            for (final Path path : dirStream) {
                if (!Files.isDirectory(path, new LinkOption[0])) {
                    if (newest_time >= path.toFile().lastModified()) {
                        continue;
                    }
                    newest_time = path.toFile().lastModified();
                    newest_file = path.toString();
                }
            }
            dirStream.close();
        }
        catch (Throwable t) {}
        return newest_file;
    }
    
    private static String[] GetCMD() {
        final File KeygenFile = new File(KeygenForm.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        final String KeygenFileName = KeygenFile.getName();
        final String JAVA_PATH = getJavaPath();
        final int JAVA_VERSION = getJavaVersion(JAVA_PATH);
        final String BURP_PATH = getBurpPath();
        final ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(JAVA_PATH);
        if (JAVA_VERSION == 0) {
            return new String[] { "Cannot Find Java! Please Install Java SE Development Kit Latest Version" };
        }
        if (JAVA_VERSION == 16) {
            cmd.add("--illegal-access=permit");
        }
        if (JAVA_VERSION >= 17) {
            cmd.add("--add-opens=java.desktop/javax.swing=ALL-UNNAMED");
            cmd.add("--add-opens=java.base/java.lang=ALL-UNNAMED");
            cmd.add("--add-opens=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED");
            cmd.add("--add-opens=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED");
            cmd.add("--add-opens=java.base/jdk.internal.org.objectweb.asm.Opcodes=ALL-UNNAMED");
        }
        if (JAVA_VERSION <= 8) {
            return new String[] { "Not support Java 8, Please Update Java SE Development Kit Latest Version" };
        }
        cmd.add("-javaagent:" + KeygenFileName);
        cmd.add("-noverify");
        cmd.add("-jar");
        cmd.add(BURP_PATH);
        return cmd.toArray(new String[0]);
    }
    
    private static String GetCMDStr(final String[] cmd) {
        final StringBuilder cmd_str = new StringBuilder();
        for (final String x : cmd) {
            cmd_str.append("\"").append(x).append("\" ");
        }
        return cmd_str.toString();
    }
    
    private static boolean verifyFile(final File javafile) {
        if (!javafile.exists() || javafile.isDirectory()) {
            return false;
        }
        if (!javafile.canExecute()) {
            System.out.println("Warning: '" + javafile.getPath() + "' Can Not Execute!");
            return false;
        }
        System.out.println("\u001b[32mSuccess\u001b[0m: '" + javafile.getPath() + "' Can Execute!");
        return true;
    }
    
    private static String verifyPath(final String path) {
        File javafile = new File(path);
        if (verifyFile(javafile)) {
            return javafile.getPath();
        }
        javafile = new File(path + ".exe");
        if (verifyFile(javafile)) {
            return javafile.getPath();
        }
        return null;
    }
    
    private static String getJavaPath() {
        final File KeygenFile = new File(KeygenForm.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        final String[] paths = { KeygenFile.getParent() + File.separator + "bin", KeygenFile.getParent() + File.separator + "jre" + File.separator + "bin", KeygenFile.getParent() + File.separator + "jdk" + File.separator + "bin", System.getProperty("java.home") + File.separator + "bin" };
        String java_path = null;
        for (String path_str : paths) {
            try {
                path_str = URLDecoder.decode(path_str, "utf-8");
            }
            catch (Exception ex) {}
            java_path = verifyPath(path_str + File.separator + "java");
            if (java_path != null) {
                break;
            }
            try {
                DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(path_str, new String[0]), "java[0-9]{1,2}");
                for (final Path path : dirStream) {
                    if (!Files.isDirectory(path, new LinkOption[0])) {
                        if (!Files.isExecutable(path)) {
                            continue;
                        }
                        return path.toString();
                    }
                }
                dirStream = Files.newDirectoryStream(Paths.get(path_str, new String[0]), "java[0-9]{1,2}\\.exe");
                for (final Path path : dirStream) {
                    if (!Files.isDirectory(path, new LinkOption[0])) {
                        if (!Files.isExecutable(path)) {
                            continue;
                        }
                        return path.toString();
                    }
                }
            }
            catch (IOException ex2) {}
        }
        return java_path;
    }
    
    private static String readProperty(final String key) {
        final Properties properties = new Properties();
        final File file = new File("Config.TannicArcher");
        try {
            file.createNewFile();
        }
        catch (Exception ignored) {
            return "0";
        }
        try {
            final InputStream inStream = Files.newInputStream(file.toPath(), new OpenOption[0]);
            properties.load(inStream);
        }
        catch (IOException e) {
            return "0";
        }
        return properties.getProperty(key);
    }
    
    private static void setProperty(final String key, final String value) {
        final Properties properties = new Properties();
        try {
            final InputStream inStream = Files.newInputStream(Paths.get("Config.TannicArcher", new String[0]), new OpenOption[0]);
            properties.load(inStream);
            properties.setProperty(key, value);
            final FileOutputStream out = new FileOutputStream("Config.TannicArcher", false);
            properties.store(out, "\n Burp Suite Pro Private Loader [ ALL Versions Modded By TannicArcher ] # [ TannicArcher ]\n Co-Author : TannicArcher\n WebSite : TannicArcher\n");
            out.close();
        }
        catch (IOException ex) {}
    }
    
    public static String GetHTTPBody(final String url) {
        try {
            final URL realUrl = new URL(url);
            final HttpsURLConnection https = (HttpsURLConnection)realUrl.openConnection();
            https.connect();
            if (https.getResponseCode() == 200) {
                final InputStream is = https.getInputStream();
                final BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                final StringBuilder sbf = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                return sbf.toString();
            }
        }
        catch (Exception ex) {}
        return "";
    }
    
    private static void trustAllHosts() {
        final TrustManager[] trustAllCerts = { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                
                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                }
                
                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                }
            } };
        try {
            final SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String GetLatestVersion() {
        final String url = "https://github.com/TannicArcher";
        if (Desktop.isDesktopSupported()) {
            final Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            }
            catch (IOException | URISyntaxException ex2) {
                final Exception ex;
                final Exception e = ex;
                e.printStackTrace();
            }
        }
        else {
            final Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        String result = GetHTTPBody("https://portswigger.net/burp/releases/data?pageSize=5");
        int a = result.indexOf("\"ProductId\":\"pro\",\"ProductPlatform\":\"Jar\",\"ProductPlatformLabel\":\"JAR\"");
        if (a == -1) {
            return "";
        }
        result = result.substring(a + 166);
        a = result.indexOf("\"");
        result = result.substring(0, a);
        return result;
    }
    
    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            trustAllHosts();
        }
        catch (Exception ex) {}
        if (readProperty("Auto_Run") == null) {
            setProperty("Auto_Run", "0");
            setProperty("Ignore", "0");
        }
        final String[] cmd = GetCMD();
        final String cmd_str = GetCMDStr(cmd);
        if (readProperty("Auto_Run").equals("1")) {
            try {
                new ProcessBuilder(cmd).start();
                if (readProperty("Ignore").equals("1") || cmd_str.contains(GetLatestVersion() + ".jar")) {
                    System.exit(0);
                }
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        KeygenForm.panel1 = new JPanel();
        KeygenForm.panel2 = new JPanel();
        KeygenForm.panel3 = new JPanel();
        KeygenForm.frame = new JFrame("Burp Suite Pro Private Loader [ ALL Versions Modded By TannicArcher ] # [ TannicArcher ]");
        KeygenForm.btn_run = new JButton("Run");
        KeygenForm.label0_1 = new JLabel("Checking The Latest Version oF Burp Suite Pro");
        final JLabel label1 = new JLabel("Loader Command:", 4);
        final JLabel label2 = new JLabel("License Text:", 4);
        (KeygenForm.label_copyright = new JLabel("CopyLeft © 1998-2099 TannicArcher, All Rights Reserved.")).setForeground(new Color(2, 90, 166));
        KeygenForm.label_copyright.setCursor(Cursor.getPredefinedCursor(12));
        KeygenForm.label_copyright.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                super.mouseClicked(e);
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/TannicArcher"));
                }
                catch (Exception ex) {}
            }
        });
        KeygenForm.text_cmd = new JTextField(cmd_str);
        (KeygenForm.text_license_text = new JTextField("Licensed to : TannicArcher [ TannicArcher ]")).setForeground(new Color(2, 92, 166));
        KeygenForm.text_license = new JTextArea(Keygen.generateLicense(KeygenForm.text_license_text.getText()));
        KeygenForm.request = new JTextArea();
        KeygenForm.response = new JTextArea();
        KeygenForm.check_autorun = new JCheckBox("Auto Run");
        KeygenForm.check_ignore = new JCheckBox("Ignore Update");
        KeygenForm.check_autorun.setBounds(150, 25, 120, 20);
        KeygenForm.check_autorun.setSelected(readProperty("Auto_Run").equals("1"));
        KeygenForm.check_autorun.addChangeListener(changeEvent -> {
            if (KeygenForm.check_autorun.isSelected()) {
                setProperty("Auto_Run", "1");
            }
            else {
                setProperty("Auto_Run", "0");
            }
            return;
        });
        KeygenForm.check_ignore.setBounds(270, 25, 160, 20);
        KeygenForm.check_ignore.setSelected(readProperty("Ignore").equals("1"));
        KeygenForm.check_ignore.addChangeListener(changeEvent -> {
            if (KeygenForm.check_ignore.isSelected()) {
                setProperty("Ignore", "1");
            }
            else {
                setProperty("Ignore", "0");
            }
            return;
        });
        KeygenForm.label0_1.setLocation(150, 5);
        label1.setBounds(5, 50, 140, 22);
        KeygenForm.text_cmd.setLocation(150, 50);
        KeygenForm.btn_run.setSize(70, 25);
        label2.setBounds(5, 77, 140, 22);
        KeygenForm.text_license_text.setLocation(150, 77);
        KeygenForm.panel1.setBorder(BorderFactory.createTitledBorder("[ License Key ]    ( Ctrl + A )    >>>>    ( Ctrl + C )"));
        KeygenForm.panel2.setBorder(BorderFactory.createTitledBorder("[ Activation Request ]"));
        KeygenForm.panel3.setBorder(BorderFactory.createTitledBorder("[ Activation Response ]    ( Ctrl + A )    >>>>    ( Ctrl + C )"));
        KeygenForm.text_license.setLocation(10, 15);
        KeygenForm.request.setLocation(10, 15);
        KeygenForm.response.setLocation(10, 15);
        KeygenForm.panel1.setLocation(5, 124);
        KeygenForm.panel1.setLayout(null);
        KeygenForm.panel2.setLayout(null);
        KeygenForm.panel3.setLayout(null);
        KeygenForm.frame.setLayout(null);
        KeygenForm.frame.setMinimumSize(new Dimension(875, 325));
        KeygenForm.frame.setLocationRelativeTo(null);
        KeygenForm.frame.setDefaultCloseOperation(3);
        KeygenForm.frame.setBackground(Color.LIGHT_GRAY);
        KeygenForm.frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                final int H = KeygenForm.frame.getHeight() - 190;
                final int W = KeygenForm.frame.getWidth();
                KeygenForm.text_cmd.setSize(W - 245, 22);
                KeygenForm.btn_run.setLocation(W - 90, 48);
                KeygenForm.text_license_text.setSize(W - 170, 35);
                KeygenForm.label0_1.setSize(W - 170, 20);
                KeygenForm.label_copyright.setSize(W - 170, 20);
                KeygenForm.label_copyright.setBounds(8, H + 156 - 28, W - 16, 20);
                KeygenForm.text_license.setSize((W - 15) / 2 - 25, H / 2 - 25);
                KeygenForm.request.setSize((W - 15) / 2 - 25, H / 2 - 25);
                KeygenForm.response.setSize(W - 43, H / 2 - 25);
                KeygenForm.panel1.setSize((W - 15) / 2 - 5, H / 2);
                KeygenForm.panel2.setBounds(3 + (W - 15) / 2, 124, (W - 15) / 2 - 5, H / 2);
                KeygenForm.panel3.setBounds(5, 129 + H / 2, W - 23, H / 2);
            }
        });
        KeygenForm.btn_run.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                super.mouseClicked(e);
                try {
                    Runtime.getRuntime().exec(GetCMD());
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
        KeygenForm.text_license.setLineWrap(true);
        KeygenForm.text_license.setEditable(false);
        KeygenForm.text_cmd.setEditable(false);
        KeygenForm.text_license_text.setEditable(false);
        KeygenForm.text_license_text.setHorizontalAlignment(0);
        KeygenForm.text_license.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        KeygenForm.text_license_text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                KeygenForm.text_license.setText(Keygen.generateLicense(KeygenForm.text_license_text.getText()));
            }
            
            @Override
            public void removeUpdate(final DocumentEvent e) {
                KeygenForm.text_license.setText(Keygen.generateLicense(KeygenForm.text_license_text.getText()));
            }
            
            @Override
            public void changedUpdate(final DocumentEvent e) {
                KeygenForm.text_license.setText(Keygen.generateLicense(KeygenForm.text_license_text.getText()));
            }
        });
        KeygenForm.request.setLineWrap(true);
        KeygenForm.request.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        KeygenForm.request.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                KeygenForm.response.setText(Keygen.generateActivation(KeygenForm.request.getText()));
            }
            
            @Override
            public void removeUpdate(final DocumentEvent e) {
                KeygenForm.response.setText(Keygen.generateActivation(KeygenForm.request.getText()));
            }
            
            @Override
            public void changedUpdate(final DocumentEvent e) {
                KeygenForm.response.setText(Keygen.generateActivation(KeygenForm.request.getText()));
            }
        });
        KeygenForm.response.setLineWrap(true);
        KeygenForm.response.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        KeygenForm.frame.add(KeygenForm.check_autorun);
        KeygenForm.frame.add(KeygenForm.check_ignore);
        KeygenForm.frame.add(KeygenForm.btn_run);
        KeygenForm.frame.add(KeygenForm.label0_1);
        KeygenForm.frame.add(KeygenForm.label_copyright);
        KeygenForm.frame.add(label1);
        KeygenForm.frame.add(label2);
        KeygenForm.frame.add(KeygenForm.panel1);
        KeygenForm.frame.add(KeygenForm.panel2);
        KeygenForm.frame.add(KeygenForm.panel3);
        KeygenForm.frame.add(KeygenForm.text_cmd);
        KeygenForm.frame.add(KeygenForm.text_license_text);
        KeygenForm.panel1.add(KeygenForm.text_license);
        KeygenForm.panel2.add(KeygenForm.request);
        KeygenForm.panel3.add(KeygenForm.response);
        if (KeygenForm.text_cmd.getText().contains("burpsuite_jar_not_found.jar")) {
            KeygenForm.btn_run.setEnabled(false);
            KeygenForm.check_autorun.setSelected(false);
            KeygenForm.check_autorun.setEnabled(false);
        }
        KeygenForm.frame.setVisible(true);
        KeygenForm.btn_run.setFocusable(false);
        KeygenForm.LatestVersion = GetLatestVersion();
        if (KeygenForm.LatestVersion.equals("")) {
            KeygenForm.label0_1.setText("Failed To Check The Latest Version oF Burp Suite Pro");
        }
        else if (!cmd_str.contains(KeygenForm.LatestVersion + ".jar")) {
            KeygenForm.label0_1.setText("Latest Version: ( " + KeygenForm.LatestVersion + " ) Click To Download");
            KeygenForm.label0_1.setForeground(Color.RED);
            KeygenForm.label0_1.setCursor(Cursor.getPredefinedCursor(12));
            KeygenForm.label0_1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    super.mouseClicked(e);
                    try {
                        Desktop.getDesktop().browse(new URI("https://portswigger-cdn.net/burp/releases/download?product=pro&type=Jar&version=" + KeygenForm.LatestVersion));
                    }
                    catch (Exception ex) {}
                }
            });
        }
        else {
            KeygenForm.label0_1.setText("Your Burp Suite Pro is Already The Latest Version ( " + KeygenForm.LatestVersion + " )");
            KeygenForm.label0_1.setForeground(new Color(0, 100, 0));
        }
    }
}
