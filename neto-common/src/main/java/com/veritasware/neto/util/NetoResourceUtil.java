package com.veritasware.neto.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by chacker on 2016-11-04.
 */
public class NetoResourceUtil {

    public String fileRead(String fileName) {
        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public File getFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return file;
    }

    public Map<Integer, Class<? extends NetoJsonMessage>> opcodeMap(String filePath, String messagePackage) throws Exception {

        BiMap<Integer, Class<? extends NetoJsonMessage>> classBiMap = HashBiMap.create();

        File opcodeJsonFile = getFile(filePath);

        ObjectMapper ob = new ObjectMapper();

        JsonNode jsonNode = ob.readTree(opcodeJsonFile);
        Iterator<String> it = jsonNode.fieldNames();

        while(it.hasNext()) {
            StringBuilder sb = new StringBuilder();
            String className = it.next();
            int opcode = jsonNode.get(className).asInt();
            sb.append(messagePackage).append('.').append(className);
            Class<? extends NetoJsonMessage> aClass = (Class<? extends NetoJsonMessage>) Class.forName(sb.toString());
            classBiMap.put(opcode, aClass);
        }

        return classBiMap;
    }
}
