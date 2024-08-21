
package smllregistry;

import blazing.Blazing;

/**
 *
 * @author ERC
 */
public class SMLLRegistry {

  public static void main(String[] args) {
    Blazing.createServer(RegistryServer.class);
  }
}
