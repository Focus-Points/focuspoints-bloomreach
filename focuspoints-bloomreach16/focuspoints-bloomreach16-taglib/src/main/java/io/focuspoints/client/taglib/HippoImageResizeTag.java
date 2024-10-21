package io.focuspoints.client.taglib;

import io.focuspoints.client.taglib.util.HippoUrlUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;

import javax.jcr.RepositoryException;
import java.net.MalformedURLException;
import java.net.URL;


@Getter
@Setter
@Slf4j
public class HippoImageResizeTag extends ImageResizeTag {

	private static final long serialVersionUID = -5455266370469158743L;

	private HippoGalleryImageSet image;

	@Override
	public URL getImageUrl() {
		if (this.getImage() == null) {
			return super.getImageUrl();
		}

		try {
			return HippoUrlUtils.createUrl(this.image);
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
	protected String getValue() {
		String value = super.getValue();

		return HippoUrlUtils.makeAbsoluteIfCmsRequest(value);
	}

	@Override
	public void setUrl(String url) {
		super.setUrl(HippoUrlUtils.makeAbsoluteIfCmsRequest(url));
	}
}
