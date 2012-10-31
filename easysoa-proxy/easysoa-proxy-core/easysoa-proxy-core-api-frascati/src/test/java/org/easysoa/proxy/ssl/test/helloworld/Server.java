
package org.easysoa.proxy.ssl.test.helloworld;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;


@Scope("composite")
public class Server
  implements HelloService
{
  // --------------------------------------------------------------------------
  // SCA Properties.
  // --------------------------------------------------------------------------

  private String header = "->";

  private int count = 1;

  public final String getHeader()
  {
    return header;
  }

  @Property
  public final void setHeader(final String header)
  {
    this.header = header;
  }

  public final int getCount()
  {
    return count;
  }

  @Property
  public final void setCount(final int count)
  {
    this.count = count;
  }

  // --------------------------------------------------------------------------
  // Default constructor.
  // --------------------------------------------------------------------------

  public Server()
  {
    System.out.println("Server created.");
  }

  // --------------------------------------------------------------------------
  // Implementation of the HelloService interface.
  // --------------------------------------------------------------------------

  public final String print(final String msg)
  {
    System.out.println("Server: begin printing...");
    for (int i = 0; i < (count); ++i) {
      System.out.println(((header) + msg));
    }
    System.out.println("Server: print done.");
    return "Helloworld, " + msg;
  }

}
