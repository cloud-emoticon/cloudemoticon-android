package org.ktachibana.cloudemoji.interfaces;

import org.ktachibana.cloudemoji.helpers.RepoXmlParser;

import java.util.List;

/**
 * Activities implementing this interface should be responsible for adding / editing favorites database
 * Multiple fragments can utilize this interface
 */
public interface OnFavoritesDatabaseOperationsListener {
    /**
     * Add an entry to the favorites database
     *
     * @param newEntry new entry to be added
     */
    public void onAddEntry(RepoXmlParser.Entry newEntry);

    /**
     * Get an entry by its string from the favorites database
     *
     * @param string string
     * @return an entry with the string (unique)
     */
    public RepoXmlParser.Entry onGetEntryByString(String string);

    /**
     * Get all entries from the favorites database
     *
     * @return list of all entries
     */
    public List<RepoXmlParser.Entry> onGetAllEntries();

    /**
     * Update an entry with string, with newEntry in the favorites database
     *
     * @param string   string of the old entry
     * @param newEntry new entry to be replace
     */
    public void onUpdateEntryByString(String string, RepoXmlParser.Entry newEntry);

    /**
     * Remove an entry by its string in the favorites database
     *
     * @param string an entry with the string (unique)
     */
    public void onRemoveEntryByString(String string);
}
