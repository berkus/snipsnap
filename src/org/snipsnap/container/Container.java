package org.snipsnap.container;

import java.util.Collection;

public interface Container {
    public Object getComponent(Class c);
    public Collection findComponents(Class c);
    public boolean containsComponent(Class c);
    public void addComponent(Class c);
}