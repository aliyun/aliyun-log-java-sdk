package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

public class CnameConfiguration {

    public enum CnameStatus {
        /**
         * Unknown status
         */
        Unknown("Unknown"),

        /**
         * Enabled
         */
        Enabled("Enabled"),

        /**
         * Disabled
         */
        Disabled("Disabled"),

        /**
         * Blocked
         */
        Blocked("Blocked"),

        /**
         * Forbidden
         */
        Forbidden("Forbidden");

        private final String cnameStatusString;

        private CnameStatus(String cnameStatusString) {
            this.cnameStatusString = cnameStatusString;
        }

        @Override
        public String toString() {
            return this.cnameStatusString;
        }

        public static CnameStatus parse(String cnameStatusString) {
            for (CnameStatus st : CnameStatus.values()) {
                if (st.toString().equalsIgnoreCase(cnameStatusString)) {
                    return st;
                }
            }
            throw new IllegalArgumentException("Unable to parse " + cnameStatusString);
        }
    }

    public enum CertStatus {
        /**
         * Unknown status
         */
        Unknown("Unknown"),

        /**
         * Enabled
         */
        Enabled("Enabled"),

        /**
         * Disabled
         */
        Disabled("Disabled"),

        /**
         * Blocked
         */
        Blocked("Blocked"),

        /**
         * Forbidden
         */
        Forbidden("Forbidden");

        private final String certStatusString;

        private CertStatus(String certStatusString) {
            this.certStatusString = certStatusString;
        }

        @Override
        public String toString() {
            return this.certStatusString;
        }

        public static CertStatus parse(String certStatusString) {
            for (CertStatus st : CertStatus.values()) {
                if (st.toString().equalsIgnoreCase(certStatusString)) {
                    return st;
                }
            }
            throw new IllegalArgumentException("Unable to parse " + certStatusString);
        }
    }

    public enum CertType {
        /**
         * Unknown type
         */
        Unknown("Unknown"),

        /**
         * Indicate the certificate is stored in CAS.
         */
        CAS("CAS"),

        /**
         * Indicate the certificate is uploaded by user
         */
        Upload("Upload");

        private final String certTypeString;

        private CertType(String certTypeString) {
            this.certTypeString = certTypeString;
        }

        @Override
        public String toString() {
            return this.certTypeString;
        }

        public static CertType parse(String certTypeString) {
            for (CertType ct : CertType.values()) {
                if (ct.toString().equalsIgnoreCase(certTypeString)) {
                    return ct;
                }
            }
            throw new IllegalArgumentException("Unable to parse " + certTypeString);
        }
    }

    public static class Certificate {
        private CertType type;
        private String certId;
        private CertStatus status;
        private String creationDate;
        private String fingerprint;
        private String validStartDate;
        private String validEndDate;

        public CertType getType() {
            return type;
        }

        public void setType(CertType type) {
            this.type = type;
        }

        public String getCertId() {
            return certId;
        }

        public void setCertId(String certId) {
            this.certId = certId;
        }

        public CertStatus getStatus() {
            return status;
        }

        public void setStatus(CertStatus status) {
            this.status = status;
        }

        public String getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        public String getValidStartDate() {
            return validStartDate;
        }

        public void setValidStartDate(String validStartDate) {
            this.validStartDate = validStartDate;
        }

        public String getValidEndDate() {
            return validEndDate;
        }

        public void setValidEndDate(String validEndDate) {
            this.validEndDate = validEndDate;
        }

        @Override
        public String toString() {
            return "Certificate{" +
                    "type=" + type +
                    ", certId='" + certId + '\'' +
                    ", status=" + status +
                    ", creationDate='" + creationDate + '\'' +
                    ", fingerprint='" + fingerprint + '\'' +
                    ", validStartDate='" + validStartDate + '\'' +
                    ", validEndDate='" + validEndDate + '\'' +
                    '}';
        }

        public void unmarshal(JSONObject unmarshalled) {
            type = CertType.parse(unmarshalled.getString("type"));
            certId = unmarshalled.getString("certId");
            status = CertStatus.parse(unmarshalled.getString("status"));
            creationDate = unmarshalled.getString("creationDate");
            fingerprint = unmarshalled.getString("fingerprint");
            validStartDate = unmarshalled.getString("validStartDate");
            validEndDate = unmarshalled.getString("validEndDate");
        }
    }

    public CnameConfiguration() {
        status = CnameStatus.Unknown;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public CnameStatus getStatus() {
        return status;
    }

    public void setStatus(CnameStatus status) {
        this.status = status;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public String toString() {
        return "CnameConfiguration{" +
                "domain='" + domain + '\'' +
                ", status=" + status +
                ", lastModifiedTime=" + lastModifiedTime +
                ", certificate=" + certificate +
                '}';
    }

    public void unmarshal(JSONObject unmarshalled) {
        domain = unmarshalled.getString("domain");
        lastModifiedTime = unmarshalled.getLongValue("lastModified");
        status = CnameStatus.parse(unmarshalled.getString("status"));
        JSONObject cert = unmarshalled.getJSONObject("certificate");
        if (cert != null) {
            certificate = new Certificate();
            certificate.unmarshal(cert);
        }
    }

    private String domain;
    private CnameStatus status;
    private long lastModifiedTime;
    private Certificate certificate;
}
