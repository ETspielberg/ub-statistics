package unidue.ub.statistics.alephConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import unidue.ub.statistics.media.monographs.Manifestation;
import unidue.ub.statistics.media.monographs.Item;

/**
 * Retrieves the items from the Aleph database.
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class ItemGetter {

	private static final Logger LOGGER = Logger.getLogger(ItemGetter.class);

	private PreparedStatement psGetCurrentItems;

	private PreparedStatement psGetDeletedItems;

	private PreparedStatement psGetEtat;
	
	private PreparedStatement psGetItemByBarCode;

	/**
	 * uses a given connection to Aleph database to retrieve items from the
	 * Aleph database
	 * 
	 * @param connection
	 *            an <code>AlephConnection</code>-object
	 * @exception SQLException exception querying the Aleph database 
	 */
	public ItemGetter(AlephConnection connection) throws SQLException {
		String sql = "select z30_call_no, z30_rec_key, z30_price, z30_collection, z30_material, z30_sub_library, z30_item_status, z30_item_process_status, z30_update_date, z30_inventory_number_date from edu50.z30 where z30_rec_key like ?";
		psGetCurrentItems = connection.prepareStatement(sql);

		sql = "select z30h_call_no, z30h_rec_key, z30h_price, z30h_collection, z30h_material, z30h_sub_library, z30h_item_status, z30h_item_process_status, z30h_inventory_number_date, z30h_h_date, z30h_update_date, z30h_h_reason_type from edu50.z30h where z30h_h_reason_type = 'DELETE' and z30h_rec_key like ?";
		psGetDeletedItems = connection.prepareStatement(sql);

		sql = "select z601_rec_key from edu50.z601,edu50.z75 where z601_rec_key_2  = z75_rec_key_2 and z75_rec_key like ? and z601_type = 'INV'";
		psGetEtat = connection.prepareStatement(sql);
		
		sql = "select z30_call_no, z30_rec_key, z30_price, z30_collection, z30_material, z30_sub_library, z30_item_status, z30_item_process_status, z30_update_date, z30_inventory_number_date from edu50.z30 where z30_barcode = ?";
		psGetItemByBarCode = connection.prepareStatement(sql);
	}

	/**
	 * retrieves items connected to an existing document
	 * 
	 * @param document
	 *            an existing document
	 * @exception SQLException exception querying the Aleph database 
	 * @return items list of items
	 */
	public List<Item> getItems(Manifestation document) throws SQLException {
		List<Item> items = new ArrayList<Item>();
		items.addAll(getCurrentItems(document));
		items.addAll(getDeletedItems(document));
		return items;
	}

	private List<Item> getCurrentItems(Manifestation document) throws SQLException {
		List<Item> items = new ArrayList<Item>();
		psGetCurrentItems.setString(1, document.getDocNumber() + "%");
		ResultSet rs = psGetCurrentItems.executeQuery();

		while (rs.next()) {
			String material = rs.getString("z30_material");
			String collection = rs.getString("z30_collection");
			String callNo = rs.getString("z30_call_no");
			String recKey = rs.getString("z30_rec_key");

			String subLibrary = rs.getString("z30_sub_library");
			int itemSequence = Integer.parseInt(recKey.substring(9));
			String price = rs.getString("z30_price");

			String itemStatus = rs.getString("z30_item_status");

			String processStatus = rs.getString("z30_item_process_status");
			String inventoryDate = rs.getString("z30_inventory_number_date");

			String deletionDate;
			if (itemStatus == null)
				itemStatus = "";
			if (itemStatus.equals("89") || itemStatus.equals("90") || itemStatus.equals("xx")) {
				deletionDate = rs.getString("z30_update_date");
			} else {
				deletionDate = "";
			}
			Item item = new Item(collection, callNo, subLibrary, itemSequence, material, itemStatus, processStatus,
					inventoryDate, deletionDate, price);
			item.setRecKey(recKey);
			items.add(item);
		}
		rs.close();
		return items;
	}

	private List<Item> getDeletedItems(Manifestation document) throws SQLException {
		List<Item> items = new ArrayList<Item>();
		psGetDeletedItems.setString(1, document.getDocNumber() + "%");
		ResultSet rsOld = psGetDeletedItems.executeQuery();

		while (rsOld.next()) {
			String material;
			material = rsOld.getString("z30h_material");
			if (material == null)
				material = "";
			String itemStatus = rsOld.getString("z30h_item_status");
			if (itemStatus == null)
				itemStatus = "";
			String callNo = "???";
			String collection = "???";
			String oldCollection = rsOld.getString("z30h_collection");
			if (oldCollection != null)
				collection = oldCollection;
			String recKey = rsOld.getString("z30h_rec_key");
			String subLibrary = rsOld.getString("z30h_sub_library");
			int itemSequence = Integer.parseInt(recKey.substring(9, recKey.length() - 6));
			String price = rsOld.getString("z30h_price");
			String processStatus = "";
			String inventoryDate = rsOld.getString("z30h_inventory_number_date");
			String deletionDate = rsOld.getString("z30h_h_date");
			if (itemStatus.equals("89") || itemStatus.equals("90") || itemStatus.equals("xx")) {
				deletionDate = rsOld.getString("z30h_update_date");
			}
			Item item = new Item(collection, callNo, subLibrary, itemSequence, material, itemStatus, processStatus,
					inventoryDate, deletionDate, price);
			item.setRecKey(recKey);
			items.add(item);
		}
		rsOld.close();
		return items;
	}

	/**
	 * retrieves the budget used to buy an item
	 * 
	 * @param item
	 *            item for which the budget code is retrieved
	 * @exception SQLException exception querying the Aleph database 
	 * @return String the budget code
	 */
	public String getEtat(Item item) throws SQLException {
		String etat = "";
		LOGGER.info(item.getRecKey());
		psGetEtat.setString(1, item.getRecKey().substring(0, 9));
		ResultSet rs = psGetEtat.executeQuery();
		while (rs.next()) {
			LOGGER.info("found entry");
			if (rs.getString("z75_rec_key_2") != null)
				etat = rs.getString("z75_rec_key_2");
			item.setEtat(etat);
		}
		return etat;
	}
	
	public Item getItemByBarCode(String barcode) throws SQLException {
	    List<Item> itemsFound = new ArrayList<>();
	    psGetItemByBarCode.setString(1, barcode);
	    ResultSet rs = psGetItemByBarCode.executeQuery();
	    while (rs.next()) {
	        String material = rs.getString("z30_material");
            String collection = rs.getString("z30_collection");
            String callNo = rs.getString("z30_call_no");
            String recKey = rs.getString("z30_rec_key");

            String subLibrary = rs.getString("z30_sub_library");
            int itemSequence = Integer.parseInt(recKey.substring(9));
            String price = rs.getString("z30_price");

            String itemStatus = rs.getString("z30_item_status");

            String processStatus = rs.getString("z30_item_process_status");
            String inventoryDate = rs.getString("z30_inventory_number_date");

            String deletionDate;
            if (itemStatus == null)
                itemStatus = "";
            if (itemStatus.equals("89") || itemStatus.equals("90") || itemStatus.equals("xx")) {
                deletionDate = rs.getString("z30_update_date");
            } else {
                deletionDate = "";
            }
            Item item = new Item(collection, callNo, subLibrary, itemSequence, material, itemStatus, processStatus,
                    inventoryDate, deletionDate, price);
            item.setRecKey(recKey);
            itemsFound.add(item);
        }
	    if (itemsFound.size() == 1)
	        return itemsFound.get(0);
	    else
	        return null;
	}

}
