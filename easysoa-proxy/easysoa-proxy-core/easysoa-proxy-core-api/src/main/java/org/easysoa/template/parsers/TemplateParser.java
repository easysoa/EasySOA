/**
 * 
 */
package org.easysoa.template.parsers;

import java.util.HashMap;

import org.easysoa.records.correlation.CandidateField;

import com.openwide.easysoa.message.OutMessage;

/**
 * @author jguillemotte
 *
 */
public interface TemplateParser {

    /**
     * Parse a JSON content to generate a fieldMap containing output fields
     * @param outMessage The out message to parse
     * @param fieldMap The field map to fill
     * @return
     */
    public HashMap<String, CandidateField> parse(OutMessage outMessage, HashMap<String, CandidateField> fieldMap);

    /**
     * Return true if the message content can be parsed by this parser
     * @param outMessage The message to parse
     * @return true if this parser can parse the message content
     */
    public boolean canParse(OutMessage outMessage);
    
}
