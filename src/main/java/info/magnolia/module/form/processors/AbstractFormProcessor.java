package info.magnolia.module.form.processors;

import java.util.Map;

import info.magnolia.cms.core.Content;

/**
 * Abstract base class for FormProcessors that want support for enabling/disabling.
 */
public abstract class AbstractFormProcessor implements FormProcessor {

    private String name;

    private boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public final String process(Content content, Map<String, String> parameters) {
        if (enabled)
            return internalProcess(content, parameters);
        return SUCCESS;
    }

    protected abstract String internalProcess(Content content, Map<String, String> parameters);
}