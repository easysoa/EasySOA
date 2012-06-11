package org.easysoa.startup;

public class BrowserHelper {

    private static final String[] BROWSERS = { "firefox", "google-chrome", "opera",
       "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" };
    
    /**
     * Opens the specified web page in the user's default browser
     * (Desktop.browse() doesn't work correctly with Ubuntu 11.10)
     * @throws Exception 
     */
    public static void openBrowser(String url) throws Exception {

        // Source: http://www.centerkey.com/java/browser/

        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            Class.forName("com.apple.eio.FileManager")
                    .getDeclaredMethod("openURL", new Class[] { String.class })
                    .invoke(null, new Object[] { url });
        } else if (osName.startsWith("Windows"))
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        else { // assume Unix or Linux
            String browser = null;
            for (String b : BROWSERS) {
                if (browser == null &&  Runtime.getRuntime().exec(
                        new String[] { "which", b }).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[] { browser = b, url });
                }
            }
            if (browser == null) {
                throw new Exception();
            }
        }
    }
}
