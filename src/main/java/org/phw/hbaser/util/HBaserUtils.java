package org.phw.hbaser.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Dates;
import org.phw.core.lang.Ios;
import org.phw.core.lang.Pair;
import org.phw.core.lang.Strings;
import org.phw.hbaser.ui.CreateTableDialog;
import org.phw.hbaser.ui.HBaserQueryPanel;
import org.phw.hbaser.ui.MultiTableOperationDialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Closeables;
import com.sun.imageio.plugins.bmp.BMPImageReader;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.png.PNGImageReader;

public class HBaserUtils {
    public static void closeQuietly(HTable hTable, ResultScanner rs) {
        Ios.closeQuietly(rs);
        Closeables.closeQuietly(hTable);
    }


    public static void error(Exception e) {
        JOptionPane.showMessageDialog(null, "Fail:" + e);
    }

    public static void message(String e) {
        JOptionPane.showMessageDialog(null, e);
    }

    public static void dispatchOperation(final JTextPane textResult, String operation, String tableName) {
        if (operation.contains("refresh")) {
            HTableDdls.listTables();
        }
        else if (operation.contains("disable")) {
            HTableDdls.disableTable(tableName, textResult);
        }
        else if (operation.contains("enable")) {
            HTableDdls.enableTable(tableName, textResult);
        }
        else if (operation.contains("truncate")) {
            HTableDdls.truncateTable(tableName, textResult);
        }
        else if (operation.contains("drop")) {
            HTableDdls.dropTable(tableName, textResult);
        }
        else if (operation.contains("describe")) {
            HTableDdls.describeTable(tableName, textResult);
        }
        else if (operation.contains("create")) {
            CreateTableDialog dialog = new CreateTableDialog(null);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
            // HTableDdls.createTable(tableName, textResult);
        }
        else if (operation.contains("multioperate")) {
            MultiTableOperationDialog dialog = new MultiTableOperationDialog();
            dialog.setVisible(true);
        }
    }

    public static String putJSONValue(String json, String location, String value) {
        JSONObject root = Strings.isBlank(json) ? new JSONObject() : JSON.parseObject(json);
        JSONObject jsonObj = root;
        String[] imageLocations = Strings.split(location, ".", true);
        for (int i = 0; i < imageLocations.length; ++i) {
            if (i == imageLocations.length - 1) {
                jsonObj.put(imageLocations[i], value);
            }
            else {
                JSONObject jsonObject = jsonObj.getJSONObject(imageLocations[i]);
                if (jsonObject == null) {
                    jsonObject = new JSONObject();
                    jsonObj.put(imageLocations[i], jsonObject);
                }

                jsonObj = jsonObject;
            }
        }

        return root.toJSONString();
    }

    public static String getJSONValue(String location, String json) {
        if (Strings.isBlank(location)) {
            return null;
        }

        JSONObject jsonObj = JSON.parseObject(json);
        String[] imageLocations = Strings.split(location, ".", true);
        for (int i = 0; i < imageLocations.length; ++i) {
            if (i == imageLocations.length - 1) {
                return jsonObj.getString(imageLocations[i]);
            }
            jsonObj = jsonObj.getJSONObject(imageLocations[i]);
            if (jsonObj == null) {
                JOptionPane.showMessageDialog(null, "找不到图片信息");
                return null;
            }
        }

        return null;
    }

    public static String getImageType(InputStream is) {
        MemoryCacheImageInputStream mcis = null;
        try {
            mcis = new MemoryCacheImageInputStream(is);
            Iterator itr = ImageIO.getImageReaders(mcis);
            if (itr.hasNext()) {
                ImageReader reader = (ImageReader) itr.next();
                if (reader instanceof GIFImageReader) {
                    return "gif";
                }
                if (reader instanceof JPEGImageReader) {
                    return "jpeg";
                }
                else if (reader instanceof PNGImageReader) {
                    return "png";
                }
                else if (reader instanceof BMPImageReader) {
                    return "bmp";
                }
            }
        }
        finally {
            Ios.closeQuietly(is);
            Ios.closeQuietly(mcis);
        }
        return "";
    }

