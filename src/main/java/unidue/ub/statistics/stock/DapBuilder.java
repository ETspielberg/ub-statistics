package unidue.ub.statistics.stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import unidue.ub.statistics.media.monographs.Item;

/**
 * Offers methods for building and managing of date-and-price lists.
 * 
 * @author Eike Spielberg
 **/
public class DapBuilder {

	/**
	 * takes a list of items and generates the corresponding date-and-price list
	 * 
	 * @param items
	 *            list of items
	 * 
	 * @return allPrices list of date-and-price
	 */
	public static ArrayList<DateAndPrice> buildDateAndPriceList(List<Item> items) {
		ArrayList<DateAndPrice> allPrices = new ArrayList<>();

		for (Item item : items) {
			if (item.getPrice() != null) {
				Double priceDouble;
				String price = item.getPrice().trim().replace(",", ".");
				if (price.length() > 2) {
					if (price.substring(0, 2).equals("DM")) {
						String priceDM = price.substring(2, price.indexOf("."));
						priceDouble = Double.parseDouble(priceDM) / 1.95583;
					} else {
						priceDouble = Double.parseDouble(price.substring(0, price.indexOf(".")));
					}
				} else if (price.indexOf(".") != -1) {
					priceDouble = Double.parseDouble(price.substring(0, price.indexOf(".")));
				} else
					priceDouble = Double.parseDouble(price);
				allPrices.add(new DateAndPrice(item.getInventoryDate(), priceDouble));
			}
		}
		return allPrices;
	}

	/**
	 * expands a date-and-price list to the given list of dates
	 * 
	 * @param dapOld
	 *            the unchanged date-and-price list
	 * @param dates
	 *            a list of dates
	 * @return list expanded date-and-price list
	 * 
	 */
	public static ArrayList<DateAndPrice> expandDateAndPriceList(List<String> dates, List<DateAndPrice> dapOld) {
		ArrayList<DateAndPrice> tacListExpanded = new ArrayList<>();
		double countCurrent = 0.0;
		for (String date : dates) {
			for (DateAndPrice dap : dapOld) {
				if (date == dap.getDate())
					countCurrent = dap.getPrice();
			}
			tacListExpanded.add(new DateAndPrice(date, countCurrent));
		}
		return tacListExpanded;
	}

	/**
	 * harmonizes several date-and-price lists to a common list of dates
	 * 
	 * @param dapAll
	 *            an array of date-and-price lists
	 * @return harmonizedTacLists an harmonized array of date-and-price list
	 * 
	 */
	public static ArrayList<ArrayList<DateAndPrice>> harmonizeDapLists(ArrayList<ArrayList<DateAndPrice>> dapAll) {
		ArrayList<String> allDates = new ArrayList<>();
		ArrayList<ArrayList<DateAndPrice>> harmonizedTacLists = new ArrayList<ArrayList<DateAndPrice>>();
		for (ArrayList<DateAndPrice> dapList : dapAll) {
			for (DateAndPrice dap : dapList)
				allDates.add(dap.getDate());
		}
		Collections.sort(allDates);
		for (ArrayList<DateAndPrice> dapList : dapAll) {
			harmonizedTacLists.add(expandDateAndPriceList(allDates, dapList));
		}
		return harmonizedTacLists;
	}

	/**
	 * prepares a csv string from the date-and-price lists for several notations
	 * 
	 * @param stellen
	 *            list of notations
	 * @param dapAll
	 *            an array of date-and-price lists
	 * 
	 * @return dapAll array of date-and-price lists
	 */
	public static String buildCSV(List<String> stellen, ArrayList<ArrayList<DateAndPrice>> dapAll) {
		String dapAllCsv = "Date";
		for (String stelle : stellen)
			dapAllCsv = dapAllCsv + stelle;
		dapAllCsv = dapAllCsv + "\n";
		for (int i = 0; i < dapAll.get(0).size(); i++) {
			String entryIndividual = dapAll.get(0).get(i).getDate();
			for (ArrayList<DateAndPrice> dapList : dapAll) {
				entryIndividual = entryIndividual + "," + String.valueOf(dapList.get(i).getPrice());
			}
			dapAllCsv = dapAllCsv + entryIndividual + "\n";
		}
		return dapAllCsv;
	}

}
