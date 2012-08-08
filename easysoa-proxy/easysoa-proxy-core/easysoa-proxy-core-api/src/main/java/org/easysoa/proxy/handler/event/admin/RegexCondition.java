package org.easysoa.proxy.handler.event.admin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.easysoa.message.InMessage;

/**
 *
 * @author fntangke
 */
public class RegexCondition implements CompiledCondition {

    private Pattern p;

    public RegexCondition() {
    }

    /**
     * Constructor
     *
     * @param regex
     */
    public RegexCondition(String regex) {
        this.p = Pattern.compile(regex.concat(".*"));
    }

    /**
     * @return true or false if the pattern's url matched
     */
    @Override
    public boolean matches(InMessage inMessage) {
        Matcher m = this.p.matcher(inMessage.buildCompleteUrl());
        return m.matches();
    }

    /**
     *
     * @return the Regex Pattern
     */
    public Pattern getP() {
        return p;
    }

    /**
     * Pattern setter
     *
     * @param p
     */
    public void setP(Pattern p) {
        this.p = p;
    }

    /**
     *
     * @return true if inMessage matches with the regex
     */
    @Override
    public boolean matches() {
        return false;
    }
}
