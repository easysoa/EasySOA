import org.easysoa.registry.test.AbstractRegistryTest;
import org.junit.Test;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;


@Deploy("org.easysoa.registry.test")
@RepositoryConfig(cleanup = Granularity.CLASS)
public class TestDeploymentTest extends AbstractRegistryTest {

    @Test
    public void testBundleAndContribution() {
        
    }
    
}
