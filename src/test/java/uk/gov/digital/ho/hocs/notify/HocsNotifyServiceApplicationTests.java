package uk.gov.digital.ho.hocs.notify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import uk.gov.digital.ho.hocs.notify.aws.config.LocalStackConfiguration;

@SpringBootTest(classes = LocalStackConfiguration.class)
class HocsNotifyServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
