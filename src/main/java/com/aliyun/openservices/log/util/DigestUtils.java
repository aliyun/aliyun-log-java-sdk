package com.aliyun.openservices.log.util;

import com.aliyun.openservices.log.common.Consts;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public final class DigestUtils {

    private DigestUtils() {
    }

    public static String md5Crypt(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(Consts.CONST_MD5);
            String res = new BigInteger(1, md.digest(bytes)).toString(16).toUpperCase();

            StringBuilder zeros = new StringBuilder();
            for (int i = 0; i + res.length() < 32; i++) {
                zeros.append("0");
            }
            return zeros.toString() + res;
        } catch (NoSuchAlgorithmException e) {
            // never happen
            throw new RuntimeException("Not Supported signature method "
                    + Consts.CONST_MD5, e);
        }
    }

    public static void addSignature(String accessKeyId, String accessKey, String verb,
                                    Map<String, String> headers, String resourceUri,
                                    Map<String, String> urlParams) {
        StringBuilder builder = new StringBuilder();
        builder.append(verb).append("\n");
        builder.append(Utils.getOrEmpty(headers, Consts.CONST_CONTENT_MD5)).append("\n");
        builder.append(Utils.getOrEmpty(headers, Consts.CONST_CONTENT_TYPE)).append("\n");
        builder.append(Utils.getOrEmpty(headers, Consts.CONST_DATE)).append("\n");
        builder.append(headersToString(headers)).append("\n");
        builder.append(resourceUri);
        if (!urlParams.isEmpty()) {
            builder.append("?");
            builder.append(urlParametersToString(urlParams));
        }
        String signature = encode(accessKey, builder.toString());
        headers.put(Consts.CONST_AUTHORIZATION, Consts.CONST_HEADSIGNATURE_PREFIX + accessKeyId + ":" + signature);
    }

    private static String urlParametersToString(Map<String, String> paras) {
        Map<String, String> treeMap = new TreeMap<String, String>(paras);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }

    private static String headersToString(Map<String, String> headers) {
        Map<String, String> treeMap = new TreeMap<String, String>(headers);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            if (!entry.getKey().startsWith(Consts.CONST_X_SLS_PREFIX)
                    && !entry.getKey().startsWith(Consts.CONST_X_ACS_PREFIX)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(entry.getKey()).append(":").append(entry.getValue());
        }
        return builder.toString();
    }

    private static String encode(String accessKey, String data) {
        try {
            byte[] keyBytes = accessKey.getBytes(Consts.UTF_8_ENCODING);
            byte[] dataBytes = data.getBytes(Consts.UTF_8_ENCODING);
            Mac mac = Mac.getInstance(Consts.HMAC_SHA1_JAVA);
            mac.init(new SecretKeySpec(keyBytes, Consts.HMAC_SHA1_JAVA));
            return new String(Base64.encodeBase64(mac.doFinal(dataBytes)));
        } catch (UnsupportedEncodingException e) { // actually these exceptions
            // should never happened
            throw new RuntimeException("Not supported encoding method "
                    + Consts.UTF_8_ENCODING, e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Not supported signature method "
                    + Consts.HMAC_SHA1, e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to calculate the signature", e);
        }
    }
}
