package com.sdl.selenium.extjs3.tab;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.extjs3.ExtJsComponent;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebDriverConfig;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TabPanel extends ExtJsComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(TabPanel.class);

    private TabPanel() {
        setClassName("TabPanel");
        setBaseCls("x-tab-panel");
        setExcludeClasses("x-masked");
    }

    public TabPanel(String text, SearchType ...searchType) {
        this();
        setText(text, searchType);
        setElPathSuffix("elPathSuffix", "count(*[contains(@class,'x-tab-panel-header')]//*[contains(@class, 'x-tab-strip-active')]//*[" + getItemPathText() + "]) > 0]/*/*[contains(@class, 'x-tab-panel-body')]/*[not(contains(@class, 'x-hide-display'))");
        setText(null);
    }

    public TabPanel(String text) {
        this(text, SearchType.EQUALS);
    }

    public TabPanel(WebLocator container, String text, SearchType searchType) {
        this(text, searchType);
        setContainer(container);
    }

    public TabPanel(WebLocator container, String text) {
        this(text);
        setContainer(container);
    }

    private String getTitlePath() {
        String returnPath = "";
        if (hasText()) {
            returnPath = "//*[contains(@class,'x-tab-panel-header')]//*[" + getItemPathText() + "]";
        }
        return returnPath;
    }

    /**
     * After the tab is set to active will wait 50ms to make sure tab is rendered
     *
     * @return true or false
     */
    public boolean setActive() {
        String baseTabPath = "//*[" + getBasePath() + "]";
        String titlePath = baseTabPath + getTitlePath();
        WebLocator titleElement = new WebLocator(getContainer()).setElPath(titlePath).setInfoMessage(getText() + " Tab");
        LOGGER.info("setActive : " + toString());
        boolean activated;
        try {
            activated = titleElement.click();
        } catch (ElementNotVisibleException e) {
            LOGGER.error("setActive Exception: " + e.getMessage());
            WebLocator tabElement = new WebLocator(getContainer()).setElPath(baseTabPath);
            String id = tabElement.getAttributeId();
            String path = "//*[@id='" + id + "']//*[contains(@class, 'x-tab-strip-inner')]";
            String script = "return Ext.getCmp('" + id + "').setActiveTab(" + getTabCount(getText(), path) + ");";
            LOGGER.warn("force TabPanel setActive with js: " + script);
            WebLocatorUtils.doExecuteScript(script);
            activated = true; // TODO verify when is not executed
        }
        if (activated) {
            Utils.sleep(300); // need to make sure this tab is rendered
        }
        return activated;
    }

    public int getTabCount(String nameTab, String path) {
        List<WebElement> element = WebDriverConfig.getDriver().findElements(By.xpath(path));
        int count = 0;
        for (WebElement el : element) {
            if (nameTab.equals(el.getText())) {
                LOGGER.debug(count + " : " + el.getText());
                return count;
            }
            count++;
        }
        return -1;
    }
}