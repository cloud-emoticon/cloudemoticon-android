package org.ktachibana.cloudemoji;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockData {
	
	public static RepoXmlParser.Emoji generateMock() {
		List<String> infoList = new ArrayList<String>();
		infoList.add("Fayu Wen");
		infoList.add("2333333");
		RepoXmlParser.Infoos infoos = new RepoXmlParser.Infoos(infoList);
		
		List<RepoXmlParser.Category> catList = new ArrayList<RepoXmlParser.Category>();
		
		List<RepoXmlParser.Entry> list1 = new ArrayList<RepoXmlParser.Entry>();
		for (int i = 0 ; i < 10 ; ++i) {
			list1.add(new RepoXmlParser.Entry("Item " + i, "Note " + i));
		}
		RepoXmlParser.Category cat1 = new RepoXmlParser.Category("Category1", list1);
		
		List<RepoXmlParser.Entry> list2 = new ArrayList<RepoXmlParser.Entry>();
		Random r = new Random();
		for (int i = 0 ; i < 10 ; ++i) {
			list2.add(new RepoXmlParser.Entry(Double.toString(r.nextDouble()), null));
		}
		RepoXmlParser.Category cat2 = new RepoXmlParser.Category("Category2", list2);
		
		catList.add(cat1);
		catList.add(cat2);
		
		RepoXmlParser.Emoji mocked = new RepoXmlParser.Emoji(infoos, catList);
		return mocked;
	}
}
