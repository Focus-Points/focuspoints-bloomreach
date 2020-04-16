package io.focuspoints.beans.gallery;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;

@Node(jcrType = FocusPointImageSetBean.TYPE_NAME)
public class FocusPointImageSetBean extends HippoGalleryImageSet implements FocusPointImageSet {

	@Override
	public String getFocusPoint() {
		return this.getProperty(FIELD_FOCUS_POINT);
	}
}
