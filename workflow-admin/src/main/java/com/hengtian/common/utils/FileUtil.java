package com.hengtian.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Created by ma on 2017/11/23.
 */
@Slf4j
public class FileUtil extends org.apache.commons.io.FileUtils {
    public static final int RET_SUCCESS = 1;
    public static final int RET_FAIL = -1;
    public static final int RET_NOT_EXISTS = 0;
    public static final int RET_INIT = -2;
    public static final int FILETYPE_ALL = 0;
    public static final int FILETYPE_FILE = 1;
    public static final int FILETYPE_DIRECTORY = 2;
    public static String tokens = "/";

    public FileUtil() {
    }

    public static String getClassPathName(String paramName) {
        String classpath = getClassPath();
        String name = paramName;
        if(org.apache.commons.lang3.StringUtils.isBlank(paramName)) {
            return classpath.endsWith("/")?classpath:StringUtils.concat(new String[]{classpath, "/"});
        } else if(classpath.endsWith("/")) {
            if(paramName.startsWith("/")) {
                name = paramName.substring(1);
            }

            return StringUtils.concat(new String[]{classpath, name});
        } else {
            return paramName.startsWith("/")?StringUtils.concat(new String[]{classpath, paramName}):StringUtils.concat(new String[]{classpath, "/", paramName});
        }
    }

    public static String getClassPath() {
        return FileUtil.class.getResource("/").getPath();
    }

    public static void creatDirectory(String url) {
        StringTokenizer st = new StringTokenizer(url, "/");
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append(st.nextToken());
        sBuffer.append(tokens);

        while(st.hasMoreTokens()) {
            sBuffer.append(st.nextToken());
            sBuffer.append(tokens);
            File inbox = new File(sBuffer.toString());
            if(!inbox.exists()) {
                inbox.mkdir();
            }
        }

    }

