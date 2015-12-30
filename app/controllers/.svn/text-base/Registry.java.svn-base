package controllers;

import com.google.gson.*;
import models.Node;
import models.Service;
import models.ServiceGroup;
import models.ServiceProperty;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.mvc.Controller;
import util.Result;
import util.Runner;

import javax.persistence.Query;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 */
public class Registry extends Controller {
    public static void add(String body) {
        Logger.info("Registry.add body=" + body);                
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(body).getAsJsonObject();

        String groupId = root.get("groupId").getAsString();
        String artifactId = root.get("artifactId").getAsString();
        String version = root.get("version").getAsString();

        ServiceGroup serviceGroup = ServiceGroup.find("byGroupIdAndArtifactIdAndVersion", groupId, artifactId, version).first();
        if(serviceGroup == null) {
            serviceGroup = new ServiceGroup(groupId, artifactId, version);
            serviceGroup.save();

            JsonArray servicesArray = root.getAsJsonArray("services");
            if(servicesArray != null) {
                for(JsonElement element : servicesArray) {
                    JsonObject serviceMap = element.getAsJsonObject();
                    String name = serviceMap.get("name").getAsString();
                    String action = serviceMap.get("action").getAsString();
                    String genClass = serviceMap.get("genClass").getAsString();
                    String type = serviceMap.get("type").getAsString();

                    Service service = new Service(name, action, genClass, type);
                    serviceGroup.addService(service);
                    service.save();

                    JsonArray servicePropertiesArray = serviceMap.getAsJsonArray("serviceProperties");
                    if(servicePropertiesArray != null) {
                        for(JsonElement propElement : servicePropertiesArray) {
                            JsonObject propServiceMap = propElement.getAsJsonObject();
                            String propName = propServiceMap.get("name").getAsString();
                            String propValue = propServiceMap.get("value").getAsString();

                            ServiceProperty serviceProperty = ServiceProperty.find("byServiceAndName", service, propName).first();
                            if(serviceProperty == null) {
                                serviceProperty = new ServiceProperty(propName, propValue, service);
                                serviceProperty.save();
                                Logger.info("Registry.add new serviceProperty propName=" + propName);

                            } else {
                                Logger.info("Registry.add serviceProperty exists propName=" + propName);
                            }
                        }
                    }

                }
            }
            Logger.info("Registry.add new serviceGroup " + groupId + "-" + artifactId + "-" + version);

        } else {
            Logger.info("Registry.add serviceGroup exists " + groupId + "-" + artifactId + "-" + version);
        }

        String host = root.get("host").getAsString();
        String port = root.get("port").getAsString();

        Query query = JPA.em().createQuery("select n from models.Node n " +
                " where n.host = :host" +
                " and n.port = :port" +
                " and :serviceGroup in elements(n.serviceGroups)");
        query.setParameter("host", host);
        query.setParameter("port", port);
        query.setParameter("serviceGroup", serviceGroup);

        List nodes = query.getResultList();       
        if(nodes.isEmpty()) {
            Node node = new Node(host, port);
            serviceGroup.addNode(node);
            serviceGroup.save();
            node.save();
            Logger.info("Registry.add new node " + host + ":" + port);

        } else {
            Logger.info("Registry.add node exists " + host + ":" + port);
        }
    }

    public static void search(String name, String host, String type) {
        Logger.info("Registry.search name=" + name);
        StringBuilder sb = new StringBuilder();
        sb.append("select new data.ServiceResult(s.id as serviceId, g.groupId, g.artifactId, g.version, s.name, s.type)" +
                " from models.Service as s join s.serviceGroup g join g.nodes n where s.disabled = false ");

        if(StringUtils.isNotBlank(name)) {
            sb.append(" and s.name like '%" + name + "%'");
        }

        if(StringUtils.isNotBlank(host)) {
            sb.append(" and n.host like '%" + host+ "%'");
        }

        if(StringUtils.isNotBlank(type)) {
            if("RPC".equals(type))
                sb.append(" and s.type = 'RPC'");
            else if("REST".equals(type))
                sb.append(" and s.type = 'REST'");
        }

        sb.append(" group by s.id, g.groupId, g.artifactId, g.version, s.name, s.type");

        Query query = JPA.em().createQuery(sb.toString());
        List result = query.getResultList();
        renderJSON(Result.success(result));
    }

