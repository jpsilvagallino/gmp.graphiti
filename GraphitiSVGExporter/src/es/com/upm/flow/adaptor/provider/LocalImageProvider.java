package es.com.upm.flow.adaptor.provider;

import java.util.Hashtable;

@SuppressWarnings("restriction")
public class LocalImageProvider {

	private Hashtable<String, String> htKeyImage = new Hashtable<String, String>();

	private String providerId;

    // The prefix for all identifiers of this image provider

    protected static final String PREFIX = 
              "com.isb.bks.flow.features."; //$NON-NLS-1$

 

    // The image identifier for an Connection.

    public static final String IMG_TRANSITION_PAD = PREFIX + "transition.pad"; //$NON-NLS-1$
    public static final String IMG_SENDFILE_STATE = PREFIX + "sendfile.state"; //$NON-NLS-1$
    public static final String IMG_END_STATE = PREFIX + "end.state"; //$NON-NLS-1$
    public static final String IMG_START_STATE = PREFIX + "start.state"; //$NON-NLS-1$
    public static final String IMG_JOIN_STATE = PREFIX + "join.state"; //$NON-NLS-1$
    public static final String IMG_PAGE_STATE = PREFIX + "page.state"; //$NON-NLS-1$
    public static final String IMG_WHITE_PAGE_STATE = PREFIX + "whitePage.state"; //$NON-NLS-1$
    public static final String IMG_NAVIGATION_STATE = PREFIX + "navigation.state"; //$NON-NLS-1$
    public static final String IMG_EXT_NAVIGATION_STATE = PREFIX + "ext.navigation.state"; //$NON-NLS-1$
    public static final String IMG_TRANSITION = PREFIX + "transition"; //$NON-NLS-1$

    public static final String IMG_TRANSITION_PAD_EVENT = PREFIX + "event."; //$NON-NLS-1$
    
    public static final String IMG_TAXONOMY_PAD_CONTEXT = PREFIX + "tax."; //$NON-NLS-1$
    
    public static final String IMG_TRANSITION_PAD_KO_EVENT = IMG_TRANSITION_PAD_EVENT + "KO"; //$NON-NLS-1$
    public static final String IMG_TRANSITION_PAD_OK_EVENT = IMG_TRANSITION_PAD_EVENT + "OK"; //$NON-NLS-1$
   
    public static final String IMG_TAXONOMY_PAD_ADD_CONTEXT = IMG_TAXONOMY_PAD_CONTEXT + "ADD"; //$NON-NLS-1$
    public static final String IMG_TAXONOMY_PAD_ITEM_CONTEXT = IMG_TRANSITION_PAD_EVENT + "TAX"; //$NON-NLS-1$
    public static final String IMG_TAXONOMY_PAD_ITEM_CUSTOM = IMG_TRANSITION_PAD_EVENT + "CUSTOM"; //$NON-NLS-1$
    public static final String IMG_PHANTOM = "PHANTOM"; //$NON-NLS-1$
    
    public static final String IMG_TRANSITION_PAD_KO_EVENT_PHANTOM = IMG_TRANSITION_PAD_EVENT + "KO" + IMG_PHANTOM; //$NON-NLS-1$
    public static final String IMG_TRANSITION_PAD_OK_EVENT_PHANTOM = IMG_TRANSITION_PAD_EVENT + "OK" + IMG_PHANTOM; //$NON-NLS-1$
    
    public static final String IMG_TAXONOMY_PAD_ITEM_CONTEXT_PHANTOM = IMG_TRANSITION_PAD_EVENT + "TAX" + IMG_PHANTOM; //$NON-NLS-1$

    public static final String IMG_NAVIGATION_STATE_ICON =  PREFIX + "navigation.state.icon"; //$NON-NLS-1$
    public static final String IMG_PAGE_STATE_ICON =  PREFIX + "page.state.icon"; //$NON-NLS-1$
    public static final String IMG_END_STATE_ICON =  PREFIX + "end.state.icon"; //$NON-NLS-1$
    public static final String IMG_SENDFILE_STATE_ICON =  PREFIX + "send.state.icon"; //$NON-NLS-1$
    public static final String IMG_EXT_NAVIGATION_STATE_ICON =  PREFIX + "ext.navigation.state.icon"; //$NON-NLS-1$
    
    public static final String IMG_TOGGLE_COLORS_ACTION = PREFIX + "toggle.colors.action"; //$NON-NLS-1$
    
    
    public static final String IMG_SUBNAVIGATION_END_ICON =  PREFIX + "end.subnavigation.state.icon"; //$NON-NLS-1$

    public static final String IMG_BACKWARD_ARROW_PREFIX = PREFIX + "backward.arrow."; //$NON-NLS-1$
 
