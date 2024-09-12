// Copyright (c) 2024 by mOctave
//
// This program is free software: you can redistribute it and/or modify it under the
// terms of the GNU Affero General Public License as published by the Free Software
// Foundation, either version 3 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
// PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License along with
// this program. If not, see <https://www.gnu.org/licenses/>.

package moctave.esmapper;

import java.util.HashMap;
import java.util.Map;

public abstract class Trade {
	public static final String TYPE = "trade";

	private static Map<String, Double[]> commodities = new HashMap<>();

	public static void addTradeInfo(Node node) {
		// The trade node has no name

		for (Node child : node.getChildren()) {

			if (child.getName().equals("commodity")) {
				Builder.mapDoubleArray(child, commodities, TYPE);
			}
		}
	}

	public static Double[] getCommodityData(String commodity) {
		return commodities.get(commodity);
	}

	public static double getMinCommodityPrice(String commodity) {
		Double[] arr = commodities.get(commodity);
		if (arr == null)
			return 0;
		
		return arr[0];
	}

	public static double getMaxCommodityPrice(String commodity) {
		Double[] arr = commodities.get(commodity);
		if (arr == null)
			return 0;
		
		return arr[1];
	}
}
