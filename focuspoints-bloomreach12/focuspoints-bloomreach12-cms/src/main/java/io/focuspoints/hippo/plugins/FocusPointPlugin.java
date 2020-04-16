package io.focuspoints.hippo.plugins;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.image.JcrImage;
import org.hippoecm.frontend.resource.JcrResourceStream;
import org.hippoecm.frontend.service.IEditor;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FocusPointPlugin extends RenderPlugin<Node> {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(FocusPointPlugin.class);
    private static final CssResourceReference PLUGIN_CSS = new CssResourceReference(FocusPointPlugin.class, "focuspoint-plugin.css");
    private static final CssResourceReference PLUGIN_JS = new CssResourceReference(FocusPointPlugin.class, "focuspoint-plugin.js");

    private static final CssResourceReference CSS_FOCUSPOINT = new CssResourceReference(FocusPointPlugin.class, "focuspoint/files/focuspoint.css");
    private static final CssResourceReference CSS_DEMO = new CssResourceReference(FocusPointPlugin.class, "focuspoint/files/demo.css");
    private static final CssResourceReference CSS_GRID = new CssResourceReference(FocusPointPlugin.class, "focuspoint/files/grid.css");
    private static final CssResourceReference CSS_HELPERTOOL = new CssResourceReference(FocusPointPlugin.class, "focuspoint/files/helper-tool.css");

    private static final JavaScriptResourceReference JS_FOCUSPOINT = new JavaScriptResourceReference(FocusPointPlugin.class, "focuspoint/files/jquery.focuspoint.js");
    private static final JavaScriptResourceReference JS_HELPERTOOL = new JavaScriptResourceReference(FocusPointPlugin.class, "focuspoint/files/jquery.focuspoint.helpertool.js");

    private boolean areExceptionsThrown;
    private boolean isVisible;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(PLUGIN_CSS));
        response.render(JavaScriptHeaderItem.forReference(PLUGIN_JS));

        response.render(CssHeaderItem.forReference(CSS_FOCUSPOINT));
        response.render(CssHeaderItem.forReference(CSS_DEMO));
        response.render(CssHeaderItem.forReference(CSS_GRID));
        response.render(CssHeaderItem.forReference(CSS_HELPERTOOL));

        response.render(JavaScriptHeaderItem.forReference(JS_FOCUSPOINT));
        response.render(JavaScriptHeaderItem.forReference(JS_HELPERTOOL));
    }

    private StringResourceModel createStringResourceModel(String resourceKey) {
    	return new StringResourceModel(resourceKey, this, null);
    }

    public FocusPointPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
        IEditor.Mode mode = IEditor.Mode.fromString(config.getString("mode"), IEditor.Mode.EDIT);
        boolean isOriginal = true;
        this.areExceptionsThrown = false;

        try {
            isOriginal = "hippogallery:original".equals((this.getModel().getObject()).getName());
        } catch (RepositoryException var5) {
            this.error(var5);
            log.error("Cannot retrieve name of original image node", var5);
            this.areExceptionsThrown = true;
        }
        this.isVisible = mode == IEditor.Mode.EDIT && isOriginal;


        Label focusPointButton = new Label("focuspoint-button", createStringResourceModel("focuspoint-button-label"));
        Fragment focusPointDiv = this.createResourceFragment("focuspoint-div", this.getModel(), config);

        focusPointButton.setVisible(this.isVisible);
        focusPointDiv.setVisible(this.isVisible);

//        if (mode == IEditor.Mode.EDIT) {
//            focusPointButton.add(new Behavior[]{new AjaxEventBehavior("onclick") {
//                private static final long serialVersionUID = 1L;
//
//                protected void onEvent(AjaxRequestTarget target) {
//                    FocusPointPlugin.this.openFocusPointLayer();
//                }
//            }});
//            focusPointButton.add(new Behavior[]{CssClass.append(new LoadableDetachableModel() {
//                private static final long serialVersionUID = 1L;
//
//                protected String load() {
//                    return FocusPointPlugin.this.isOriginal && !FocusPointPlugin.this.areExceptionsThrown ? "focuspoint-button active" : "focuspoint-button inactive";
//                }
//            })});
//        }

        this.add(
            new Component[]{
                focusPointButton,
                focusPointDiv
            }
        );
    }

    private Fragment createResourceFragment(String id, IModel<Node> model, IPluginConfig config) {
        JcrResourceStream resource = new JcrResourceStream(model);
        Fragment fragment = new Fragment(id, "unknown", this);

        try {
            Node ex = this.getModelObject();
            fragment = this.createImageFragment(id, resource, ex, config);
        } catch (RepositoryException var10) {
            log.error(var10.getMessage());
        }

        return fragment;
    }

    protected Fragment createImageFragment(String id, JcrResourceStream resource, Node node, IPluginConfig config) throws RepositoryException {
        Fragment fragment = new Fragment(id, "image", this);
        int width = this.getWidthOrZero(node);
        int height = this.getHeightOrZero(node);
        fragment.add(new Component[]{new JcrImage("helper-tool-img", resource, width, height)});
        fragment.add(new Component[]{new JcrImage("target-overlay", resource, width, height)});
        return fragment;
    }

    private int getWidthOrZero(Node imageNode) throws RepositoryException {
        try {
            return (int)imageNode.getProperty("hippogallery:width").getLong();
        } catch (PathNotFoundException var3) {
            return 0;
        }
    }

    private int getHeightOrZero(Node imageNode) throws RepositoryException {
        try {
            return (int)imageNode.getProperty("hippogallery:height").getLong();
        } catch (PathNotFoundException var3) {
            return 0;
        }
    }


    @Override
    protected void onModelChanged() {
        super.onModelChanged();
        this.redraw();
    }

}
