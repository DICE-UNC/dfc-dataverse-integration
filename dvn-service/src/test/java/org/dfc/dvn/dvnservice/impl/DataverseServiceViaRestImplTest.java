package org.dfc.dvn.dvnservice.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.dfc.dvn.dvnservice.DataverseService;
import org.dfc.dvn.dvnservice.domain.DataVerseConfig;
import org.dfc.dvn.dvnservice.utils.DvnTestingPropertiesHelper;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.testutils.AssertionHelper;
import org.irods.jargon.testutils.IRODSTestSetupUtilities;
import org.irods.jargon.testutils.TestingPropertiesHelper;
import org.irods.jargon.testutils.filemanip.FileGenerator;
import org.irods.jargon.testutils.filemanip.ScratchFileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataverseServiceViaRestImplTest {

	private static Properties testingProperties = new Properties();
	private static TestingPropertiesHelper testingPropertiesHelper = new TestingPropertiesHelper();
	private static ScratchFileUtils scratchFileUtils = null;
	public static final String IRODS_TEST_SUBDIR_PATH = "DataverseServiceViaRestImplTest";
	private static IRODSTestSetupUtilities irodsTestSetupUtilities = null;
	private static AssertionHelper assertionHelper = null;
	private static IRODSFileSystem irodsFileSystem;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestingPropertiesHelper testingPropertiesLoader = new TestingPropertiesHelper();
		testingProperties = testingPropertiesLoader.getTestProperties();
		scratchFileUtils = new ScratchFileUtils(testingProperties);
		scratchFileUtils
				.clearAndReinitializeScratchDirectory(IRODS_TEST_SUBDIR_PATH);
		irodsTestSetupUtilities = new IRODSTestSetupUtilities();
		irodsTestSetupUtilities.initializeIrodsScratchDirectory();
		irodsTestSetupUtilities
				.initializeDirectoryForTest(IRODS_TEST_SUBDIR_PATH);
		assertionHelper = new AssertionHelper();
		irodsFileSystem = IRODSFileSystem.instance();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		irodsFileSystem.closeAndEatExceptions();
	}

	@Test
	public void importStudyToDvn() throws Exception {
		// create a file

		String testFileName = "importStudyToDvn.txt";
		String absPath = scratchFileUtils
				.createAndReturnAbsoluteScratchPath(IRODS_TEST_SUBDIR_PATH);
		String localFileName = FileGenerator
				.generateFileOfFixedLengthGivenName(absPath, testFileName, 1);

		File localFile = new File(localFileName);

		// now put the file
		String targetIrodsFile = testingPropertiesHelper
				.buildIRODSCollectionAbsolutePathFromTestProperties(
						testingProperties, IRODS_TEST_SUBDIR_PATH);

		IRODSAccount irodsAccount = testingPropertiesHelper
				.buildIRODSAccountFromTestProperties(testingProperties);
		IRODSAccessObjectFactory accessObjectFactory = irodsFileSystem
				.getIRODSAccessObjectFactory();

		DataTransferOperations dto = accessObjectFactory
				.getDataTransferOperations(irodsAccount);

		String targetFileName = targetIrodsFile + "/" + testFileName;

		dto.putOperation(localFileName, targetFileName, "", null, null);

		InputStream irodsFileInputStream = new BufferedInputStream(
				irodsFileSystem.getIRODSFileFactory(irodsAccount)
						.instanceIRODSFileInputStream(targetFileName));

		DataVerseConfig dataVerseConfig = new DataVerseConfig();
		dataVerseConfig.setHost(testingProperties
				.getProperty(DvnTestingPropertiesHelper.DVN_URL_HOST));
		dataVerseConfig.setPort(testingProperties
				.getProperty(DvnTestingPropertiesHelper.DVN_URL_PORT));
		dataVerseConfig
				.setRequestRoot(testingProperties
						.getProperty(DvnTestingPropertiesHelper.DVN_DEPOSIT_REQUEST_ROOT));
		dataVerseConfig.setSsl(Boolean.valueOf(testingProperties
				.getProperty(DvnTestingPropertiesHelper.DVN_URL_HTTPS)));
		dataVerseConfig.setStudyId(testingProperties
				.getProperty(DvnTestingPropertiesHelper.DVN_STUDY));
		dataVerseConfig
				.setVerb(testingProperties
						.getProperty(DvnTestingPropertiesHelper.DVN_DEPOSIT_VARIABLE_PART));

		DataverseService dataverseService = new DataverseServiceViaRestImpl(
				dataVerseConfig);

		dataverseService.importStudyToDvn(targetFileName, irodsFileInputStream);
		irodsFileSystem.closeAndEatExceptions();

	}

}