    public static byte[] getImage(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);
        return baos.toByteArray();
    }

    final private static String IMAGE_FLAG = "#Cache#\\d+";

    /**
     * 从JSON对象中读取图片.
     * 
     * @param jsonObj
     * @return
     */
    public static HashMap<String, ImageMeta> getImageMetasFromJSON(JSONObject jsonObj) {
        HashMap<String, ImageMeta> metas = new HashMap<String, ImageMeta>();
        for (Entry<String, Object> entry : jsonObj.entrySet()) {
            if (entry.getValue().getClass() != JSONObject.class) {
                continue;
            }
            for (Entry<String, Object> entry2 : ((JSONObject) entry.getValue()).entrySet()) {
                if (entry2.getValue().getClass() != String.class) {
                    continue;
                }
                String content = (String) entry2.getValue();
                if (content.matches(IMAGE_FLAG)) {
                    try {
                        ImageMeta meta = new ImageMeta();
                        meta.setFamily(entry.getKey()); // 列族
                        meta.setQualifier(entry2.getKey()); // 列
                        byte[] imageBytes = BigBytesCache.getCache(content);
                        meta.setImage(imageBytes); // 图片数据
                        meta.setFormat(getImageType(new ByteArrayInputStream(imageBytes))); // 图片格式
                        metas.put(content, meta); // 缓存编号为Key
                    }
                    catch (Exception e) {
                        HBaserUtils.error(e);
                        ImageMeta meta = new ImageMeta();
                        meta.setFamily(entry.getKey());
                        meta.setQualifier(entry2.getKey());
                        meta.setImage(null);
                        meta.setFormat("");
                        metas.put(content, meta);
                        continue;
                    }
                }
            }
        }
        return metas;
    }

    public static void loadToCache(byte[] imageBytes, JTextPane textResult) {
        StyledDocument doc = textResult.getStyledDocument();
        long start = System.currentTimeMillis();
        String cacheID = BigBytesCache.putCache(imageBytes);
        long end = System.currentTimeMillis();
        try {
            doc.insertString(doc.getLength(),
                    Dates.format(end) + " 用时" + (end - start) / 1000.
                            + "秒。Cache Ref is " + cacheID + "\n\n",
                    JTextPaneStyle.FAMATTR);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static enum FileOp {
        OpenFile, OpenDir, Save
    }

    public static File getSelectedFile(JFileChooser fc, FileOp fileOp) {
        int result = showFileDialog(fc, fileOp);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = fc.getSelectedFile();
        if (fileOp == FileOp.Save && file.exists()) {
            result = JOptionPane.showConfirmDialog(null, file + "已经存在，是否覆盖?");
            if (result != JFileChooser.APPROVE_OPTION) {
                return null;
            }
        }

        Preferences prefs = Preferences.userNodeForPackage(HBaserQueryPanel.class);
        prefs.put("HBASER_DATA_DIR", file.getPath());

        return file;
    }

    public static int showFileDialog(JFileChooser fc, FileOp fileOp) {
        int result = JFileChooser.CANCEL_OPTION;
        switch (fileOp) {
        case OpenDir:
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            result = fc.showOpenDialog(null);
            break;
        case OpenFile:
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            result = fc.showOpenDialog(null);
            break;
        case Save:
            result = fc.showSaveDialog(null);
            break;
        default:
            break;
        }

        return result;
    }

    public static JFileChooser getJFileChooserWithLastPath() {
        JFileChooser fc = new JFileChooser();
        Preferences prefs = Preferences.userNodeForPackage(HBaserQueryPanel.class);
        String lastOutputDir = prefs.get("HBASER_DATA_DIR", ".");
        fc.setCurrentDirectory(new File(lastOutputDir));

        return fc;
    }

    public static void selectCurrentPositionLine(JTextPane textResult) {
        /*
         * 注意：光标位置，要去掉\n换行符，才准确。
         */
        int offset = textResult.getCaretPosition();
        String str = textResult.getText();

        int startLine = -2;
        int lastStart = 0;
        int linebreaks = 0;
        do {
            lastStart = startLine;
            startLine = str.indexOf("\n", startLine + 2);
            ++offset;
            ++linebreaks;
        }
        while (startLine >= 0 && startLine < offset);

        if (lastStart < 0) {
            lastStart = 0;
        }

        int endLine = str.indexOf("\n", offset);
        if (endLine < 0) {
            endLine = str.length();
        }

        textResult.setSelectionStart(lastStart - linebreaks);
        textResult.setSelectionEnd(endLine - linebreaks);
    }

    /**
     * 计算光标所在行的JSON。
     * 
     * @param textResult
     * @return
     */
    public static String getCurrentPositionJson(final JTextPane textResult) {
        /*
         * 注意：光标位置，要去掉\n换行符，才准确。
         */
        int offset = textResult.getCaretPosition();
        String str = textResult.getText();

        int startLine = -2;
        int lastStart = 0;
        do {
            lastStart = startLine;
            startLine = str.indexOf("\n", startLine + 2);
            ++offset;
        }
        while (startLine >= 0 && startLine < offset);

        if (lastStart < 0) {
            lastStart = 0;
        }

        int endLine = str.indexOf("\n", offset);
        if (endLine < 0) {
            endLine = str.length();
        }

        String resultLine = textResult.getText().substring(lastStart, endLine).trim();
        // JOptionPane.showMessageDialog(null, resultLine);
        int posOfStart = resultLine.indexOf('{');
        String json = posOfStart >= 0 ? resultLine.substring(posOfStart).trim() : null;
        return json;
    }

    public static void clearTextPane(final JTextPane textResult) {
        StyledDocument doc = textResult.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        }
        catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Remaps a given result map from hbase to an order by timestamp
     * 
     * @param map a map as returned by Result.getMap()
     * @return a map family->ts->column->value
     */
    public static HashMap<String, List<Entry<Long, List<Pair<byte[], byte[]>>>>>
            remapByFamilyAndTimestamp(NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map) {

        HashMap<String, List<Entry<Long, List<Pair<byte[], byte[]>>>>> out = null;
        out = new HashMap<String, List<Entry<Long, List<Pair<byte[], byte[]>>>>>();
        for (Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> familyNameBytes : map.entrySet()) {
            TreeMap<Long, List<Pair<byte[], byte[]>>> familyValues = new TreeMap<Long, List<Pair<byte[], byte[]>>>();

            for (Entry<byte[], NavigableMap<Long, byte[]>> columnNameBytes : familyNameBytes.getValue().entrySet()) {
                for (Entry<Long, byte[]> tsEntry : columnNameBytes.getValue().entrySet()) {
                    byte[] valueBytes = tsEntry.getValue();

                    Long ts = tsEntry.getKey();
                    List<Pair<byte[], byte[]>> version = familyValues.get(ts);
                    if (version == null) {
                        version = new ArrayList<Pair<byte[], byte[]>>();
                        familyValues.put(ts, version);
                    }

                    version.add(Pair.makePair(columnNameBytes.getKey(), valueBytes));
                }
            }

            List<Entry<Long, List<Pair<byte[], byte[]>>>> arr = null;
            arr = new ArrayList<Entry<Long, List<Pair<byte[], byte[]>>>>(familyValues.size());
            for (Entry<Long, List<Pair<byte[], byte[]>>> tsValues : familyValues.entrySet()) {
                arr.add(tsValues);
            }

            out.put(Bytes.toString(familyNameBytes.getKey()), arr);
        }

        return out;
    }

}
