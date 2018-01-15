package xyz.shanmugavel.gf.listener;

import org.apache.geode.cache.query.CqEvent;

public class CQListener {

    public void handleEvent(CqEvent event) {
        System.out.println("Received a CQ event " + event);
    }

}
