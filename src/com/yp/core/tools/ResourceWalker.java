package com.yp.core.tools;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.yp.core.BaseConstants;

public class ResourceWalker {

	// ->/sql
	public void walk(String pResourcePath) throws URISyntaxException, IOException {

		FileSystem fileSystem = null;
		Stream<Path> walk = null;
		try {
			URL url = BaseConstants.class.getResource(pResourcePath);
			if (url != null) {
				URI uri = BaseConstants.class.getResource(pResourcePath).toURI();
				Path myPath;
				if (uri.getScheme().equals("jar")) {
					fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
					myPath = fileSystem.getPath(pResourcePath);
				} else {
					myPath = Paths.get(uri);
				}
				walk = Files.walk(myPath, 1);
				for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
					System.out.println(it.next());
				}
				walk.close();
			}
		} finally {
			if (walk != null)
				walk.close();

			if (fileSystem != null)
				fileSystem.close();
		}
	}

	public List<Path> getResourceFiles(String pResourcePath, String pFileExtention)
			throws URISyntaxException, IOException {
		FileSystem fileSystem = null;
		Stream<Path> walk = null;
		try {
			URL url = BaseConstants.class.getResource(pResourcePath);
			if (url != null) {
				URI uri = BaseConstants.class.getResource(pResourcePath).toURI();
				Path myPath;
				if (uri.getScheme().equals("jar")) {
					fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
					myPath = fileSystem.getPath(pResourcePath);
				} else {
					myPath = Paths.get(uri);
				}
				List<Path> resourceList = new ArrayList<>();
				walk = Files.walk(myPath, 1);
				for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
					Path resource = it.next();
					if (resource.toString().endsWith(pFileExtention))
						resourceList.add(resource);
				}
				walk.close();
				return resourceList;
			}
		} finally {
			if (walk != null)
				walk.close();

			if (fileSystem != null)
				fileSystem.close();
		}

		return null;
	}
}