    public static void nodes(Long serviceId) {
        Logger.info("Registry.nodes serviceId=" + serviceId);
        List<Node> results = new ArrayList<Node>();
        if(serviceId != null) {
            Service service = Service.findById(serviceId);
            Set<Node> nodes = service.serviceGroup.nodes;

            for(Node node : nodes) {
                results.add(new Node(node.host, node.port));
            }
        }
        renderJSON(Result.success(results));
    }

    public static void disableService(Long serviceId) {
        Logger.info("Registry.disableService serviceId=" + serviceId);
        Service service = Service.findById(serviceId);
        service.disabled = true;
        service.save();
        renderJSON(Result.success(Result.success("true")));
    }

    public static void cloneService(Long serviceId, String serviceName) {
        Logger.info("Registry.cloneService serviceId=" + serviceId);
        Service service = Service.findById(serviceId);

        Service newService = new Service(serviceName, service.action, service.genClass, service.type);
        newService.serviceGroup = service.serviceGroup;
        newService.save();

        List<ServiceProperty> serviceProperties = ServiceProperty.find("byService", service).fetch();
        for(ServiceProperty serviceProperty : serviceProperties) {
            ServiceProperty newServiceProperty = new ServiceProperty(
                    serviceProperty.name, serviceProperty.value, newService);
            newServiceProperty.save();
            Logger.debug("copy serviceProperty propName=" + newServiceProperty.name);
        }

        renderJSON(Result.success(Result.success("true")));
    }

    public static void getServiceDetails(Long serviceId) {
        Logger.info("Registry.getServiceDetails serviceId=" + serviceId);
        Map<String, String> props = new HashMap<String, String>();
        Service service = Service.findById(serviceId);
        props.put("serviceId", String.valueOf(service.id));
        props.put("description", service.description);
        renderJSON(Result.success(props));
    }


    public static void setServiceDetails(Long serviceId, String description) {
        Logger.info("Registry.setServiceDetails serviceId=" + serviceId);
        Service service = Service.findById(serviceId);
        service.description = description;
        service.save();
        renderJSON(Result.success(Result.success("true")));
    }

    public static void setServiceProperty(final Long serviceId, final String name, final String value) {
        Logger.info("Registry.setServiceProperty serviceId=" + serviceId);

        Service service = Service.findById(serviceId);
        ServiceProperty serviceProperty = ServiceProperty.find("byServiceAndName", service, name).first();
        if(serviceProperty == null) {
            serviceProperty = new ServiceProperty(name, value, service);
        } else {
            serviceProperty.value = value;
        }
        serviceProperty.save();

        renderJSON(Result.success(Result.success("true")));
    }

    public static void getServiceProperties(final Long serviceId) {
        Logger.info("Registry.getServiceProperties serviceId=" + serviceId);
        List results = new ArrayList();
        if(serviceId != null) {
            Service service = Service.findById(serviceId);
            Query query = JPA.em().createQuery(
                    "select new data.ServicePropertyResult(p.name, p.value)" +
                    " from models.ServiceProperty as p where p.service.id = :serviceId");
            query.setParameter("serviceId", serviceId);
            results = query.getResultList();
        }
        renderJSON(Result.success(results));
    }

    public static void getServiceMethods(final Long serviceId) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        Logger.info("Registry.getServiceMethods serviceId=" + serviceId);
        Service service = Service.findById(serviceId);

