package org.easysoa.records.assertions;

import java.util.List;

import org.easysoa.template.TemplateFieldSuggestions;

import com.openwide.easysoa.message.OutMessage;

public interface AssertionEngine {

    /**
     * Suggest assertions and store them in a 'asr' file.
     * @param fieldSuggestions
     * @param recordID
     * @param storeName
     * @return AssertionSuggestions
     * @throws Exception If a problem occurs during the creation of the 'asr' file
     */
    public abstract AssertionSuggestions suggestAssertions(TemplateFieldSuggestions fieldSuggestions, String recordID, String storeName) throws Exception;

    /**
     * Executes several assertions
     * @param assertions
     * @return
     */
    public abstract List<AssertionResult> executeAssertions(AssertionSuggestions assertionSuggestions, OutMessage originalMessage, OutMessage replayedMessage);

    /**
     * Execute one assertion
     * @param assertion
     * @return 
     */
    public abstract AssertionResult executeAssertion(Assertion assertion, OutMessage originalMessage, OutMessage replayedMessage);

}