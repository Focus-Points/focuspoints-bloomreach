package io.focuspoints.client.taglib.util;

import io.focuspoints.client.util.UrlUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedMount;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HippoUrlUtils extends UrlUtils {

	public static String makeAbsoluteIfCmsRequest(String path) {
		HstRequestContext hstRequestContext = RequestContextProvider.get();

		if (!hstRequestContext.isChannelManagerPreviewRequest()
				&& !hstRequestContext.isPreview()) {
			return path;
		}

		try {
			return HippoUrlUtils.createUrl(path).toExternalForm();
		} catch (MalformedURLException e) {
			log.error("Failed to convert relative url %s to absolute url", path);
			return path;
		}
	}

	public static URL createUrl(String path) throws MalformedURLException {
		if (isAbsoluteUrl(path)) {
			return new URL(path);
		}

		HstRequestContext requestContext = getHstRequestContext();
		HstLinkCreator linkCreator = requestContext.getHstLinkCreator();
		HstLink link = linkCreator.create(path, requestContext.getResolvedMount().getMount());

		return new URL(link.toUrlForm(requestContext, true));
	}

	public static URL createUrl(HippoGalleryImageSet image) throws MalformedURLException {
		HstRequestContext requestContext = getHstRequestContext();
		HstLinkCreator linkCreator = requestContext.getHstLinkCreator();
		HstLink link = linkCreator.create(image.getOriginal(), requestContext);

		return new URL(link.toUrlForm(requestContext, true));
	}

	private static HstRequestContext getHstRequestContext() {
		HstRequestContext requestContext = RequestContextProvider.get();
		HstRequestContextProxyInvocationHandler invocationHandler =
				new HstRequestContextProxyInvocationHandler(requestContext);
		
		ClassLoader loader = HippoUrlUtils.class.getClassLoader();
		Class<?>[] interfaces = {HstRequestContext.class};
		
		return (HstRequestContext) Proxy.newProxyInstance(loader, interfaces, invocationHandler);
	}

	private static class HstRequestContextProxyInvocationHandler implements InvocationHandler {

		private HstRequestContext requestContext;
		private ResolvedMount resolvedMount;

		public HstRequestContextProxyInvocationHandler(HstRequestContext requestContext) {
			this.requestContext = requestContext;

			ResolvedMountProxyInvocationHandler resolvedMountProxyInvocationHandler =
					new ResolvedMountProxyInvocationHandler(requestContext.getResolvedMount());
			
			ClassLoader loader = this.getClass().getClassLoader();
			Class<?>[] interfaces = {ResolvedMount.class};
			
			this.resolvedMount =
					(ResolvedMount) Proxy.newProxyInstance(loader, interfaces, resolvedMountProxyInvocationHandler);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			switch(method.getName()) {
				case "isChannelManagerPreviewRequest":
				case "isPreview":
					return Boolean.FALSE;
				case "getResolvedMount":
					return this.resolvedMount;
				default:
					return method.invoke(this.requestContext, args);
			}
		}
	}

	private static class ResolvedMountProxyInvocationHandler implements InvocationHandler {

		private ResolvedMount resolvedMount;

		public ResolvedMountProxyInvocationHandler(ResolvedMount resolvedMount) {
			this.resolvedMount = resolvedMount;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			switch(method.getName()) {
				case "getMatchingIgnoredPrefix":
					return null;
				default: 
					return method.invoke(this.resolvedMount, args);
			}
		}
	}
}
