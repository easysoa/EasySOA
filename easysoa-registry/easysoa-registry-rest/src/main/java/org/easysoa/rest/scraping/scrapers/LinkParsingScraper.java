package org.easysoa.rest.scraping.scrapers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.easysoa.listeners.HttpFile;
import org.easysoa.rest.scraping.FoundService;
import org.easysoa.rest.scraping.ServiceScraper;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class LinkParsingScraper implements ServiceScraper {

    @Override
    public List<FoundService> scrapeURL(URL url) throws Exception {

        List<FoundService> foundServices = new LinkedList<FoundService>();

        // Web page download
        HttpFile f = new HttpFile(url);
        f.download();

        // Web page parsing
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode cleanHtml = cleaner.clean(f.getFile());

        // Find app name / service name
        /*
         * TagNode[] titles = cleanHtml.getElementsByName("title", true); if
         * (titles.length > 0) { result.put("applicationName",
         * titles[0].getText().toString()); }
         */ // TODO

        // Find links
        Object[] links = cleanHtml.evaluateXPath("//a");
        changeToAbsolutePath(links, "href", url);

        for (Object o : links) {
            TagNode link = (TagNode) o;
            try {
                String ref = new URL(url, link.getAttributeByName("href"))
                        .toString();
                String name = (link.getText() != null) ? link.getText()
                        .toString() : ref;

                // Truncate if name is an URL (serviceName cannot contain
                // slashes)
                if (name.contains("/")) {
                    String[] nameParts = name.split("/}");
                    name = nameParts[nameParts.length - 1].replaceAll(
                            "(\\?|\\.|wsdl)", "");
                }

                // Append digits to the link name if it already exists
                int i = 1;
                if (ref != null && ref.toLowerCase().endsWith("wsdl")) {
                    /*
                     * while (foundServices.has(name)) { name = (i == 1 ? name +
                     * i++ : name.substring(0, name.length() - 1)) + i++; }
                     */// TODO
                    foundServices.add(new FoundService(name.replaceAll(
                            "([\n\r]|[ ]*WSDL|[ ]*wsdl)", "").trim(), ref));
                }

            } catch (MalformedURLException e) {
                // Nothing (link parsing failure)
            }
        }

        if (f != null) {
            f.delete();
        }
        return foundServices;

    }

    private static void changeToAbsolutePath(Object[] tagNodes,
            String attribute, URL context) {
        for (Object o : tagNodes) {
            TagNode tag = (TagNode) o;
            String attrValue = tag.getAttributeByName(attribute);
            if ((attrValue != null) && (!attrValue.startsWith("http://"))) {
                try {
                    tag.setAttribute(attribute,
                            new URL(context, attrValue).toString());
                } catch (Exception e) {
                    // Nothing (Could not set attrValue to absolute path)
                }
            }
        }
    }

}
