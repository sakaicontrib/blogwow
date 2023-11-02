/**
 * Copyright (c) 2003-2007 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.blogwow.tool.producers;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.blogwow.tool.params.SimpleBlogParams;

import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class MySettingsProducer implements ViewComponentProducer, ViewParamsReporter, NavigationCaseReporter {

    public static final String VIEW_ID = "my_settings";
    public String getViewID() {
        return VIEW_ID;
    }

    private NavBarRenderer navBarRenderer;
    private TextInputEvolver richTextEvolver;

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

        UIMessage.make(tofill, "page-title", "blogwow.settings.title");

        SimpleBlogParams params = (SimpleBlogParams) viewparams;
        String blogid = params.blogid;

        String blogLocator = "BlogLocator";
        String blogOTP = blogLocator + "." + blogid;

        navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);

        UIMessage.make(tofill, "my-settings-header", "blogwow.settings.settingsheader");

        UIForm form = UIForm.make(tofill, "my-settings-form");

        UIMessage.make(form, "my-blog-profile", "blogwow.settings.profile");
        UIInput profiletext = UIInput.make(form, "profile-text-input:", blogOTP + ".profile");
        richTextEvolver.evolveTextInput(profiletext);

        UIMessage.make(form, "picture-url-label", "blogwow.settings.pictureURLtext");
        UIInput.make(form, "picture-url-input", blogOTP + ".imageUrl");

        UICommand.make(form, "change-settings-button", UIMessage.make("blogwow.settings.save"), blogLocator + ".saveAll");
        // this cancel button does nothing except send the user to the default navigation case
        UICommand.make(form, "cancel-settings-button", UIMessage.make("blogwow.settings.cancel"));
    }

    public ViewParameters getViewParameters() {
        return new SimpleBlogParams();
    }

    public List<NavigationCase> reportNavigationCases() {
        List<NavigationCase> l = new ArrayList<NavigationCase>();
        // first arg null defines the default navigation case
        l.add(new NavigationCase(null, new SimpleViewParameters(HomeProducer.VIEW_ID)));
        return l;
    }

    public void setNavBarRenderer(NavBarRenderer navBarRenderer) {
        this.navBarRenderer = navBarRenderer;
    }

    public void setRichTextEvolver(TextInputEvolver richTextEvolver) {
        this.richTextEvolver = richTextEvolver;
    }

}