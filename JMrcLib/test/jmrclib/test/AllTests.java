package jmrclib.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	UtilsTest.class, 
	DirectoryTest.class, 
	RecordTest.class,
	InputSourceTest.class 
	})
public class AllTests {

}
