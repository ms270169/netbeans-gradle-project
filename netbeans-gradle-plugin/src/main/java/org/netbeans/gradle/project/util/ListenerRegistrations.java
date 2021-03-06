package org.netbeans.gradle.project.util;

import java.util.LinkedList;
import java.util.List;
import org.jtrim.event.ListenerRef;

public final class ListenerRegistrations {
    private final List<ListenerRef> listenerRefs;

    public ListenerRegistrations() {
        this.listenerRefs = new LinkedList<>();
    }

    public void add(ListenerRef listenerRef) {
        listenerRefs.add(listenerRef);
    }

    public void unregisterAll() {
        for (ListenerRef listenerRef: listenerRefs) {
            listenerRef.unregister();
        }
        listenerRefs.clear();
    }
}
