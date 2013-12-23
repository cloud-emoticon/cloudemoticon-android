package org.ktachibana.cloudemoji;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RepoXmlParser {
    private static final String ns = null;

    public Emoji parse(Reader reader) throws XmlPullParserException,
            IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(reader);
        parser.nextTag();
        return readEmoji(parser);
    }

    /**
     * Read the root tag <emoji>
     */
    private Emoji readEmoji(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "emoji");
        Infoos infoos = null;
        List<Category> categories = new ArrayList<Category>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("infoos")) {
                infoos = readInfoos(parser);
            } else if (name.equals("category")) {
                categories.add(readCategory(parser));
            }
        }
        return new Emoji(infoos, categories);
    }

    /**
     * Read the infoos tag <infoos>
     */
    private Infoos readInfoos(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "infoos");
        List<String> info = new ArrayList<String>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("info")) {
                info.add(readInfo(parser));
            }
        }
        return new Infoos(info);
    }

    /**
     * Read the info tag <info> and its content
     */
    private String readInfo(XmlPullParser parser)
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
     * Read the category tag <category>
     */
    private Category readCategory(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "category");
        String catName = parser.getAttributeValue(null, "name");
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
        return new Category(catName, entries);
    }

    /**
     * Read the entry tag <entry>
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
     * Read the string tag <string> and its content
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
     * Read the note tag <note> and its content
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

    /**
     * Static class holding infoos and list of categories
     */
    public static class Emoji implements Serializable {
        private static final long serialVersionUID = 1L;
        public final Infoos infoos;
        public List<Category> categories;

        public Emoji(Infoos infoos, List<Category> categories) {
            this.infoos = infoos;
            this.categories = categories;
        }
    }

    /**
     * Static class holding list of repository information strings
     */
    public static class Infoos implements Serializable {
        private static final long serialVersionUID = 1L;
        public final List<String> infoos;

        public Infoos(List<String> infoos) {
            this.infoos = infoos;
        }
    }

    /**
     * Static class holding category name and list of entries
     */
    public static class Category implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String name;
        public final List<Entry> entries;

        public Category(String name, List<Entry> entries) {
            this.name = name;
            this.entries = entries;
        }
    }

    /**
     * Static class holding a string and its description if necessary
     */
    public static class Entry implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String string;
        public final String note;

        public Entry(String string, String note) {
            this.string = string;
            this.note = note;
        }
    }

    /**
     * Generate a mocked emoji object
     *
     * @return a mocked emoji object
     */
    public static RepoXmlParser.Emoji generateMock() {
        List<String> infoList = new ArrayList<String>();
        infoList.add("Fayu Wen");
        infoList.add("2333333");
        RepoXmlParser.Infoos infoos = new RepoXmlParser.Infoos(infoList);

        List<RepoXmlParser.Category> catList = new ArrayList<RepoXmlParser.Category>();

        List<RepoXmlParser.Entry> list1 = new ArrayList<RepoXmlParser.Entry>();
        for (int i = 0; i < 10; ++i) {
            list1.add(new RepoXmlParser.Entry("Item " + i, "Note " + i));
        }
        RepoXmlParser.Category cat1 = new RepoXmlParser.Category("Category1",
                list1);

        List<RepoXmlParser.Entry> list2 = new ArrayList<RepoXmlParser.Entry>();
        Random r = new Random();
        for (int i = 0; i < 10; ++i) {
            list2.add(new RepoXmlParser.Entry(Double.toString(r.nextDouble()),
                    null));
        }
        RepoXmlParser.Category cat2 = new RepoXmlParser.Category("Category2",
                list2);

        catList.add(cat1);
        catList.add(cat2);

        return new Emoji(infoos, catList);
    }
}
