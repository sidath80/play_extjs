package util;

import com.ebuilder.ngetp.service.thrift.THttpTransport;
import models.Node;
import models.Service;
import models.ServiceGroup;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import play.Logger;
import play.Play;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 */
public abstract class Runner {
    private Service service;

    private String className;
    private URL[] classUrls;
    private URL serviceUrl;

    private Object object;
    private Class clientClass;
    private Class iFaceClass;

    public Object getObject() {
        return object;
    }

    public Class getClientClass() {
        return clientClass;
    }

    public Class getIFaceClass() {
        return iFaceClass;
    }

    public Runner(Service service) {
        this.service = service;
    }

    public void init() throws MalformedURLException {
        ServiceGroup serviceGroup = service.serviceGroup;

        URL serviceUrl = null;
        for(Node node : service.serviceGroup.nodes) {
            URL tryServiceUrl = new URL("http://"
                    + node.host
                    + (node.port != null ? ":" + node.port : "")
                    + "/"
                    + "service.ServiceEndPoint"
                    + "/"
                    + service.action);

            try {
                Logger.info("Trying " + tryServiceUrl);
                tryServiceUrl.openConnection().connect();
                serviceUrl = tryServiceUrl;
                break;

            } catch (IOException e) {
                Logger.info("Not working " + tryServiceUrl);
            }

        }
        if(serviceUrl == null) {
            throw new RuntimeException("A working service point not found for " + service);
        }
        Logger.info("serviceUrl=" + serviceUrl);

        URL[] classUrls = new URL[1];
        classUrls[0] = new URL(Play.configuration.getProperty("repository.release")
                + "/"
                + serviceGroup.groupId.replaceAll("\\.", "/")
                + "/"
                + serviceGroup.artifactId
                + "-gen"
                + "/"
                + serviceGroup.version
                + "/"
                + serviceGroup.artifactId
                + "-gen-"
                + serviceGroup.version
                + ".jar");
        Logger.info("classUrl=" + classUrls[0]);
        
        this.classUrls = classUrls;
        this.serviceUrl = serviceUrl;
        this.className = service.genClass;
    }

    public Object start() {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            init();

            URLClassLoader newClassLoader = URLClassLoader.newInstance(classUrls, originalClassLoader);
            Thread.currentThread().setContextClassLoader(newClassLoader);

            Class clazz = newClassLoader.loadClass(className);
            TTransport trans = new THttpTransport(serviceUrl);
            TProtocol prot = new TBinaryProtocol(trans);

            for(Class decClass : clazz.getDeclaredClasses()) {
                if((className + "$Client").equals(decClass.getName())) {
                    clientClass = decClass;
                    Constructor cons = decClass.getConstructor(TProtocol.class);
                    object = cons.newInstance(prot);
                    Logger.debug("object " + object);
                }

                if((className + "$Iface").equals(decClass.getName())) {
                    iFaceClass = decClass;
                    Logger.debug("IFace " + iFaceClass);
                }
            }

            return run();

        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    public abstract Object run() throws Exception;
}
