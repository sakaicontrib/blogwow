package org.sakaiproject.blogwow.tool.producers;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.blogwow.constants.BlogConstants;
import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.CommentLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.otp.CommentLocator;
import org.sakaiproject.blogwow.tool.params.BlogEntryParams;
import org.sakaiproject.blogwow.tool.params.BlogParams;
import org.sakaiproject.blogwow.tool.params.SimpleBlogParams;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.rsf.components.ELReference;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIDeletionBinding;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.UIAlternativeTextDecorator;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class BlogViewProducer implements ViewComponentProducer, ViewParamsReporter {

    public static final String VIEW_ID = "blog_view";

    public String getViewID() {
        return VIEW_ID;
    }

    private NavBarRenderer navBarRenderer;
    private BlogLogic blogLogic;
    private EntryLogic entryLogic;
    private CommentLogic commentLogic;
    private Locale locale;
    private ExternalLogic externalLogic;
    private MessageLocator messageLocator;
    private ServerConfigurationService serverConfigurationService;

    private TargettedMessageList messages;
	public void setMessages(TargettedMessageList messages) {
		this.messages = messages;
	}
    
    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

        UIMessage.make(tofill, "page-title", "blogwow.blogview.title");

        int entriesPerPage = 10;

        String entryLocator = "EntryLocator";
        String commentLocator = "CommentLocator";
        String currentUserId = externalLogic.getCurrentUserId();

        // use a date which is related to the current users locale
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);

        BlogParams params = (BlogParams) viewparams;

        // BW-66
        if (!ExternalLogic.NO_LOCATION.equals(externalLogic.getCurrentLocationId())) {
        	navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);
        }
        List<BlogWowBlog> blogs = new ArrayList<>();
        if(params.blogid == null){
            //this is a "group" view, so display all available blogs:
            if (serverConfigurationService.getBoolean("blogwow.allow-list-all-blogs", true)) {
                blogs = blogLogic.getAllVisibleBlogs(params.locationId, null, true, 0, 0);
            } else {
                blogs = new ArrayList<>();
            }
        }else{
            blogs.add(blogLogic.getBlogById(params.blogid));
        }
        
        for(BlogWowBlog blog : blogs){
        	List<BlogWowEntry> entries = new ArrayList<BlogWowEntry>();
            if (params.entryid == null) {
                if (params.skip == null) {
                    entries = entryLogic.getAllVisibleEntries(blog.getId(), currentUserId, null, false, 0, entriesPerPage);
                } else {
                    entries = entryLogic.getAllVisibleEntries(blog.getId(), currentUserId, null, false, params.skip, entriesPerPage);
                }
            } else {
                entries.add(entryLogic.getEntryById(params.entryid, externalLogic.getCurrentLocationId()));
            }
            if (entries.size() <= 0) {
                //UIMessage.make(blogdiv, "blog-entry:empty", "blogwow.blogview.empty");
                continue;
            }
        	params.blogid = blog.getId();
        UIBranchContainer blogdiv = UIBranchContainer.make(tofill, "blog:");	
        UIOutput.make(blogdiv, "blog-title", blog.getTitle());
        UIInternalLink.make(blogdiv, "blog-url", new SimpleBlogParams(BlogViewProducer.VIEW_ID, blog.getId()));
        //UILink.make(tofill, "blog-url", externalLogic.getBlogUrl(blog.getId()));
        
        String profileText = externalLogic.useGlobalProfile() ? "" : blog.getProfile();
        if(profileText == null){
        	profileText = "";
        }
        UIVerbatim.make(blogdiv, "profile-verbatim-text", profileText);

        String profileImageUrl = blog.getImageUrl();
        String profileFormattedUrl = "javascript:;";
        final String siteId = blog.getLocation().replace("/site/", "");

        if(externalLogic.useGlobalProfile()){
        	profileImageUrl = "/direct/" +  externalLogic.getProfileEntityPrefix()+ "/" + blog.getOwnerId() + "/image/thumb?siteId=" + siteId;
        	profileFormattedUrl = "/direct/" +  externalLogic.getProfileEntityPrefix()+ "/" + blog.getOwnerId() + "/formatted";
        }
        if ("".equals(profileImageUrl)) {
            profileImageUrl = null; // this will use the default in the template
        }

        UILink.make(blogdiv, "profile-image", profileImageUrl);
        UILink.make(blogdiv, "profile-formatted", profileFormattedUrl);
        UIOutput.make(blogdiv, "profile-name", externalLogic.getUserDisplayName(blog.getOwnerId()));
        
        for (int i = 0; i < entries.size(); i++) {
            BlogWowEntry entry = entries.get(i);
            if(entry == null)
            	continue;
            String entryOTP = entryLocator + "." + entry.getId();

            UIBranchContainer entrydiv = UIBranchContainer.make(blogdiv, "blog-entry:");
            UIOutput.make(entrydiv, "entry-title", entry.getTitle());
            UIOutput.make(entrydiv, "blog-date", df.format(entry.getDateCreated()));

            String privSetting = entry.getPrivacySetting();
            if (privSetting.equals(BlogConstants.PRIVACY_PRIVATE)) {
                fillEntryIcon(entrydiv, "../images/lock.png", "blogwow.blogview.draftviewalt", "blogwow.blogview.draftviewtitle", true);
            } else if (privSetting.equals(BlogConstants.PRIVACY_GROUP_LEADER)) {
                fillEntryIcon(entrydiv, "../images/user_gray.png", "blogwow.blogview.privviewalt", "blogwow.blogview.privviewtitle", false);
            } else if (privSetting.equals(BlogConstants.PRIVACY_GROUP)) {
                fillEntryIcon(entrydiv, "../images/group.png", "blogwow.blogview.siteviewalt", "blogwow.blogview.siteviewtitle", false);
            } else if (privSetting.equals(BlogConstants.PRIVACY_PUBLIC)) {
                fillEntryIcon(entrydiv, "../images/world.png", "blogwow.blogview.pubviewalt", "blogwow.blogview.pubviewtitle", false);
            }

            UIVerbatim.make(entrydiv, "verbatim-blog-text", entry.getText());

            // BW-66
            if (!ExternalLogic.NO_LOCATION.equals(externalLogic.getCurrentLocationId())) {
            	UIOutput.make(entrydiv, "action-items");
            }
            
            if (entryLogic.canWriteEntry(entry.getId(), currentUserId)) {
                UIInternalLink.make(entrydiv, "entry-link:", UIMessage.make("blogwow.blogview.edit-entry"), new BlogEntryParams(AddEntryProducer.VIEW_ID, blog
                        .getId(), entry.getId()));

                // remove the entry using a OTP deletion binding
                UIForm removeform = UIForm.make(entrydiv, "remove-entry-form");
                UICommand removeCommand = UICommand.make(removeform, "remove-command");
                removeCommand.parameters.add(new UIDeletionBinding(entryOTP));

                // create a fake link that submits the form
                UILink removelink = UILink.make(entrydiv, "entry-link:", UIMessage.make("blogwow.blogview.remove-entry"), null);
                Map<String, String> attr = new HashMap<String, String>();

                // TODO ack! Inline Java Script
                attr.put("onclick", "if (confirm('"+ messageLocator.getMessage("blogwow.blogview.remove-entry-confirm")+"')){document.getElementById('" + removeCommand.getFullID() + "').click();}return false;");
                removelink.decorate(new UIFreeAttributeDecorator(attr));
            }

            List<BlogWowComment> comments = commentLogic.getComments(entry.getId(), null, true, 0, 0);
            UIInternalLink.make(entrydiv, "entry-link:", UIMessage.make("blogwow.blogview.comments", new Object[] { (comments.size() + "") }), new BlogParams(
                    BlogViewProducer.VIEW_ID, blog.getId(), entry.getId(), true));
            if (commentLogic.canAddComment(entry.getId(), currentUserId)){
                UIInternalLink.make(entrydiv, "entry-link:", UIMessage.make("blogwow.blogview.add-comment"), new BlogParams(BlogViewProducer.VIEW_ID, blog.getId(),
                        entry.getId(), true, true));
            }

            UILink.make(entrydiv, "entry-link:", UIMessage.make("blogwow.permalink.permalinktitle"), 
                    externalLogic.getBlogEntryUrl(entry.getId()))
                    .decorate( new UIFreeAttributeDecorator("target","_NEW"));

            // Render Comments if they are visible
            if (params.showcomments) {
            	String lineSeparator = System.getProperty("line.separator");
            	
                for (int j = 0; j < comments.size(); j++) {
                    BlogWowComment comment = comments.get(j);
                    String commentText = externalLogic.cleanupUserStrings(comment.getText());
                    commentText = commentText.replaceAll(lineSeparator, "<br/>");
                    
                    UIBranchContainer commentdiv = UIBranchContainer.make(entrydiv, "comment-div:");
                    String username = externalLogic.getUserDisplayName(comment.getOwnerId());

                    UIMessage.make(commentdiv, "comment-header", "blogwow.comments.commentstitle",
                            new Object[] { username, df.format(comment.getDateCreated()) });
                    UIVerbatim.make(commentdiv, "comment-text", commentText);
                    if (commentLogic.canRemoveComment(comment.getId(), currentUserId))
                    {
                        String commentOTP = commentLocator + "." + comment.getId();
                        UIForm removeform = UIForm.make(commentdiv, "remove-comment-form");
                        UICommand removeCommand = UICommand.make(removeform, "remove-comment-command");
                        removeCommand.parameters.add(new UIDeletionBinding(commentOTP));

                        // BW-66
                        if (!ExternalLogic.NO_LOCATION.equals(externalLogic.getCurrentLocationId())) {
                        	UILink ul = UILink.make(commentdiv, "rm-comment", UIMessage.make("blogwow.blogview.rm-comment"), null);
                        	
                        	// TODO ack! Inline Java Script
                        	UIFreeAttributeDecorator ufad = new UIFreeAttributeDecorator("onclick",
                                "if (confirm('"+ messageLocator.getMessage("blogwow.blogview.confirm-rm-comment")+"')){document.getElementById('" + removeCommand.getFullID() + "').click();}return false;");
                        	ul.decorate(ufad);
                        }
                        

                    }
                }
            }

            // Render Leave Comment if comments are visible
            if (params.addcomment && commentLogic.canAddComment(entry.getId(), currentUserId)) {
                String commentOTP = commentLocator + "." + CommentLocator.NEW_1;

                UIOutput.make(entrydiv, "add-comment-div");
                UIForm addCommentForm = UIForm.make(entrydiv, "add-comment-form");
                UIMessage.make(entrydiv, "add-comment-header", "blogwow.comments.addcommenttitle");
                UIInput commentInput = UIInput.make(addCommentForm, "comment-text", commentOTP + ".text", "");

                UICommand publishButton = UICommand.make(addCommentForm, "publish-button", UIMessage.make("blogwow.comments.submit"), commentLocator
                        + ".publishAll");
                publishButton.parameters.add(new UIELBinding(commentOTP + ".entry", new ELReference(entryLocator + "." + entry.getId())));
                UICommand cancelButton = UICommand.make(addCommentForm, "cancel-button", UIMessage.make("blogwow.comments.cancel"));
                cancelButton.addParameter(new UIELBinding("ARIResult.resultingView.addcomment", Boolean.FALSE));
                cancelButton.setReturn("cancel");

                // TODO ack! Inline Java Script
                UIVerbatim.make(entrydiv, "scroll-here-script",
                        "window.onload=function(){var scrollHere = document.getElementById('" + publishButton.getFullID() + "'); scrollHere.scrollIntoView(false); scrollHere.focus();};");
            }
        }
				// Render forward and back buttons if we have more entries and we aren't viewing an entry
				if (params.skip != null && params.entryid == null) {
						if (params.skip + entriesPerPage < entryLogic.getVisibleEntryCount(blog.getId(), currentUserId)) {
								UIMessage um = UIMessage.make("blogwow.blogview.previous", new Object[] { entriesPerPage });
								BlogParams bp = new BlogParams(BlogViewProducer.VIEW_ID, blog.getId(), params.skip + entriesPerPage);
								UIInternalLink.make(blogdiv, "previous-page", um, bp);

								UILink ul = UILink.make(blogdiv, "back-img", null);
								ul.decorate(new UIAlternativeTextDecorator(UIMessage.make("blogwow.blogview.previousalt")));
						}
						if (params.skip > 0) {
								UIMessage um = UIMessage.make("blogwow.blogview.next", new Object[] { entriesPerPage });
								BlogParams bp = new BlogParams(BlogViewProducer.VIEW_ID, blog.getId(), params.skip - entriesPerPage);
								UIInternalLink.make(blogdiv, "next-page", um, bp);

								UILink ul = UILink.make(blogdiv, "forward-img", null);
								ul.decorate(new UIAlternativeTextDecorator(UIMessage.make("blogwow.blogview.nextalt")));
						}
				} else if (entryLogic.getVisibleEntryCount(blog.getId(), currentUserId) > entriesPerPage && params.entryid == null) {
						UIMessage um = UIMessage.make("blogwow.blogview.previous", new Object[] { entriesPerPage });
						BlogParams bp = new BlogParams(BlogViewProducer.VIEW_ID, blog.getId(), entriesPerPage);
						UIInternalLink.make(blogdiv, "previous-page", um, bp);

						UILink ul = UILink.make(blogdiv, "back-img", null);
						ul.decorate(new UIAlternativeTextDecorator(UIMessage.make("blogwow.blogview.previousalt")));
				}
        }
    }

    private void fillEntryIcon(UIBranchContainer entrydiv, String imgUrl, String altKey, String titleKey, Boolean draft) {
        UILink ul = UILink.make(entrydiv, "blog-visibility", imgUrl);
        ul.decorate(new UIAlternativeTextDecorator(UIMessage.make(altKey)));
        ul.decorate(new UITooltipDecorator(UIMessage.make(titleKey)));

        if (draft) {
            UIMessage.make(entrydiv, "blog-draft", "blogwow.blogview.draft");
        } else {
            UIOutput.make(entrydiv, "blog-draft", " ");
        }
    }

    public ViewParameters getViewParameters() {
        return new BlogParams();
    }

    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setCommentLogic(CommentLogic commentLogic) {
        this.commentLogic = commentLogic;
    }

    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setNavBarRenderer(NavBarRenderer navBarRenderer) {
        this.navBarRenderer = navBarRenderer;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }
}