        Runner runner = new Runner(service) {
            @Override
            public Object run() {
                List<String> methods = new ArrayList<String>();
                for(Method method : getIFaceClass().getMethods()) {
                    Logger.debug("method " + method);
                    String name = method.getName();
                    methods.add(name);
                }
                return methods;
            }
        };
        Object methods = runner.start();
        Logger.debug("methods " + methods);
        renderJSON(Result.success(methods));
    }

    public static void getServiceMethodArgs(final Long serviceId, final String methodName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        Logger.info("Registry.getServiceMethodArgs serviceId=" + serviceId);
        Service service = Service.findById(serviceId);

        Runner runner = new Runner(service) {
            @Override
            public Object run() throws NoSuchMethodException {
                List<String> methodArgs = new ArrayList<String>();
                for(Method method : getIFaceClass().getMethods()) {
                    Logger.debug("method " + method);
                    String name = method.getName();
                    if(name.equals(methodName)) {
                         Class[] paramTypes = method.getParameterTypes();
                        for(Class paramType : paramTypes) {
                            Logger.debug("paramType " + paramType);
                            methodArgs.add(paramType.getName());
                        }
                    }

                }
                return methodArgs;
            }
        };
        Object params = runner.start();
        Logger.debug("params " + params);
        renderJSON(Result.success(params));
    }

    public static void callServiceMethod(final Long serviceId, final String methodName, final String[] methodValues) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        Logger.info("Registry.callServiceMethod serviceId=" + serviceId);
        Service service = Service.findById(serviceId);

        Runner runner = new Runner(service) {
            @Override
            public Object run() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
                List<Class> methodArgs = new ArrayList<Class>();
                for(Method method : getIFaceClass().getMethods()) {
                    Logger.debug("method " + method);
                    String name = method.getName();
                    if(name.equals(methodName)) {
                        Class[] paramTypes = method.getParameterTypes();
                        Object[] inArgs = new Object[paramTypes.length];
                        for(int i = 0; i < paramTypes.length; i++) {
                            Class paramType = paramTypes[i];
                            Logger.debug("paramType " + paramType);
                            
                            if(paramType.isPrimitive()) {
                                if(Boolean.TYPE.equals(paramType)) {
                                    inArgs[i] = new Boolean(methodValues[i]);

                                } if(Character.TYPE.equals(paramType)) {
                                    inArgs[i] = new Character(methodValues[i].charAt(0));

                                } if(Byte.TYPE.equals(paramType)) {
                                    inArgs[i] = new Byte(methodValues[i]);

                                } if(Short.TYPE.equals(paramType)) {
                                    inArgs[i] = new Short(methodValues[i]);

                                } if(Integer.TYPE.equals(paramType)) {
                                    inArgs[i] = new Integer(methodValues[i]);

                                } if(Long.TYPE.equals(paramType)) {
                                    inArgs[i] = new Long(methodValues[i]);

                                } if(Float.TYPE.equals(paramType)) {
                                    inArgs[i] = new Float(methodValues[i]);

                                } if(Double.TYPE.equals(paramType)) {
                                    inArgs[i] = new Double(methodValues[i]);
                                }

                            } else {
                                try {
                                    Constructor cons = paramType.getConstructor(String.class);
                                    inArgs[i] = cons.newInstance(methodValues[i]);

                                } catch (NoSuchMethodException e) {
                                    Method meth = paramType.getMethod("valueOf", String.class);
                                    inArgs[i] = meth.invoke(null, methodValues[i]);
                                }
                            }

                        }
                        Logger.debug("inArgs " + Arrays.toString(inArgs));
                        Object returnObj = method.invoke(getObject(), inArgs);
                        Logger.debug("returnObj " + returnObj);
                        return returnObj;
                    }

                }
                return "Registry.callServiceMethod unexpected end reached";
            }
        };
        Object returnValue = runner.start();
        Logger.debug("returnValue " + returnValue);
        renderJSON(Result.success(returnValue.toString()));
    }

    public static void getJavaClientUrl(final Long serviceId) throws MalformedURLException {
        Logger.info("Registry.getJavaClientUrl serviceId=" + serviceId);
        Service service = Service.findById(serviceId);
        ServiceGroup serviceGroup = service.serviceGroup;

        URL url= new URL(Play.configuration.getProperty("repository.release")
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

        Logger.debug("url " + url);
        renderJSON(Result.success(url.toString()));        
    }
}
