package com.github.asm0dey;

import org.apache.commons.cli.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class.
 */
public class Main {
    public static DefaultHttpClient httpclient;
    // Base URI the Grizzly HTTP server will listen on

    public static String getUri(int port) {
        return "http://0.0.0.0:" + port + "/";
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static void startServer(int port) throws IOException {
        GrizzlyHttpServerFactory.createHttpServer(URI.create(getUri(port)), new ResourceConfig().packages("com.github.asm0dey")).start();
    }

    public static void setUp(String login, String password) throws IOException {
        HttpPost auth = new HttpPost("http://login.rutracker.org/forum/login.php");
        httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("login_username", login));
        nvps.add(new BasicNameValuePair("login_password", password));
        nvps.add(new BasicNameValuePair("login", "Вход"));
        auth.setEntity(new UrlEncodedFormEntity(nvps));
        HttpResponse execute = httpclient.execute(auth);
        EntityUtils.consume(execute.getEntity());
    }

    public static void main(String[] arguments) throws IOException, ParseException {
        CommandLineParser parser = new GnuParser();
        Option port = new Option("p", "port", true, "Port on which run server");
        port.setArgs(1);
        port.setOptionalArg(true);
        Option username = new Option("U", "username", true, "username on rutracker");
        username.setArgs(1);
        username.setOptionalArg(false);
        Option password = new Option("P", "password", true, "password on rutracker");
        password.setArgs(1);
        password.setOptionalArg(false);
        Options options = new Options();
        options.addOption(port);
        options.addOption(username);
        options.addOption(password);
        CommandLine cL = parser.parse(options, arguments);
        String username1 = cL.getOptionValue("username");
        String password1 = cL.getOptionValue("password");
        String port1 = cL.getOptionValue("port");
        if (username1 == null || password1 == null) {
            printHelp(options, 80, "Options", "-- HELP --", 3, 5, true, System.out);
            System.exit(0);
        } else if (port1 == null) port1 = "8080";

        initClient();
        setUp(username1, password1);
        startServer(Integer.valueOf(port1));
        prepareForExit();
    }

    private static void prepareForExit() throws IOException {
        System.out.println("Press enter to exit");
        System.in.read();
        System.out.println("Good bye!");
    }

    private static void initClient() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        httpclient = new DefaultHttpClient(cm);
    }

    public static void printHelp(final Options options, final int printedRowWidth, final String header, final String footer, final int spacesBeforeOption, final int spacesBeforeOptionDescription, final boolean displayUsage, final OutputStream out) {
        final String commandLineSyntax = "java rutracker-server.jar";//подсказка по запуску самой программы
        final PrintWriter writer = new PrintWriter(out);// куда печатаем help
        final HelpFormatter helpFormatter = new HelpFormatter();// создаем объект для вывода help`а
        helpFormatter.printHelp(writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption, spacesBeforeOptionDescription, footer, displayUsage);//формирование справки
        writer.flush(); // вывод
    }
}

