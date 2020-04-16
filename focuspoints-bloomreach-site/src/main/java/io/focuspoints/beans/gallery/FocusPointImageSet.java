package io.focuspoints.beans.gallery;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public interface FocusPointImageSet {

	static String TYPE_NAME = "tfe:focuspointimageset";

	static String FIELD_FOCUS_POINT = "tfe:focuspoint";

	static String FOCUS_POINT_SEPARATOR = ",";

	String getFocusPoint();

	default Coordinate getFocusPointCoordinate() {
		Coordinate coordinate = new Coordinate();

		String focusPoint = this.getFocusPoint();

		if (StringUtils.isBlank(focusPoint)) {
			return coordinate;
		}

		String[] focusPointParts = StringUtils.split(focusPoint, FOCUS_POINT_SEPARATOR);

		if (focusPointParts.length > 0) {
			coordinate.setX(NumberUtils.toDouble(focusPointParts[0]));

			if (focusPointParts.length > 1) {
				coordinate.setY(NumberUtils.toDouble(focusPointParts[1]));
			}
		}

		return coordinate;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Coordinate implements Serializable {
		private static final long serialVersionUID = -7205776104564337861L;

		private Double x;
		private Double y;
	}
}
