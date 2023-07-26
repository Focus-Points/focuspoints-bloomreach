package io.focuspoints.beans.gallery;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.Serializable;

public interface FocusPointImageSet {

	String FOCUS_POINT_SEPARATOR = ",";

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
	class Coordinate implements Serializable {
		private static final long serialVersionUID = -7205776104564337861L;

		private Double x;
		private Double y;

		public Coordinate(Double x, Double y) {
			setX(x);
			setY(y);
		}

		public void setX(Double x) {
			// dit is nodig omdat de taglib anders een IllegalArgumentException
			// gooit wanneer er een waarde hoger dan 1 of kleiner dan -1 wordt ingevoerd.
			if (x < -1) {
				this.x = -1.0;
			} else if (x > 1) {
				this.x = 1.0;
			} else {
				this.x = x;
			}
		}

		public void setY(Double y) {
			if (y < -1) {
				this.y = -1.0;
			} else if (y > 1) {
				this.y = 1.0;
			} else {
				this.y = y;
			}
		}
	}
}
