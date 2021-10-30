package com.yp.core.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathTool {

	public enum ROUND_MODE {
		NO_ROUND, ROUND_UP_TO_01, ROUND_UP_TO_1, ROUND_UP_TO_5;
	}

	private MathTool() {

	}

	public static double round(double value, ROUND_MODE pRoundMode) {
		if (pRoundMode == ROUND_MODE.ROUND_UP_TO_01)
			return up(value, 1);
		else if (pRoundMode == ROUND_MODE.ROUND_UP_TO_1)
			return up(value, 0);
		else if (pRoundMode == ROUND_MODE.ROUND_UP_TO_5) {
			double newValue = up(value, 0);
			if (newValue > 10) {
				BigDecimal newValueB = BigDecimal.valueOf(newValue);
				int rem = newValueB.remainder(BigDecimal.TEN).setScale(0).intValue();
				int add = 0;
				if (rem > 5)
					add = 10 - rem;
				else if (rem < 5 && rem > 0)
					add = 5 - rem;
				newValue += add;
			} else if (newValue > 5) {
				newValue = 10;
			} else if (newValue > 0) {
				newValue = 5;
			}
			return newValue;
		} else return value;

	}

	public static double round(Double value, int scale) {
		return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_EVEN).doubleValue();
	}

	public static double up(double value, int scale) {
		return BigDecimal.valueOf(value).setScale(scale, RoundingMode.UP).doubleValue();
	}

	public static double down(double value, int scale) {
		return BigDecimal.valueOf(value).setScale(scale, RoundingMode.DOWN).doubleValue();
	}

}
