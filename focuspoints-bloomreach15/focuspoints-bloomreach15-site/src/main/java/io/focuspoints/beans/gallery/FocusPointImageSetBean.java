package io.focuspoints.beans.gallery;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;

@Node(jcrType = FocusPointImageSetBean.TYPE_NAME)
public class FocusPointImageSetBean extends HippoGalleryImageSet implements FocusPointImageSet {
	public static final String TYPE_NAME = "focuspoints:focuspointimageset";
	public static final String FIELD_FOCUS_POINT = "focuspoints:focuspoint";

	@Override
	public String getFocusPoint() {
		return this.getSingleProperty(FIELD_FOCUS_POINT);
	}
}
