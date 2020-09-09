package io.focuspoints.client.taglib;

import io.focuspoints.beans.gallery.FocusPointImageSet;
import io.focuspoints.client.taglib.util.HippoUrlUtils;
import io.mikael.urlbuilder.UrlBuilder;
import java.net.MalformedURLException;
import java.net.URL;
import javax.jcr.RepositoryException;
import javax.servlet.jsp.JspException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;

@Getter
@Setter
@Slf4j
public class HippoImageTransformationTag extends ImageTransformationTag {

	private static final long serialVersionUID = 621238687570992405L;

	private HippoGalleryImageSet image;

	@Override
	public URL getImageUrl() {
		if (this.getImage() == null) {
			return super.getImageUrl();
		}

		try {
			return UrlBuilder.fromUrl(HippoUrlUtils.createUrl(this.image))
					.addParameter("ts", String.valueOf(this.image.getOriginal().getLastModified().getTimeInMillis()))
					.toUrl();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getFilename() {
		if (this.getImage() == null) {
			return super.getFilename();
		}
		try {
			return this.getImage().getNode().getName();
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
		}
		return super.getFilename();
	}

	@Override
	public Double getFocusPointX() {
		if (this.getImage() == null) {
			return super.getFocusPointX();
		}

		FocusPointImageSet.Coordinate focusPointImageCoordinate = this.getFocusPointImageCoordinate(this.getImage());

		if (focusPointImageCoordinate == null
			|| focusPointImageCoordinate.getX() == null) {

			return super.getFocusPointX();
		}

		return focusPointImageCoordinate.getX();
	}

	@Override
	public Double getFocusPointY() {
		if (this.getImage() == null) {
			return super.getFocusPointY();
		}

		FocusPointImageSet.Coordinate focusPointImageCoordinate = this.getFocusPointImageCoordinate(this.getImage());

		if (focusPointImageCoordinate == null
			|| focusPointImageCoordinate.getY() == null) {

			return super.getFocusPointY();
		}

		return focusPointImageCoordinate.getY();
	}

	private FocusPointImageSet.Coordinate getFocusPointImageCoordinate(HippoGalleryImageSet image) {
		if (image == null) {
			return null;
		}

		if (!FocusPointImageSet.class.isAssignableFrom(image.getClass())) {
			return null;
		}

		FocusPointImageSet focusPointImage = FocusPointImageSet.class.cast(this.getImage());
		return focusPointImage.getFocusPointCoordinate();
	}

	@Override
	protected String getValue() throws JspException {
		String value = super.getValue();

		return HippoUrlUtils.makeAbsoluteIfCmsRequest(value);
	}

	@Override
	public void setUrl(String url) {
		super.setUrl(HippoUrlUtils.makeAbsoluteIfCmsRequest(url));
	}
}
