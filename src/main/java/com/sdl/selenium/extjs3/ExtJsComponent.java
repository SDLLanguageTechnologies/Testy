package com.sdl.selenium.extjs3;

import com.sdl.selenium.web.By;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtJsComponent extends WebLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtJsComponent.class);

    public ExtJsComponent(By... bys) {
        getPathBuilder().defaults(By.template("visibility", "count(ancestor-or-self::*[contains(@class, 'x-hide-display')]) = 0"),
                By.template("disabled", "/ancestor-or-self::*[contains(@class, 'x-masked') or contains(@class, 'x-item-disabled')]")).init(bys);
    }

    /**
     * @param cls css class
     */
    public ExtJsComponent(String cls) {
        this(By.classes(cls));
    }

    public ExtJsComponent(WebLocator container) {
        this(By.container(container));
    }

    public ExtJsComponent(WebLocator container, String elPath) {
        this(By.container(container), By.xpath(elPath));
    }

    public ExtJsComponent(String cls, WebLocator container) {
        this(By.container(container), By.classes(cls));
    }

    public ExtJsComponent(String text, String cls, WebLocator container) {
        this(By.container(container), By.classes(cls), By.text(text));
    }

    @Override
    public boolean isVisible() {
        boolean visible = super.isVisible();
        if (visible) {
            String cls = getAttributeClass();
            if (cls != null && cls.contains("x-hide-display")) {
                visible = false;
            }
        }
        return visible;
    }

    /**
     * @return true if element has mask or some parent container has mask
     * has mask = element contains class 'x-masked'
     */
    public boolean hasMask() {
        // to make sure mask is for this element get his ID and start from it
//        String id = getAttributeId();
        String id = getCurrentElementAttribute("id");
        WebLocator mask = getMaskElement();
        if ((id == null || id.equals("")) && getPathBuilder().getContainer() != null) {
            id = getPathBuilder().getContainer().getAttributeId();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(this + " does not have ID, try to look at container ID : " + id + " > " + getPathBuilder().getContainer().getClassName());
            }
        }
        if (id != null && !id.equals("")) {
            WebLocator thisEl = new WebLocator().setId(id);
            mask.getPathBuilder().setContainer(thisEl);
        }
        boolean hasMask = mask.isElementPresent();
        if (LOGGER.isDebugEnabled() && hasMask) {
//            LOGGER.debug("MaskXPath=" + mask.getPath());
            LOGGER.debug(this + " masked : " + hasMask);
        }
        return hasMask;
    }

    public WebLocator getMaskElement() {
        String maskXPathSelector = "/ancestor-or-self::*[contains(@class, 'x-masked')]";
        return new WebLocator(By.container(this), By.xpath(maskXPathSelector));
    }

    /**
     * Wait for the element to be activated when there is deactivation mask on top of it
     *
     * @param seconds
     */
    public boolean waitToActivate(int seconds) {
        String info = toString();
        //LOGGER.debug("waitToActivate:" + seconds + " sec; " + info);
        int count = 0;
        boolean hasMask;
        while ((hasMask = hasMask()) && (count < seconds)) {
            count++;
            LOGGER.info("waitToActivate:" + (seconds - count) + " seconds; " + info);
            Utils.sleep(1000);
        }
        return !hasMask;
    }
}