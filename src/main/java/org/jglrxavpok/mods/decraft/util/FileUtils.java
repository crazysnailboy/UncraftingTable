package org.jglrxavpok.mods.decraft.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.jglrxavpok.mods.decraft.ModUncrafting;


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
		catch(Exception ex){ ModUncrafting.LOGGER.catching(ex); }
		return fileContents;
	}

}
