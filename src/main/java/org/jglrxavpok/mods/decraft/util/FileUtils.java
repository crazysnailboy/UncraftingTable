package org.jglrxavpok.mods.decraft.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.jglrxavpok.mods.decraft.ModUncrafting;

import net.minecraftforge.fml.common.Loader;


public class FileUtils
{

	public static String readFileContentsFromMod(String fileName)
	{
		String fileContents = "";
		try
		{
			InputStream stream = ModUncrafting.instance.getClass().getClassLoader().getResourceAsStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

			fileContents = IOUtils.toString(stream);

			reader.close();
			stream.close();

		}
		catch(Exception ex){ ModUncrafting.instance.getLogger().catching(ex); }
		return fileContents;
	}

}
