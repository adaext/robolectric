package org.robolectric.util;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.internal.ManifestFactory;
import org.robolectric.internal.ManifestIdentifier;
import org.robolectric.res.Fs;

@SuppressWarnings("NewApi")
public class TestRunnerWithManifest extends RobolectricTestRunner {
  public TestRunnerWithManifest(Class<?> testClass) throws InitializationError {
    super(testClass, RobolectricTestRunner.defaultInjector()
        .bind(ManifestFactory.class, config ->
            new ManifestIdentifier(
                "org.robolectric",
                resourceFile("AndroidManifest.xml"),
                resourceFile("res"),
                resourceFile("assets"),
                Collections.emptyList()
            )
        ).build());
  }

  private static Path resourceFile(String... pathParts) {
    return Fs.join(resourcesBaseDir(), pathParts);
  }

  private static Path resourcesBaseDir() {
    // Try to locate the manifest file as a classpath resource.
    final String resourceName = "/src/test/resources/AndroidManifest.xml";
    final URL resourceUrl = TestRunnerWithManifest.class.getResource(resourceName);
    if (resourceUrl != null && "file".equals(resourceUrl.getProtocol())) {
      // Construct a path to the manifest file relative to the current working directory.
      final URI workingDirectory = URI.create(System.getProperty("user.dir"));
      final URI absolutePath = URI.create(resourceUrl.getPath());
      final URI relativePath = workingDirectory.relativize(absolutePath);
      return new File(relativePath.toString()).getParentFile().toPath();
    }

    // Return a path relative to the current working directory.
    return Util.file("src", "test", "resources").toPath();
  }
}