    public static boolean isExsit(String filepathname) {
        boolean returnValue = false;
        File file = null;

        try {
            if(filepathname == null) {
                return false;
            }

            file = new File(filepathname);
            returnValue = file.exists();
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return returnValue;
    }

    public static int createDirectory(String directoryPathName) {
        File file = null;
        byte returnValue = -1;

        try {
            file = new File(directoryPathName);
            if(file.exists()) {
                byte var3 = 1;
                return var3;
            }

            if(file.mkdirs()) {
                returnValue = 1;
            }
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
            returnValue = -1;
        } finally {
            file = null;
        }

        return returnValue;
    }

    public static int createDirectory(String directoryPath, String directoryName) {
        return createDirectory(directoryPath + "/" + directoryName);
    }

    public static int deleteFileAndDirectory(File file) {
        int returnValue = -2;
        File[] fileList = null;

        try {
            if(!file.exists()) {
                byte var10 = 0;
                return var10;
            }

            if(file.isDirectory()) {
                fileList = file.listFiles();

                for(int i = 0; i < fileList.length; ++i) {
                    if(deleteFileAndDirectory(fileList[i]) == -1) {
                        byte var4 = -1;
                        return var4;
                    }
                }
            }

            if(file.delete()) {
                returnValue = 1;
            } else {
                returnValue = -1;
            }
        } catch (Exception var8) {
            log.error(var8.getMessage(), var8);
            return returnValue;
        } finally {
            file = null;
        }

        return returnValue;
    }

    public static int deleteDirectory(String directoryPathName) {
        return deleteFileAndDirectory(new File(directoryPathName));
    }

    public static int deleteDirectory(String directoryPath, String directoryName) {
        return deleteFileAndDirectory(new File(directoryPath + "/" + directoryName));
    }

    public static int deleteFile(String filePathName) {
        return deleteFileAndDirectory(new File(filePathName));
    }

    public static int deleteFile(String filePath, String fileName) {
        return deleteFileAndDirectory(new File(filePath + "/" + fileName));
    }

    public static int moveFile(String oldFileAbsolutePath, String newFileAbsolutePath) {
        if(oldFileAbsolutePath != null && newFileAbsolutePath != null) {
            return moveFileByFile(new File(oldFileAbsolutePath), new File(newFileAbsolutePath));
        } else {
            log.error("moveFile(String oldFileAbsolutePath, String newFileAbsolutePath) method parameter is null");
            return -1;
        }
    }

    public static int moveFileByFile(File oldFile, File newFile) {
        byte returnValue = -1;

        try {
            if(oldFile == null || newFile == null) {
                return -1;
            }

            if(!oldFile.exists()) {
                return 0;
            }

            if(!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
            }

            if(newFile.exists()) {
                newFile.delete();
            }

            if(oldFile.renameTo(newFile) && oldFile.exists()) {
                oldFile.delete();
            }

            returnValue = 1;
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
        }

        return returnValue;
    }

    public static boolean isVaildExtNames(String filename, String[] extNameArray) {
        boolean returnValue = false;
        if(filename == null) {
            return false;
        } else if(extNameArray != null && extNameArray.length != 0) {
            for(int i = 0; i < extNameArray.length; ++i) {
                if(isVaildExtName(filename, extNameArray[i])) {
                    return true;
                }
            }

            return returnValue;
        } else {
            return true;
        }
    }

    public static boolean isVaildExtName(String filename, String extName) {
        boolean returnValue = false;
        String fileNameString = null;
        String extNameString = null;
        String extNameTemp = null;

        try {
            if(filename == null) {
                return false;
            }

            if(extName == null) {
                return true;
            }

            if(!extName.equals("")) {
                if(extName.indexOf(".") != 0) {
                    extName = "." + extName;
                }

                fileNameString = filename.toUpperCase();
                extNameString = extName.toUpperCase();
                int index = fileNameString.lastIndexOf(extNameString);
                if(index > -1) {
                    extNameTemp = fileNameString.substring(index, fileNameString.length());
                    if(extNameTemp.equals(extNameString)) {
                        returnValue = true;
                    }
                }
            } else if(filename.indexOf(".") == -1) {
                returnValue = true;
            }
        } catch (Exception var8) {
            log.error(var8.getMessage(), var8);
        }

        return returnValue;
    }

    public static String modifyExtFileName(String filepathname, String oldExtName, String newExtName, boolean isRealFile) {
        String returnValue = null;
        File file = null;
        File newFile = null;

        try {
            if(filepathname == null || oldExtName == null || newExtName == null) {
                return null;
            }

            if(isRealFile) {
                file = new File(filepathname);
                if(!file.exists()) {
                    return null;
                }
            }

            if(newExtName.indexOf(".") != 0 && !newExtName.equals("")) {
                newExtName = "." + newExtName;
            }

            if(oldExtName.equals("")) {
                if(filepathname.lastIndexOf(".") == -1) {
                    returnValue = filepathname + newExtName;
                } else {
                    returnValue = filepathname.substring(0, filepathname.lastIndexOf(".")) + newExtName;
                }
            } else {
                if(oldExtName.indexOf(".") != 0) {
                    oldExtName = "." + oldExtName;
                }

                if(!isVaildExtName(filepathname, oldExtName)) {
                    return null;
                }

                returnValue = filepathname.substring(0, filepathname.lastIndexOf(oldExtName)) + newExtName;
            }

            if(isRealFile) {
                newFile = new File(returnValue);
                if(newFile.exists()) {
                    newFile.delete();
                }

                if(!file.renameTo(newFile)) {
                    returnValue = null;
                }
            }
        } catch (Exception var8) {
            log.error(var8.getMessage(), var8);
        }

        return returnValue;
    }

    public static boolean isContainStrings(String filename, String[] containStringArray) {
        boolean returnValue = false;
        if(filename == null) {
            return false;
        } else if(containStringArray != null && containStringArray.length != 0) {
            for(int i = 0; i < containStringArray.length; ++i) {
                if(isContainString(filename, containStringArray[i])) {
                    return true;
                }
            }

            return returnValue;
        } else {
            return true;
        }
    }

    public static boolean isContainString(String filename, String containString) {
        boolean returnValue = false;
        if(filename == null) {
            return false;
        } else if(containString == null) {
            return true;
        } else {
            filename = filename.toUpperCase();
            containString = containString.toUpperCase();
            if(filename.indexOf(containString) > -1) {
                returnValue = true;
            }

            return returnValue;
        }
    }

    public static List<String> getList(String directoryPath, int returnType, String[] containNameStringArray, String[] extNameArray) {
        List<String> returnValue = null;
        File file = null;
        File[] fileList = null;

        try {
            file = new File(directoryPath);
            if(!file.exists()) {
                return null;
            }

            fileList = file.listFiles();
            if(fileList == null || fileList.length == 0) {
                return null;
            }

            returnValue = new ArrayList();

            for(int i = 0; i < fileList.length; ++i) {
                if(returnType == 2 && fileList[i].isDirectory()) {
                    if(isContainStrings(fileList[i].getName(), containNameStringArray)) {
                        returnValue.add(fileList[i].getAbsolutePath());
                    }
                } else if(returnType == 1 && fileList[i].isFile()) {
                    if(isContainStrings(fileList[i].getName(), containNameStringArray) && isVaildExtNames(fileList[i].getName(), extNameArray)) {
                        returnValue.add(fileList[i].getAbsolutePath());
                    }
                } else if(returnType == 0 && isContainStrings(fileList[i].getName(), containNameStringArray)) {
                    returnValue.add(fileList[i].getAbsolutePath());
                }
            }
        } catch (Exception var8) {
            log.error(var8.getMessage(), var8);
        }

        return returnValue;
    }

    public static String getFilePathName(String directoryPath, int returnType, String containNameString, String extName) {
        String returnValue = null;
        File file = null;
        File[] fileList = null;

        try {
            file = new File(directoryPath);
            if(!file.exists()) {
                log.error("directoryPath not exists:" + directoryPath);
                return null;
            }

            fileList = file.listFiles();
            if(fileList == null || fileList.length == 0) {
                return null;
            }

            for(int i = 0; i < fileList.length; ++i) {
                if(returnType == 2 && fileList[i].isDirectory()) {
                    if(isContainString(fileList[i].getName(), containNameString)) {
                        returnValue = fileList[i].getAbsolutePath();
                    }
                } else if(returnType == 1 && fileList[i].isFile()) {
                    if(isContainString(fileList[i].getName(), containNameString) && isVaildExtName(fileList[i].getName(), extName)) {
                        returnValue = fileList[i].getAbsolutePath();
                    }
                } else if(returnType == 0 && isContainString(fileList[i].getName(), containNameString)) {
                    returnValue = fileList[i].getAbsolutePath();
                }
            }
        } catch (Exception var8) {
            log.error(var8.getMessage(), var8);
        }

        return returnValue;
    }

    public static List<String> getFilePathNameList(String directoryPath, int returnType, String containNameString, String extName) {
        List<String> resultList = new ArrayList();
        File file = null;
        File[] fileList = null;

        try {
            if(directoryPath != null) {
                file = new File(directoryPath);
                if(file.exists()) {
                    fileList = file.listFiles();
                    if(fileList != null && fileList.length != 0) {
                        for(int i = 0; i < fileList.length; ++i) {
                            if(returnType == 2 && fileList[i].isDirectory()) {
                                if(isContainString(fileList[i].getName(), containNameString)) {
                                    resultList.add(fileList[i].getAbsolutePath());
                                }
                            } else if(returnType == 1 && fileList[i].isFile()) {
                                if(isContainString(fileList[i].getName(), containNameString) && isVaildExtName(fileList[i].getName(), extName)) {
                                    resultList.add(fileList[i].getAbsolutePath());
                                }
                            } else if(returnType == 0 && isContainString(fileList[i].getName(), containNameString)) {
                                resultList.add(fileList[i].getAbsolutePath());
                            }
                        }
                    }
                } else {
                    log.error("Directory does not exist:" + directoryPath);
                }
            } else {
                log.error("parameter 'directoryPath' is null");
            }
        } catch (Exception var8) {
            log.error(var8.getMessage(), var8);
        }

        return resultList;
    }

    public static List<String> readFile(String filename) {
        List<String> returnValue = null;
        File file = null;
        file = new File(filename);
        returnValue = readFile(file);
        return returnValue;
    }

    public static List<String> readFile(File file) {
        List<String> returnValue = null;
        BufferedReader bufferedReader = null;
        String line = null;

        Object var4;
        try {
            if(file.exists()) {
                bufferedReader = new BufferedReader(new FileReader(file));
                returnValue = new ArrayList();

                while((line = bufferedReader.readLine()) != null) {
                    returnValue.add(line);
                }

                return returnValue;
            }

            var4 = null;
        } catch (Exception var15) {
            log.error(var15.getMessage(), var15);
            return returnValue;
        } finally {
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }

                bufferedReader = null;
                line = null;
            } catch (IOException var14) {
                log.error(var14.getMessage(), var14);
            }

        }

