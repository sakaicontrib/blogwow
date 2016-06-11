package org.sakaiproject.blogwow.tool.producers;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.authz.api.PermissionsHelper;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.rsf.helper.HelperViewParameters;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;


import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

/* This producer is responsible for calling the Permissions helper from Sakai.
 * 
 */
public class PermissionsProducer implements ViewComponentProducer, ViewParamsReporter, NavigationCaseReporter {

    public static final String VIEW_ID = "Permissions";
    public String getViewID() {
        return VIEW_ID;
    }

    // Injection
    private SessionManager sessionManager;
    private ExternalLogic externalLogic;
    private MessageLocator messageLocator;

    private static final String HELPER = "sakai.permissions.helper";

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
        String locationId = externalLogic.getCurrentLocationId();
        ToolSession session = sessionManager.getCurrentToolSession();

        session.setAttribute(PermissionsHelper.TARGET_REF, locationId);
        session.setAttribute(PermissionsHelper.DESCRIPTION, 
                messageLocator.getMessage("blogwow.permissions.header", externalLogic.getLocationTitle(locationId)) );
        session.setAttribute(PermissionsHelper.PREFIX, "blogwow.");

        UIOutput.make(tofill, HelperViewParameters.HELPER_ID, HELPER);
        UICommand.make(tofill, HelperViewParameters.POST_HELPER_BINDING, "", null);
    }

    public ViewParameters getViewParameters() {
        return new HelperViewParameters();
    }

    public List<NavigationCase> reportNavigationCases() {
        List<NavigationCase> l = new ArrayList<NavigationCase>();
        // default navigation case
        l.add(new NavigationCase(null, new SimpleViewParameters(HomeProducer.VIEW_ID)));
        return l;
    }


    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
