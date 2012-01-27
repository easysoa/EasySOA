package org.easysoa.records.correlation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.easysoa.records.ExchangeRecord;
import org.junit.Before;
import org.junit.Test;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.message.PostData;
import com.openwide.easysoa.message.QueryParam;
import com.openwide.easysoa.message.QueryString;

public class ReqResCorrelationTest {
    
    private ExchangeRecord jsonPostExchange;
    private ExchangeRecord jsonGetExchange;
    private ExchangeRecord jsonGetQueryExchange;
    private ExchangeRecord jsonGetAllExchange;
    private ExchangeRecord jsonPostQueryExchange;
    
    @Before
    public void setUp() {
        jsonPostExchange = new ExchangeRecord("id1",
                new InMessage("POST", "/test"),
                new OutMessage(200, "{ id: 1, name:'test1', description:'a fine test' }"));
        jsonPostExchange.getInMessage().setPostData(new PostData("application/javascript", "{ name:'test1', description:'a fine test' }", null));
        jsonGetExchange = new ExchangeRecord("id2",
                new InMessage("GET", "/test/1"),
                new OutMessage(200, "{ id: 1, name:'test1', description:'a fine test' }"));
        jsonGetQueryExchange = new ExchangeRecord("id3",
                new InMessage("GET", "/test?id=1"),
                new OutMessage(200, "{ id: 1, name:'test1', description:'a fine test' }"));
        jsonGetQueryExchange.getInMessage().setQueryString(new QueryString());
        jsonGetQueryExchange.getInMessage().getQueryString().addQueryParam(new QueryParam("id", "1"));
        jsonGetAllExchange = new ExchangeRecord("id4",
                new InMessage("GET", "/test"),
                new OutMessage(200, "[ { id: 1, name:'test1', description:'a fine test' } ]"));
        jsonPostQueryExchange = new ExchangeRecord("id1",
                new InMessage("POST", "/test?http_method=GET"),
                new OutMessage(200, "{ id: 1, name:'test1', description:'a fine test' }"));
        jsonPostQueryExchange.getInMessage().setPostData(new PostData("application/javascript", "{ id: 1 }", null));
    }
    
    public class UrlNode {
        public String url;
        public String element;
        public ArrayList<CandidateField> candidates = new ArrayList<CandidateField>();
    }
    
    public class InNode {
        public String path;
        public String name;
        public ArrayList<CandidateField> candidates = new ArrayList<CandidateField>();
    }
    
    private void putField(HashMap<String, CandidateField> fields, CandidateField candidateField) {
        fields.put(candidateField.getPath(), candidateField);
    }
    

    ///////////////////////////////
    // JSON like tests
    