        return (List)var4;
    }

    public static String readFileDefaultStr(File file) {
        StringBuffer buff = new StringBuffer();
        BufferedReader bufferedReader = null;
        String line = null;

        Object var4;
        try {
            if(file.exists()) {
                bufferedReader = new BufferedReader(new FileReader(file));

                while((line = bufferedReader.readLine()) != null) {
                    buff.append(line);
                }

                return buff.toString();
            }

            var4 = null;
        } catch (Exception var15) {
            log.error(var15.getMessage(), var15);
            return buff.toString();
        } finally {
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }

                bufferedReader = null;
                line = null;
            } catch (IOException var14) {
                log.error(var14.getMessage(), var14);
            }

        }

        return (String)var4;
    }

    public static String readFileStr(File file) {
        StringBuffer buff = new StringBuffer();
        BufferedReader bufferedReader = null;
        InputStreamReader isr = null;
        FileInputStream fis = null;
        String line = null;

        try {
            if(!file.exists()) {
                Object var6 = null;
                return (String)var6;
            }

            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "UTF-8");
            bufferedReader = new BufferedReader(isr);

            while((line = bufferedReader.readLine()) != null) {
                buff.append(line);
            }
        } catch (Exception var27) {
            log.error(var27.getMessage(), var27);
        } finally {
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }

                bufferedReader = null;
                line = null;
            } catch (IOException var26) {
                log.error(var26.getMessage(), var26);
            }

            if(isr != null) {
                try {
                    isr.close();
                } catch (IOException var25) {
                    log.error(var25.getMessage(), var25);
                }
            }

            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException var24) {
                    log.error(var24.getMessage(), var24);
                }
            }

        }

        return buff.toString();
    }

    public static Properties readProperties(String path) {
        if(org.apache.commons.lang3.StringUtils.isEmpty(path)) {
            log.error("文件路径为空");
            return null;
        } else {
            Properties properties = new Properties();
            FileInputStream inputStream = null;

            Object var4;
            try {
                inputStream = new FileInputStream(path);
                properties.load(inputStream);
                return properties;
            } catch (Exception var14) {
                log.error("文件路径{}", path, var14);
                var4 = null;
            } finally {
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException var13) {
                        log.error("文件路径{}", path, var13);
                        return null;
                    }
                }

            }

            return (Properties)var4;
        }
    }

    public static boolean writeFile(File file, String content, boolean append) {
        boolean returnValue = false;
        BufferedWriter bufferedWriter = null;

        boolean var5;
        try {
            if(file.getParentFile().exists() || file.getParentFile().mkdirs()) {
                if(!file.exists() && !file.createNewFile()) {
                    var5 = false;
                    return var5;
                }

                bufferedWriter = new BufferedWriter(new FileWriter(file, append));
                bufferedWriter.write(content);
                returnValue = true;
                return returnValue;
            }

            var5 = false;
        } catch (Exception var17) {
            log.error(var17.getMessage(), var17);
            return returnValue;
        } finally {
            try {
                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }

                bufferedWriter = null;
            } catch (IOException var16) {
                log.error(var16.getMessage(), var16);
            }

        }

        return var5;
    }

    public static boolean writeFile(String filepathname, String content, boolean append) {
        boolean returnValue = false;
        File file = null;

        try {
            file = new File(filepathname);
            returnValue = writeFile(file, content, append);
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
        }

        return returnValue;
    }

    public static boolean writeFile(String filepathname, StringBuffer content, boolean append) {
        boolean returnValue = false;
        File file = null;

        try {
            file = new File(filepathname);
            returnValue = writeFile(file, content.toString(), append);
            if(returnValue) {
                content.delete(0, content.length());
            }
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
        }

        return returnValue;
    }

    public static boolean writeFile(String filepathname, TreeMap<String, String> contentMap, boolean append) {
        boolean returnValue = false;
        File file = null;
        Set<Map.Entry<String, String>> set = null;
        Iterator<Map.Entry<String, String>> iterator = null;
        StringBuffer contentBuffer = null;

        try {
            if(contentMap == null || contentMap.size() == 0) {
                return false;
            }

            file = new File(filepathname);
            contentBuffer = new StringBuffer();
            set = contentMap.entrySet();
            iterator = set.iterator();

            while(iterator.hasNext()) {
                Map.Entry<String, String> me = (Map.Entry)iterator.next();
                String key = (String)me.getKey();
                String value = (String)contentMap.get(key);
                if(value == null) {
                    contentBuffer.append(key + "\n");
                } else {
                    contentBuffer.append(key + " " + value + "\n");
                }
            }

            returnValue = writeFile(file, contentBuffer.toString(), append);
        } catch (Exception var11) {
            log.error(var11.getMessage(), var11);
        }

        return returnValue;
    }

}
