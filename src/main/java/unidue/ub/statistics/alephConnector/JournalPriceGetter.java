package unidue.ub.statistics.alephConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

import unidue.ub.statistics.media.journal.AnchorOrder;
import unidue.ub.statistics.media.journal.AnchorOrderDAO;
import unidue.ub.statistics.media.journal.JournalCollection;
import unidue.ub.statistics.media.journal.JournalTitle;

/**
 * Retrieves the prices for journals or journal collections from the Aleph database.
 * 
 * @author Jutta Kleinfeld, Eike Spielberg
 * @version 1
 */
public class JournalPriceGetter {

    private PreparedStatement psGetJournalPricesByCollection;

    private PreparedStatement psGetJournalPricesByISSN;

    private static final Logger LOGGER = Logger.getLogger(JournalPriceGetter.class);

    private static final Pattern yearPattern = Pattern.compile("((19|20)\\d\\d)");

    /**
     * uses a given connection to Aleph database to prepare the statements
     * 
     * @param connection
     *            an <code>AlephConnection</code>-object
     * @exception SQLException exception querying the Aleph database 
     */
    public JournalPriceGetter(AlephConnection connection) throws SQLException {
        String getPriceCollection = "select z68_rec_key, z68_vendor_code , z68_order_number, z75_i_total_amount, z75_i_date_from, z75_i_date_to, z75_i_note from edu50.z68, edu50.z75 where z68_rec_key = z75_rec_key and z68_order_number =?";
        String getPriceISSN = "select substr(z103_rec_key,6,9),  z75_i_total_amount, z75_i_date_from, z75_i_date_to, z75_i_note from edu50.z103, edu01.z13, edu50.z75 where substr(z103_rec_key_1,6,9) = z13_rec_key and substr(z103_rec_key_1,1,5) = 'EDU01' and  substr(z75_rec_key,1,9) = substr(z103_rec_key,6,9) and z13_isbn_issn = ?";

        psGetJournalPricesByCollection = connection.prepareStatement(getPriceCollection);
        psGetJournalPricesByISSN = connection.prepareStatement(getPriceISSN);
    }

    /**
     * when queried with a single journal collection, the prices from the Aleph database are retrieved and a list of journal collections for each year is returned. The price is set within each journal collection.
     * 
     * @param collection
     *            an journal collection
     * @return a list of journal collections, each containing the price for a specific year
     * @exception SQLException exception querying the Aleph database 
     */
    public Hashtable<Integer, Double> getCollectionPrice(JournalCollection collection) throws SQLException {
        Hashtable<Integer, Double> prices = new Hashtable<Integer, Double>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sushiData");
        EntityManager em = emf.createEntityManager();
        AnchorOrder anchorOrder = AnchorOrderDAO.getAnchorOrder(collection.getAnchor(), em);
        if (anchorOrder != null) {
            psGetJournalPricesByCollection.setString(1, anchorOrder.getOrderNumber());
            ResultSet rs = psGetJournalPricesByCollection.executeQuery();
            while (rs.next()) {
                String priceString = rs.getString("z75_i_total_amount");
                Double price = Double.parseDouble(priceString) / 100;
                String startYearString = rs.getString("z75_i_date_from");
                Integer startYear = LocalDate.now().getYear();
                if (startYearString.length() == 4)
                    startYear = Integer.parseInt(rs.getString("z75_i_date_from"));
                else if (startYearString.length() > 4)
                    startYear = Integer.parseInt(rs.getString("z75_i_date_from").substring(0, 4));
                else {
                    String note = rs.getString("z75_i_note");
                    if (!note.isEmpty()) {
                        Matcher matcher = yearPattern.matcher(note);
                        if (matcher.find())
                            startYear = Integer.parseInt(matcher.group());
                    }
                }
                String endYearString = rs.getString("z75_i_date_to");
                Integer endYear = startYear;
                if (endYearString.length() == 4)
                    endYear = Integer.parseInt(endYearString);
                else if (endYearString.length() > 4)
                    endYear = Integer.parseInt(endYearString.substring(0, 4));
                for (int i = startYear; i <= endYear; i++) {
                    if (prices.containsKey(i)) {
                        prices.replace(i,prices.get(i) + price);
                    } else
                        prices.put(i, price);
                    LOGGER.info("found price for collection " + collection.getAnchor() + " and year: " + i + ": " + price);
                }
            }
        } else {
            List<JournalTitle> titles = collection.getJournals();
            for (JournalTitle title : titles) {
                Integer year = title.getYear();
                Double price = title.getPrice();
                if (prices.containsKey(year)) {
                    Double pricePerYear = prices.get(year);
                    prices.replace(year,pricePerYear + price);
                } else {
                    prices.put(year, price);
                    LOGGER.info("found price for collection " + collection.getAnchor() + " and year " + year + ": " + price);
                }
            }
        }
        em.close();
        return prices;
    }

    /**
     * when queried with a single journal title, the prices from the Aleph database are retrieved and a list of journal titles for each year is returned. The price is set within each journal title.
     * 
     * @param issn
     *            the ISSN of an journal title
     * @return a list of journal title, each containing the price for a specific year
     * @exception SQLException exception querying the Aleph database 
     */
    public Hashtable<Integer, Double> getJournalPrice(String issn) throws SQLException {
        Hashtable<Integer, Double> prices = new Hashtable<Integer, Double>();
        psGetJournalPricesByISSN.setString(1, issn);
        ResultSet rs = psGetJournalPricesByISSN.executeQuery();
        while (rs.next()) {
            String priceString = rs.getString("z75_i_total_amount");
            Double price = Double.parseDouble(priceString) / 100;
            String startYearString = rs.getString("z75_i_date_from");
            Integer startYear = LocalDate.now().getYear();
            if (startYearString.length() == 4)
                startYear = Integer.parseInt(rs.getString("z75_i_date_from"));
            else if (startYearString.length() > 4)
                startYear = Integer.parseInt(rs.getString("z75_i_date_from").substring(0, 4));
            else {
                String note = rs.getString("z75_i_note");
                if (!note.isEmpty()) {
                    Matcher matcher = yearPattern.matcher(note);
                    if (matcher.find())
                        startYear = Integer.parseInt(matcher.group());
                }
            }
            String endYearString = rs.getString("z75_i_date_to");
            Integer endYear = startYear;
            if (endYearString.length() == 4)
                endYear = Integer.parseInt(endYearString);
            else if (endYearString.length() > 4)
                endYear = Integer.parseInt(endYearString.substring(0, 4));
            for (int i = startYear; i <= endYear; i++) {
                if (prices.containsKey(i)) {
                    prices.replace(i,prices.get(i) + price);
                } else
                    prices.put(i, price);
                LOGGER.info("found price for issn " + issn + " and year " + i + ": " + price);
            }
        }
        return prices;
    }
}
