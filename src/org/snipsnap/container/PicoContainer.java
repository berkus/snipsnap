package org.snipsnap.container;

import dynaop.Aspects;
import dynaop.Pointcuts;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipSpaceImpl;
import org.snipsnap.snip.label.LabelManager;
import org.snipsnap.snip.attachment.storage.AttachmentStorage;
import org.snipsnap.snip.attachment.storage.FileAttachmentStorage;
import org.snipsnap.snip.storage.*;
import org.snipsnap.interceptor.custom.MissingInterceptor;
import org.snipsnap.interceptor.custom.SnipSpaceACLInterceptor;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.versioning.*;
import org.snipsnap.versioning.cookbook.CookbookDifferenceService;
import org.snipsnap.app.ApplicationStorage;
import org.snipsnap.app.PropertyFileApplicationStorage;
import org.snipsnap.app.JDBCApplicationStorage;
import org.snipsnap.app.ApplicationManager;
import org.snipsnap.jdbc.LazyDataSource;
import org.snipsnap.user.*;
import org.snipsnap.security.AccessController;
import org.snipsnap.security.DefaultAccessController;
import org.snipsnap.render.SnipRenderEngine;
import org.snipsnap.render.PlainTextRenderEngine;
import org.snipsnap.xmlrpc.*;
import org.snipsnap.notification.MessageService;
import org.snipsnap.notification.jmdns.JmDnsService;
import org.snipsnap.feeder.FeederRepository;
import org.snipsnap.feeder.BasicFeederRepository;
import org.nanocontainer.dynaop.DynaopComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.radeox.util.Service;

import javax.sql.DataSource;

public class PicoContainer implements Container {
    public final static String DEFAULT_ENGINE = "defaultRenderEngine";


    private static org.picocontainer.MutablePicoContainer container;

    public PicoContainer() {
                 Aspects aspects = new Aspects();
      aspects.interceptor(Pointcuts.instancesOf(SnipSpace.class),
                          Pointcuts.ALL_METHODS, new MissingInterceptor());
      aspects.interceptor(Pointcuts.instancesOf(SnipSpace.class),
                          Pointcuts.ALL_METHODS, new SnipSpaceACLInterceptor());

      DynaopComponentAdapterFactory factory = new DynaopComponentAdapterFactory(
          new DefaultComponentAdapterFactory(), aspects);
      MutablePicoContainer nc = new DefaultPicoContainer(factory);


      Globals globals = ConfigurationProxy.getInstance();
      String database = globals.getDatabase();
      try {
        if("file".equals(database)) {
          addComponent(UserStorage.class, PropertyFileUserStorage.class);
          addComponent(SnipStorage.class, PropertyFileSnipStorage.class);
          addComponent(VersionStorage.class, PropertyFileSnipStorage.class);
          addComponent(ApplicationStorage.class, PropertyFileApplicationStorage.class);
        } else {
          nc.registerComponentInstance(DataSource.class, new LazyDataSource());
          addComponent(SnipStorage.class, JDBCSnipStorage.class);
          addComponent(UserStorage.class, JDBCUserStorage.class);
          addComponent(VersionStorage.class, JDBCVersionStorage.class);
          addComponent(ApplicationStorage.class, JDBCApplicationStorage.class);
        }
        addComponent(AttachmentStorage.class, FileAttachmentStorage.class);
        addComponent(PermissionManager.class, DefaultPermissionManager.class);
        addComponent(AccessController.class, DefaultAccessController.class);
        addComponent(UserManager.class, DefaultUserManager.class);
        addComponent(AuthenticationService.class, DefaultAuthenticationService.class);
        addComponent(PasswordService.class);
        addComponent(SessionService.class, DefaultSessionService.class);
        addComponent(DEFAULT_ENGINE, SnipRenderEngine.class);
        addComponent(PlainTextRenderEngine.class);
        addComponent(SnipSpace.class, SnipSpaceImpl.class);

        // Sec
        // XML-RPC Handlers
        addComponent(BloggerAPI.class, BloggerHandler.class);
        addComponent(MetaWeblogAPI.class, MetaWeblogHandler.class);

        addComponent(WeblogsPingHandler.class);
        addComponent(GeneratorHandler.class);
        addComponent(WeblogHandler.class);
        addComponent(SnipSnapHandler.class);

        //Others
        //addComponent(RegexService.class);
        addComponent(MessageService.class);
        addComponent(ApplicationManager.class);
        addComponent(LabelManager.class);

        addComponent(JmDnsService.class);
        // Feeders
        addComponent(FeederRepository.class, BasicFeederRepository.class);

        // Versioning
        addComponent(VersionManager.class, DefaultVersionManager.class);
        addComponent(DifferenceService.class, CookbookDifferenceService.class);

        Iterator iterator = Service.providerClasses(Component.class);
        while (iterator.hasNext()) {
          Class component = (Class) iterator.next();
          addComponent(component);
        }

        nc.start();
//        Component component = (MessageLogService) nc.getComponentInstance(MessageLogService.class);
//       System.out.println("keys="+nc.getComponentKeys());
        container = nc;
      } catch (Exception e) {
        e.printStackTrace();
      }
      //System.out.println(" PicoContainer ok.");
    }

    public Object getComponent(Class c) {
        return container.getComponentInstanceOfType(c);
    }

    public Collection findComponents(Class c) {
        List components = container.getComponentInstances();
        List result = new ArrayList();
        Iterator iterator = components.iterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();
            if (o.getClass().equals(c)) {
                result.add(o);
            }

        }
        return result;
    }

    public boolean containsComponent(Class c) {
        return null != container.getComponentInstance(c);
    }

    public void addComponent(Class c) {
        container.registerComponentImplementation(c);
    }

    public void addComponent(Class i, Class c) {
        container.registerComponentImplementation(i, c);
    }

}