    public static final String IMG_BACKWARD_ARROW_0 = IMG_BACKWARD_ARROW_PREFIX + "0"; //$NON-NLS-1$
    public static final String IMG_BACKWARD_ARROW_1 = IMG_BACKWARD_ARROW_PREFIX + "1"; //$NON-NLS-1$
    public static final String IMG_BACKWARD_ARROW_2 = IMG_BACKWARD_ARROW_PREFIX + "2"; //$NON-NLS-1$
    public static final String IMG_BACKWARD_ARROW_3 = IMG_BACKWARD_ARROW_PREFIX + "3"; //$NON-NLS-1$
    public static final String IMG_BACKWARD_ARROW_4 = IMG_BACKWARD_ARROW_PREFIX + "4"; //$NON-NLS-1$
    public static final String IMG_BACKWARD_ARROW_5 = IMG_BACKWARD_ARROW_PREFIX + "5"; //$NON-NLS-1$
    public static final String IMG_BACKWARD_ARROW_6 = IMG_BACKWARD_ARROW_PREFIX + "6"; //$NON-NLS-1$
    public static final String IMG_BACKWARD_ARROW_7 = IMG_BACKWARD_ARROW_PREFIX + "7"; //$NON-NLS-1$
    public static final String IMG_BACKWARD_ARROW_8 = IMG_BACKWARD_ARROW_PREFIX + "8"; //$NON-NLS-1$
    public static final String IMG_BACKWARD_ARROW_9 = IMG_BACKWARD_ARROW_PREFIX + "9"; //$NON-NLS-1$

    public static final String IMG_LABEL_STATE_ICON =  PREFIX + "label.state.icon"; //$NON-NLS-1$
    
    public static final String IMG_LABEL_STATE_ICON_0 = IMG_LABEL_STATE_ICON + ".0"; //$NON-NLS-1$
    public static final String IMG_LABEL_STATE_ICON_1 = IMG_LABEL_STATE_ICON + ".1"; //$NON-NLS-1$
    public static final String IMG_LABEL_STATE_ICON_2 = IMG_LABEL_STATE_ICON + ".2"; //$NON-NLS-1$
    public static final String IMG_LABEL_STATE_ICON_3 = IMG_LABEL_STATE_ICON + ".3"; //$NON-NLS-1$
    public static final String IMG_LABEL_STATE_ICON_4 = IMG_LABEL_STATE_ICON + ".4"; //$NON-NLS-1$
    public static final String IMG_LABEL_STATE_ICON_5 = IMG_LABEL_STATE_ICON + ".5"; //$NON-NLS-1$
    public static final String IMG_LABEL_STATE_ICON_6 = IMG_LABEL_STATE_ICON + ".6"; //$NON-NLS-1$
    public static final String IMG_LABEL_STATE_ICON_7 = IMG_LABEL_STATE_ICON + ".7"; //$NON-NLS-1$
    public static final String IMG_LABEL_STATE_ICON_8 = IMG_LABEL_STATE_ICON + ".8"; //$NON-NLS-1$
    public static final String IMG_LABEL_STATE_ICON_9 = IMG_LABEL_STATE_ICON + ".9"; //$NON-NLS-1$
    
