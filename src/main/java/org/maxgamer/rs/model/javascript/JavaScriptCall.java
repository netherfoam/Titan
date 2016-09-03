package org.maxgamer.rs.model.javascript;

import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.Undefined;

public class JavaScriptCall {
    private JavaScriptFiber fiber;
    private ContinuationPending state;
    private Object result;
    private String name;

    public JavaScriptCall(JavaScriptFiber fiber, String name) {
        this.fiber = fiber;
        this.name = name;
    }

    public String getFunction() {
        return name;
    }

    public JavaScriptFiber getFiber() {
        return fiber;
    }

    public boolean isFinished() {
        return state == null;
    }

    public ContinuationPending getState() {
        return state;
    }

    public void setState(ContinuationPending state) {
        this.state = state;
    }

    public Object getResult() {
        if (isFinished() == false) {
            throw new IllegalStateException("JavaScriptCall not complete!");
        }
        return result;
    }

    public void setResult(Object o) {
        setState(null);
        if (o instanceof Undefined) {
            o = null;
        }
        this.result = o;
    }

    public void terminate() {
        setState(null);
    }

    @Override
    public String toString() {
        return getFunction() + "(..)";
    }
}