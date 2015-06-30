package org.ktachibana.cloudemoji.parsing;

import android.util.Xml;

import org.ktachibana.cloudemoji.models.memory.Category;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class SourceXmlParser {
    private static final String ns = null;
    private String alias;

    public Source parse(String alias, Reader reader) throws XmlPullParserException,
            IOException {
        this.alias = alias;
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(reader);
        parser.nextTag();
        return readEmoji(parser);
    }

    /**
     * Read the whole source in tag <emoji>
     */
    private Source readEmoji(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "emoji");
        ArrayList<String> information = new ArrayList<String>();
        List<Category> categories = new ArrayList<Category>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("infoos")) {
                information = readInformations(parser);
            } else if (name.equals("category")) {
                categories.add(readCategory(parser));
            }
        }
        return new Source(alias, information, categories);
    }

    /**
     * Read informations in tag <infoos>
     */
    private ArrayList<String> readInformations(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "infoos");
        ArrayList<String> information = new ArrayList<String>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("info")) {
                information.add(readInformation(parser));
            }
        }
        return information;
    }

    /**
     * Read one information in tag <info>
     */
    private String readInformation(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "info");
        String text = parser.nextText();
        if (parser.getEventType() != XmlPullParser.END_TAG) {
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "info");
        return text;
    }

    /**
     * Read the whole category in tag <category>
     */
    private Category readCategory(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "category");
        String categoryName = parser.getAttributeValue(null, "name");
        List<Entry> entries = new ArrayList<Entry>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            }
        }
        return new Category(categoryName, entries);
    }

    /**
     * Read the entry in tag <entry>
     */
    private Entry readEntry(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String string = "";
        String note = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("string")) {
                string = readString(parser);
            } else if (name.equals("note")) {
                note = readNote(parser);
            }
        }
        return new Entry(string, note);
    }

    /**
     * Read the string in tag <string>
     */
    private String readString(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "string");
        String string = parser.nextText();
        if (parser.getEventType() != XmlPullParser.END_TAG) {
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "string");
        return string;
    }

    /**
     * Read the note in tag <note>
     */
    private String readNote(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "note");
        String note = parser.nextText();
        if (parser.getEventType() != XmlPullParser.END_TAG) {
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "note");
        return note;
    }

}