    public LocalImageProvider() {
		addAvailableImages();
	}
    /**
	 * Add image file path.
	 * 
	 * @param imageId
	 *            the image id
	 * @param imageFilePath
	 *            the image file path
	 */
	final protected void addImageFilePath(String imageId, String imageFilePath) {
		if (this.htKeyImage.get(imageId) != null) {
			System.out.println("Error, Imagen ya registrada");
		} else {
			this.htKeyImage.put(imageId, imageFilePath);
		}
	}
    protected void addAvailableImages() 
    {
        addImageFilePath(IMG_PAGE_STATE_ICON, "icons/page.png"); //$NON-NLS-1$
        addImageFilePath(IMG_NAVIGATION_STATE_ICON, "icons/navigation_state_white.png"); //$NON-NLS-1$
        addImageFilePath(IMG_END_STATE_ICON, "icons/end.png"); //$NON-NLS-1$
        addImageFilePath(IMG_SENDFILE_STATE_ICON, "icons/sendfile.png"); //$NON-NLS-1$
        addImageFilePath(IMG_EXT_NAVIGATION_STATE_ICON, "icons/ext_navigation.png"); //$NON-NLS-1$

        addImageFilePath(IMG_TRANSITION_PAD, "icons/transition_pad.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_SENDFILE_STATE, "icons/sendfile_state.png"); //$NON-NLS-1$
        addImageFilePath(IMG_END_STATE, "icons/end_state.png"); //$NON-NLS-1$
        addImageFilePath(IMG_START_STATE, "icons/start_state.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_JOIN_STATE, "icons/join_state.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_PAGE_STATE, "icons/page_state.png"); //$NON-NLS-1$
        addImageFilePath(IMG_WHITE_PAGE_STATE, "icons/white_page_state.png"); //$NON-NLS-1$
        addImageFilePath(IMG_NAVIGATION_STATE, "icons/navigation_state.png"); //$NON-NLS-1$
        addImageFilePath(IMG_EXT_NAVIGATION_STATE, "icons/ext_navigation_state.png"); //$NON-NLS-1$
        addImageFilePath(IMG_TRANSITION, "icons/transition.gif"); //$NON-NLS-1$

        addImageFilePath(IMG_TRANSITION_PAD_KO_EVENT, "icons/actions/cancel-icon.png"); //$NON-NLS-1$
        addImageFilePath(IMG_TRANSITION_PAD_OK_EVENT, "icons/actions/ok-icon.png"); //$NON-NLS-1$
        
        addImageFilePath(IMG_TAXONOMY_PAD_ADD_CONTEXT, "icons/actions/add-icon.png"); //$NON-NLS-1$
        addImageFilePath(IMG_TAXONOMY_PAD_ITEM_CONTEXT, "icons/actions/tax-icon.png"); //$NON-NLS-1$
        addImageFilePath(IMG_TAXONOMY_PAD_ITEM_CUSTOM, "icons/actions/custom-icon.png"); //$NON-NLS-1$
        
        addImageFilePath(IMG_TRANSITION_PAD_KO_EVENT_PHANTOM , "icons/actions/cancel-icon-phanton.png"); //$NON-NLS-1$
        addImageFilePath(IMG_TRANSITION_PAD_OK_EVENT_PHANTOM , "icons/actions/ok-icon-phanton.png"); //$NON-NLS-1$
        
        addImageFilePath(IMG_TAXONOMY_PAD_ITEM_CONTEXT_PHANTOM, "icons/actions/tax-icon-phanton.png"); //$NON-NLS-1$
        
        addImageFilePath(IMG_PHANTOM, "icons/actions/phantom.png"); //$NON-NLS-1$
        
        addImageFilePath(IMG_SUBNAVIGATION_END_ICON, "icons/actions/subnavigation_end.png"); //$NON-NLS-1$

        addImageFilePath(IMG_TOGGLE_COLORS_ACTION, "icons/colors/arrows/celeste.png"); //$NON-NLS-1$

        addImageFilePath(IMG_BACKWARD_ARROW_0, "icons/colors/arrows/red_arrow.png"); //$NON-NLS-1$
        addImageFilePath(IMG_BACKWARD_ARROW_1, "icons/colors/arrows/green_arrow.png"); //$NON-NLS-1$
        addImageFilePath(IMG_BACKWARD_ARROW_2, "icons/colors/arrows/purple_arrow.png"); //$NON-NLS-1$
        addImageFilePath(IMG_BACKWARD_ARROW_3, "icons/colors/arrows/yellow_arrow.png"); //$NON-NLS-1$
        addImageFilePath(IMG_BACKWARD_ARROW_4, "icons/colors/arrows/pink_arrow.png"); //$NON-NLS-1$
        addImageFilePath(IMG_BACKWARD_ARROW_5, "icons/colors/arrows/brown_arrow.png"); //$NON-NLS-1$
        addImageFilePath(IMG_BACKWARD_ARROW_6, "icons/colors/arrows/light_blue_arrow.png"); //$NON-NLS-1$
        addImageFilePath(IMG_BACKWARD_ARROW_7, "icons/colors/arrows/orange_arrow.png"); //$NON-NLS-1$
        addImageFilePath(IMG_BACKWARD_ARROW_8, "icons/colors/arrows/grey_arrow.png"); //$NON-NLS-1$
        addImageFilePath(IMG_BACKWARD_ARROW_9, "icons/colors/arrows/blue_arrow.png"); //$NON-NLS-1$

        addImageFilePath(IMG_LABEL_STATE_ICON, "icons/colors/labels/empty_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_0, "icons/colors/labels/red_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_1, "icons/colors/labels/green_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_2, "icons/colors/labels/purple_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_3, "icons/colors/labels/yellow_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_4, "icons/colors/labels/pink_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_5, "icons/colors/labels/brown_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_6, "icons/colors/labels/light_blue_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_7, "icons/colors/labels/orange_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_8, "icons/colors/labels/grey_label.png"); //$NON-NLS-1$
        addImageFilePath(IMG_LABEL_STATE_ICON_9, "icons/colors/labels/blue_label.png"); //$NON-NLS-1$
        
    }
    final public String getImageFilePath(String imageId) {
		Object htObject = this.htKeyImage.get(imageId);
		if (htObject instanceof String) {
			return (String) htObject;
		}
		return null;
	}
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
}


