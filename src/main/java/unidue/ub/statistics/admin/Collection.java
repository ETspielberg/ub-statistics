/**
 * 
 */
package unidue.ub.statistics.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Plain old java object holding the collection of places and the corresponding map file name.
 * 
 * @author Eike Spielberg
 * @version 1
 *
 */
@Entity
public class Collection {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    private String map;
    
    private String collections;
    
    
    /**
     * general constructor and intialization
     */
    public Collection() {
        collections = "";
        map = "";
        name="";
    }

    /**
     * returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * returns the file name of the map file
     * @return the map
     */
    public String getMap() {
        return map;
    }

    /**
     * returns the List of places
     * @return the collections
     */
    public String getCollections() {
        return collections;
    }

    /**
     * returns the name of the collection
     * @param name the name to set
     * @return Collection the updated <code>collection</code> object
     */
    public Collection setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * sets the file name of the map file
     * @param map the map to set
     * @return Collection the updated <code>collection</code> object
     */
    public Collection setMap(String map) {
        this.map = map;
        return this;
    }

    /**
     * sets the collection of places
     * @param collections the collections to set
     * @return Collection the updated <code>collection</code> object
     */
    public Collection setCollections(String collections) {
        this.collections = collections;
        return this;
    }
    
    /**
     * adds another place to the collection of places
     * @param collection the collections to set
     */
    public void addCollection(String collection) {
        if (collections.isEmpty())
            collections = collection;
        else 
            collections = collections + " " + collection;
    }
    
    /**
     * obtain a list of all defined collections
     * @return a list of all collections available
     */
    public List<String> getCollectionsList() {
        List<String> collectionsList = new ArrayList<>();
        if (collections.contains(" "))
            collectionsList = Arrays.asList(collections.split(" "));
        else
            collectionsList.add(collections);
        return collectionsList;
    }

}
