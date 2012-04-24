/**
 * EasySOA Proxy core
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.template.AbstractTemplateField.TemplateFieldType;
import org.easysoa.template.InputTemplateField;
import org.easysoa.template.TemplateFieldSuggestions;

/**
 * Implementation of CorrelationEngine interface
 * 
 * @author jguillemotte
 *
 */
// TODO : Huge refactoring to simplify this class
// TODO REST JAXRS service, web UI
public class CorrelationEngineImpl implements CorrelationEngine {

	// Logger
	private static Logger logger = Logger.getLogger(CorrelationEngineImpl.class.getName());	
	
	/**
	public CorrelationResult correlate(String exchangeRecordFileStorePath) {
		return null;
	}*/
	
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
    public void correlate(ExchangeRecord jsonExchange, HashMap<String, CandidateField> inPathFields,
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


    /**
     * 
     * @param correlationLevels
     * @param reqResFieldCorrelation
     */
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
        logger.debug("Found correlations, sorted y level :");
        ArrayList<CorrelationLevel> correlationLevels = new ArrayList<CorrelationLevel>(correlationLevelMap.values());
        Collections.sort(correlationLevels, new Comparator<CorrelationLevel>() {
            @Override
            public int compare(CorrelationLevel corrLvl1, CorrelationLevel corrLvl2) {
                return - (corrLvl1.getLevel() - corrLvl2.getLevel());
            }
        });
        
        
        boolean isAnd, isPost, isGetOnId, isOr = false;
        //ArrayList<String> andInPathes, orOutPathes = null;
        for (CorrelationLevel correlationLevel : correlationLevels) {
            //int levelCorrelationNb = correlationLevel.getCorrelations().size();
            logger.debug("");
            logger.debug(correlationLevel.getLevel() + ":");
            for (ReqResFieldCorrelation correlation : correlationLevel.getCorrelations()) {
                logger.debug("\t" + correlation.getInField() + "\t" + correlation.getOutField() + "\t" + correlation.getInfo());
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
            isPost = maxInPathSet.isEmpty() && otherInPathSet.isEmpty() && !singleInPathSet.isEmpty() && outFieldNb / inFieldNb <= 2 & inFieldNb <= outFieldNb;
            isGetOnId = maxInPathSet.isEmpty() && otherInPathSet.isEmpty() && !singleInPathSet.isEmpty() && outFieldNb / inFieldNb > 2; 
            logger.debug("max/other/single/diff: " + maxInPathSet + " ; " + otherInPathSet + " ; " + singleInPathSet + " ; " + inFieldNb + ", " + outFieldNb);
            if (isAnd) {
                logger.debug("Query (and) ! on [" + maxInPathSet + "] and optional [" + otherInPathSet + " ; " + singleInPathSet + "]");
            } else if (isPost) {
                logger.debug("post ! computed fields (including ids) are [out pathes which are not in correlations]");
            } else if (isGetOnId) {
                logger.debug("get on id ! id is [" + singleInPathSet + "]");
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

    /*private void printCorrelations(ArrayList<ReqResFieldCorrelation> correlations) {
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
    }*/

    /**
     * 
     * @param correlationLevels
     * @param level
     * @param foundOutFields
     * @param inFields
     */
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

    /**
     * 
     * @param foundOutFields
     * @param inQueryField
     * @return
     */
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

    /**
     * 
     * @param foundOutFields
     * @param inQueryField
     * @return
     */
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
     * ----------------------------------------------------------------------------------------------------------
     */
    
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
    @Override
    public TemplateFieldSuggestions correlateWithSubpath(ExchangeRecord jsonExchange, HashMap<String, CandidateField> inPathFields,
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
        
        // Only for debug
        logger.debug("Found correlations with subpath : ");
        for (Object[] correlation : correlations) {
            logger.debug(correlation[0] + "\t" + correlation[1] + "\t" + correlation[2] + "\t" + correlation[3]);
        }
        logger.debug("");

        // Convert the correlation result into TemplateFieldSuggestions object
        return convertToInputTemplateFieldSuggestions(correlations);
    }

    /**
     * Convert the correlation object list in TemplateFieldSuggestions
     * @param correlations Correlations
     * @return A <code>TemplatefieldSuggestions</code> containing the fields detected by correlations
     */
    private TemplateFieldSuggestions convertToInputTemplateFieldSuggestions(ArrayList<Object[]> correlations) {
    	TemplateFieldSuggestions suggestions = new TemplateFieldSuggestions();
    	InputTemplateField templateField;
    	for (Object[] correlation : correlations) {
    	    int level = (Integer)correlation[0];
    		CandidateField inField = (CandidateField)correlation[1];
    		CandidateField outField = (CandidateField)correlation[2];
            // TODO : Returns only ImputTemplateFields at the moment
    		// Make modifications to returns also OutputTemplateFields
    		templateField = new InputTemplateField();
            templateField.setFieldName(outField.getName());
            templateField.setDefaultValue(inField.getValue());
            templateField.setFieldType(inField.getType());
            templateField.setParamType(TemplateFieldType.valueOf(inField.getKind()));
            templateField.setCorrelationLevel(level);
            if("PATH_PARAM".equals(inField.getKind())){
            	try {
            		int paramPathPosition = Integer.valueOf(inField.getName().substring(inField.getName().lastIndexOf(".")+1, inField.getName().length()));
            		templateField.setPathParamPosition(paramPathPosition);
            	}catch(Exception ex){
            		// Do nothing, leave simply the attribute empty
            		logger.debug("Unable to get field position in path", ex);
            	}
            }
            suggestions.add(templateField);
    	}
    	return suggestions;
    }
    
    /**
     * 
     * @param correlations
     * @param level
     * @param foundOutFields
     * @param inFields
     */
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

    /**
     * 
     * @param foundOutFields
     * @param inPathField
     * @return
     */
    // exact only
    // TODO substract depth    
    private List<CandidateField> getFromOutByValue(HashMap<String, CandidateField> foundOutFields, CandidateField inPathField) {
        ArrayList<CandidateField> res = new ArrayList<CandidateField>();
        for (CandidateField outField : foundOutFields.values()) {
            if (outField != null && inPathField.getValue().equals(outField.getValue())) {
            	outField.setKind(inPathField.getKind());
                res.add(outField);
            }   
        }
        return res;
    }
    
    ///////////////////////////////////////
    // slightly OBSOLETE stuff

    //private ExchangeRecord jsonGetExchange;
    
    /*public void testJsonContent() {
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
    }*/
    
    /**
     * 
     * @param jsonObject
     * @param foundFields
     * @param pathStack
     */
    public void visit(JSONObject jsonObject, HashMap<String, CandidateField> foundFields, Stack<Object> pathStack) {
        for (Object key : jsonObject.keySet()) {
            Object obj = jsonObject.get(key);
            pathStack.push(key);
            visitAny(obj, foundFields, pathStack);
            pathStack.pop();
        }
    }

    /**
     * 
     * @param jsonArray
     * @param foundFields
     * @param pathStack
     */
    public void visit(JSONArray jsonArray, HashMap<String, CandidateField> foundFields, Stack<Object> pathStack) {
        for (int i = 0; i < jsonArray.size(); i++) {
            Object jsonArrayObj = jsonArray.get(i);
            pathStack.push(i);
            visitAny(jsonArrayObj, foundFields, pathStack);
            pathStack.pop();
        }
    }
    
    /**
     * 
     * @param obj
     * @param foundFields
     * @param pathStack
     */
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
    
    /**
     * 
     * @param obj
     * @param foundFields
     * @param pathStack
     */
    private void putCandidateFields(Object obj, HashMap<String, CandidateField> foundFields, Stack<Object> pathStack) {
        CandidateField candidateField = new CandidateField(toPath(pathStack), String.valueOf(obj));
        foundFields.put(candidateField.getPath(), candidateField);
    }

    /**
     * 
     * @param pathStack
     * @return
     */
    public String toPath(Stack<Object> pathStack) {
        StringBuffer sbuf = new StringBuffer();
        for (Object pathElt : pathStack) {
            sbuf.append("/");
            sbuf.append(pathElt);
        }
        return sbuf.toString();
    }
	
}
