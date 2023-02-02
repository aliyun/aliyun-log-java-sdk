package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.JSONObject;

public class CertificateConfiguration {
    /**
     * Certificate public key, PEM format
     */
    private String publicKey;

    /**
     * Certificate private key, PEM format
     */
    private String privateKey;

    /**
     * Certificate ID in CAS
     */
    private String certId;

    /**
     * If enabled, SLS will not check the previous certificate ID.
     */
    private boolean forceOverwriteCert = false;

    /**
     * Previous certificate ID.
     */
    private String previousCertId;

    /**
     * If enabled, Delete certificate.
     */
    private Boolean deleteCertificate;


    /**
     * Gets the certificate public key.
     *
     * @return the certificate public key.
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Sets the certificate public key.
     *
     * @param publicKey the certificate public key.
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Sets the certificate public key and return the updated CertificateConfiguration object.
     *
     * @param publicKey certificate public key.
     * @return The {@link CertificateConfiguration} instance.
     */
    public CertificateConfiguration withPublicKey(String publicKey) {
        setPublicKey(publicKey);
        return this;
    }

    /**
     * Gets the certificate private key.
     *
     * @return the certificate private key.
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Sets the certificate private key.
     *
     * @param privateKey the certificate private key.
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * Sets the certificate private key and return the updated CertificateConfiguration object.
     *
     * @param privateKey the certificate private key.
     * @return The {@link CertificateConfiguration} instance.
     */
    public CertificateConfiguration withPrivateKey(String privateKey) {
        setPrivateKey(privateKey);
        return this;
    }

    /**
     * Gets the certificate ID.
     *
     * @return the certificate ID
     */
    public String getCertId() {
        return certId;
    }

    /**
     * Sets the certificate ID.
     *
     * @param certId the certificate ID
     */
    public void setCertId(String certId) {
        this.certId = certId;
    }

    /**
     * Sets the certificate ID and return the updated CertificateConfiguration object.
     *
     * @param certId the certificate ID
     * @return The {@link CertificateConfiguration} instance.
     */
    public CertificateConfiguration withCertId(String certId) {
        setCertId(certId);
        return this;
    }

    /**
     * Get if force overwrite certificate or not.
     *
     * @return True if force overwrite cert; False if not.
     */
    public boolean isForceOverwriteCert() {
        return forceOverwriteCert;
    }

    /**
     * Set if force overwrite certificate or not.
     *
     * @param forceOverwriteCert If enabled, SLS will not check the previous certificate ID.
     */
    public void setForceOverwriteCert(boolean forceOverwriteCert) {
        this.forceOverwriteCert = forceOverwriteCert;
    }

    /**
     * Set if force overwrite certificate or not and return the updated CertificateConfiguration object.
     *
     * @param forceOverwriteCert If enabled, SLS will not check the previous certificate ID.
     * @return The {@link CertificateConfiguration} instance.
     */
    public CertificateConfiguration withForceOverwriteCert(boolean forceOverwriteCert) {
        setForceOverwriteCert(forceOverwriteCert);
        return this;
    }

    /**
     * Gets the previous certificate ID.
     *
     * @return the previous certificate ID.
     */
    public String getPreviousCertId() {
        return previousCertId;
    }

    /**
     * Sets the previous certificate ID.
     *
     * @param previousCertId the previous certificate ID.
     */
    public void setPreviousCertId(String previousCertId) {
        this.previousCertId = previousCertId;
    }

    /**
     * Sets the previous certificate ID and return the updated CertificateConfiguration object.
     *
     * @param previousCertId the previous certificate ID.
     * @return The {@link CertificateConfiguration} instance.
     */
    public CertificateConfiguration withPreviousCertId(String previousCertId) {
        setPreviousCertId(previousCertId);
        return this;
    }

    /**
     * Gets the delete certificate flag.
     *
     * @return True if delete certificate; False if not.
     */
    public Boolean getDeleteCertificate() {
        return deleteCertificate;
    }

    /**
     * Sets the delete certificate flag.
     *
     * @param deleteCertificate delete certificate flag.
     */
    public void setDeleteCertificate(Boolean deleteCertificate) {
        this.deleteCertificate = deleteCertificate;
    }

    /**
     * Sets the deleting certificate flag and return the updated CertificateConfiguration object.
     *
     * @param deleteCertificate delete certificate flag.
     * @return The {@link CertificateConfiguration} instance.
     */
    public CertificateConfiguration withDeleteCertificate(Boolean deleteCertificate) {
        setDeleteCertificate(deleteCertificate);
        return this;
    }

    public JSONObject marshal() {
        JSONObject object = new JSONObject();
        object.put("publicKey", publicKey);
        object.put("privateKey", privateKey);
        if (certId != null) {
            object.put("certId", certId);
        }
        if (forceOverwriteCert) {
            object.put("force", true);
        }
        object.put("previousCertId", previousCertId);
        if (deleteCertificate != null) {
            object.put("deleteCertificate", deleteCertificate);
        }
        return object;
    }
}

