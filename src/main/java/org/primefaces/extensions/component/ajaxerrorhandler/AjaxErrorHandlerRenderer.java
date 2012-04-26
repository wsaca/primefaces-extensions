/*
 * Copyright 2011 PrimeFaces Extensions.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */
package org.primefaces.extensions.component.ajaxerrorhandler;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.extensions.util.GsonConverter;
import org.primefaces.renderkit.CoreRenderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AjaxErrorHandlerRenderer
 *
 * @author Pavol Slany / last modified by $Author$
 * @version $Revision$
 * @since 0.5
 */
public class AjaxErrorHandlerRenderer extends CoreRenderer {
    private String hostnameRequestKey = AjaxErrorHandlerRenderer.class.getCanonicalName()+".hostnameIsDefined";

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        encodeScript(context, (AjaxErrorHandler) component);
    }


    protected void encodeScript(final FacesContext context, final AjaxErrorHandler ajaxErrorHandler) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ajaxErrorHandler.getClientId(context);
        String widgetVar = ajaxErrorHandler.resolveWidgetVar();


        Map<String, String> values = getConfigJSON(ajaxErrorHandler);
        boolean hostnameIsDefined = context.getExternalContext().getRequestMap().get(hostnameRequestKey)!=null;


        // If component has facet ...
//        writeFacetIfExists(context, writer, ajaxErrorHandler, values, AjaxErrorHandler.PropertyKeys.title.toString(), clientId);
//        writeFacetIfExists(context, writer, ajaxErrorHandler, values, AjaxErrorHandler.PropertyKeys.body.toString(), clientId);
//        writeCustomBodyIfExists(context, writer, ajaxErrorHandler, values, clientId);

        if (values.size()>0) {
            String json = GsonConverter.getGson().toJson(values);
            startScript(writer, clientId);
            if (!hostnameIsDefined) {
                writer.write("PrimeFacesExt.AjaxErrorHandler.setHostname('"+getHostname()+"');");
                hostnameIsDefined = true;
                context.getExternalContext().getRequestMap().put(hostnameRequestKey, true);
            }
            if (widgetVar!=null)
                writer.write(widgetVar+" = ");

            writer.write("PrimeFacesExt.AjaxErrorHandler.addErrorSettings("+json+");");
            endScript(writer);
        }

        // If hostname is not added ...
        if (!hostnameIsDefined) {
            startScript(writer, clientId);
            writer.write("PrimeFacesExt.AjaxErrorHandler.setHostname('"+getHostname()+"');");
            context.getExternalContext().getRequestMap().put(hostnameRequestKey, true);
            endScript(writer);
        }
    }
    protected String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (Exception e) {
            return "???unknown???";
        }
    }


    private final static String CONF_TYPE = "type";
    private final static String CONF_TITLE = "title";
    private final static String CONF_BODY = "body";
    private final static String CONF_BUTTON_TEXT = "buttonText";
    private final static String CONF_BUTTON_ONCLICK = "buttonOnClick";
    private final static String CONF_ONERROR = "onerror";
//    private final static String CONF_CUSTOM_CONTENT_ID = "customContentId";
    protected Map<String, String> getConfigJSON(final AjaxErrorHandler ajaxErrorHandler) {
        Map<String, String> values = new LinkedHashMap<String, String>();
        if (!StringUtils.isEmpty(ajaxErrorHandler.getType())) {
            values.put(CONF_TYPE, ajaxErrorHandler.getType());
        }
        if (ajaxErrorHandler.getTitle() != null) {
            values.put(CONF_TITLE, ajaxErrorHandler.getTitle());
        }
        if (!StringUtils.isEmpty(ajaxErrorHandler.getBody())) {
            values.put(CONF_BODY, ajaxErrorHandler.getBody());
        }
        if (!StringUtils.isEmpty(ajaxErrorHandler.getButton())) {
            values.put(CONF_BUTTON_TEXT, ajaxErrorHandler.getButton());
        }
        if (!StringUtils.isEmpty(ajaxErrorHandler.getButtonOnclick())) {
            values.put(CONF_BUTTON_ONCLICK, ajaxErrorHandler.getButtonOnclick());
        }
        if (!StringUtils.isEmpty(ajaxErrorHandler.getOnerror())) {
            values.put(CONF_ONERROR, ajaxErrorHandler.getOnerror());
        }
        return values;
    }

//    protected void writeFacetIfExists(FacesContext facesContext, ResponseWriter writer, AjaxErrorHandler ajaxErrorHandler, Map<String, String> jsonConfig, String type, String clientId) throws IOException {
//        UIComponent facet = ajaxErrorHandler.getFacet(type);
//        if (facet == null) return;
//
//        String facetId = clientId+"_"+type;
//
//        writer.startElement("div", null);
//        writer.writeAttribute("style","display:none;",null);
//        writer.writeAttribute("id", facetId, null);
//
//        renderChild(facesContext, facet);
//
//        writer.endElement("div");
//
//        jsonConfig.put(type+"Id", facetId);
//    }
//
//    protected void writeCustomBodyIfExists (FacesContext facesContext, ResponseWriter writer, AjaxErrorHandler ajaxErrorHandler, Map<String, String> jsonConfig, String clientId) throws IOException {
//        if (ajaxErrorHandler.getChildCount()==0) return;
//
//
//        String contentId = clientId+"_customContent";
//
//        writer.startElement("div", null);
//        writer.writeAttribute("style","display:none;",null);
//        writer.writeAttribute("id", contentId, null);
//
//        for (UIComponent child : ajaxErrorHandler.getChildren()) {
//            renderChild(facesContext, child);
//        }
//
//        writer.endElement("div");
//
//        jsonConfig.put(CONF_CUSTOM_CONTENT_ID, contentId);
//    }

//    @Override
//    protected void startScript(ResponseWriter writer, String clientId) throws IOException {
//        super.startScript(writer, clientId);
//        writer.write("$(function(){");
//    }
//
//    @Override
//    protected void endScript(ResponseWriter writer) throws IOException {
//        writer.write("});");
//        super.endScript(writer);
//    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }
    @Override
    public boolean getRendersChildren() {
        return true;
    }
}