    @Test
    public void testGet() {
        System.out.println("testGet -");
        HashMap<String,CandidateField> inContentFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inQueryFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();
        
        //putField(inContentFields, new CandidateField("/id", "1")); //jsonPostQueryExchange
        
        //putField(inQueryFields, new CandidateField("id", "1")); //jsonGetQueryExchange
        
        putField(inPathFields, new CandidateField("path.0", "test"));
        putField(inPathFields, new CandidateField("path.1", "1"));
        //...

        putField(foundOutFields, new CandidateField("id", "1"));
        putField(foundOutFields, new CandidateField("name", "test1"));
        putField(foundOutFields, new CandidateField("description", "a fine test"));
        putField(foundOutFields, new CandidateField("second/id", "11")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("third/id", "1")); // third "wrong" id, found but at lower level
        
        correlate(jsonGetExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
        
        correlateWithSubpath(jsonGetExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
    }
    
    @Test
    public void testGetQuery() {
        System.out.println("testGetQuery -");
        HashMap<String,CandidateField> inContentFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inQueryFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();
        
        putField(inQueryFields, new CandidateField("id", "1")); //jsonGetQueryExchange
        
        putField(foundOutFields, new CandidateField("id", "1"));
        putField(foundOutFields, new CandidateField("name", "test1"));
        putField(foundOutFields, new CandidateField("description", "a fine test"));
        putField(foundOutFields, new CandidateField("second/id", "11")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("third/id", "1")); // third "wrong" id, found but at lower level
        
        correlate(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
        
        correlateWithSubpath(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
    }
    
    @Test
    public void testGetQueryList() {
        System.out.println("testGetQueryList -");
        HashMap<String,CandidateField> inContentFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inQueryFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();
        
        putField(inQueryFields, new CandidateField("id", "1")); //jsonGetQueryExchange
        
        putField(foundOutFields, new CandidateField("0/id", "1"));
        putField(foundOutFields, new CandidateField("0/name", "test1"));
        putField(foundOutFields, new CandidateField("0/description", "a fine test"));
        putField(foundOutFields, new CandidateField("0/second/id", "11")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("0/third/id", "1")); // second "wrong" id, found but at lower level
        
        correlate(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
        
        correlateWithSubpath(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
    }
    
    @Test
    public void testGetQueryListWithMoreResults() {
        System.out.println("testGetQueryListWithMoreResults -");
        HashMap<String,CandidateField> inContentFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inQueryFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();
        
        putField(inQueryFields, new CandidateField("name", "test1"));
        putField(inQueryFields, new CandidateField("description", "a fine test"));
        
        putField(foundOutFields, new CandidateField("0/id", "1"));
        putField(foundOutFields, new CandidateField("0/name", "test1"));
        putField(foundOutFields, new CandidateField("0/description", "a fine test"));
        putField(foundOutFields, new CandidateField("0/second/id", "11")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("0/third/id", "1")); // second "wrong" id, found but at lower level
        
        putField(foundOutFields, new CandidateField("1/id", "2"));
        putField(foundOutFields, new CandidateField("1/name", "test1"));
        putField(foundOutFields, new CandidateField("1/description", "a fine test"));
        putField(foundOutFields, new CandidateField("1/second/id", "22")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("1/third/id", "1")); // third "wrong" id, found but at lower level
        
        correlate(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
        
        correlateWithSubpath(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
    }
    
    @Test
    public void testPostQuery() {
        System.out.println("testPostQuery -");
        HashMap<String,CandidateField> inContentFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inQueryFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();
        
        putField(inContentFields, new CandidateField("id", "1")); //jsonPostQueryExchange
        
        putField(foundOutFields, new CandidateField("id", "1"));
        putField(foundOutFields, new CandidateField("name", "test1"));
        putField(foundOutFields, new CandidateField("description", "a fine test"));
        putField(foundOutFields, new CandidateField("second/id", "11")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("third/id", "1")); // third "wrong" id, found but at lower level
        
        correlate(jsonPostQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
        
        correlateWithSubpath(jsonPostQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
    }
    
    @Test
    public void testPostQueryList() {
        System.out.println("testPostQueryList -");
        HashMap<String,CandidateField> inContentFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inQueryFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();
        
        putField(inContentFields, new CandidateField("id", "1")); //jsonPostQueryExchange
        
        putField(foundOutFields, new CandidateField("0/id", "1"));
        putField(foundOutFields, new CandidateField("0/name", "test1"));
        putField(foundOutFields, new CandidateField("0/description", "a fine test"));
        putField(foundOutFields, new CandidateField("0/second/id", "11")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("0/third/id", "1")); // second "wrong" id, found but at lower level
        
        correlate(jsonPostQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
        
        correlateWithSubpath(jsonPostQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
    }
    
    @Test
    public void testPost() {
        System.out.println("testPost -");
        HashMap<String,CandidateField> inContentFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inQueryFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();
        
        putField(inContentFields, new CandidateField("name", "test1"));
        putField(inContentFields, new CandidateField("description", "a fine test"));
        putField(inContentFields, new CandidateField("second/id", "11")); // second "wrong" id
        
        putField(foundOutFields, new CandidateField("id", "1"));
        putField(foundOutFields, new CandidateField("name", "test1"));
        putField(foundOutFields, new CandidateField("description", "a fine test"));
        putField(foundOutFields, new CandidateField("second/id", "11")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("third/id", "1")); // third "wrong" id, found but at lower level, computed
        
        correlate(jsonPostExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);

        correlateWithSubpath(jsonPostExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
    }

    ///////////////////////////////
    // XML like tests
    
    @Test
    public void testGetQueryListWithMoreResultsXMLStyle() {
        System.out.println("testGetQueryListWithMoreResultsXMLStyle -");
        HashMap<String,CandidateField> inContentFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inQueryFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();

        //String ns = "{http://www.easysoa.com/test}"; // ns is by default

        // "filter" query pattern :
        putField(inQueryFields, new CandidateField("filter/name", "test1"));
        // object field query pattern TODO move :
        putField(inQueryFields, new CandidateField("item/description", "a fine test")); // another way
        
        // returns with "items" root tag :
        putField(foundOutFields, new CandidateField("items/0/item/id", "1")); // 0/item/item means the first list element, which is of item type
        putField(foundOutFields, new CandidateField("items/0/item/name", "test1"));
        putField(foundOutFields, new CandidateField("items/0/item/description", "a fine test"));
        putField(foundOutFields, new CandidateField("items/0/item/second/id", "11")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("items/0/item/third/id", "1")); // third "wrong" id, found but at lower level
        
        putField(foundOutFields, new CandidateField("items/1/item/id", "2"));
        putField(foundOutFields, new CandidateField("items/1/item/name", "test1"));
        putField(foundOutFields, new CandidateField("items/1/item/description", "a fine test"));
        putField(foundOutFields, new CandidateField("items/1/item/second/id", "22")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("items/1/item/third/id", "1")); // third "wrong" id, found but at lower level
        
        correlate(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
        
        correlateWithSubpath(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
    }

    @Test
    public void testGetQueryListWithMoreResultsXMLStyleWithNS() {
        System.out.println("testGetQueryListWithMoreResultsXMLStyle -");
        HashMap<String,CandidateField> inContentFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inQueryFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();

        //String ns = "{http://www.easysoa.com/test}"; // ns is by default
        //String fourthNs = "{fourth}"; // fourthNs is implied in fourth

        // "filter" query pattern :
        putField(inQueryFields, new CandidateField("filter/name", "test1"));
        putField(inQueryFields, new CandidateField("filter/@/name", "test1"));
        // object field query pattern TODO move :
        putField(inQueryFields, new CandidateField("item/description", "a fine test")); // another way
        // object field in filter query pattern TODO move :
        putField(inQueryFields, new CandidateField("filter/object/name", "test1")); // another way

        // returns with "items" root tag :
        putField(foundOutFields, new CandidateField("items/0/item/id", "1")); // 0/item/item means the first list element, which is of item type
        putField(foundOutFields, new CandidateField("items/0/item/name", "test1"));
        putField(foundOutFields, new CandidateField("items/0/item/description", "a fine test"));
        putField(foundOutFields, new CandidateField("items/0/item/second/id", "11")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("items/0/item/third/id", "1")); // third "wrong" id, found but at lower level
        putField(foundOutFields, new CandidateField("items/0/item/fourth/id", "1")); // fourth "wrong" id, but with wrong ns
        
        putField(foundOutFields, new CandidateField("items/1/item/id", "2"));
        putField(foundOutFields, new CandidateField("items/1/item/name", "test1"));
        putField(foundOutFields, new CandidateField("items/1/item/description", "a fine test"));
        putField(foundOutFields, new CandidateField("items/1/item/second/id", "22")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("items/1/item/third/id", "1")); // third "wrong" id, found but at lower level
        putField(foundOutFields, new CandidateField("items/1/item/fourth/id", "1")); // fourth "wrong" id, but with wrong ns

        // returns with "results" root tag TODO move :
        putField(foundOutFields, new CandidateField("results/2/object/id", "3"));
        putField(foundOutFields, new CandidateField("results/2/object/name", "test1"));
        putField(foundOutFields, new CandidateField("results/2/object/second/id", "22")); // second "wrong" id, not found because of value
        putField(foundOutFields, new CandidateField("results/2/object/third/id", "1")); // third "wrong" id, found but at lower level
        putField(foundOutFields, new CandidateField("results/2/object/fourth/id", "1")); // fourth "wrong" id, but with wrong ns
        
        // ?
        putField(foundOutFields, new CandidateField("items/3/filter/id", "4"));
        putField(foundOutFields, new CandidateField("items/3/filter/@/name", "test1"));
        
        correlate(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
        
        correlateWithSubpath(jsonGetQueryExchange, inPathFields, inQueryFields, inContentFields, foundOutFields);
    }
    
    
    
    //////////////////////////////////////////
    // correlation and analysis algorithms
    
    /**
     * First correlation algorithm : tries to find correlation
     *  - if in content, value and path correlations
     *  - else (otherwise lowered) if in query, value and (path or name) correlations
     *  - else (otherwise lowered) for all path elements, value correlations
     * @param jsonExchange
     * @param inPathFields
     * @param inQueryFields
     * @param inContentFields
     * @param foundOutFields
     */
    private void correlate(ExchangeRecord jsonExchange, HashMap<String, CandidateField> inPathFields,
            HashMap<String, CandidateField> inQueryFields,
            HashMap<String, CandidateField> inContentFields,
            HashMap<String, CandidateField> foundOutFields) {
        // looking for correlations :
        int level = 16;
        // if in content, value and path correlations
        //ArrayList<ReqResFieldCorrelation> correlations = new ArrayList<ReqResFieldCorrelation>();
        HashMap<Integer, CorrelationLevel> correlationLevels = new HashMap<Integer, CorrelationLevel>();
        
        if (jsonExchange.getInMessage().getPostData() != null) {
            addCorrelationsFromOutByPathOrNameAndValue(correlationLevels, level, foundOutFields, inContentFields);
            level = level / 4;
        }
        
        // else (otherwise lowered) if in query, value and path correlations
        if (jsonExchange.getInMessage().getQueryString() != null) {
            addCorrelationsFromOutByPathOrNameAndValue(correlationLevels, level, foundOutFields, inQueryFields);
            level = level / 4;
        }
        
        // else (otherwise lowered) for all path elements, value correlations
        for (CandidateField inPathField : inPathFields.values()) {
            List<CandidateField> foundByValueFields = getFromOutByValue(foundOutFields, inPathField);
            for (CandidateField field : foundByValueFields) {
                // exact only
                //correlations.add(new ReqResFieldCorrelation(level - field.getPath().split("/").length*2, inPathField, field, "byValue"));
                //correlationLevels.putCorrelation(new ReqResFieldCorrelation(level - field.getPath().split("/").length*2, inPathField, field, "byValue"));
                putCorrelation(correlationLevels, new ReqResFieldCorrelation(level - field.getPath().split("/").length*2, inPathField, field, "byValue"));
            }
        }

        //printCorrelations(correlations);
        int inFieldNb = inPathFields.size() + inQueryFields.size() + inContentFields.size();
        int outFieldNb = foundOutFields.size();
        printCorrelations(correlationLevels, inFieldNb, outFieldNb);
    }


    private void putCorrelation(
            HashMap<Integer, CorrelationLevel> correlationLevels,
            ReqResFieldCorrelation reqResFieldCorrelation) {
        int level = reqResFieldCorrelation.getLevel();
        CorrelationLevel correlationLevel = correlationLevels.get(level);
        if (correlationLevel == null) {
            correlationLevel = new CorrelationLevel(level);
            correlationLevels.put(level, correlationLevel);
        }
        correlationLevel.addCorrelation(reqResFieldCorrelation);
    }

    
    /**
     * First correlation algorithm result analysis :
     * computes indicators on fields within single (top) level, then tries to find
     * if the exchange is like a query (and), post or get on id, and what role those
     * fields play in this pattern.
     * 
     * @param correlationLevelMap
     * @param inFieldNb
     * @param outFieldNb
     */
    private void printCorrelations(HashMap<Integer, CorrelationLevel> correlationLevelMap, int inFieldNb, int outFieldNb) {
        System.out.println("Found correlations, sorted y level :");
        ArrayList<CorrelationLevel> correlationLevels = new ArrayList<CorrelationLevel>(correlationLevelMap.values());
        Collections.sort(correlationLevels, new Comparator<CorrelationLevel>() {
            @Override
            public int compare(CorrelationLevel corrLvl1, CorrelationLevel corrLvl2) {
                return - (corrLvl1.getLevel() - corrLvl2.getLevel());
            }
        });
        
        
        boolean isAnd, isPost, isGetOnId, isOr = false;
        ArrayList<String> andInPathes, orOutPathes = null;
        for (CorrelationLevel correlationLevel : correlationLevels) {
            //int levelCorrelationNb = correlationLevel.getCorrelations().size();
            System.out.println();
            System.out.println(correlationLevel.getLevel() + ":");
            for (ReqResFieldCorrelation correlation : correlationLevel.getCorrelations()) {
                System.out.println("\t" + correlation.getInField() + "\t" + correlation.getOutField() + "\t" + correlation.getInfo());
            }
            
            // TODO better : all in SQL & SQL queries, or datamining...
            
            // for AND
            String maxInPath;
            int maxInPathNb = 0;
            //ArrayList<ReqResFieldCorrelation> maxInPathCorrelations = new ArrayList<ReqResFieldCorrelation>();
            HashMap<String, Integer> inFieldPathCorrelationMap = new HashMap<String, Integer>();
            for (ReqResFieldCorrelation correlation : correlationLevel.getCorrelations()) {
                String path = correlation.getInField().getPath();
                Object found =  inFieldPathCorrelationMap.get(path);
                int nb = 1;
                if (found != null) {
                    nb += inFieldPathCorrelationMap.get(path);
                    if (nb > maxInPathNb) {
                        maxInPath = path;
                        maxInPathNb = nb;
                        //maxInPathCorrelations.clear();
                    }
                    //maxInPathCorrelations.add(correlation);
                }
                inFieldPathCorrelationMap.put(path, nb);
            }
            //ArrayList<ReqResFieldCorrelation> singleInPathCorrelations = new ArrayList<ReqResFieldCorrelation>();
            //ArrayList<ReqResFieldCorrelation> otherInPathCorrelations = new ArrayList<ReqResFieldCorrelation>();
            HashSet<String> maxInPathSet = new HashSet<String>();
            HashSet<String> singleInPathSet = new HashSet<String>();
            HashSet<String> otherInPathSet = new HashSet<String>();
            for (String inPath : inFieldPathCorrelationMap.keySet()) {
                Integer inPathNb = inFieldPathCorrelationMap.get(inPath);
                if (inFieldPathCorrelationMap.get(inPath) == 1) {
                    singleInPathSet.add(inPath);
                } else if (inPathNb == maxInPathNb) { // except if nb == 1
                    maxInPathSet.add(inPath);
                } else {
                    otherInPathSet.add(inPath);
                }
            }
            isAnd = !maxInPathSet.isEmpty()/* && (outFieldNb / ((maxInPathNb == 0) ? 1 : maxInPathNb)) / inFieldNb >= 2*/; // OPTIONAL more result field than query ones ; maxInPathNb = nb of results, outFieldNb / maxInPathNb = nb of fields per item
            isPost = maxInPathSet.isEmpty() && otherInPathSet.isEmpty() && !singleInPathSet.isEmpty() && outFieldNb / inFieldNb < 2 & inFieldNb <= outFieldNb;
            isGetOnId = maxInPathSet.isEmpty() && otherInPathSet.isEmpty() && !singleInPathSet.isEmpty() && outFieldNb / inFieldNb >= 2; 
            System.out.println("max/other/single/diff: " + maxInPathSet + " ; " + otherInPathSet + " ; " + singleInPathSet + " ; " + inFieldNb + ", " + outFieldNb);
            if (isAnd) {
                System.out.println("Query (and) ! on [" + maxInPathSet + "] and optional [" + otherInPathSet + " ; " + singleInPathSet + "]");
            } else if (isPost) {
                System.out.println("post ! computed fields (including ids) are [out pathes which are not in correlations]");
            } else if (isGetOnId) {
                System.out.println("get on id ! id is [" + singleInPathSet + "]");
            }
            
            // for OR
            HashMap<String, Integer> outFieldPathCorrelationMap = new HashMap<String, Integer>();
            for (ReqResFieldCorrelation correlation : correlationLevel.getCorrelations()) {
                String path = correlation.getOutField().getPath();
                Object found =  outFieldPathCorrelationMap.get(path);
                int nb = 1;
                if (found != null) {
                    nb += outFieldPathCorrelationMap.get(path);
                }
                outFieldPathCorrelationMap.put(path, nb);
            }
            ArrayList<String> curOrOutPathes = new ArrayList<String>();
            for (String outPath : outFieldPathCorrelationMap.keySet()) {
                if (outFieldPathCorrelationMap.get(outPath) > 1) {
                    curOrOutPathes.add(outPath);
                }
            }
            isOr = !curOrOutPathes.isEmpty();
            // TODO
        }
    }

    private void printCorrelations(ArrayList<ReqResFieldCorrelation> correlations) {
        System.out.println("Found correlations, sorted y level :");
        Collections.sort(correlations, new Comparator<ReqResFieldCorrelation>() {
            @Override
            public int compare(ReqResFieldCorrelation corr1, ReqResFieldCorrelation corr2) {
                return corr1.getLevel() - corr2.getLevel();
            }
        });
        for (ReqResFieldCorrelation correlation : correlations) {
            System.out.println(correlation.getLevel() + "\t" + correlation.getInField()
                    + "\t" + correlation.getOutField() + "\t" + correlation.getInfo());
        }
    }

    private void addCorrelationsFromOutByPathOrNameAndValue(HashMap<Integer, CorrelationLevel> correlationLevels, int level,
            HashMap<String, CandidateField> foundOutFields, HashMap<String, CandidateField> inFields) {
        for (CandidateField inField : inFields.values()) {
            int queryLevel = level;
            List<CandidateField> foundByPathAndValueFields = getFromOutByPathAndValue(foundOutFields, inField);
            if (foundByPathAndValueFields.size() != 0) {
                for (CandidateField field : foundByPathAndValueFields) {
                    // TODO also contains (or more comparison)
                    ReqResFieldCorrelation reqResFieldCorrelation = new ReqResFieldCorrelation(queryLevel, inField, field, "byPathAndValue");
                    putCorrelation(correlationLevels, reqResFieldCorrelation);
                }
                queryLevel = queryLevel / 4;
            }
            List<CandidateField> foundByNameAndValueFields = getFromOutByNameAndValue(foundOutFields, inField);
            foundByNameAndValueFields.removeAll(foundByPathAndValueFields); // removing fields already found by path
            if (foundByNameAndValueFields.size() != 0) {
                for (CandidateField field : foundByNameAndValueFields) {
                    // TODO also contains (or more comparison)
                    // substract depth to level
                    ReqResFieldCorrelation reqResFieldCorrelation = new ReqResFieldCorrelation(queryLevel - field.getPath().split("/").length*2, inField, field, "byNameAndValue");
                    putCorrelation(correlationLevels, reqResFieldCorrelation);
                }
                //queryLevel = queryLevel / 2;
            }
        }
    }

    // TODO also substract place number in out
    // TODO also contains (or more comparison)
    // TODO also contains (or more)
    private List<CandidateField> getFromOutByPathAndValue(
            HashMap<String, CandidateField> foundOutFields,
            CandidateField inQueryField) {
        ArrayList<CandidateField> res = new ArrayList<CandidateField>();
        CandidateField found = foundOutFields.get(inQueryField.getPath());
        if (found != null && inQueryField.getValue().equals(found.getValue())) {
            res.add(found);
        }
        return res;
    }

    // TODO also contains (or more comparison)
    // TODO substract depth
    private List<CandidateField> getFromOutByNameAndValue(
            HashMap<String, CandidateField> foundOutFields,
            CandidateField inQueryField) {
        ArrayList<CandidateField> res = new ArrayList<CandidateField>();
        for (CandidateField outField : foundOutFields.values()) {
            if (outField != null && inQueryField.getName().equals(outField.getName())
                    && inQueryField.getValue().equals(outField.getValue())) {
                res.add(outField);
            }   
        }
        return res;
    }
    
    

    
    /**
     * Second correlation algorithm : tries to find correlation
     *  - if in content, value and (sub)path correlations
     *  - else (otherwise lowered) if in query, value and (sub)path correlations
     *  - else (otherwise lowered) for all path elements, value correlations
     *  
     * @param jsonExchange
     * @param inPathFields
     * @param inQueryFields
     * @param inContentFields
     * @param foundOutFields
     */
    private void correlateWithSubpath(ExchangeRecord jsonExchange, HashMap<String, CandidateField> inPathFields,
            HashMap<String, CandidateField> inQueryFields,
            HashMap<String, CandidateField> inContentFields,
            HashMap<String, CandidateField> foundOutFields) {
        // looking for correlations :
        int level = 16;
        // if in content, value and path correlations
        ArrayList<Object[]> correlations = new ArrayList<Object[]>();
        
        if (jsonExchange.getInMessage().getPostData() != null) {
            addCorrelationsFromOutBySubpathAndValue(correlations, level, foundOutFields, inContentFields);
            level = level / 4;
        }
        
        // else (otherwise lowered) if in query, value and path correlations
        if (jsonExchange.getInMessage().getQueryString() != null) {
            addCorrelationsFromOutBySubpathAndValue(correlations, level, foundOutFields, inQueryFields);
            level = level / 4;
        }
        
        // else (otherwise lowered) for all path elements, value correlations
        for (CandidateField inPathField : inPathFields.values()) {
            List<CandidateField> foundByValueFields = getFromOutByValue(foundOutFields, inPathField);
            for (CandidateField field : foundByValueFields) {
                // exact only
                correlations.add(new Object[]{ level - field.getPath().split("/").length*2, inPathField, field, "byValue" });
            }
        }

        System.out.println("Found correlations with subpath :");
        for (Object[] correlation : correlations) {
            System.out.println(correlation[0] + "\t" + correlation[1] + "\t" + correlation[2] + "\t" + correlation[3]);
        }
        System.out.println();
    }

    private void addCorrelationsFromOutBySubpathAndValue(ArrayList<Object[]> correlations, int level,
            HashMap<String, CandidateField> foundOutFields, HashMap<String, CandidateField> inFields) {
        for (CandidateField inField : inFields.values()) {
            addCorrelationsFromOutBySubpathAndValue(correlations, level, foundOutFields, inField);
        }
    }

    /**
     * TODO also contains (or more comparison)
     * TODO merge fullpath with subpath, once 
     * @param correlations
     * @param level
     * @param foundOutFields
     * @param inField
     * @return
     */
    private int addCorrelationsFromOutBySubpathAndValue(ArrayList<Object[]> correlations, int level,
            HashMap<String, CandidateField> foundOutFields, CandidateField inField) {
        //ArrayList<CandidateField> res = new ArrayList<CandidateField>();

        // full path :
        CandidateField fullPathOutField = foundOutFields.get(inField.getPath());
        if (fullPathOutField != null) {
            if (inField.getValue().equals(fullPathOutField.getValue())) {
                //res.add(found);
                // TODO also more comparison
                correlations.add(new Object[]{ level, inField, fullPathOutField, "byPathAndValue" });
                
                // either hit hard on level or even stop there
                level = level - 6; // 4
                //return level;
                
            } else {
                // path only, trying out ?!?
                correlations.add(new Object[]{ level / 4, inField, fullPathOutField, "byPath" }); // ?!?
            }
        }
        
        int matchingInFieldSubPathDepth = inField.getPath().split("/").length;
        
        // full path partially :
        boolean foundAtLeastOne = false;
        for (CandidateField outField : foundOutFields.values()) {
            if (outField.getPath().endsWith('/' + inField.getPath())
                    && inField.getValue().equals(outField.getValue())) {
                //res.add(outField);
                // TODO also more comparison
                // outDepth is bad (at least 1), more than 1 matchingInFieldSubPathDepth is good
                int outDepth = outField.getPath().split("/").length - matchingInFieldSubPathDepth;
                correlations.add(new Object[]{ level - outDepth*2 + 2*(matchingInFieldSubPathDepth - 1), inField, outField, "byPartialPathAndValue" });
                foundAtLeastOne = true;
            }   
        }
        if (foundAtLeastOne) {
            // either hit hard on level or even stop there
            level = level - 6; // 4
            //return level;
        }

        int inDepth = 1; // = nonMatchingInFieldSubpathDepth
        int indexOfSlashPlusOne = 0;
        while ((indexOfSlashPlusOne = inField.getPath().indexOf('/', indexOfSlashPlusOne) + 1) != 0) {
            String subpath = inField.getPath().substring(indexOfSlashPlusOne);
            matchingInFieldSubPathDepth--;
            inDepth++;
            boolean foundAtLeastOneInSubpath = false;

            for (CandidateField outField : foundOutFields.values()) {
                if (outField.getPath().equals(subpath) || outField.getPath().endsWith('/' + subpath)
                        && inField.getValue().equals(outField.getValue())) {
                    //res.add(outField);
                    // TODO also more comparison
                    // outDepth is bad, inDepth worse (but already >= 1), more than 1 matchingInFieldSubPathDepth is good
                    int outDepth = outField.getPath().split("/").length - matchingInFieldSubPathDepth;
                    correlations.add(new Object[]{ level - outDepth*2 - inDepth*2 + 2*(matchingInFieldSubPathDepth - 1), inField, outField, "bySubpathAndValue:" + subpath });
                    foundAtLeastOneInSubpath = true;
                }   
            }
            if (foundAtLeastOneInSubpath) {
                // either hit hard on level or even stop there
                level = level - 6; // 4
                //return level;
            }
        }
        return level;
    }

    // exact only
    // TODO substract depth
    private List<CandidateField> getFromOutByValue(
            HashMap<String, CandidateField> foundOutFields, CandidateField inPathField) {
        ArrayList<CandidateField> res = new ArrayList<CandidateField>();
        for (CandidateField outField : foundOutFields.values()) {
            if (outField != null && inPathField.getValue().equals(outField.getValue())) {
                res.add(outField);
            }   
        }
        return res;
    }
    
    
    
    
    
    
    
    ///////////////////////////////////////
    // slightly OBSOLETE stuff

    public void testJsonContent() {
        String trimmedOutContent = jsonGetExchange.getOutMessage().getMessageContent().getContent();
        if (trimmedOutContent.startsWith("[") || trimmedOutContent.startsWith("{")) {
            JSONObject jsonOutObject = (JSONObject) JSONSerializer.toJSON(trimmedOutContent);
            HashMap<String,CandidateField> foundOutFields = new HashMap<String,CandidateField>();
            Stack<Object> pathStack = new Stack<Object>();
            visit(jsonOutObject, foundOutFields, pathStack);
            
            HashMap<String,CandidateField> inPathFields = new HashMap<String,CandidateField>();
            putField(inPathFields, new CandidateField("path.0", "1"));
            putField(inPathFields, new CandidateField("path.0", "test"));
            //...
        }
    }
    
    public void visit(JSONObject jsonObject, HashMap<String, CandidateField> foundFields, Stack<Object> pathStack) {
        for (Object key : jsonObject.keySet()) {
            Object obj = jsonObject.get(key);
            pathStack.push(key);
            visitAny(obj, foundFields, pathStack);
            pathStack.pop();
        }
    }

    public void visit(JSONArray jsonArray, HashMap<String, CandidateField> foundFields, Stack<Object> pathStack) {
        for (int i = 0; i < jsonArray.size(); i++) {
            Object jsonArrayObj = jsonArray.get(i);
            pathStack.push(i);
            visitAny(jsonArrayObj, foundFields, pathStack);
            pathStack.pop();
        }
    }
    
    private void visitAny(Object obj, HashMap<String, CandidateField> foundFields, Stack<Object> pathStack) {
        if (obj instanceof JSONObject) {
            visit((JSONObject) obj, foundFields, pathStack);
        } else if (obj instanceof JSONArray) {
            visit((JSONArray) obj, foundFields, pathStack);
        } else {
            // field
            putCandidateFields(obj, foundFields, pathStack);
        }
    }
    
    private void putCandidateFields(Object obj, HashMap<String, CandidateField> foundFields, Stack<Object> pathStack) {
        CandidateField candidateField = new CandidateField(toPath(pathStack), String.valueOf(obj));
        foundFields.put(candidateField.getPath(), candidateField);
    }

    public String toPath(Stack<Object> pathStack) {
        StringBuffer sbuf = new StringBuffer();
        for (Object pathElt : pathStack) {
            sbuf.append("/");
            sbuf.append(pathElt);
        }
        return sbuf.toString();
    }
    
    
    
    /////////////////////////////////
    // XML specific OBSOLETE ! "by type" can be done statically, otherwise just like JSON with longer names (due to ns)

    @Test
    public void testXMLGet() {
        System.out.println("testXMLGet -");
        HashMap<String,CandidateField> xmlInFields = new HashMap<String,CandidateField>();
        HashMap<String,CandidateField> xmlOutFields = new HashMap<String,CandidateField>();
        String ns = "{http://www.easysoa.com/test}";
        /*
        xmlInFields.put("id", new CandidateField("id", "1"), ns + "id"); //jsonPostQueryExchange
        
        xmlOutFields.put("id", new CandidateField("id", "1"), ns + "id");
        xmlOutFields.put("name", new CandidateField("name", "test1"), ns + "name");
        xmlOutFields.put("description", new CandidateField("description", "a fine test"), ns + "description");
        xmlOutFields.put("second/id", new CandidateField("second/id", "11"), ns + "id"); // second "wrong" id, not found because of value
        xmlOutFields.put("third/id", new CandidateField("third/id", "1")); // third "wrong" id, found but at lower level
        */
        correlateXML(xmlInFields, xmlOutFields);
    }
    
    /**
     * XML differs from JSON in :
     * - typing, which is like "byPath", in a perfect way when it's on concrete (business) types,
     * and in a less accurate way when it matches only on extended (including primitive) types
     * (ex. similar to a getBySubpath, or even exactly a getByValue if primitive type).
     * They can be handled in a good way by prefixing by ns.
     * NB. without typing & ns (or empty ns everywhere) it is similar to JSON & "byPath".
     * - attributes, which are typed fields like others, or field-typed fields if no typing.
     * They can be handled in a good way by prefixing by ns and "@".
     * @param xmlInFields
     * @param xmlOutFields
     */
    private void correlateXML(HashMap<String, CandidateField> xmlInFields,
            HashMap<String, CandidateField> xmlOutFields) {
        // looking for correlations :
        int level = 16;
        // if in content, value and path correlations
        ArrayList<Object[]> correlations = new ArrayList<Object[]>();
    
        for (CandidateField inContentField : xmlInFields.values()) {
            List<CandidateField> foundByTypeAndValueFields = getFromOutByTypeAndValue(xmlOutFields, inContentField);
            if (foundByTypeAndValueFields.size() != 0) {
                for (CandidateField field : foundByTypeAndValueFields) {
                    // TODO also contains (or more comparison)
                    correlations.add(new Object[]{ level, inContentField, field, "byTypeAndValue" });
                }
                level = level / 4;
            }
            List<CandidateField> foundByPathAndValueFields = getFromOutByPathAndValue(xmlOutFields, inContentField);
            if (foundByPathAndValueFields.size() != 0) {
                for (CandidateField field : foundByPathAndValueFields) {
                    // TODO also contains (or more comparison)
                    correlations.add(new Object[]{ level, inContentField, field, "byPathAndValue" }); // not really interesting since already found by type ; else containing path ??
                }
                level = level / 4;
            }
            List<CandidateField> foundByNameAndValueFields = getFromOutByNameAndValue(xmlOutFields, inContentField);
            foundByNameAndValueFields.removeAll(foundByPathAndValueFields); // removing fields already found by path
            if (foundByNameAndValueFields.size() != 0) {
                for (CandidateField field : foundByNameAndValueFields) {
                    // TODO also contains (or more comparison)
                    // substract depth to level
                    correlations.add(new Object[]{ level - field.getPath().split("/").length*2, inContentField, field, "byNameAndValue" });
                }
                //level = level / 2;
            }
        }

        System.out.println("Found correlations :");
        for (Object[] correlation : correlations) {
            System.out.println(correlation[0] + "\t" + correlation[1] + "\t" + correlation[2] + "\t" + correlation[3]);
        }
    }

    // TODO also substract place number in out
    // TODO also contains (or more comparison)
    // TODO also contains (or more)
    private List<CandidateField> getFromOutByTypeAndValue(
            HashMap<String, CandidateField> xmlOutFields,
            CandidateField xmlInField) {
        ArrayList<CandidateField> res = new ArrayList<CandidateField>();
        for (CandidateField outField : xmlOutFields.values()) {
            if (outField != null && xmlInField.getType().equals(outField.getType())
                    && xmlInField.getValue().equals(outField.getValue())) {
                res.add(outField);
            }   
        }
        return res;
    }
    
}
