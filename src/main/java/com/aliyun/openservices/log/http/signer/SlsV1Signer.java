package com.aliyun.openservices.log.http.signer;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.auth.Credentials;
import com.aliyun.openservices.log.common.auth.CredentialsProvider;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.utils.DateUtil;
import com.aliyun.openservices.log.util.Utils;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class SlsV1Signer extends SlsSignerBase implements SlsSigner {

    public SlsV1Signer(CredentialsProvider credentialsProvider) {
        super(credentialsProvider);
    }

    public static String md5Crypt(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(Consts.CONST_MD5);
            String res = new BigInteger(1, md.digest(bytes)).toString(16).toUpperCase();

            StringBuilder zeros = new StringBuilder();
            for (int i = 0; i + res.length() < 32; i++) {
                zeros.append("0");
            }
            return zeros + res;
        } catch (NoSuchAlgorithmException e) {
            // never happen
            throw new RuntimeException("Not Supported signature method "
                    + Consts.CONST_MD5, e);
        }
    }

    @Override
    public void sign(HttpMethod httpMethod,
                     Map<String, String> headers,
                     String resourceUri,
                     Map<String, String> urlParams,
                     byte[] body) {
        Credentials credentials = credentialsProvider.getCredentials();
        addHeaderSecretToken(credentials.getSecurityToken(), headers);

        String contendMD5;
        if (body != null && body.length > 0) {
            contendMD5 = SlsV1Signer.md5Crypt(body);
            headers.put(Consts.CONST_CONTENT_MD5, contendMD5);
            headers.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(body.length));
        } else {
            contendMD5 = Consts.EMPTY_STRING;
            headers.put(Consts.CONST_CONTENT_LENGTH, Consts.EMPTY_STRING_LENGTH);
        }
        String dateGMT = DateUtil.formatRfc822Date(new Date());
        headers.put(Consts.CONST_DATE, dateGMT);
        headers.put(Consts.CONST_X_SLS_SIGNATUREMETHOD, Consts.HMAC_SHA1);
        StringBuilder builder = new StringBuilder();
        builder.append(httpMethod.toString()).append("\n");
        builder.append(contendMD5).append("\n");
        builder.append(Utils.getOrEmpty(headers, Consts.CONST_CONTENT_TYPE)).append("\n");
        builder.append(dateGMT).append("\n");
        builder.append(headersToString(headers)).append("\n");
        builder.append(resourceUri);
        if (!urlParams.isEmpty()) {
            builder.append("?");
            builder.append(urlParametersToString(urlParams));
        }

        String signature = encode(credentials.getAccessKeySecret(), builder.toString());
        headers.put(Consts.CONST_AUTHORIZATION, Consts.CONST_HEADSIGNATURE_PREFIX + credentials.getAccessKeyId() + ":" + signature);
    }

    static String urlParametersToString(Map<String, String> paras) {
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
            // should never have happened
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
